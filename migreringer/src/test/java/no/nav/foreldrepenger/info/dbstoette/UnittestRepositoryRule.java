package no.nav.foreldrepenger.info.dbstoette;

import no.nav.vedtak.felles.testutilities.db.RepositoryRule;

public class UnittestRepositoryRule extends RepositoryRule {

    static {
        Databaseskjemainitialisering.kjørMigreringHvisNødvendig();
    }

    public UnittestRepositoryRule() {
        super();
    }

    public UnittestRepositoryRule(boolean transaksjonell) {
        super(transaksjonell);
    }

    @Override
    protected void init() {
    }

}
