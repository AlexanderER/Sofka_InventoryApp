package epa.InventoryApp.usecase;

import epa.InventoryApp.drivenAdapters.bus.RabbitMqPublisher;
import epa.InventoryApp.drivenAdapters.repository.ProductoRepository;
import epa.InventoryApp.models.Producto;
import epa.InventoryApp.models.dto.MovimientosDTO;
import epa.InventoryApp.models.dto.ProductoDTO;
import epa.InventoryApp.usecase.producto.CrearProductoUseCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

public class CrearProductoUseCaseTest
{
    @Mock
    private ProductoRepository repositorio;

    @Mock
    private RabbitMqPublisher eventBus;

    @InjectMocks
    private CrearProductoUseCase crearProductoUseCase;

    @BeforeEach
    void setUp()
    {
        // Verifica que la inicialización de Mockito se esté realizando correctamente.
        MockitoAnnotations.openMocks(this);
    }

    private Producto getMockProducto()
    {
        return new Producto("id",
                "Producto #1",
                10,
                BigDecimal.valueOf(5.0),
                BigDecimal.valueOf(20.0),
                BigDecimal.valueOf(15.0),
                5);
    }

    private ProductoDTO getMockProductoDTO()
    {
        return new ProductoDTO("id",
                "Producto #1",
                10,
                BigDecimal.valueOf(5.0),
                BigDecimal.valueOf(20.0),
                BigDecimal.valueOf(15.0),
                5);
    }

    @Test
    @DisplayName("UseCase Producto Test: Crear Producto OK")
    void crearProductoTestOK()
    {
        Mockito.when(repositorio.save(Mockito.any(Producto.class)))
                .thenReturn(Mono.just(getMockProducto()));

        Mockito.doNothing().when(eventBus).publishMovement(Mockito.any(MovimientosDTO.class));

        ProductoDTO productoDTO = getMockProductoDTO();

        StepVerifier.create(crearProductoUseCase.apply(productoDTO))
                .expectNextMatches(result -> result.getId().equals("id"))
                .verifyComplete();
    }

    @Test
    @DisplayName("UseCase Producto Test: Crear Producto FALSE")
    void crearProductoTestFalse()
    {
        Mockito.when(repositorio.save(Mockito.any(Producto.class)))
                .thenReturn(Mono.error(new RuntimeException("[Producto] Error al guardar el producto.")));

        Mockito.doNothing().when(eventBus).publishError(Mockito.anyString());

        ProductoDTO productoDTO = getMockProductoDTO();

        StepVerifier.create(crearProductoUseCase.apply(productoDTO))
                .expectErrorMatches(throwable -> throwable.getMessage().equals("[Producto] Error al guardar el producto."))
                .verify();
    }

}
