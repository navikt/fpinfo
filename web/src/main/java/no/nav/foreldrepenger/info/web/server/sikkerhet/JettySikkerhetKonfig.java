package no.nav.foreldrepenger.info.web.server.sikkerhet;

import java.io.File;
import java.security.Security;

import javax.security.auth.message.config.AuthConfigFactory;

import org.apache.geronimo.components.jaspi.AuthConfigFactoryImpl;
import org.eclipse.jetty.jaas.JAASLoginService;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.DefaultIdentityService;
import org.eclipse.jetty.security.jaspi.JaspiAuthenticatorFactory;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.vedtak.util.env.Environment;

/**
 * Konfigurerer sikkerhet for jetty webappContext.
 */
public class JettySikkerhetKonfig {

    private static final Logger LOG = LoggerFactory.getLogger(JettySikkerhetKonfig.class);

    private static final Environment ENV = Environment.current();

    public void konfigurer(WebAppContext webAppContext) {

        // configure login-service
        JAASLoginService loginService = new JAASLoginService();
        loginService.setName("jetty-login");
        loginService.setLoginModuleName("jetty-login");
        loginService.setIdentityService(new DefaultIdentityService());
        ConstraintSecurityHandler sh = new ConstraintSecurityHandler();
        sh.setAuthenticatorFactory(new JaspiAuthenticatorFactory());
        sh.setLoginService(loginService);
        webAppContext.setSecurityHandler(sh);

        setOppSikkerhet();

    }

    private static void setOppSikkerhet() {
        Security.setProperty(AuthConfigFactory.DEFAULT_FACTORY_SECURITY_PROPERTY,
                AuthConfigFactoryImpl.class.getCanonicalName());

        System.setProperty("no.nav.modig.core.context.subjectHandlerImplementationClass",
                "no.nav.foreldrepenger.info.web.server.sikkerhet.JettySubjectHandler");
        String confDir = ENV.getProperty("app.confdir", "./conf");

        setFileProperty("org.apache.geronimo.jaspic.configurationFile", confDir + "/jaspi-conf.xml");
        setFileProperty("java.security.auth.login.config", confDir + "/login.conf");
    }

    private static void setFileProperty(String property, String filePath) {
        if (!(new File(filePath)).exists()) { // NOSONAR
            throw new IllegalArgumentException("Finner ikke fil lokalt: " + filePath);
        }
        System.setProperty(property, filePath);
    }

}
