package epa.InventoryApp.drivenAdapters.bus;
import com.google.gson.Gson;
import epa.InventoryApp.drivenAdapters.config.RabbitConfig;
import epa.InventoryApp.models.dto.MovimientosDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.OutboundMessage;
import reactor.rabbitmq.Sender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class RabbitMqPublisher
{
    private static final Logger log = LoggerFactory.getLogger(RabbitMqPublisher.class);

    @Autowired
    private Sender sender;

    @Autowired
    private Gson gson;

    public void publishMovement(MovimientosDTO object)
    {
        String json = gson.toJson(object);

        log.info("{}Registrando Movimiento: " + json + "{}", "\u001B[32m", "\u001B[0m");

        sender
                .send(Mono.just(new OutboundMessage(RabbitConfig.EXCHANGE_NAME_MOVEMENTS,
                                                    RabbitConfig.ROUTING_KEY_NAME_MOVEMENTS,
                                                    json.getBytes()))
                     )
                .subscribe();
    }

    public void publishError(String object)
    {
        //String json = gson.toJson(object);

        log.info("{}Registrando Error: " + object + "{}", "\u001B[32m", "\u001B[0m");

        sender
                .send(Mono.just(new OutboundMessage(RabbitConfig.EXCHANGE_NAME_ERRORS,
                        RabbitConfig.ROUTING_KEY_NAME_ERRORS,
                        object.getBytes()))
                )
                .subscribe();
    }
}
