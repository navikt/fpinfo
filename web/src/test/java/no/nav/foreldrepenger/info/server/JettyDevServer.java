package no.nav.foreldrepenger.info.server;


import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ws.rs.ApplicationPath;

import io.swagger.v3.oas.integration.OpenApiConfigurationException;
import no.nav.foreldrepenger.info.v2.TestTjeneste;
import no.nav.foreldrepenger.info.app.konfig.ApiConfig;
import no.nav.foreldrepenger.konfig.Environment;

public class JettyDevServer extends JettyServer {

    private static final Environment ENV = Environment.current();

    public static void main(String[] args) throws Exception {
        jettyServer(args).bootStrap();
    }

    protected static JettyDevServer jettyServer(String[] args) {
        if (args.length > 0) {
            return new JettyDevServer(Integer.parseUnsignedInt(args[0]));
        }
        return new JettyDevServer(ENV.getProperty("server.port", Integer.class, 8080));
    }

    private JettyDevServer(int serverPort) {
        super(serverPort);
    }

    @Override
    protected List<Class<?>> getWebInfClasses() {
        return List.of(LocalApplicationConfig.class);
    }

    @ApplicationPath(ApiConfig.API_URI)
    private static class LocalApplicationConfig extends ApiConfig {

        public LocalApplicationConfig() throws OpenApiConfigurationException {
            super();
        }

        @Override
        public Set<Class<?>> getClasses() {
            return Stream.concat(super.getClasses().stream(),
                            Stream.of(TestTjeneste.class))
                    .collect(Collectors.toUnmodifiableSet());
        }
    }
}

