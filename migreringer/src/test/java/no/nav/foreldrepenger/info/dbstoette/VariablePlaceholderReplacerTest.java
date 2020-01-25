package no.nav.foreldrepenger.info.dbstoette;

import static org.junit.Assert.assertEquals;

import java.util.Properties;

import org.junit.Test;

public class VariablePlaceholderReplacerTest {

    @Test
    public void test() {

        Properties properties = new Properties();
        properties.setProperty("flyway.placeholders.fpinfo.fpsak.schema.navn", "fpsak");
        properties.setProperty("flyway.placeholders.fpinfoschema.schema.navn", "fpinfo_schema");
        properties.setProperty("flyway.placeholders.fpinfo.schema.navn", "fpinfo");

        VariablePlaceholderReplacer vpr = new VariablePlaceholderReplacer(properties);
        assertEquals(vpr.replacePlaceholders("${flyway.placeholders.fpinfo.schema.navn}"), "fpinfo");
        assertEquals(vpr.replacePlaceholders("${flyway.placeholders.fpinfoschema.schema.navn}"), "fpinfo_schema");
        assertEquals(vpr.replacePlaceholders("${flyway.placeholders.fpinfo.fpsak.schema.navn}"), "fpsak");
        assertEquals(vpr.replacePlaceholders("ingen"), "ingen");
        assertEquals(vpr.replacePlaceholders("${flyway.test}"), "hallo");
    }
}