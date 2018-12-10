package com.clic.springkafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

/**
 * 单线程手动提交
 */
public class OneThreadHand {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("bootstrap.servers", "10.211.55.10:9092");
        props.put("group.id", "manualOffsetControlTest");
        //手动提交
        props.put("enable.auto.commit", "false");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList("test"));
        //每次处理200条消息后才提交
        final int minBatchSize = 200;
        //用于保存消息的list
        ArrayList<ConsumerRecord<String, String>> buffer = new ArrayList<>();
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records) {
                buffer.add(record);
            }
            //如果读取到的消息满了200条, 就进行处理
            if (buffer.size() >= minBatchSize) {
                //doSomething(buffer);
                //处理完之后进行提交
                consumer.commitAsync();
                //清除list, 继续接收
                buffer.clear();
            }
        }

    }
}
