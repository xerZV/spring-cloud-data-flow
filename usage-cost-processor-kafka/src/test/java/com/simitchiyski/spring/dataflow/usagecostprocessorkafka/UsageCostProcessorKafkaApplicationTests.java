package com.simitchiyski.spring.dataflow.usagecostprocessorkafka;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class UsageCostProcessorKafkaApplicationTests {
    @Autowired
    private Processor processor;

    @Autowired
    private MessageCollector messageCollector;

    @Test
    public void contextLoads() {
    }

    @Test
    public void testUsageCostProcessor() throws Exception {
        this.processor.input().send(MessageBuilder.withPayload("{\"userId\":\"user3\",\"duration\":101,\"data\":502}").build());

        Message message = this.messageCollector.forChannel(this.processor.output()).poll(1, TimeUnit.SECONDS);

        assertTrue(message.getPayload().toString().equals("{\"userId\":\"user3\",\"callCost\":10.100000000000001,\"dataCost\":25.1}"));
    }

}
