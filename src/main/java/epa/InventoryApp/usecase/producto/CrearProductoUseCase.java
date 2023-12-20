package epa.InventoryApp.usecase.producto;

import epa.InventoryApp.drivenAdapters.bus.RabbitMqPublisher;
import epa.InventoryApp.drivenAdapters.repository.ProductoRepository;
import epa.InventoryApp.models.Producto;
import epa.InventoryApp.models.TipoMovimiento;
import epa.InventoryApp.models.dto.MovimientosDTO;
import epa.InventoryApp.models.dto.MovimientosDTObuilder;
import epa.InventoryApp.models.dto.ProductoDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.function.Function;

@Service
public class CrearProductoUseCase implements Function<ProductoDTO, Mono<ProductoDTO>>
{
    //------------------------------------------------------------------------- (Inyeccion de Dependencias)
    private ProductoRepository repositorio;
    private RabbitMqPublisher eventBus;

    public CrearProductoUseCase(ProductoRepository repositorio, RabbitMqPublisher eventBus)
    {
        this.repositorio = repositorio;
        this.eventBus = eventBus;
    }


    //------------------------------------------------------------------------- (Implementación Uso de Caso)
    @Override
    public Mono<ProductoDTO> apply(ProductoDTO productoDTO)
    {
        Producto producto = getProducto(productoDTO);

        return repositorio.save(producto)
                .doOnSuccess(productoModel -> {
                                                MovimientosDTO movimiento = new MovimientosDTObuilder().setIdProducto(productoModel.getId())
                                                                                                       .setTipo(TipoMovimiento.CREACION_PRODUCTO)
                                                                                                       .setCantidad(productoModel.getExistencia())
                                                                                                       .setExistenciaInicial(0)
                                                                                                       .setExistenciaFinal(productoModel.getExistencia())
                                                                                                       .setCosto(productoModel.getCosto())
                                                                                                       .setPrecio(BigDecimal.ZERO)
                                                                                                       .build();
                                                eventBus.publishMovement(movimiento);
                                              })
                .doOnError(error -> {
                                        eventBus.publishError("[CrearProductoUseCase] [" + LocalDateTime.now().toString() + "] Error al crear producto. Descripcion: " + productoDTO.getDescripcion());
                                        Mono.error(new RuntimeException("[Producto] Error al guardar el producto.", error));
                                    })
                .map(this::getProductoDTO);
    }

    //------------------------------------------------------------------------- (Casteo)
    private Producto getProducto(ProductoDTO productoDTO)
    {
        return new Producto(productoDTO.getId(),
                productoDTO.getDescripcion(),
                productoDTO.getExistencia(),
                productoDTO.getCosto(),
                productoDTO.getPrecioUnitarioAlDetalle(),
                productoDTO.getPrecioUnitarioAlPorMayor(),
                productoDTO.getUnidadesMinimaAlPorMayor()
        );
    }

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
