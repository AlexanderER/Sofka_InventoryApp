package epa.InventoryApp.usecase.movimientosInventario;

import epa.InventoryApp.drivenAdapters.bus.RabbitMqPublisher;
import epa.InventoryApp.drivenAdapters.repository.ProductoRepository;
import epa.InventoryApp.models.Producto;
import epa.InventoryApp.models.TipoMovimiento;
import epa.InventoryApp.models.dto.*;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

@Service
public class VentaAlDetalleUseCase implements Function<List<VentaInventarioDTO>, Flux<ProductoDTO>>
{
    //------------------------------------------------------------------------- (Inyeccion de Dependencias)
    private ProductoRepository repositorio;
    private RabbitMqPublisher eventBus;

    public VentaAlDetalleUseCase(ProductoRepository repositorio, RabbitMqPublisher eventBus)
    {
        this.repositorio = repositorio;
        this.eventBus = eventBus;
    }


    //------------------------------------------------------------------------- (Implementación Uso de Caso)
    @Override
    public Flux<ProductoDTO> apply(List<VentaInventarioDTO> ventaInventarioDTOS)
    {
        return Flux.fromIterable(ventaInventarioDTOS)
                .flatMap(this::cumpleCondiciones)
                .collectList()
                .flatMapMany(cumpleTodas -> //Cada elemento se transforma en un Flux
                {
                    if (cumpleTodas.stream().allMatch(Boolean::booleanValue))
                    {
                        return Flux.fromIterable(ventaInventarioDTOS)
                                .concatMap(this::realizarVenta);
                    }
                    else
                    {
                        eventBus.publishError("[VentaAlDetalleUseCase] [" + LocalDateTime.now().toString() + "] Al menos un elemento no aplica para venta al Detalle.");
                        return Flux.error(new RuntimeException("[Producto] Error al menos un elemento no aplica para venta al Detalle."));
                    }
                });
    }

    private Mono<Boolean> cumpleCondiciones(VentaInventarioDTO dto)
    {
        String idProducto     = dto.getIdProducto();
        Integer cantidadVenta = dto.getCantidad();

        return repositorio.findById(idProducto)
                          .map(Producto::getUnidadesMinimaAlPorMayor)
                          .map(unidadesMinimaAlPorMayor -> cantidadVenta < unidadesMinimaAlPorMayor)
                          .defaultIfEmpty(false);
    }

    private Mono<ProductoDTO> realizarVenta(VentaInventarioDTO ventaInventarioDTO)
    {
        String idProducto = ventaInventarioDTO.getIdProducto();
        Integer cantidadVenta = ventaInventarioDTO.getCantidad();

        return repositorio.findById(idProducto)
                .flatMap(producto ->
                {
                    Integer inventarioInicial = producto.getExistencia();
                    producto.setExistencia(producto.getExistencia() - cantidadVenta);

                    return repositorio.save(producto)
                            .doOnSuccess(productoModel -> {
                                    MovimientosDTO movimiento = new MovimientosDTObuilder()
                                                                .setIdProducto(productoModel.getId())
                                                                .setTipo(TipoMovimiento.VENTA_AL_DETALLE)
                                                                .setCantidad(cantidadVenta)
                                                                .setExistenciaInicial(inventarioInicial)
                                                                .setExistenciaFinal(productoModel.getExistencia())
                                                                .setCosto(productoModel.getCosto())
                                                                .setPrecio(producto.getPrecioUnitarioAlDetalle())
                                                                .build();

                                    eventBus.publishMovement(movimiento);
                            })
                            .doOnError(error -> {
                                                    eventBus.publishError("[VentaAlDetalleUseCase] [" + LocalDateTime.now().toString() + "] Error al Procesar Venta al Detalle. Id Producto: " + idProducto + " Cant: " + cantidadVenta);
                                                    Mono.error(new RuntimeException("[Producto] Error al registrar venta al Detalle.", error));
                                                })
                            .map(this::getProductoDTO);
                })
                .switchIfEmpty(Mono.defer(() -> {  // Para asegurarse de que eventBus.publishError("") se ejecute solo cuando switchIfEmpty se activa
                                                    eventBus.publishError("[VentaAlDetalleUseCase] [" + LocalDateTime.now().toString() + "] Producto no encontrado. Id: " + idProducto);
                                                    return Mono.error(new RuntimeException("Producto no encontrado con ID: " + idProducto));
                                                }));
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
