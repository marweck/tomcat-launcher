package com.github.marweck.tomcat.launcher;

import org.apache.catalina.Host;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.loader.WebappLoader;
import org.apache.catalina.startup.Constants;
import org.apache.catalina.startup.ContextConfig;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.startup.Tomcat.DefaultWebXmlListener;
import org.apache.catalina.startup.Tomcat.FixContextListener;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.naming.resources.VirtualDirContext;

import javax.servlet.ServletException;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Embedded Tomcat Launcher
 * <p>
 * Usage:
 * <p>
 * <pre>
 * public static void main(String[] args) throws Exception {
 * 	new TomcatLauncher(8080, "app").launch();
 * }
 * </pre>
 *
 * @author Marcio Carvalho
 */
public class TomcatLauncher {

    /**
     * JULI logger
     */
    private static final Log log = LogFactory.getLog(TomcatLauncher.class);

    /**
     * NIO protocol handler
     */
    private static final String DEFAULT_PROTOCOL = "org.apache.coyote.http11.Http11NioProtocol";

    /**
     * Application context name
     */
    private String appContext;

    /**
     * Connector port
     */
    private int port;

    /**
     * Full constructor
     *
     * @param port       Default 8080
     * @param appContext Application web context name. To define the root context, null
     *                   must be used
     */
    public TomcatLauncher(Integer port, String appContext) {

        this.port = port;

        if (port == null) {
            this.port = 8080;
        }

        this.appContext = appContext;

        if (appContext == null) {
            this.appContext = "";
        } else if (!appContext.startsWith("/")) {
            this.appContext = "/" + appContext;
        }
    }

    /**
     * Constructor taking just the port. The application context name will be
     * the root.
     *
     * @param port
     */
    public TomcatLauncher(Integer port) {
        this(port, null);
    }

    /**
     * Default constructor: port equals to 8080 and root context
     */
    public TomcatLauncher() {
        this(8080);
    }

    /**
     * Configures and starts the embedded Tomcat. When returned, the tomcat will
     * have stopped.
     *
     * @throws Exception
     */
    public void launch() throws Exception {

        Banner.printBanner(System.out);

        if (!PortUtil.available(port)) {
            log.error(Banner.redText("\n\n >>>>>>>>>> BOOOOOOOOM!!! Port already in use... <<<<<<<<<<\n\n"));
            return;
        }

        long start = System.currentTimeMillis();

        Tomcat tomcat = startServer();

        log.info(Banner.blueText("Server localhost:" + port + appContext + " started in " +
                (System.currentTimeMillis() - start) + "ms"));

        tomcat.getServer().await();
    }

    /**
     * Creates and starts the Tomcat server instance
     *
     * @return
     * @throws IOException
     * @throws ServletException
     * @throws LifecycleException
     */
    private Tomcat startServer() throws IOException, ServletException, LifecycleException {

        initEnvironmentVariables();

        Tomcat tomcat = new Tomcat();

        tomcat.setBaseDir(PathUtil.createTempDir("tomcat-base-dir", Integer.toString(port)).toString());
        tomcat.setPort(port);
        tomcat.setSilent(true);
        tomcat.enableNaming();

        prepareContext(tomcat);
        prepareConnector(tomcat);

        tomcat.start();

        return tomcat;
    }

    /**
     * Creates and prepares the application context.
     *
     * @param tomcat
     * @return
     * @throws ServletException
     */
    private StandardContext prepareContext(Tomcat tomcat) throws ServletException {

        Host host = tomcat.getHost();
        StandardContext context = new StandardContext();

        context.setName(appContext);
        context.setPath(appContext);

        File documentBase = PathUtil.getDocumentBase();
        context.setDocBase(documentBase.getAbsolutePath());
        context.setParentClassLoader(getClass().getClassLoader());
        context.setConfigFile(PathUtil.getWebappConfigFile(documentBase));

        context.addLifecycleListener(new DefaultWebXmlListener());
        context.addLifecycleListener(createLifecycleListener(host));
        context.addLifecycleListener(new FixContextListener());

        // target/classes if existent
        addAlternativeResources(context);

        WebappLoader loader = new WebappLoader(context.getParentClassLoader());
        loader.setDelegate(true);
        context.setLoader(loader);

        host.addChild(context);

        return context;
    }

    /**
     * Creates and configures the server connector.
     * <p>
     * This connector uses compression by default for every text file (html, js,
     * xml, css, json) with size greater than 4KB.
     *
     * @param tomcat
     * @return
     */
    private Connector prepareConnector(Tomcat tomcat) {

        Connector connector = new Connector(DEFAULT_PROTOCOL);

        connector.setPort(port);
        connector.setURIEncoding("UTF-8");
        connector.setProperty("bindOnInit", "false");
        connector.setProperty("compression", "on");
        connector.setProperty("compressionMinSize", "4096");
        connector.setProperty("noCompressionUserAgents", "gozilla, traviata");
        connector.setProperty("compressableMimeType",
                "text/html,text/xml,text/css,application/json,application/javascript");

        tomcat.getService().addConnector(connector);
        tomcat.setConnector(connector);

        return connector;
    }

    /**
     * Adds target/classes, if it exists, as an alternative resource dir. This
     * is specially useful when executing the application inside an IDE.
     *
     * @param context server context
     */
    private void addAlternativeResources(StandardContext context) {
        File alternative = new File("target/classes");

        if (alternative.exists()) {
            VirtualDirContext resources = new VirtualDirContext();
            resources.setExtraResourcePaths("/WEB-INF/classes=" + alternative.getAbsolutePath());
            context.setResources(resources);
        }
    }

    /**
     * Instantiates the the approppriate lifecycle listener for the host
     *
     * @param host
     * @return
     */
    private LifecycleListener createLifecycleListener(Host host) {

        LifecycleListener listener = null;

        try {
            Class<?> clazz = Class.forName(host.getConfigClass());
            listener = (LifecycleListener) clazz.newInstance();

            if (listener instanceof ContextConfig) {
                ((ContextConfig) listener).setDefaultWebXml(Constants.NoDefaultWebXml);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }

        return listener;
    }

    /**
     * Setting some system properties important for JSF applications
     */
    private void initEnvironmentVariables() {
        System.setProperty("org.apache.catalina.startup.EXIT_ON_INIT_FAILURE", "true");
        System.setProperty("org.apache.el.parser.COERCE_TO_ZERO", "false");
        System.setProperty("file.encoding", "UTF-8");
    }
}
