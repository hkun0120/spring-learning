###工程说明
这个工程是基于契约系统与打印系统交互时候产生的问题，契约系统
调用打印系统的查询服务用来更新本系统中的单据状态，平均调用
时间为0.3s，轮询9000条数据预计在45分钟左右，时效性太差。
所以打算采用kafka作为消息中间件，让打印系统作为生产者，
契约系统作为消费者。
###需要注意的问题
* offset的记录和存储方式，在数据库还是在zk
* 不同分区并发消费，线程池或者别的？
* 消费端的幂等如何实现？
###实现细节
* 由于从打印系统过来的数据是有时间先后顺序的，单据在不同时间
的状态不同，所以如果生产者将数据放入不同的partition上面
会不会造成消费者消费到的数据不按照时间顺序排列了？
这是需要验证的一个点。
* 另外，不同分区的并发消费用线程池的实现也是此案例的一个
重要部分。消费的时候也会造成时间顺序混乱吗？还是消费后
再按照时间顺序进行排列？
* 幂等，保证消费过的数据不会再被消费，是这个意思么？
暂时没懂幂等是什么意思。
* offset下标自动提交其实在很多场景都不适用，因为自动提交是在kafka拉取到数据之后就直接提交，这样很容易丢失数据，尤其是在需要事物控制的时候。
很多情况下我们需要从kafka成功拉取数据之后，对数据进行相应的处理之后再进行提交。如拉取数据之后进行写入mysql这种 ， 所以这时我们就需要进行手动提交kafka的offset下标。
### 验证结果
* 如果放在有多个partition的topic上面，必然造成消费到的数据不会严格按照时间顺序排列
* 只有一个分区，并发消费的消费者如果不在一个consumer group则不影响，如果在一个group中则没有必要吧


###相关命令
* 创建topic
`bin/kafka-topics.sh --zookeeper localhost:2181 --create --topic test --replication-factor 3 --partitions 3`
* 验证消息生产成功
`bin/kafka-run-class.sh kafka.tools.GetOffsetShell --broker-list localhost:9092,localhost:9093,localhost:9094 --topic test --time -1`
* 创建console consumer group
`bin/kafka-console-consumer.sh --bootstrap-server localhost:9092,localhost:9093,localhost:9094 --topic test --from-beginning --new-consumer
`
* 获取该consumer group的group id
`bin/kafka-consumer-groups.sh --bootstrap-server localhost:9092,localhost:9093,localhost:9094 --list --new-consumer`
* 查询__consumer_offsets topic所有内容
`bin/kafka-console-consumer.sh --topic __consumer_offsets --zookeeper localhost:2181 --formatter "kafka.coordinator.GroupMetadataManager\$OffsetsMessageFormatter" --consumer.config config/consumer.properties --from-beginning`
* 查询消费到的offset
`如果是根据kafka默认的api来消费，即【org.apache.kafka.clients.consumer.KafkaConsumer】，我们会配置参数【bootstrap.servers】来消费。而其消费者的offset会更新到一个kafka自带的topic【__consumer_offsets】下面，查看当前group的消费进度，则要依靠kafka自带的工具【kafka-consumer-offset-checker】
消费组的id为程序中指定的group.id  
bin/kafka-consumer-offset-checker.sh --zookeeper master:2181 --topic 5partTopic --group test`
* 查看所有当前的消费组
`bin/kafka-consumer-groups.sh --bootstrap-server master:9092 --list`
* 消费组位移重设(0.10.2.2版本中没有)
`bin/kafka-consumer-groups.sh --bootstrap-server localhost:9092 --group test-group --reset-offsets`
