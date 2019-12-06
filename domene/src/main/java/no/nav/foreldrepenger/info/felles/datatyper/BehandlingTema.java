package no.nav.foreldrepenger.info.felles.datatyper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableMap;

public class BehandlingTema {

    private static final String KEY_ADOPSJON = "adopsjon";
    private static final String KEY_FØDSEL = "fødsel";
    private static final String KEY_ENGANGSSTØNAD = "engangsstønad";
    private static final String KEY_FORELDREPENGER = "foreldrepenger";

    private static final ImmutableMap<String, List<String>> HENDELSER = ImmutableMap.of(
            KEY_ADOPSJON, Collections.unmodifiableList(Arrays.asList(FamilieHendelseType.ADOPSJON.getVerdi(), FamilieHendelseType.OMSORGOVERDRAGELSE.getVerdi())),
            KEY_FØDSEL, Collections.unmodifiableList(Arrays.asList(FamilieHendelseType.FØDSEL.getVerdi(), FamilieHendelseType.TERMIN.getVerdi()))
    );

    private static final ImmutableMap<String, List<String>> YTELSE_TYPER = ImmutableMap.of(
            KEY_ENGANGSSTØNAD, Collections.unmodifiableList(Arrays.asList(FagsakYtelseType.ES.getVerdi())),
            KEY_FORELDREPENGER, Collections.unmodifiableList(Arrays.asList(FagsakYtelseType.FP.getVerdi(), FagsakYtelseType.ENDRING_FP.getVerdi()))
    );

    public static final String ENGANGSTØNAD = "ENGST";
    public static final String ENGANGSTØNAD_FØDSEL = "ENGST_FODS";
    public static final String ENGANGSTØNAD_ADOPSJON = "ENGST_ADOP";
    public static final String FORELDREPENGER = "FORP";
    public static final String FORELDREPENGER_ADOPSJON = "FORP_ADOP";
    public static final String FORELDREPENGER_FØDSEL = "FORP_FODS";
    public static final String UDEFINERT = "-";

    private BehandlingTema() {

    }

    public static String fraYtelse(String ytelseType, String hendelse) {
        if (gjelderEngangstønad(ytelseType)) {
            if (gjelderAdopsjon(hendelse)) {
                return ENGANGSTØNAD_ADOPSJON;
            } else if (gjelderFødsel(ytelseType)) {
                return ENGANGSTØNAD_FØDSEL;
            }
            return ENGANGSTØNAD;
        } else if (gjelderForeldrepenger(ytelseType)) {
            if (gjelderAdopsjon(hendelse)) {
                return FORELDREPENGER_ADOPSJON;
            } else if (gjelderFødsel(hendelse)) {
                return FORELDREPENGER_FØDSEL;
            }
            return FORELDREPENGER;
        }
        return UDEFINERT;
    }

    protected static boolean gjelderAdopsjon(String hendelse) {
        return HENDELSER.get(KEY_ADOPSJON).contains(hendelse);
    }

    protected static boolean gjelderFødsel(String hendelse) {
        return HENDELSER.get(KEY_FØDSEL).contains(hendelse);
    }

    protected static boolean gjelderEngangstønad(String ytelseType) {
        return YTELSE_TYPER.get(KEY_ENGANGSSTØNAD).contains(ytelseType);
    }

    protected static boolean gjelderForeldrepenger(String ytelseType) {
        return YTELSE_TYPER.get(KEY_FORELDREPENGER).contains(ytelseType);
    }
}

