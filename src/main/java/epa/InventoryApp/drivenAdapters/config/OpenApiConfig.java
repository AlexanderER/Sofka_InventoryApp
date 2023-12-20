package epa.InventoryApp.drivenAdapters.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig
{
    private String devUrl = "http://localhost:8083";

    @Bean
    public OpenAPI myOpenAPI()
    {
        Server devServer = new Server();
        devServer.setUrl(devUrl);
        devServer.setDescription("URL del Servidor de Desarrollo");

        Contact contact = new Contact();
        contact.setEmail("info@epa.com");
        contact.setName("EPA");
        contact.setUrl("https://cr.epaenlinea.com/contact");

        Info info = new Info()
                .title("Documentación API´s Reto EPA")
                .version("1.0")
                .contact(contact)
                .description("Los metodos expuestos brindan la funcionalidad para el Modulo de Inventario.");

        return new OpenAPI().info(info).servers(List.of(devServer));
    }
}
