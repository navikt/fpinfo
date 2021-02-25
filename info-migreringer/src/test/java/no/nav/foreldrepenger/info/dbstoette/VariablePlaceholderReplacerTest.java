package no.nav.foreldrepenger.info.dbstoette;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class VariablePlaceholderReplacerTest {

    @Test
    public void test() {

        System.setProperty("flyway.placeholders.fpinfo.fpsak.schema.navn", "fpsak");
        System.setProperty("flyway.placeholders.fpinfoschema.schema.navn", "fpinfo_schema");
        System.setProperty("flyway.placeholders.fpinfo.schema.navn", "fpinfo");
        assertEquals(VariablePlaceholderReplacer.replacePlaceholders("${flyway.placeholders.fpinfo.schema.navn}"),
                "fpinfo");
        assertEquals(VariablePlaceholderReplacer.replacePlaceholders("${flyway.placeholders.fpinfoschema.schema.navn}"),
                "fpinfo_schema");
        assertEquals(VariablePlaceholderReplacer.replacePlaceholders("${flyway.placeholders.fpinfo.fpsak.schema.navn}"),
                "fpsak");
        assertEquals(VariablePlaceholderReplacer.replacePlaceholders("ingen"), "ingen");
        assertEquals(VariablePlaceholderReplacer.replacePlaceholders("${flyway.test}"), "hallo");
        assertThrows(IllegalStateException.class, () -> {
            VariablePlaceholderReplacer.replacePlaceholders("${ingen}");
        });
    }
}