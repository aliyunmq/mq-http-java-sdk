# MQ JAVA HTTP SDK

Alyun MQ Documents: http://www.aliyun.com/product/ons

Aliyun MQ Console: https://ons.console.aliyun.com

## Use

### Add Maven Dependency
```
<dependency>
    <groupId>com.aliyun.mq</groupId>
    <artifactId>mq-http-sdk</artifactId>
    <version>1.0.3</version>
</dependency>
```

Or With-dependencies
```
<dependency>
    <groupId>com.aliyun.mq</groupId>
    <artifactId>mq-http-sdk</artifactId>
    <version>1.0.3</version>
    <classifier>jar-with-dependencies</classifier>
</dependency>
```

## Note
1. Http consumer only support timer msg (less than 3 days), no matter the msg is produced from http or tcp protocol.
2. Order is only supported at special server cluster.

### Sample (github)

[Publish Message](https://github.com/aliyunmq/mq-http-samples/blob/master/java/src/main/java/Producer.java)

[Consume Message](https://github.com/aliyunmq/mq-http-samples/blob/master/java/src/main/java/Consumer.java)

[Transaction Message](https://github.com/aliyunmq/mq-http-samples/blob/master/java/src/main/java/TransProducer.java)

[Publish Order Message](https://github.com/aliyunmq/mq-http-samples/blob/master/java/src/main/java/OrderProducer.java)

[Consume Order Message](https://github.com/aliyunmq/mq-http-samples/blob/master/java/src/main/java/OrderConsumer.java)

### Sample (code.aliyun.com)

[Publish Message](https://code.aliyun.com/aliware_rocketmq/mq-http-samples/blob/master/java/src/main/java/Producer.java)

[Consume Message](https://code.aliyun.com/aliware_rocketmq/mq-http-samples/blob/master/java/src/main/java/Consumer.java)

[Transaction Message](https://code.aliyun.com/aliware_rocketmq/mq-http-samples/blob/master/java/src/main/java/TransProducer.java)

[Publish Order Message](https://code.aliyun.com/aliware_rocketmq/mq-http-samples/blob/master/java/src/main/java/OrderProducer.java)

[Consume Order Message](https://code.aliyun.com/aliware_rocketmq/mq-http-samples/blob/master/java/src/main/java/OrderConsumer.java)