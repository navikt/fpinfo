package no.nav.foreldrepenger.info.web.server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


class DatabaseKonfigINaisEnvironment {

    private static final Set<String> FASIT_RESURSSER = new HashSet<>();
    private static final Map<String, String> DEFAULT_FLYWAY_PLACEHOLDERE = new HashMap<>();

    private DatabaseKonfigINaisEnvironment() {
    }

    static {
        FASIT_RESURSSER.add("DEFAULTDS_URL");
        FASIT_RESURSSER.add("DEFAULTDS_USERNAME");
        FASIT_RESURSSER.add("DEFAULTDS_PASSWORD");
        FASIT_RESURSSER.add("FPINFOSCHEMA_URL");
        FASIT_RESURSSER.add("FPINFOSCHEMA_USERNAME");
        FASIT_RESURSSER.add("FPINFOSCHEMA_PASSWORD");

        //Disse flyway-placeholderne refererer til skjemanavn i alle NAIS-testmiljøer og PROD.
        DEFAULT_FLYWAY_PLACEHOLDERE.put("flyway.placeholders.fpinfo.fpsak.schema.navn", "fpsak");
        DEFAULT_FLYWAY_PLACEHOLDERE.put("flyway.placeholders.fpinfo.schema.navn", "fpinfo");
        DEFAULT_FLYWAY_PLACEHOLDERE.put("flyway.placeholders.fpinfoschema.schema.navn", "fpinfo_schema");
    }

    public static void setup() {
        readInNaisEnvironment();
        opprettSystemPropertiesHvisIkkeFinnes();
    }

    /**
     * Alle ApplicationProperties fra FASIT blir gjort om til miljøvariabler ved deploy til NAIS-miljø. NAIS erstatter '.' med '_', og
     * små bokstaver til store bokstaver. Denne klassen utfører den motsatte operasjonen og legger inn
     * utvalgte miljøvariabler som System-properties.
     */
    private static void readInNaisEnvironment() {
        Map<String, String> environment = System.getenv();
        environment.keySet().stream().forEach(key -> {
            if (skalOppretteSystemProperty(key)) {
                String systemPropertyKey = key.replaceAll("_", ".").toLowerCase();
                System.setProperty(systemPropertyKey, environment.get(key));
            }
        });
    }

    private static void opprettSystemPropertiesHvisIkkeFinnes() {
        DEFAULT_FLYWAY_PLACEHOLDERE.keySet().stream().forEach(key -> {
            if (System.getProperty(key) == null) {
                System.setProperty(key, DEFAULT_FLYWAY_PLACEHOLDERE.get(key));
            }
        });
    }

    private static boolean skalOppretteSystemProperty(String key) {
        return FASIT_RESURSSER.contains(key);
    }

}
