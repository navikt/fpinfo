package no.nav.foreldrepenger.info.dbstoette;

import java.util.Arrays;

class Wrapper {
    public Schema[] schemas;

    @Override
    public String toString() {
        return "Wrapper [schemas=" + Arrays.toString(schemas) + "]";
    }
}