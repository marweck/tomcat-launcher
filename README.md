# Embedded Tomcat Launcher

This small project was created specifically for your old java 6/8 and servlet 2.5/3.1
projects that need a tomcat/jboss plugin on your IDE to be run during development.

If you can, you should try the amazing Spring Boot by Pivotal. If you are still
stuck with an old Application Server, just like me with some of my apps, you can
use this library to lower the burden of having to use any IDE plugin to start your
application.

## Quick Start

Just  create a Java class with a main method with the following code:

    public class AppStarter {
        public static void main(String[] args) throws Exception {
            new Tomcat7Launcher(8080, "/app").launch();
        }
    }

In this case, you are creating an Embedded Tomcat 7 on port 8080 and with the 
context /app. If your project uses Java 8, you might prefer the Embedded Tomcat 9
version. Just replace the Tomcat7Launcher class by the Tomcat9Launcher class.

To access the running app, open your web browser to

    http://localhost:8080/app

Or if you prefer to publish your app on the root of the embedded tomcat, change
the main method to:

    public static void main(String[] args) throws Exception {
        new Tomcat7Launcher(8080).launch();
    }

Or use the Tomcat9Launcher project if you're working with Java 8.

Open your web browser to:

    http://localhost:8080