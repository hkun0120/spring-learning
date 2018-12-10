package com.clic.springkafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Arrays;
import java.util.Properties;

/**
 *单线程自动提交
 */
public class OneThreadAuto {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("bootstrap.servers", "10.211.55.10:9092");
        props.put("group.id", "autoCommitGroup");
        //自动提交
        props.put("enable.auto.commit", "true");
        //自动提交时间间隔
        props.put("auto.commit.interval.ms", "1000");
        //key和value的序列化类
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList("test"));
        while (true) {
            //而是在poll时才会进行offset提交
            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records)
                System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
        }

    }
}
