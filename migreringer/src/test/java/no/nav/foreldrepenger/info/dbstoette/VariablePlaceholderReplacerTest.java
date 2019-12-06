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
        String fpinfoSchemaNavn = vpr.replacePlaceholders("${flyway.placeholders.fpinfo.schema.navn}");
        assertEquals(fpinfoSchemaNavn.toLowerCase(), "fpinfo");
        String fpinfo_schemaSchemaNavn = vpr.replacePlaceholders("${flyway.placeholders.fpinfoschema.schema.navn}");
        assertEquals(fpinfo_schemaSchemaNavn.toLowerCase(), "fpinfo_schema");
        String fpsakSchema = vpr.replacePlaceholders("${flyway.placeholders.fpinfo.fpsak.schema.navn}");
        assertEquals(fpsakSchema.toLowerCase(), "fpsak");

        //TODO(Humle): Forbedre test

    }

}