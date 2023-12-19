package epa.InventoryApp;

import com.google.gson.*;
import epa.InventoryApp.models.dto.MovimientosDTO;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SpringBootApplication
public class InventoryAppApplication {

	@Bean
	public Gson createGson(){
		return new GsonBuilder()
				.registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) ->
						new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
				.registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, typeOfT, context) ->
						LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME))
				.create();
	}

	public static void main(String[] args)
	{
		SpringApplication.run(InventoryAppApplication.class, args);
	}

}
