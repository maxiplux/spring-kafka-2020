package blog.juanfrancisco.springkafka2020.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class SimpleListener {


    /*@KafkaListener(topics ="${kafka.topic}", groupId ="consumer")
    public void listen(String message) {
        log.warn("Received Messasge in group foo:"+message);
    }*/

    @KafkaListener(topics ="${kafka.topic}", groupId ="consumer",id ="autoStartup", autoStartup ="true")
    public void listen(ConsumerRecord<Integer,String> message) {
        Map<String, String> values = new HashMap<String, String>();

        StrSubstitutor sub = new StrSubstitutor(values, "%(", ")");



            values.put("Offset", ""+message.offset());
            values.put("Key", message.key().toString());
            values.put("Partition", message.partition()+"");
            values.put("Value", message.value());

            log.info(sub.replace("Offset {%(Offset)} Partition= {%(Partition)}  Key = {%(Key)} Value = {%(Value)}"));

    }

}
