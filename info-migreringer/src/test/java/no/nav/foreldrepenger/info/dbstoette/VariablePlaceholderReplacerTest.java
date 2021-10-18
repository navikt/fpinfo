package no.nav.foreldrepenger.info.dbstoette;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class VariablePlaceholderReplacerTest {

    @Test
    void test() {
        System.setProperty("flyway.placeholders.fpinfoschema.schema.navn", "fpinfo_schema");
        assertThat(VariablePlaceholderReplacer.replacePlaceholders("${flyway.placeholders.fpinfoschema.schema.navn}")).isEqualTo("fpinfo_schema");
        assertThat(VariablePlaceholderReplacer.replacePlaceholders("ingen")).isEqualTo("ingen");
        //application-local.properties
        assertThat(VariablePlaceholderReplacer.replacePlaceholders("${flyway.test}")).isEqualTo("hallo");
        assertThrows(IllegalStateException.class, () -> {
            VariablePlaceholderReplacer.replacePlaceholders("${ingen}");
        });
    }
}
