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
 
#### UsageCostLogger Sink

Again we use `UsageCostDetail`
##### `UsageCostDetail` class contains
 - `userId` 
 - `callCost` 
 - `dataCost`
 
##### `UsageCostLogger` class receives the `UsageCostDetail` (which is produced by `UsageCostProcessor`) and just loggs the received info.

 - In the preceding application, the `@EnableBinding` annotation indicates that you want to bind your application to the messaging middleware. 
 The annotation takes one or more interfaces as a parameter — in this case, the `Sink` interface that defines the input channel.
   
 - The `@StreamListener` annotation binds the application's `input` channel to the `process` method by converting the incoming JSON into `UsageCostDetail` object. 
 We configure the `Kafka` topic that is bound to the `input` channel later.
 
###### Config

When configuring the `consumer` application, we need to set the `input` binding destination (a Kafka topic).

So in the config file:
<pre>spring.cloud.stream.bindings.input.destination=usage-cost</pre>
     
 - The `spring.cloud.stream.bindings.input.destination` property binds the `UsageCostLogger` object's `input` to the `usage-cost` Kafka topic.
     
###### Building

Now we can build the Usage Cost Logger application. In the `usage-cost-logger` directory, use the following command to build the project using maven:
<pre>./mvnw clean package</pre>

###### Testing

And we can test it - `UsageCostLoggerKafkaApplicationTests`

 - The `contextLoads` test case verifies the application starts successfully.
 - The `testUsageCostLogger` test case verifies that the `process` method of `UsageCostLogger` is invoked by using `Mockito`. 
 To do this, the `TestConfig` static class overrides the existing `UsageCostLogger` bean to create a Mock bean of `UsageCostLogger`. 
 Since we are mocking the `UsageCostLogger` bean, the `TestConfig` also explicitly annotates `@EnableBinding` and `@EnableAutoConfiguration`.
 
 
### Deployment

We deploy the applications we created earlier to the local machine.
When we deploy these three applications (`UsageDetailSender`, `UsageCostProcessor` and `UsageCostLogger`), the flow of message is as follows:
<pre>UsageDetailSender --> UsageCostProcessor --> UsageCostLogger</pre>
The `UsageDetailSender` source application's `output` is connected to the `UsageCostProcessor` `processor` application's `input`. 
The `UsageCostProcessor` application's `output` is connected to the `UsageCostLogger` sink application's `input`.

When these applications run, the `Kafka` binder binds the applications' `output` and `input` boundaries to the corresponding `topics` in Kafka.

**How to run the three applications as standalone applications in your local environment?**

_If you have not already done so, you must download and set up `Kafka` and `ZooKeeper` in your local environment.
Hints for Windows 10 installation:
 - When configuring `Kafka`
   - in the `Kafka` dir find `config` folder and then in `server.properties` set `log.dirs` to `log.dirs=C:\yourKafkaDirectory\kafka-logs`
 - When configuring `ZooKeeper`
   - in the `ZooKeeper`'s `conf` folder rename `zoo_sample.cfg` to `zoo.cfg` and set `dataDir` to `dataDir=C:\yourZooKeeperDirectory\data`
   - Set system variable `ZOOKEEPER_HOME` with value `C:\yourZooKeeperDirectory`_
   
Start the `ZooKeeper` and `Kafka` servers by running the following commands:
<pre>./bin/zookeeper-server-start.sh config/zookeeper.properties &
or in windows
`zkServer`</pre>
<pre>./bin/kafka-server-start.sh config/server.properties &</pre>

Now we are ready to run our applications:

###### Running the UsageDetailSender Source

By using the _pre-defined_ configuration properties (along with a unique server port) for `UsageDetailSender`, you can run the application, as follows:
<pre>java -jar target\usage-detail-sender-kafka-0.0.1-SNAPSHOT.jar --server.port=9001 &</pre>

Now you can see the messages being sent to the `usage-detail` Kafka topic by using the Kafka console consumer, as follows:
<pre>./bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic usage-detail</pre>

To list the topics, run the following command:
<pre>./bin/kafka-topics.sh --zookeeper localhost:2181 --list</pre>

###### Running the UsageCostProcessor Processor

By using the _pre-defined_ configuration properties (along with a unique server port) for `UsageCostProcessor`, you can run the application, as follows:
<pre>java -jar target\usage-cost-processor-kafka-0.0.1-SNAPSHOT.jar --server.port=9002 &</pre>

With the `UsageDetail` data in the `usage-detail` Kafka topic from the `UsageDetailSender` source application, 
you can see the `UsageCostDetail` from the `usage-cost` Kafka topic, as follows:
<pre>./bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic usage-cost</pre>

###### Running the UsageCostLogger Sink
By using the _pre-defined_ configuration properties (along with a unique server port) for `UsageCostLogger`, you can run the application, as follows:
<pre>java -jar target\usage-cost-sink-kafka-0.0.1-SNAPSHOT.jar --server.port=9003 &</pre>

Now you can see that this application logs the usage cost detail.