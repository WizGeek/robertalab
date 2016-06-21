package de.fhg.iais.roberta.main;

import java.io.IOException;
import java.util.Properties;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.session.HashSessionManager;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Injector;
import com.google.inject.servlet.GuiceFilter;
import com.sun.jersey.spi.container.servlet.ServletContainer;

import de.fhg.iais.roberta.guice.RobertaGuiceServletConfig;
import de.fhg.iais.roberta.javaServer.websocket.Ev3SensorLoggingWS;
import de.fhg.iais.roberta.persistence.dao.ProgramDao;
import de.fhg.iais.roberta.persistence.util.DbSession;
import de.fhg.iais.roberta.persistence.util.SessionFactoryWrapper;
import de.fhg.iais.roberta.util.Util1;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

/**
 * <b>the main class of the application, the main activity is starting the server.</b><br>
 * <br>
 * - starts an embedded jetty (see {@link ServletContainer}<br>
 * - configures jersey and the package with the resources<br>
 * - configures jaxb and the package with the providers<br>
 * - configures a resource holder for static content<br>
 * - configures hibernate and tests the connection to the database.<br>
 * <br>
 *
 * @author rbudde
 */
public class ServerStarter {
    private static final Logger LOG = LoggerFactory.getLogger(ServerStarter.class);

    private final Properties properties;
    private Injector injector;

    /**
     * startup and shutdown of the server. See {@link ServerStarter}. Uses the first element of the args array. This contains the URI of a property file and
     * starts either with "file:" if a path of the file system should be used or "classpath:" if the properties should be loaded as a resource from the
     * classpath. May be <code>null</code>, if the default resource from the classpath should be loaded.
     *
     * @param args first element may contain the URI of a property file.
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        OptionParser parser = new OptionParser();
        OptionSpec<String> propertiesOpt = parser.accepts("properties").withOptionalArg().ofType(String.class);
        OptionSpec<String> ipOpt = parser.accepts("ip").withRequiredArg().ofType(String.class);
        OptionSpec<Integer> portOpt = parser.accepts("port").withRequiredArg().ofType(Integer.class);
        OptionSet options = parser.parse(args);
        String properties = propertiesOpt.value(options);
        String ip = ipOpt.value(options);
        Integer port = portOpt.value(options);
        ServerStarter serverStarter = new ServerStarter(properties);
        Server server = serverStarter.start(ip != null ? ip : "0.0.0.0", port != null ? port : 1999);
        ServerStarter.LOG.info("*** server started using URI: " + server.getURI() + " ***");
        serverStarter.logTheNumberOfStoredPrograms();
        server.join();
        System.exit(0);
    }

    /**
     * create the starter. Load the properties.
     *
     * @param propertyPath optional URI to properties resource. May be null.
     */
    public ServerStarter(String propertyPath) {
        Properties mailProperties = Util1.loadProperties("classpath:openRobertaMailServer.properties");
        Properties tmpProperties = Util1.loadProperties(propertyPath);
        this.properties = mergeProperties(mailProperties, tmpProperties);
    }

    private Properties mergeProperties(Properties mailProperties, Properties tmpProperties) {
        if ( mailProperties != null ) {
            tmpProperties.putAll(mailProperties);
        }
        return tmpProperties;
    }

    /**
     * startup. See {@link ServerStarter}. If the server could not be created, <b>the process will be terminated by System.exit(status) with status > 0</b>.
     *
     * @return the server
     */
    public Server start(String host, int port) throws IOException {
        String versionFrom = this.properties.getProperty("validversionrange.From", "?");
        String versionTo = this.properties.getProperty("validversionrange.To", "?");
        //        Assert.isTrue(new VersionChecker(versionFrom, versionTo).validateServerSide(), "invalid versions found - this should NEVER occur");
        Server server = new Server();
        ServerConnector http = new ServerConnector(server);
        http.setHost(host);
        http.setPort(port);
        server.setConnectors(new ServerConnector[] {
            http
        });

        RobertaGuiceServletConfig robertaGuiceServletConfig = new RobertaGuiceServletConfig(this.properties);

        // REST API with /rest/<version>/ prefix
        ServletContextHandler versionedHttpHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        versionedHttpHandler.setContextPath("/rest");
        versionedHttpHandler.setSessionHandler(new SessionHandler(new HashSessionManager()));

        versionedHttpHandler.addEventListener(robertaGuiceServletConfig);
        versionedHttpHandler.addFilter(GuiceFilter.class, "/*", null);
        versionedHttpHandler.addServlet(DefaultServlet.class, "/*");

        // REST API without prefix - deprecated
        ServletContextHandler deprecatedHttpHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        deprecatedHttpHandler.setContextPath("/*");
        deprecatedHttpHandler.setSessionHandler(new SessionHandler(new HashSessionManager()));

        deprecatedHttpHandler.addEventListener(robertaGuiceServletConfig);
        deprecatedHttpHandler.addFilter(GuiceFilter.class, "/alive/*", null);
        deprecatedHttpHandler.addFilter(GuiceFilter.class, "/admin/*", null);
        deprecatedHttpHandler.addFilter(GuiceFilter.class, "/conf/*", null);
        deprecatedHttpHandler.addFilter(GuiceFilter.class, "/ping/*", null);
        deprecatedHttpHandler.addFilter(GuiceFilter.class, "/program/*", null);
        deprecatedHttpHandler.addFilter(GuiceFilter.class, "/toolbox/*", null);
        deprecatedHttpHandler.addFilter(GuiceFilter.class, "/user/*", null);
        deprecatedHttpHandler.addFilter(GuiceFilter.class, "/hello/*", null);
        deprecatedHttpHandler.addFilter(GuiceFilter.class, "/pushcmd/*", null);
        deprecatedHttpHandler.addFilter(GuiceFilter.class, "/download/*", null);
        deprecatedHttpHandler.addFilter(GuiceFilter.class, "/update/*", null);
        deprecatedHttpHandler.addServlet(DefaultServlet.class, "/*");

        // websockets with /ws/<version>/ prefix
        ServletContextHandler wsHandler = new ServletContextHandler();
        wsHandler.setContextPath("/ws");
        wsHandler.addServlet(WebSocketServiceServlet.class, "/*");

        ResourceHandler staticResourceHandler = new ResourceHandler();
        staticResourceHandler.setDirectoriesListed(true);
        staticResourceHandler.setResourceBase("staticResources");

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] {
            staticResourceHandler,
            versionedHttpHandler,
            wsHandler,
            deprecatedHttpHandler
        });
        server.setHandler(handlers);

        try {
            server.start();
        } catch ( Exception e ) {
            ServerStarter.LOG.error("Could not start the server at " + host + ":" + port, e);
            System.exit(16);
        }
        this.injector = robertaGuiceServletConfig.getCreatedInjector();
        Ev3SensorLoggingWS.setGuiceInjector(this.injector);
        return server;
    }

    /**
     * returns the guice injector configured in this class. Even if this not dangerous per se, it should <b>only be used in tests</b>
     *
     * @return the injector
     */
    public Injector getInjectorForTests() {
        return this.injector;
    }

    private void logTheNumberOfStoredPrograms() {
        try {
            DbSession session = this.injector.getInstance(SessionFactoryWrapper.class).getSession();
            ProgramDao projectDao = new ProgramDao(session);
            int numberOfPrograms = projectDao.loadAll().size();
            ServerStarter.LOG.info("There are " + numberOfPrograms + " programs stored in the database");
            session.close();
        } catch ( Exception e ) {
            ServerStarter.LOG.error("Server was started, but could not connect to the database", e);
            System.exit(20);
        }

    }

    public static class WebSocketServiceServlet extends WebSocketServlet {
        private static final long serialVersionUID = -2697779106901658247L;

        @Override
        public void configure(WebSocketServletFactory factory) {
            factory.register(Ev3SensorLoggingWS.class);
        }
    }
}
