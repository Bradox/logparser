The goal is to write a parser in Java that parses web server access log file, loads the log to MySQL and checks if a given IP makes more than a certain number of requests for the given duration. 

Java
----

(1) Create a java tool that can parse and load the given log file to MySQL. The delimiter of the log file is pipe (|)

(2) The tool takes "startDate", "duration" and "threshold" as command line arguments. "startDate" is of "yyyy-MM-dd.HH:mm:ss" format, "duration" can take only "hourly", "daily" as inputs and "threshold" can be an integer.

(3) This is how the tool works:

    java -cp "parser.jar" com.ef.Parser --startDate=2017-01-01.13:00:00 --duration=hourly --threshold=100
	
	The tool will find any IPs that made more than 100 requests starting from 2017-01-01.13:00:00 to 2017-01-01.14:00:00 (one hour) and print them to console AND also load them to another MySQL table with comments on why it's blocked.

	java -cp "parser.jar" com.ef.Parser --startDate=2017-01-01.13:00:00 --duration=daily --threshold=250

	The tool will find any IPs that made more than 250 requests starting from 2017-01-01.13:00:00 to 2017-01-02.13:00:00 (24 hours) and print them to console AND also load them to another MySQL table with comments on why it's blocked.


SQL
---

(1) Write MySQL query to find IPs that mode more than a certain number of requests for a given time period.

    Ex: Write SQL to find IPs that made more than 100 requests starting from 2017-01-01.13:00:00 to 2017-01-01.14:00:00.

(2) Write MySQL query to find requests made by a given IP.
 	

LOG Format
----------
Date, IP, Request, Status, User Agent (pipe delimited, open the example file in text editor)

Date Format: "yyyy-MM-dd HH:mm:ss.SSS"

Also, please find attached a log file for your reference. 

The log file assumes 200 as hourly limit and 500 as daily limit, meaning:

(1) 
When you run your parser against this file with the following parameters

java -cp "parser.jar" com.ef.Parser --startDate=2017-01-01.15:00:00 --duration=hourly --threshold=200

The output will have 192.168.11.231. If you open the log file, 192.168.11.231 has 200 or more requests between 2017-01-01.15:00:00 and 2017-01-01.15:59:59

(2) 
When you run your parser against this file with the following parameters

java -cp "parser.jar" com.ef.Parser --startDate=2017-01-01.00:00:00 --duration=daily --threshold=500

The output will have  192.168.102.136. If you open the log file, 192.168.102.136 has 500 or more requests between 2017-01-01.00:00:00 and 2017-01-01.23:59:59


Deliverables
------------

(1) Java program that can be run from command line
	
    java -cp "parser.jar" com.ef.Parser --accesslog=/path/to/file --startDate=2017-01-01.13:00:00 --duration=hourly --threshold=200 

(2) Source Code for the Java program

(3) MySQL schema used for the log data

(4) SQL queries for SQL test

Solution
========

Java
----
The application was implemented using java8, spring-boot and maven

(1) create a database schema, named, for example logparser.
(2) Update configuration. In deliverables/application.properties set up the connection changing the database url, user and password.
(3) To run the program go to folder deliverables and run:

    java -jar parser-1.0.jar --accesslog=[PATH_TO_ACCESS_LOG]  --startDate=[START DATE] --duration=[HOURLY] --threshold=[INTEGER]
    
    for example: 
    
     java -jar parser-1.0.jar --accesslog=access.log  --startDate=2017-01-01.13:00:00 --duration=hourly --threshold=100

To show help run:

    java -jar parser-1.0.jar --help

                
Deliverables
------------

(1) Java program that can be run from command line
	
	deliverables/parser-1.0.jar
   
(2) Source Code for the Java program

    Provided by git

(3) MySQL schema used for the log data

    deliverables/schema.sql

(4) SQL queries for SQL test   
 	
 	deliverables/sql.test
 	
TODO
========

1. Make it fail safe application. One way could be to track the entries that have been process, so that if the execution ends abruptly we can reprocess without deletion of the database nor encuenter issues with duplicates<br/>
2. Investigate issues with ApplicationArguments impossible:<br/>
   - why dbunit made mocking of ApplicationArguments impossible
   - why @autowire ApplicationArguments in a service does not work
3. Make it a spring application, no spring boot. 
4. Use logs instead of writing to the console (System.out).<br/>
5. Findout how to pass arguments to tests<br/>