package epa.InventoryApp.drivenAdapters.config;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.rabbitmq.*;


import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

@Configuration
public class RabbitConfig
{
    //------------------------------------------------------------------------------------ (Variables)
    // EXCHANGES
    public static final String EXCHANGE_NAME_MOVEMENTS = "movements-exchange";


    // ROUTING KEY
    public static final String ROUTING_KEY_NAME_MOVEMENTS = "movements.routing.key";


    // QUEUES
    public static final String QUEUE_NAME_MOVEMENTS = "movements-queue";


    // URL
    //public static final String URI_NAME = "amqps://dzvtwtas:CIc5hiR1KTAW8IfQSRpSa8MDt5-xHSqg@woodpecker.rmq.cloudamqp.com/dzvtwtas";

    @Value("${rabbitmq.uri}")
    private String URI_NAME;

    @Bean
    public AmqpAdmin amqpAdmin()
    {
        //  Defino Conexión
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(URI.create(URI_NAME));
        var amqpAdmin =  new RabbitAdmin(connectionFactory);

        // Defino los exchange
        var exchangeMovements = new TopicExchange(EXCHANGE_NAME_MOVEMENTS);


        // Defino las Colas
        var queueMovements = new Queue(QUEUE_NAME_MOVEMENTS, true, false, false);


        // Inicializo en la conexion
        amqpAdmin.declareExchange(exchangeMovements);

        amqpAdmin.declareQueue(queueMovements);

        amqpAdmin.declareBinding(BindingBuilder.bind(queueMovements).to(exchangeMovements).with(ROUTING_KEY_NAME_MOVEMENTS));

        return amqpAdmin;
    }

    @Bean
    public ConnectionFactory connectionFactory() throws NoSuchAlgorithmException, KeyManagementException, URISyntaxException
    {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.useNio();
        connectionFactory.setUri(URI_NAME);
        return connectionFactory;
    }

    @Bean
    public Mono<Connection> connectionMono(@Value("spring.application.name") String name, ConnectionFactory connectionFactory)
    {
        return Mono.fromCallable(() -> connectionFactory.newConnection(name)).cache();
    }

    @Bean
    public SenderOptions senderOptions(Mono<Connection> connectionMono) {
        return new SenderOptions()
                .connectionMono(connectionMono)
                .resourceManagementScheduler(Schedulers.boundedElastic());
    }

    @Bean
    public Sender sender(SenderOptions senderOptions) {
        return RabbitFlux.createSender(senderOptions);
    }


    @Bean
    public ReceiverOptions receiverOptions(Mono<Connection> connectionMono) {
        return new ReceiverOptions()
                .connectionMono(connectionMono);
    }

    @Bean
    public Receiver receiver(ReceiverOptions receiverOptions) {
        return RabbitFlux.createReceiver(receiverOptions);
    }
}
