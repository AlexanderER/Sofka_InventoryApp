package epa.InventoryApp.routes;

import epa.InventoryApp.drivenAdapters.config.RabbitConfig;
import epa.InventoryApp.models.Producto;
import epa.InventoryApp.models.dto.ProductoDTO;
import epa.InventoryApp.usecase.producto.CrearProductoUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.Sender;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.StatusResultMatchersExtensionsKt.isEqualTo;

@ExtendWith(MockitoExtension.class)
public class CrearProductorRouterTest
{
    private WebTestClient webTestClient;

    @Mock
    private CrearProductoUseCase usecase;

    @InjectMocks
    ProductoRouter router;

    @BeforeEach
    void setUp()
    {
        webTestClient = WebTestClient
                            .bindToRouterFunction(router.crearProductoRoute(usecase))
                            .build();
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
    @DisplayName("Router Producto Test: Crear Producto OK")
    void crearProductoTestOK()
    {
        ProductoDTO resultadoEsperado = getMockProductoDTO();

        when(usecase.apply(any(ProductoDTO.class))).
                thenReturn(Mono.just(resultadoEsperado));

        webTestClient.post().uri("/productos/crear")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(getMockProductoDTO()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProductoDTO.class)
                .isEqualTo(resultadoEsperado);
    }

    @Test
    @DisplayName("Router Producto Test: Crear Producto False")
    void crearProductoTestFalse()
    {
        when(usecase.apply(any(ProductoDTO.class)))
                .thenReturn(Mono.error(new RuntimeException("[Producto] Error al guardar el producto.")));

        webTestClient.post().uri("/productos/crear")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(getMockProductoDTO()))
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(String.class)
                .isEqualTo("[Producto] Error al guardar el producto.");
    }
}
