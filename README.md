Monispan: Basic Infinispan example
=================================
Author: Anna Manukyan
Level: Intermediate
Technologies: Infinispan, CDI, RestEasy, RichFaces, JSFlot
Summary: Shows how to use Infinispan instead of a relational database.
Target Product: JDG

What is it?
-----------

Monispan is a simple web application that uses Infinispan Cache instead of a relational database.

The application simulates basic monitoring system which collects and shows basic statistics for website, e.g. users or sent notifications count.
The purpose is to simulate the continuous fresh data flow, so that the old data is evicted and stored in cache store and the fresh data is always on the page.
The application consists of simulator threads, which simulate the reports from different nodes containing statistical data.
This data is calculated and stored in the cache.

The home page of the application shows the calculated statistical data for the last minute. The data for current time is shown
in data table, and the data for the last minute is shown via chart using JSFlot technology. The page also contains the
cache usage statistics as well as the duration of data retreival for the last minute.

There is also full report available, which shows the statistics for the whole lifetime of the application.

Application Configuration
-------------------------

There is edg.properties file which contains the necessary configuration for application.

simulatorNodeNum           - the number of simulator threads;
execFreq                   - the frequency with which the simulator threads work;
serverHost                 - the host where the application server is running;
serverPort                 - the port on which the application server is running;

System requirements
-------------------

All you need to build this project is Java 6.0 (Java SDK 1.6) or better, Maven 3.0 or better.

The application this project produces is designed to be run on JBoss Enterprise Application Platform 6 or JBoss AS 7. 

 
Configure Maven
---------------

If you have not yet done so, you must [Configure Maven](../README.md#configure-maven-) before testing the quickstarts.


Start JBoss Enterprise Application Platform 6 or JBoss AS 7
-----------------------------------------------------------

1. Open a command line and navigate to the root of the JBoss server directory.
2. The following shows the command line to start the server with the web profile:

        For Linux:   JBOSS_HOME/bin/standalone.sh
        For Windows: JBOSS_HOME\bin\standalone.bat


Build and Deploy the Application in Library Mode
-----------------------------------------------

_NOTE: The following build command assumes you have configured your Maven user settings. If you have not, you must include Maven setting arguments on the command line. See [Build and Deploy the Quickstarts](../README.md#buildanddeploy) for complete instructions and additional options.

1. Make sure you have started the JBoss Server as described above.
2. Open a command line and navigate to the root directory of this quickstart.
3. Type this command to build and deploy the archive:

        mvn clean package jboss-as:deploy
        
4. This will deploy `target/jboss-as-carmart.war` to the running instance of the server.
 

Access the application
---------------------

The application will be running at the following URL: <http://localhost:8080/jboss-as-monispan/>


Undeploy the Archive
--------------------

1. Make sure you have started the JBoss Server as described above.
2. Open a command line and navigate to the root directory of this quickstart.
3. When you are finished testing, type this command to undeploy the archive:

        mvn jboss-as:undeploy