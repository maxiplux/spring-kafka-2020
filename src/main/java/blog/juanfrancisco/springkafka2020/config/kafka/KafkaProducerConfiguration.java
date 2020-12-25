package blog.juanfrancisco.springkafka2020.config.kafka;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.*;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableScheduling

public class KafkaProducerConfiguration {

    @Value("${kafka.topic}")
    private  String Kafkatopic;

    @Value( "${kafka.cluster}")
    private  String kafkaClusterServers;


    private Map<String, Object> producerProps() { Map<String, Object> props=new HashMap<>();
        // Todo:Move to application.proertioes
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaClusterServers);
        props.put(ProducerConfig.RETRIES_CONFIG, 0);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,IntegerSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,StringSerializer.class);
        return props;
    }

    @Bean(name = "producer")
    public MeterRegistry meterRegistry() {
        PrometheusMeterRegistry prometheusMeterRegistry=new PrometheusMeterRegistry (PrometheusConfig.DEFAULT);
        return prometheusMeterRegistry;
    }

    @Bean
    public KafkaTemplate<Integer, String> createTemplate() {
        Map<String, Object>senderProps= producerProps();

        ProducerFactory<Integer, String> producerFactory= new DefaultKafkaProducerFactory<Integer, String>(senderProps);

        producerFactory.addListener(new MicrometerProducerListener<Integer,String>(meterRegistry()));

        KafkaTemplate<Integer, String> template=new KafkaTemplate<>(producerFactory);
        return template;
    }


}
