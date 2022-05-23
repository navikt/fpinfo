package no.nav.foreldrepenger.info.dbstoette;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class VariablePlaceholderReplacerTest {

    @Test
    void test() {
        System.setProperty("flyway.placeholders.fpinfo.fpsak.schema.navn", "fpsak");
        System.setProperty("flyway.placeholders.fpinfoschema.schema.navn", "fpinfo_schema");
        System.setProperty("flyway.placeholders.fpinfo.schema.navn", "fpinfo");
        assertThat(VariablePlaceholderReplacer.replacePlaceholders("${flyway.placeholders.fpinfo.schema.navn}")).isEqualTo("fpinfo");
        assertThat(VariablePlaceholderReplacer.replacePlaceholders("${flyway.placeholders.fpinfoschema.schema.navn}")).isEqualTo("fpinfo_schema");
        assertThat(VariablePlaceholderReplacer.replacePlaceholders("${flyway.placeholders.fpinfo.fpsak.schema.navn}")).isEqualTo("fpsak");
        assertThat(VariablePlaceholderReplacer.replacePlaceholders("ingen")).isEqualTo("ingen");
        //application-local.properties
        assertThat(VariablePlaceholderReplacer.replacePlaceholders("${flyway.test}")).isEqualTo("hallo");
        assertThrows(IllegalStateException.class, () -> {
            VariablePlaceholderReplacer.replacePlaceholders("${ingen}");
        });
    }
}