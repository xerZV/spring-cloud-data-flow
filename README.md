### Stream Processing with Apache Kafka
Develop three Spring Boot applications that use Spring Cloud Stream's support for Apache Kafka and deploy them on your local machine. 

#### Development
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
 
 
##### UsageDetailSender source

###### `UsageDetail` class contains 
 
 - `userId` 
 - `data` 
 - `duration` 
 
`UsageDetailSender` class contains logic to send new a usageDetail instance per 2 sec
 
  - The `@EnableBinding` annotation indicates that you want to bind your application to the messaging middleware. 
  The annotation takes one or more interfaces as a parameter — in this case, the Source interface that defines an output channel named `output`. 
  In the case of Kafka, messages sent to the `output` channel are, in turn, sent the Kafka topic.
  - The `@EnableScheduling` annotation indicates that you want to enable Spring's scheduling capabilities, 
  which invoke methods annotated with `@Scheduled` with the specified `fixedDelay` of 2 second.
  - The `sendEvents` method constructs a `UsageDetail` object and then sends it to the the `output` channel by accessing the `Source` object's `output().send()` method.
  
It is a `producer` application so we need to set the `output` binding destination (Kafka topic in this scenario) where the producer publishes the data. So we add 
<pre>
spring.cloud.stream.bindings.output.destination=usage-detail</pre>
The `spring.cloud.stream.bindings.output.destination` property binds the `UsageDetailSender` object's output to the `usage-detail` Kafka topic.