package no.nav.foreldrepenger.info.web;

import java.util.TimeZone;

import no.nav.foreldrepenger.info.dbstoette.Databaseskjemainitialisering;
import no.nav.vedtak.felles.testutilities.db.EntityManagerAwareExtension;

public class EntityManagerFPInfoAwareExtension extends EntityManagerAwareExtension {

    @Override
    protected void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Oslo"));
        Databaseskjemainitialisering.kjørMigreringHvisNødvendig();
    }
}