Example: Embedded Tomcat w/ Servlet 2.5
=====================================================

This is a maven project setup as a WAR packaging, with a Main class that
is responsible to call the TomcatLauncher class. The embedded tomcat created
by the Main class, runs at the root context on port 8080.

Quick Start
-----------

Run the Main class on your favorite IDE, or just:

    $. mvn exec:java

Open your web browser to

    http://localhost:8080/  for the root of the project