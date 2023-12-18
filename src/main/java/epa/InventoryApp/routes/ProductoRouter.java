package epa.InventoryApp.routes;

import epa.InventoryApp.models.dto.ProductoDTO;
import epa.InventoryApp.usecase.producto.CrearProductoUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class ProductoRouter
{
    @Bean
    public RouterFunction<ServerResponse> crearProductoRoute(CrearProductoUseCase useCase)
    {
        return route(POST("/productos/crear")
                        .and(accept(MediaType.APPLICATION_JSON)),
                        request -> request.bodyToMono(ProductoDTO.class)
                                .flatMap(useCase::apply)
                                .flatMap(result -> ServerResponse.ok()
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .bodyValue(result))
                                .onErrorResume(throwable -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build())
                    );
    }

}
