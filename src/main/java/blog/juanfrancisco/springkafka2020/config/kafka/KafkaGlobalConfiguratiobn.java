package blog.juanfrancisco.springkafka2020.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaGlobalConfiguratiobn {


    @Value("${kafka.topic}")
    private String kafkaTopic;

    @Value("${kafka.build.replicas}")
    private Integer kafkaBuildReplicas;

    @Value("${kafka.build.partitions}")
    private Integer kafkaBuildPartitions ;

    @Bean
    public NewTopic defaultTopicConfiguration() {
        return TopicBuilder.name(kafkaTopic)
                .partitions(kafkaBuildPartitions)
                .replicas(kafkaBuildReplicas)
                .compact()
                .build();
    }


}
