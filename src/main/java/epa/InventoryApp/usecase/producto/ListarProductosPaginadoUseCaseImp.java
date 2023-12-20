package epa.InventoryApp.usecase.producto;

import epa.InventoryApp.drivenAdapters.bus.RabbitMqPublisher;
import epa.InventoryApp.drivenAdapters.repository.ProductoRepository;
import epa.InventoryApp.models.Producto;
import epa.InventoryApp.models.dto.ProductoDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class ListarProductosPaginadoUseCaseImp implements ListarProductosPaginadoUseCase
{
    //------------------------------------------------------------------------- (Inyeccion de Dependencias)
    private ProductoRepository repositorio;
    private RabbitMqPublisher eventBus;

    public ListarProductosPaginadoUseCaseImp(ProductoRepository repositorio, RabbitMqPublisher eventBus)
    {
        this.repositorio = repositorio;
        this.eventBus    = eventBus;
    }

    //------------------------------------------------------------------------- (Implementación Uso de Caso)
    @Override
    public Flux<ProductoDTO> apply(int pagina, int cantPorPagina)
    {
        return repositorio.findAll()
                .skip((pagina - 1) * cantPorPagina)
                .take(cantPorPagina)
                .map(this::getProductoDTO);
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
