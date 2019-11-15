package com.simitchiyski.spring.dataflow.usagecostloggerkafka;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.messaging.support.MessageBuilder;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UsageCostLoggerKafkaApplicationTests {

    @Autowired
    protected Sink sink;

    @Autowired
    protected UsageCostLogger usageCostLogger;

    @Test
    void contextLoads() {
        // verifies the application starts successfully.
    }

    @Test
    public void testUsageCostLogger() throws Exception {
		// verifies that the process method of UsageCostLogger is invoked by using Mockito.
		// To do this, the TestConfig static class overrides the existing UsageCostLogger bean
		// to create a Mock bean of UsageCostLogger. Since we are mocking the UsageCostLogger bean,
		// the TestConfig also explicitly annotates @EnableBinding and @EnableAutoConfiguration.

        ArgumentCaptor<UsageCostDetail> captor = ArgumentCaptor.forClass(UsageCostDetail.class);
        this.sink.input().send(MessageBuilder.withPayload("{\"userId\":\"user3\",\"callCost\":10.100000000000001,\"dataCost\":25.1}").build());
        verify(this.usageCostLogger).process(captor.capture());
    }

    @EnableAutoConfiguration
    @EnableBinding(Sink.class)
    static class TestConfig {

        // Override `UsageCostLogger` bean for spying.
        @Bean
        @Primary
        public UsageCostLogger usageCostLogger() {
            return spy(new UsageCostLogger());
        }
    }
}
