package com.clic.springkafka.producer;

import java.util.Map;

/**
 * kafka生产者测试（消费者使用spring启动监听，自动执行onMessage方法）
 */
public class KafkaProducerTester {
    public static void main(String[] args) {
        KafkaProducerServer kafkaProducerServer = new KafkaProducerServer();
        String topic = "test";
        String value = "chinalife";
        String ifPartition = "0";
        Integer partitionNum = 3;
        String role = "test";// 用来生成key
        Map<String,Object> res = kafkaProducerServer.sndMesForTemplate
                (topic, value, ifPartition, partitionNum, role);

        System.out.println("测试结果如下：===============");
        String message = (String)res.get("message");
        String code = (String)res.get("code");

        System.out.println("code:"+code);
        System.out.println("message:"+message);
    }
}
