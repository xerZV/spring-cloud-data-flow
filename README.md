## Stream Processing with Apache Kafka
Develop three Spring Boot applications that use Spring Cloud Stream's support for Apache Kafka and deploy them on your local machine. 

### Development
Create three Spring Cloud Stream applications that communicate using Kafka.

The scenario is a cell phone company creating bills for its customers. 
Each call made by a user has a duration and an amount of data used during the call. 
As part of the process to generate a bill, the raw call data needs to be converted to a cost for the duration of the call and a cost for the amount of data used.

The call is modeled by using the `UsageDetail` class, which contains the `duration` of the call and the amount of `data` used during the call. 
The bill is modeled by using the `UsageCostDetail` class, which contains the cost of the call (`costCall`) and the cost of the data (`costData`). 
Each class contains an ID (`userId`) to identify the person making the call.

The three streaming applications are as follows:

 - The Source application (named `UsageDetailSender`) generates the user's call `duration` and amount of `data` used per `userId` and sends a message containing the `UsageDetail` object as JSON.

 - The Processor application (named `UsageCostProcessor`) consumes the `UsageDetail` and computes the `cost` of the call and the cost of the data per `userId`. It sends the `UsageCostDetail` object as JSON.

 - The Sink application (named `UsageCostLogger`) consumes the `UsageCostDetail` object and logs the cost of the call and the cost of the data.
 
 
#### UsageDetailSender Source

##### `UsageDetail` class contains
 
 - `userId` 
 - `data` 
 - `duration` 
 
##### `UsageDetailSender` class contains logic to send new a usageDetail instance per 2 sec
 
  - The `@EnableBinding` annotation indicates that you want to bind your application to the messaging middleware. 
  The annotation takes one or more interfaces as a parameter — in this case, the Source interface that defines an output channel named `output`. 
  In the case of Kafka, messages sent to the `output` channel are, in turn, sent the Kafka topic.
  - The `@EnableScheduling` annotation indicates that you want to enable Spring's scheduling capabilities, 
  which invoke methods annotated with `@Scheduled` with the specified `fixedDelay` of 2 second.
  - The `sendEvents` method constructs a `UsageDetail` object and then sends it to the the `output` channel by accessing the `Source` object's `output().send()` method.
  
###### Config

It is a `producer` application so we need to set the `output` binding destination (Kafka topic in this scenario) where the producer publishes the data. So we add 
<pre>
spring.cloud.stream.bindings.output.destination=usage-detail</pre>
The `spring.cloud.stream.bindings.output.destination` property binds the `UsageDetailSender` object's output to the `usage-detail` Kafka topic.

###### Building

Now we can build the Usage Detail Sender application. In the `usage-detail-sender` directory, use the following command to build the project using maven:
<pre>./mvnw clean package</pre>

###### Testing

And we can test it - `UsageDetailSenderKafkaApplicationTests`
Spring Cloud Stream provides the `spring-cloud-stream-test-support` dependency to test the Spring Cloud Stream application. 
Instead of the `Kafka` binder, the tests use the `Test` binder to trace and test your application's outbound and inbound messages. 
The `Test` binder uses a utility class called `MessageCollector`, which stores the messages in-memory.
When using the `spring-cloud-stream-test-support` dependency, your application's `output` and `input` are bound to the `Test` binder.
 - The `contextLoads` test case verifies the application starts successfully.
 - The `testUsageDetailSender` test case uses the Test binder's `MessageCollector` to collect the messages sent by the `UsageDetailSender`.
 
#### UsageCostProcessor Processor

Again we use `UsageDetail`
##### `UsageDetail` class contains
 
 - `userId` 
 - `data` 
 - `duration` 
 
But we need another class to represents the cost details - `UsageCostDetail`
##### `UsageCostDetail` class contains
 - `userId` 
 - `callCost` 
 - `dataCost`
 
##### `UsageCostProcessor` class receives the `UsageDetail` (which is produced by `UsageDetailSender`), computes the call and data cost and sends a `UsageCostDetail` message.

 - In the preceding application, the `@EnableBinding` annotation indicates that you want to bind your application to the messaging middleware. 
 The annotation takes one or more interfaces as a parameter — in this case, the `Processor` that defines and input and output channels.
   
 - The `@StreamListener` annotation binds the application's `input` channel to the `processUsageCost` method by converting the incoming JSON into `UsageDetail` object. 
 We configure the `Kafka` topic that is bound to the `input` channel later.
   
 - The `@SendTo` annotation sends the `processUsageCost` method's `output` to the application's `output` channel, 
 which is, in turn, sent to the a `Kafka` topic that we configure later.
 
###### Config

When configuring the `consumer` application, we need to set the `input` binding destination (a Kafka topic).

Since the `UsageCostProcessor` application is also a `producer` application, 
we need to set the `output` binding destination (a Kafka topic) where the `producer` publishes the data.
So in the config file:
<pre>spring.cloud.stream.bindings.input.destination=usage-detail
     spring.cloud.stream.bindings.output.destination=usage-cost</pre>
     
 - The `spring.cloud.stream.bindings.input.destination` property binds the `UsageCostProcessor` object's `input` to the `usage-detail` Kafka topic.
 - The `spring.cloud.stream.bindings.output.destination` property binds the `UsageCostProcessor` object's `output` to the `usage-cost` Kafka topic.
     
###### Building

Now we can build the Usage Cost Processor application. In the `usage-cost-processor` directory, use the following command to build the project using maven:
<pre>./mvnw clean package</pre>

###### Testing

And we can test it - `UsageCostProcessorKafkaApplicationTests`

 - The `contextLoads` test case verifies the application starts successfully.
 - The `testUsageCostProcessor` test case uses the `Test` binder's `MessageCollector` to collect the messages from the `UsageCostProcessor` object's `output`.
