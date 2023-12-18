package epa.InventoryApp.usecase.producto;

import epa.InventoryApp.drivenAdapters.repository.ProductoRepository;
import epa.InventoryApp.models.Producto;
import epa.InventoryApp.models.dto.ProductoDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Service
public class CrearProductoUseCase implements Function<ProductoDTO, Mono<ProductoDTO>>
{
    //------------------------------------------------------------------------- (Inyeccion de Dependencias)
    private ProductoRepository repositorio;

    public CrearProductoUseCase(ProductoRepository repositorio)
    {
        this.repositorio = repositorio;
    }

    @Override
    public Mono<ProductoDTO> apply(ProductoDTO productoDTO)
    {
        Producto producto = new Producto(productoDTO.getId(),
                                         productoDTO.getDescripcion(),
                                         productoDTO.getExistencia(),
                                         productoDTO.getCosto(),
                                         productoDTO.getPrecioUnitarioAlDetalle(),
                                         productoDTO.getPrecioUnitarioAlPorMayor(),
                                         productoDTO.getUnidadesMinimaAlPorMayor()
                                        );

        return repositorio.save(producto)
                .map(productoModel -> {
                    return new ProductoDTO( productoModel.getId(),
                                            productoModel.getDescripcion(),
                                            productoModel.getExistencia(),
                                            productoModel.getCosto(),
                                            productoModel.getPrecioUnitarioAlDetalle(),
                                            productoModel.getPrecioUnitarioAlPorMayor(),
                                            productoModel.getUnidadesMinimaAlPorMayor());
                });
    }

}
