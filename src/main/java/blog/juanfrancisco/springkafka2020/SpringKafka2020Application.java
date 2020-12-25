package blog.juanfrancisco.springkafka2020;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.core.KafkaProducerException;
import org.springframework.kafka.core.KafkaSendCallback;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.concurrent.ListenableFuture;



@SpringBootApplication
@Slf4j
public class SpringKafka2020Application implements CommandLineRunner {

    @Autowired
    @Qualifier("producer")
    private MeterRegistry meterRegistryProducer;


    @Autowired
    @Qualifier("consumer")
    private MeterRegistry meterRegistryConsumer;


    @Value("${kafka.topic}")
    private  String Kafkatopic;

    // this is only to test, I know that is a bad place to call it , but I am tired today ;)
    @Autowired
    private KafkaTemplate<Integer, String> kafkaTemplate;


    public static void main(String[] args) {
        SpringApplication.run(SpringKafka2020Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        for (Integer index = 0; index < 110 ; index++) {

           // this.kafkaTemplate.send(Kafkatopic,1001,"Juan francisco envio un mensaje"+index);

            ListenableFuture<SendResult<Integer, String>> future=kafkaTemplate.send(Kafkatopic,index,"Sample message ");

            future.addCallback(new KafkaSendCallback<Integer, String>() {
                @Override
                public void onSuccess(SendResult<Integer, String>result) {
                    log.info("#################### Message sent");
                }
                @Override
                public void onFailure(Throwable ex) {
                    log.error(">>>>>>>>>>>>>>>>>>>>>>>>>>> Error sending message ",ex);
                }
                @Override
                public void onFailure(KafkaProducerException ex) {
                    log.error("---------------------------Error sending message ",ex);
                }
            });

        }



            /*
        ListenableFuture<SendResult<Integer, String>> future=kafkaTemplate.send(Kafkatopic,"Sample message ");

        future.addCallback(new KafkaSendCallback<Integer, String>() {
            @Override
            public void onSuccess(SendResult<Integer, String>result) {
                log.info("#################### Message sent");
            }
            @Override
            public void onFailure(Throwable ex) {
                log.error(">>>>>>>>>>>>>>>>>>>>>>>>>>> Error sending message ",ex);
            }
            @Override
            public void onFailure(KafkaProducerException ex) {
                log.error("---------------------------Error sending message ",ex);
            }
        });
*/
        //log.warn("Enviado mensaje a ");
    }


    @Scheduled(fixedDelay = 2000, initialDelay = 100)
    public void messageProduccerCountMetric() {


        double count = meterRegistryProducer.get("kafka.producer.record.send.total").functionCounter().count();
        log.info("messageProduccerCountMetric -> Count ->" + count);
    }


    @Scheduled(fixedDelay = 3000, initialDelay = 500)
    public void messageConsumerCountMetric() {
        double count = meterRegistryConsumer.get("kafka.consumer.fetch.manager.records.consumed.total").functionCounter().count();
        log.info("messageConsumerCountMetric -> Count ->" + count);
    }

}
