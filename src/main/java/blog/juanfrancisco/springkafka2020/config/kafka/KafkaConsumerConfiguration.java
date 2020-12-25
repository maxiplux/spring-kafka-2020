package blog.juanfrancisco.springkafka2020.config.kafka;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.MicrometerConsumerListener;
import org.springframework.kafka.core.MicrometerProducerListener;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfiguration {

    @Value("${kafka.comsumer.concurencyFactor}")
    private  Integer kafkaComsumerConcurencyFactor;

    @Bean
    public Map<String, Object> consumerProps() {

        // Todo:Move to application.proertioes
        Map<String, Object>props=new HashMap<>();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "group");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,true);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG,"100");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG,"15000");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,IntegerDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class);

        return props;

    }

    @Bean
    public ConsumerFactory<Integer, String> consumerFactory() {

        ConsumerFactory<Integer, String>  consumerFactory=new DefaultKafkaConsumerFactory<>(consumerProps());

        consumerFactory.addListener(new MicrometerConsumerListener<Integer, String>(meterRegistry()));
        return  consumerFactory;
    }

    @Bean(name = "consumer")
    public MeterRegistry meterRegistry() {
        PrometheusMeterRegistry prometheusMeterRegistry=new PrometheusMeterRegistry (PrometheusConfig.DEFAULT);
        return prometheusMeterRegistry;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<Integer, String>    kafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<Integer, String>  listenerContainerFactory = new ConcurrentKafkaListenerContainerFactory<>();
        listenerContainerFactory.setConsumerFactory(consumerFactory());



        listenerContainerFactory.setConcurrency(kafkaComsumerConcurencyFactor);


        return listenerContainerFactory;
    }


}
