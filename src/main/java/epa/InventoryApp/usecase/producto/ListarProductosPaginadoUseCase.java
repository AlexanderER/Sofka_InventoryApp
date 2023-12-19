package epa.InventoryApp.usecase.producto;

import epa.InventoryApp.models.dto.ProductoDTO;
import reactor.core.publisher.Flux;

@FunctionalInterface
public interface ListarProductosPaginadoUseCase
{
    Flux<ProductoDTO> apply(int pagina, int tamanoPagina);
}
