package no.nav.foreldrepenger.info.dbstoette;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.vedtak.felles.testutilities.db.RepositoryRule;

public class UnittestRepositoryRule extends RepositoryRule {

    private static final Logger log = LoggerFactory.getLogger(UnittestRepositoryRule.class);

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
