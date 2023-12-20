package epa.InventoryApp.usecase.movimientosInventario;

import epa.InventoryApp.drivenAdapters.bus.RabbitMqPublisher;
import epa.InventoryApp.drivenAdapters.repository.ProductoRepository;
import epa.InventoryApp.models.Producto;
import epa.InventoryApp.models.TipoMovimiento;
import epa.InventoryApp.models.dto.AgregarInventarioDTO;
import epa.InventoryApp.models.dto.MovimientosDTO;
import epa.InventoryApp.models.dto.MovimientosDTObuilder;
import epa.InventoryApp.models.dto.ProductoDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

@Service
public class AgregarInventarioPorLoteUseCase implements Function<List<AgregarInventarioDTO>, Flux<ProductoDTO>>
{
    //------------------------------------------------------------------------- (Inyeccion de Dependencias)
    private ProductoRepository repositorio;
    private RabbitMqPublisher eventBus;

    public AgregarInventarioPorLoteUseCase(ProductoRepository repositorio, RabbitMqPublisher eventBus)
    {
        this.repositorio = repositorio;
        this.eventBus = eventBus;
    }

    //------------------------------------------------------------------------- (Implementación Uso de Caso)
    @Override
    public Flux<ProductoDTO> apply(List<AgregarInventarioDTO> agregarInventarioDTOS)
    {
        return Flux.fromIterable(agregarInventarioDTOS)
                .concatMap(dto ->  // Tenia FlatMap pero lo cambie por concatMap para que se ejecute en orden y espere que la operacion anterior finalice
                {
                    String idProducto = dto.getIdProducto();
                    Integer cantidad  = dto.getCantidad();

                    return repositorio.findById(idProducto)
                            .flatMap(producto ->
                            {
                                Integer inventarioInicial = producto.getExistencia();
                                producto.setExistencia(producto.getExistencia() + cantidad);

                                return repositorio.save(producto)
                                        .doOnSuccess(productoModel -> {
                                            MovimientosDTO movimiento = new MovimientosDTObuilder().setIdProducto(productoModel.getId())
                                                                                                   .setTipo(TipoMovimiento.ACTUALIZACION_LOTE)
                                                                                                   .setCantidad(cantidad)
                                                                                                   .setExistenciaInicial(inventarioInicial)
                                                                                                   .setExistenciaFinal(productoModel.getExistencia())
                                                                                                   .setCosto(productoModel.getCosto())
                                                                                                   .setPrecio(BigDecimal.ZERO)
                                                                                                   .build();
                                            eventBus.publishMovement(movimiento);
                                        })
                                        .doOnError(error -> {
                                                                eventBus.publishError("[AgregarInventarioPorLoteUseCase] [" + LocalDateTime.now().toString() + "] Error al agregar inventario. Id Producto: " + idProducto + " Cant: " + cantidad);
                                                                Mono.error(new RuntimeException("[Producto] Error al Agregar Inventario por Lote.", error));
                                                            })
                                        .map(this::getProductoDTO);
                            })
                            .switchIfEmpty(Mono.defer(() -> {  // Para asegurarse de que eventBus.publishError("") se ejecute solo cuando switchIfEmpty se activa
                                                                eventBus.publishError("[AgregarInventarioPorLoteUseCase] [" + LocalDateTime.now().toString() + "] Producto no encontrado. Id: " + idProducto);
                                                                return Mono.error(new RuntimeException("[Producto] Error producto no encontrado."));
                                                            }));
                });
    }

    //------------------------------------------------------------------------- (Casteo)
    private ProductoDTO getProductoDTO(Producto productoModel)
    {
        return new ProductoDTO(productoModel.getId(),
                productoModel.getDescripcion(),
                productoModel.getExistencia(),
                productoModel.getCosto(),
                productoModel.getPrecioUnitarioAlDetalle(),
                productoModel.getPrecioUnitarioAlPorMayor(),
                productoModel.getUnidadesMinimaAlPorMayor());
    }


}
