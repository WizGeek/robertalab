package de.fhg.iais.roberta.persistence.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import de.fhg.iais.roberta.util.dbc.Assert;

/**
 * class, that wraps the session factory of hibernate. Creating the session factory of hibernate is expensive. The session factory hides almost complete the
 * underlying database.<br>
 * - Retrieving sessions from the factory is thread-safe and cheap.<br>
 * - The generated sessions are not thread-safe.<br>
 * <br>
 * The class <b>should</b> be used as a singleton. Use <b>GUICE</b> to enforce that.
 *
 * @author rbudde
 */
public final class SessionFactoryWrapper {
    private static final Logger LOG = LoggerFactory.getLogger(SessionFactoryWrapper.class);
    private SessionFactory sessionFactory;

    /**
     * configure the session factory
     */
    @Inject
    public SessionFactoryWrapper(@Named("hibernate.config.xml") String cfgXml, @Named("hibernate.connection.url") String databaseUrl) {
        try {
            Configuration configuration = new Configuration();
            configuration.configure(cfgXml);
            configuration.setProperty("hibernate.connection.url", databaseUrl);
            ServiceRegistryBuilder serviceRegistryBuilder = new ServiceRegistryBuilder().applySettings(configuration.getProperties());
            this.sessionFactory = configuration.buildSessionFactory(serviceRegistryBuilder.buildServiceRegistry());
            LOG.info("created");
        } catch ( Exception e ) {
            LOG.error("Initial SessionFactory creation failed." + e.getMessage(), e);
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * get a new session-wrapper from the session factory. The session-wrapper and the session contained are <b>not</b> thread-safe.
     *
     * @return the session-wrapper, never null
     */
    public DbSession getSession() {
        Assert.notNull(this.sessionFactory, "previous attempt to initialize the session factory failed");
        Session session = this.sessionFactory.openSession();
        Assert.notNull(session, "creation of session failed");
        return new DbSession(session);
    }

    /**
     * get a new NATIVE session from the session factory. The session is <b>not</b> thread-safe. <b>Use rarely - know what you do</b>
     *
     * @return the session-wrapper, never null
     */
    public Session getNativeSession() {
        Assert.notNull(this.sessionFactory, "previous attempt to initialize the session factory failed");
        Session session = this.sessionFactory.openSession();
        Assert.notNull(session, "creation of session failed");
        return session;
    }
}