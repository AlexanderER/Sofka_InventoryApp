package epa.InventoryApp.usecase.movimientosInventario;

import epa.InventoryApp.drivenAdapters.bus.RabbitMqPublisher;
import epa.InventoryApp.drivenAdapters.repository.ProductoRepository;
import epa.InventoryApp.models.Producto;
import epa.InventoryApp.models.TipoMovimiento;
import epa.InventoryApp.models.dto.MovimientosDTO;
import epa.InventoryApp.models.dto.MovimientosDTObuilder;
import epa.InventoryApp.models.dto.ProductoDTO;
import epa.InventoryApp.models.dto.VentaInventarioDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Function;

@Service
public class VentaAlPorMayorUseCase implements Function<List<VentaInventarioDTO>, Flux<ProductoDTO>>
{
    //------------------------------------------------------------------------- (Inyeccion de Dependencias)
    private ProductoRepository repositorio;
    private RabbitMqPublisher eventBus;

    public VentaAlPorMayorUseCase(ProductoRepository repositorio, RabbitMqPublisher eventBus)
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
                        return Flux.error(new RuntimeException("[Producto] Error al menos un elemento no aplica para venta al por Mayor."));
                    }
                });
    }

    private Mono<Boolean> cumpleCondiciones(VentaInventarioDTO dto)
    {
        String idProducto     = dto.getIdProducto();
        Integer cantidadVenta = dto.getCantidad();

        return repositorio.findById(idProducto)
                .map(Producto::getUnidadesMinimaAlPorMayor)
                .map(unidadesMinimaAlPorMayor -> cantidadVenta >= unidadesMinimaAlPorMayor)
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
                                        .setTipo(TipoMovimiento.VENTA_AL_POR_MAYOR)
                                        .setCantidad(cantidadVenta)
                                        .setExistenciaInicial(inventarioInicial)
                                        .setExistenciaFinal(productoModel.getExistencia())
                                        .setCosto(productoModel.getCosto())
                                        .setPrecio(producto.getPrecioUnitarioAlPorMayor())
                                        .build();

                                eventBus.publishMovement(movimiento);
                            })
                            .doOnError(error -> Mono.error(new RuntimeException("[Producto] Error al Procesar Venta al Por Mayor.", error)))
                            .map(this::getProductoDTO);
                })
                .switchIfEmpty(Mono.error(new RuntimeException("Producto no encontrado con ID: " + idProducto)));
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
