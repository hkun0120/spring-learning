package com.clic.springkafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Arrays;
import java.util.Properties;

/**
 * 此生产者用于向topic生产数据
 * 向只有一个partition的topic生产数据
 * 向有多个partition的topic生产数据
 * 确认topic中每个topic中的数据是否丢失，以及是否按照时间顺序排列
 */
public class KafkaConsumerWithoutSpring {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("bootstrap.servers", "master:9092");
        props.put("group.id", "test");
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("enable.auto.commit", "false");
        props.put("max.poll.records", 10);
//        consume1PartitionTopic(props);
        consume5PartitionTopic(props);
    }

    public static void consume1PartitionTopic(Properties properties){
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Arrays.asList("test1"));
        while (true) {
            /*
             * poll() 方法返回一个记录列表。
             * 每条记录都包含了记录所属主题的信息、记录所在分区的信息、记录在分区里的偏移量，以及记录的键值对。
             * 我们一般会遍历这个列表，逐条处理这些记录。
             * 传给poll() 方法的参数是一个超时时间，用于控制 poll() 方法的阻塞时间（在消费者的缓冲区里没有可用数据时会发生阻塞）。
             * 如果该参数被设为 0，poll() 会立即返回，否则它会在指定的毫秒数内一直等待 broker 返回数据。
             * 而在经过了指定的时间后，即使还是没有获取到数据，poll()也会返回结果。
             */
            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records)
                System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
        }
    }

    public static void consume5PartitionTopic(Properties properties){
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Arrays.asList("5partTopic"));
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(1000);
            System.out.println(System.currentTimeMillis()+ " poll到的用于消费的记录数为："+records.count());
            for (ConsumerRecord<String, String> record : records)
                System.out.printf("partitionId=%d,offset = %d, key = %s, value = %s%n",record.partition(), record.offset(), record.key(), record.value());
                consumer.commitAsync();
        }
    }
}
