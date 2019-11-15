package com.simitchiyski.spring.dataflow.usagecostloggerkafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;

@EnableBinding(Sink.class)
public class UsageCostLogger {
    private static final Logger logger = LoggerFactory.getLogger(UsageCostLoggerKafkaApplication.class);

    @StreamListener(Sink.INPUT)
    public void process(UsageCostDetail usageCostDetail) {
        logger.info(usageCostDetail.toString());
    }
}
