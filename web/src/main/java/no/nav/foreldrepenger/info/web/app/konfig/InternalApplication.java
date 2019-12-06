package no.nav.foreldrepenger.info.web.app.konfig;

import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import io.swagger.jaxrs.config.BeanConfig;
import no.nav.foreldrepenger.info.web.app.tjenester.NaisRestTjeneste;
import no.nav.foreldrepenger.info.web.app.tjenester.SelftestRestTjeneste;
import no.nav.vedtak.isso.config.ServerInfo;

@ApplicationPath(InternalApplication.API_URL)
public class InternalApplication extends Application {

    public static final String API_URL = "/internal";

    public InternalApplication() {
        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setVersion("1.0");
        if (ServerInfo.instance().isUsingTLS()) {
            beanConfig.setSchemes(new String[]{"https"});
        } else {
            beanConfig.setSchemes(new String[]{"http"});
        }
        beanConfig.setBasePath("/fpabakus/" + API_URL);
        beanConfig.setResourcePackage("no.nav");
        beanConfig.setTitle("Vedtaksløsningen - Abakus");
        beanConfig.setDescription("REST grensesnitt for Vedtaksløsningen.");
        beanConfig.setScan(true);
    }

    @Override
    public Set<Class<?>> getClasses() {

        return Set.of(NaisRestTjeneste.class, SelftestRestTjeneste.class);
    }

}
