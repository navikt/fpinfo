package no.nav.foreldrepenger.info.web.app.selftest.checks;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.TimeZone;

import javax.naming.NameNotFoundException;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import no.nav.foreldrepenger.info.web.EntityManagerFPInfoAwareExtension;
import no.nav.foreldrepenger.info.web.app.selftest.checks.ExtHealthCheck.InternalResult;
import no.nav.vedtak.felles.testutilities.db.EntityManagerAwareTest;

@ExtendWith(EntityManagerFPInfoAwareExtension.class)
@Disabled
public class DatabaseHealthCheckTest extends EntityManagerAwareTest {

    static {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Oslo"));
    }

    @Test
    public void test_check_healthy() {
        DatabaseHealthCheck dbCheck = new DatabaseHealthCheck();

        InternalResult result = dbCheck.performCheck();
        assertThat(result.isOk()).as(result.getMessage()).isTrue();
        assertThat(result.getResponseTimeMs()).isNotNull();
    }

    @Test
    public void skal_feile_pga_ukjent_jndi_name() {
        DatabaseHealthCheck dbCheck = new DatabaseHealthCheck("jndi/ukjent");

        InternalResult result = dbCheck.performCheck();

        assertThat(result.isOk()).isFalse();
        assertThat(result.getMessage()).contains("Feil ved JNDI-oppslag for jndi/ukjent");
        assertThat(result.getException()).isInstanceOf(NameNotFoundException.class);
    }

}
