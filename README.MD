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

The application simulates basic monitoring system which collects and shows basic statistics for website, e.g. users count, sent notifications count, number of subscriptions and cancellations.
The purpose is to simulate the continuous fresh data flow, so that the old data is evicted and stored in cache store and the fresh data is always on the page.
The application consists of simulator threads, which simulate the reports from different nodes containing statistical data.

This data is calculated and stored in the cache.

The home page of the application shows the calculated statistical data as well as the cache usage statistics.

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

_NOTE: The following build command assumes you have configured your Maven user settings. If you have not, you must include Maven setting arguments on the command line. See [Build and Deploy the Quickstarts](../README.md#buildanddeploy) for complete instructions and additional options._

1. Make sure you have started the JBoss Server as described above.
2. Open a command line and navigate to the root directory of this quickstart.
3. Type this command to build and deploy the archive:

        mvn clean package jboss-as:deploy
        
4. This will deploy `target/jboss-as-carmart.war` to the running instance of the server.
 

Access the application
---------------------

The application will be running at the following URL: <http://localhost:8080/jboss-as-carmart/>


Undeploy the Archive
--------------------

1. Make sure you have started the JBoss Server as described above.
2. Open a command line and navigate to the root directory of this quickstart.
3. When you are finished testing, type this command to undeploy the archive:

        mvn jboss-as:undeploy


Debug the Application
------------------------------------

If you want to debug the source code or look at the Javadocs of any library in the project, run either of the following commands to pull them into your local repository. The IDE should then detect them.

        mvn dependency:sources
        mvn dependency:resolve -Dclassifier=javadoc


Build and Start the Application in Client-server Mode (using the HotRod client)
---------------------------------------------------------------------------------

NOTE: The application must be deployed to JBoss Enterprise Application Platform 6 or JBoss AS 7. It can not be deployed to JDG since it does not support deployment of applications. 

1. Obtain the JDG server distribution. See the following for more information: <http://www.redhat.com/products/jbossenterprisemiddleware/data-grid/>

2. Configure the remote datagrid in the `$JDG_HOME/standalone/configuration/standalone.xml` file. Copy the following XML into the Infinispan subsystem before the ending </cache-container> tag. If you have an existing `carcache` element, be sure to replace it with this one.
       
            <local-cache name="carcache" start="EAGER" batching="false" indexing="NONE">
                <locking isolation="REPEATABLE_READ" striping="false" acquire-timeout="20000" concurrency-level="500"/>
                <eviction strategy="LIRS" max-entries="4"/>
            </local-cache>
   
3. Start the JDG server using on localhost 100 on port offset: 
    
        $JDG_HOME/bin/standalone.sh -Djboss.socket.binding.port-offset=100

4. Start JBoss AS 7 into which you want to deploy your application

        $JBOSS_HOME/bin/standalone.sh

5. The application finds the JDG server using the values in the src/main/resources/META-INF/JDG.properties file. If you are not running the JDG server on the default host and port, you must modify this file to contain the correct values. If you need to change the JDG address:port information, edit src/main/resources/META-INF/JDG.properties file and specify address and port of the JDG server

        datagrid.host=localhost
        datagrid.hotrod.port=11322

6. Build the application in the example's directory:

        mvn clean package -Premote

7. Deploy the application

        mvn jboss-as:deploy -Premote

8. The application will be running at the following URL: <http://localhost:8080/jboss-as-carmart/>

9. Undeploy the application

        mvn jboss-as:undeploy -Premote


