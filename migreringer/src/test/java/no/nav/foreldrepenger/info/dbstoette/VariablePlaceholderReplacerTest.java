package no.nav.foreldrepenger.info.dbstoette;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class VariablePlaceholderReplacerTest {

    @Test
    public void test() {

        System.setProperty("flyway.placeholders.fpinfo.fpsak.schema.navn", "fpsak");
        System.setProperty("flyway.placeholders.fpinfoschema.schema.navn", "fpinfo_schema");
        System.setProperty("flyway.placeholders.fpinfo.schema.navn", "fpinfo");
        VariablePlaceholderReplacer vpr = new VariablePlaceholderReplacer();
        assertEquals(vpr.replacePlaceholders("${flyway.placeholders.fpinfo.schema.navn}"), "fpinfo");
        assertEquals(vpr.replacePlaceholders("${flyway.placeholders.fpinfoschema.schema.navn}"), "fpinfo_schema");
        assertEquals(vpr.replacePlaceholders("${flyway.placeholders.fpinfo.fpsak.schema.navn}"), "fpsak");
        assertEquals(vpr.replacePlaceholders("ingen"), "ingen");
        assertEquals(vpr.replacePlaceholders("${flyway.test}"), "hallo");
    }
}