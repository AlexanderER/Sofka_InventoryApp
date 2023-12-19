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
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.function.Function;

@Service
public class AgregarInventarioPorUnidadUseCase implements Function<AgregarInventarioDTO, Mono<ProductoDTO>>
{
    //------------------------------------------------------------------------- (Inyeccion de Dependencias)
    private ProductoRepository repositorio;
    private RabbitMqPublisher eventBus;

    public AgregarInventarioPorUnidadUseCase(ProductoRepository repositorio, RabbitMqPublisher eventBus)
    {
        this.repositorio = repositorio;
        this.eventBus = eventBus;
    }

    //------------------------------------------------------------------------- (Implementación Uso de Caso)
    @Override
    public Mono<ProductoDTO> apply(AgregarInventarioDTO agregarInventarioDTO)
    {
        String idProducto = agregarInventarioDTO.getIdProducto();
        Integer cantidad  = agregarInventarioDTO.getCantidad();

        return repositorio.findById(idProducto)
                .flatMap(producto -> {
                                        Integer inventarioInicial = producto.getExistencia();
                                        producto.setExistencia(producto.getExistencia() + cantidad);

                                        return repositorio.save(producto)
                                                .doOnSuccess(productoModel -> {
                                                    MovimientosDTO movimiento = new MovimientosDTObuilder().setIdProducto(productoModel.getId())
                                                                                                           .setTipo(TipoMovimiento.ACTUALIZACION_UNIDAD)
                                                                                                           .setCantidad(cantidad)
                                                                                                           .setExistenciaInicial(inventarioInicial)
                                                                                                           .setExistenciaFinal(productoModel.getExistencia())
                                                                                                           .setCosto(productoModel.getCosto())
                                                                                                           .setPrecio(BigDecimal.ZERO)
                                                                                                           .build();
                                                    eventBus.publishMovement(movimiento);
                                                })
                                                .doOnError(error -> Mono.error(new RuntimeException("[Producto] Error al Agregar Inventario por Unidad.", error)))
                                                .map(this::getProductoDTO);
                                     })
                .switchIfEmpty(Mono.error(new RuntimeException("[Producto] Error producto no encontrado.")));
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
