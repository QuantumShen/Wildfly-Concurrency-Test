#Performance Test Report of CPU Intensive REST API on AWS t2.micro Wildfly Server

## Server-side Design
#### Server Configuration
1. AWS t2.micro Cloud Server
	* CPU: Single Core, Intel Xeon E5-2670v2 @2.50GHz
	* Memory: 1GB
	* Hard Disk: General SSD 30GB
	* Public IP: 52.42.56.228

2. Software Platform
	* Windows Server 2012 R2
	* Wildlfly 10.0.0
	* Apache Maven 3.3.0 
	* JDK 1.8.0
	
#### REST API service
1. Implementation based JBoss ReasEasy, which is a `JAX_RS 2.0` Implementation provided by JBoss AS and bundled with Wildfly.
2. Four HTTP GET APIs are implemented to provide a calculator service, in such forms:

		http://52.42.56.228:8080/add?operand1=10&operand2=20
		http://52.42.56.228:8080/substract?operand1=10&operand2=20 
		http://52.42.56.228:8080/multiply?operand1=10&operand2=20
		http://52.42.56.228:8080/divide?operand1=10&operand2=20
		
4. To get a `HTTP 200` response, the APIs are case sensitive. 
5. The successful respond message is plain text. The message is `error input` if no valid operands are given. Valid operands are integers, floating point numbers in plain form or scientific form like `3.14`, `2E10`, `1.0e5`.
6. Project is managed and deployed using Maven to root folder of wildfly server.
7. The source code is called `CalculatorREST.java` which can be found in the folder `/calculator/src/main/java/com/sensorhound/calculator` with this report.

## Client-side Design

#### Client test principles
1. To fully stress the server in concurrency requests, the client should simulate many users requesting the API simultaneously.
2. The users are simulated by multiple threads. Each thread connects to the server and requests repeatedly. To parallel the threads, the waiting of response must be non-blocked. 
3. Above principles can be realized using `javax.ws.rs.client` in RestEasy client library and `java.util.concurrent`.

#### Client program user guide (Same as `Readme.txt` in `/calculatorClient/`)
1. The client can accept five optional parameters.
	(1) Number of users: an integer (A user is a standalone thread)
	(2) Requests per user: an integer
	The above two parameters should be provided together. Otherwise, 10 users and 10 requests are used by default.
	(3) Operator: `+`, `-`, `*`, `/` , `all`
	Operator is `all` if not provided. `all` means all the four APIs will be tested randomly.
	(4) Operand1: either integer or float point number
	(5) Operand2: either integer or float point number
	The two operands should be provided together. Otherwise, the operands will be a pair of random integers for each user.

	In fact, the best way to use the client is to provide only the first two parameters. The client will test all APIs with random operands automatically.

	
2. Build and execute the client
	(1) You need to have JDK 1.8.0 and Maven (better newest version) on your computer and configured correctly.
	(2) Go to `/calculatorClient/` in command line.
	(3) Execute `mvn clean compile`
	(4) Execute `mvn exec:java -Dexec.mainClass="com.sensorhound.client.CalculatorClient"` to run the default 10 x 10 test.
	(5) If succeed, you will find a `log10x10.csv` file in the current folder `/calculatorClient/`.
	(6) Try larger parameters such as:
	`mvn exec:java -Dexec.mainClass="com.sensorhound.client.CalculatorClient" -Dexec.args="1000 1000"`
	(7)  A new empty `logMxN.csv` will be created each time you execute the client. So if you want to keep file after execution, you must rename it or move it away.
