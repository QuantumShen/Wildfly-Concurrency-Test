Client program user guide
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