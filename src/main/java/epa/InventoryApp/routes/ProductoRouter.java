package epa.InventoryApp.routes;

import epa.InventoryApp.models.dto.AgregarInventarioDTO;
import epa.InventoryApp.models.dto.ProductoDTO;
import epa.InventoryApp.models.dto.VentaInventarioDTO;
import epa.InventoryApp.usecase.movimientosInventario.AgregarInventarioPorLoteUseCase;
import epa.InventoryApp.usecase.movimientosInventario.AgregarInventarioPorUnidadUseCase;
import epa.InventoryApp.usecase.movimientosInventario.VentaAlDetalleUseCase;
import epa.InventoryApp.usecase.movimientosInventario.VentaAlPorMayorUseCase;
import epa.InventoryApp.usecase.producto.CrearProductoUseCase;
import epa.InventoryApp.usecase.producto.ListarProductosPaginadoUseCaseImp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class ProductoRouter
{
    @Bean
    @RouterOperation
    (
            path = "/productos/crear",
            produces = MediaType.APPLICATION_JSON_VALUE,
            beanClass = CrearProductoUseCase.class,
            beanMethod = "apply",
            method = RequestMethod.POST,
            operation = @Operation(
                    operationId = "crearProducto",
                    tags = "Endpoints Productos",
                    responses = {
                            @ApiResponse(
                                    responseCode = "200",
                                    description = "Producto creado exitosamente",
                                    content = @Content(schema = @Schema(implementation = ProductoDTO.class))
                            ),
                            @ApiResponse(
                                    responseCode = "400",
                                    description = "Detalles del producto inválidos"
                            )
                    },
                    requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = ProductoDTO.class)))
            )
    )
    public RouterFunction<ServerResponse> crearProductoRoute(CrearProductoUseCase useCase)
    {
        return route(POST("/productos/crear")
                        .and(accept(MediaType.APPLICATION_JSON)),
                        request -> request.bodyToMono(ProductoDTO.class)
                                .flatMap(useCase::apply)
                                .flatMap(result -> ServerResponse.ok()
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .bodyValue(result))
                                .onErrorResume(throwable -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue(throwable.getMessage()))
                    );
    }

    @Bean
    @RouterOperation
    (
        path = "/productos/listar/{pagina}/{cantPorPagina}",
        beanClass = ListarProductosPaginadoUseCaseImp.class,
        beanMethod = "apply",
        method = RequestMethod.GET,
        operation = @Operation(
                operationId = "listarProductoPaginado",
                tags = "Endpoints Productos",
                parameters = {
                        @Parameter(name = "pagina", in = ParameterIn.PATH, required = true, description = "Número de página"),
                        @Parameter(name = "cantPorPagina", in = ParameterIn.PATH, required = true, description = "Cantidad de elementos por página")
                },
                responses = {
                        @ApiResponse(
                                responseCode = "200",
                                description = "Productos paginados devueltos exitosamente",
                                content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductoDTO.class)))
                        )
                }
        )
    )
    public RouterFunction<ServerResponse> listarProductoPaginadoRoute(ListarProductosPaginadoUseCaseImp useCase)
    {
        return route(GET("/productos/listar/{pagina}/{cantPorPagina}"),
                     request -> {
                                    Integer pagina       = Integer.valueOf(request.pathVariable("pagina"));
                                    Integer cantPorPagina = Integer.valueOf(request.pathVariable("cantPorPagina"));

                                    return ServerResponse.ok()
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .body(BodyInserters.fromPublisher(useCase.apply(pagina, cantPorPagina), ProductoDTO.class))
                                            .onErrorResume(throwable -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue(throwable.getMessage()));
                                }
                    );
    }


    @Bean
    @RouterOperation(
            path = "/productos/agregarporunidad",
            beanClass = AgregarInventarioPorUnidadUseCase.class,
            beanMethod = "apply",
            operation = @Operation(
                    operationId = "agregarInventarioPorUnidad",
                    tags = "Endpoints Suma Inventario",
                    requestBody = @RequestBody(description = "DTO para agregar inventario por unidad", content = @Content(schema = @Schema(implementation = AgregarInventarioDTO.class))),
                    responses = {
                            @ApiResponse(
                                    responseCode = "200",
                                    description = "Inventario agregado exitosamente",
                                    content = @Content(schema = @Schema(implementation = ProductoDTO.class))
                            ),
                            @ApiResponse(
                                    responseCode = "400",
                                    description = "No se encontró el producto",
                                    content = @Content()
                            )
                    }
            )
    )
    public RouterFunction<ServerResponse> AgregarInventarioPorUnidadRoute(AgregarInventarioPorUnidadUseCase useCase)
    {
        return route(POST("/productos/agregarporunidad")
                        .and(accept(MediaType.APPLICATION_JSON)),
                request -> request.bodyToMono(AgregarInventarioDTO.class)
                        .flatMap(useCase::apply)
                        .flatMap(result -> ServerResponse.ok()
                                                         .contentType(MediaType.APPLICATION_JSON)
                                                         .bodyValue(result))
                        .onErrorResume(throwable -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue(throwable.getMessage()))
        );
    }


    @Bean
    @RouterOperation(
            path = "/productos/agregarporlote",
            beanClass = AgregarInventarioPorLoteUseCase.class,
            beanMethod = "apply",
            operation = @Operation(
                    operationId = "agregarInventarioPorLote",
                    tags = "Endpoints Suma Inventario",
                    requestBody = @RequestBody(description = "Lista de DTO para agregar inventario por lote", content = @Content(array = @ArraySchema(schema = @Schema(implementation = AgregarInventarioDTO.class)))),
                    responses = {
                            @ApiResponse(
                                    responseCode = "200",
                                    description = "Inventario agregado exitosamente",
                                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductoDTO.class)))
                            ),
                            @ApiResponse(
                                    responseCode = "400",
                                    description = "No se encontró el producto",
                                    content = @Content()
                            )
                    }
            )
    )
    public RouterFunction<ServerResponse> AgregarInventarioPorLoteRoute(AgregarInventarioPorLoteUseCase useCase)
    {
        return route(POST("/productos/agregarporlote")
                        .and(accept(MediaType.APPLICATION_JSON)),
                request -> request.bodyToFlux(AgregarInventarioDTO.class)
                        .collectList()
                        .flatMapMany(useCase::apply)
                        .collectList()
                        .flatMap(result -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(result))
                        .onErrorResume(throwable -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue(throwable.getMessage()))
        );
    }


    @Bean
    @RouterOperation(
            path = "/productos/ventadetalle",
            beanClass = VentaAlDetalleUseCase.class,
            beanMethod = "apply",
            operation = @Operation(
                    operationId = "ventaAlDetalle",
                    tags = "Endpoints Resta Inventario",
                    requestBody = @RequestBody(description = "Lista de DTO para venta al detalle", content = @Content(array = @ArraySchema(schema = @Schema(implementation = VentaInventarioDTO.class)))),
                    responses = {
                            @ApiResponse(
                                    responseCode = "200",
                                    description = "Venta al detalle procesada exitosamente",
                                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductoDTO.class)))
                            ),
                            @ApiResponse(
                                    responseCode = "400",
                                    description = "No se encontró el producto",
                                    content = @Content()
                            )
                    }
            )
    )
    public RouterFunction<ServerResponse> ventaAlDetalleRoute(VentaAlDetalleUseCase useCase) {
        return route(POST("/productos/ventadetalle")
                        .and(accept(MediaType.APPLICATION_JSON)),
                request -> request.bodyToFlux(VentaInventarioDTO.class)
                        .collectList()
                        .flatMapMany(useCase::apply)
                        .collectList()
                        .flatMap(result -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(result))
                        .onErrorResume(throwable -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue(throwable.getMessage()))
        );
    }

    @Bean
    @RouterOperation(
            path = "/productos/ventapormayor",
            beanClass = VentaAlPorMayorUseCase.class,
            beanMethod = "apply",
            operation = @Operation(
                    operationId = "ventaAlPorMayor",
                    tags = "Endpoints Resta Inventario",
                    requestBody = @RequestBody(description = "Lista de DTO para venta al por mayor", content = @Content(array = @ArraySchema(schema = @Schema(implementation = VentaInventarioDTO.class)))),
                    responses = {
                            @ApiResponse(
                                    responseCode = "200",
                                    description = "Venta al por mayor procesada exitosamente",
                                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductoDTO.class)))
                            ),
                            @ApiResponse(
                                    responseCode = "400",
                                    description = "No se encontró el producto",
                                    content = @Content()
                            )
                    }
            )
    )
    public RouterFunction<ServerResponse> ventaAlPorMayorRoute(VentaAlPorMayorUseCase useCase) {
        return route(POST("/productos/ventapormayor")
                        .and(accept(MediaType.APPLICATION_JSON)),
                request -> request.bodyToFlux(VentaInventarioDTO.class)
                        .collectList()
                        .flatMapMany(useCase::apply)
                        .collectList()
                        .flatMap(result -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(result))
                        .onErrorResume(throwable -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue(throwable.getMessage()))
        );
    }

}
