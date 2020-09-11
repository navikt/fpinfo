package no.nav.foreldrepenger.info.web;

import no.nav.foreldrepenger.info.dbstoette.Databaseskjemainitialisering;
import no.nav.vedtak.felles.testutilities.db.EntityManagerAwareExtension;

public class EntityManagerFPInfoAwareExtension extends EntityManagerAwareExtension {

    @Override
    protected void init() {
        Databaseskjemainitialisering.kjørMigreringHvisNødvendig();

    }
}