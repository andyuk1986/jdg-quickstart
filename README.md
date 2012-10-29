Monispan: Basic Infinispan example
=================================
Author: Anna Manukyan
Level: Intermediate
Technologies: Infinispan, CDI, RestEasy, JSF, RichFaces, JSFlot
Summary: Shows how to use Infinispan instead of a relational database.
Target Product: JDG

What is it?
-----------

Monispan is a simple web application that uses Infinispan Cache instead of a relational database.

The application simulates basic monitoring system which collects and shows basic statistics for website, e.g. users or sent notifications count.
The purpose is to simulate the continuous fresh data flow, so that the old data is evicted and stored in cache store and the fresh data is always on the page.

The application simulates the monitoring system. It consists of simulator threads, which work periodically with the provided frequency.

Each thread represents the fake front end server, which has some online users on it and it provides periodically information about the status
of online users or sent emails to these users on it.

So the threads are performing REST calls to the application's 2nd part, and provides this basic information to it like the number of users or
the number of sent email notifications to these users (of course the numbers are simulated).

The reports are delivered by the REST service at the same moment, are merged and stored as one report in the cache. This process
happens periodically with the specified period of time.

The application provides the WEB UI, which has 2 views:

1. The full data report view - the whole data is shown as a graph as well as average calculated numbers.

2. The recent data report view - the data for the last minute is shown with it's corresponding graph and avg calculations.

Both reports are visualised with graph generated using JSFlot library.
The page also contains the cache usage statistics as well as the duration of data retrieval for the last minute.

Application Configuration
-------------------------

There is edg.properties file which contains the necessary configuration for application.

simulatorNodeNum           - the number of simulator threads;
execFreq                   - the frequency with which the simulator threads work;
serverHost                 - the host where the application server is running;
serverPort                 - the port on which the application server is running;

System requirements
-------------------

All you need to build this project is Java 6.0 (Java SDK 1.6) or higher, Maven 3.0 or higher.

The application, which this project produces, is designed to be run on JBoss Enterprise Application Platform 6 or JBoss AS 7.

 
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
        
4. This will deploy `target/jboss-as-monispan.war` to the running instance of the server.
 

Access the application
---------------------

The application will be running at the following URL: <http://localhost:8080/jboss-as-monispan/>


Undeploy the Archive
--------------------

1. Make sure you have started the JBoss Server as described above.
2. Open a command line and navigate to the root directory of this quickstart.
3. When you are finished testing, type this command to undeploy the archive:

        mvn jboss-as:undeploy