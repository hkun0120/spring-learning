package com.clic.springkafka.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class ProducerWithoutSpring {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("bootstrap.servers", "10.211.55.10:9092");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        Producer<String, String> producer = new KafkaProducer(props);
        for (int i = 0; i < 100; i++){
            System.out.println("producer sends message: " + i);
            producer.send(new ProducerRecord<String, String>("5partTopic", i + "", i + ""));
        }
        producer.close();

    }
}
