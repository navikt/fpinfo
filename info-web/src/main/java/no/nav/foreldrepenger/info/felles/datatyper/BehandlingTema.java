package no.nav.foreldrepenger.info.felles.datatyper;

import static no.nav.foreldrepenger.info.felles.datatyper.FagsakYtelseType.ENDRING_FP;
import static no.nav.foreldrepenger.info.felles.datatyper.FagsakYtelseType.ES;
import static no.nav.foreldrepenger.info.felles.datatyper.FagsakYtelseType.FP;
import static no.nav.foreldrepenger.info.felles.datatyper.FamilieHendelseType.ADOPSJON;
import static no.nav.foreldrepenger.info.felles.datatyper.FamilieHendelseType.FØDSEL;
import static no.nav.foreldrepenger.info.felles.datatyper.FamilieHendelseType.OMSORGOVERDRAGELSE;
import static no.nav.foreldrepenger.info.felles.datatyper.FamilieHendelseType.TERMIN;

import java.util.List;
import java.util.Map;

public class BehandlingTema {

    private static final String KEY_ADOPSJON = "adopsjon";
    private static final String KEY_FØDSEL = "fødsel";
    private static final String KEY_ENGANGSSTØNAD = "engangsstønad";
    private static final String KEY_FORELDREPENGER = "foreldrepenger";

    private static final Map<String, List<String>> HENDELSER = Map.of(
            KEY_ADOPSJON,
            List.of(ADOPSJON.getVerdi(),
                    OMSORGOVERDRAGELSE.getVerdi()),
            KEY_FØDSEL,
            List.of(FØDSEL.getVerdi(),
                    TERMIN.getVerdi()));

    private static final Map<String, List<String>> YTELSE_TYPER = Map.of(
            KEY_ENGANGSSTØNAD,
            List.of(ES.name()),
            KEY_FORELDREPENGER,
            List.of(FP.name(),
                    ENDRING_FP.name()));

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
            }
            if (gjelderFødsel(ytelseType)) {
                return ENGANGSTØNAD_FØDSEL;
            }
            return ENGANGSTØNAD;
        }
        if (gjelderForeldrepenger(ytelseType)) {
            if (gjelderAdopsjon(hendelse)) {
                return FORELDREPENGER_ADOPSJON;
            }
            if (gjelderFødsel(hendelse)) {
                return FORELDREPENGER_FØDSEL;
            }
            return FORELDREPENGER;
        }
        return UDEFINERT;
    }

    protected static boolean gjelderAdopsjon(String hendelse) {
        return hendelse != null && HENDELSER.get(KEY_ADOPSJON).contains(hendelse);
    }

    protected static boolean gjelderFødsel(String hendelse) {
        return hendelse != null && HENDELSER.get(KEY_FØDSEL).contains(hendelse);
    }

    protected static boolean gjelderEngangstønad(String ytelseType) {
        return YTELSE_TYPER.get(KEY_ENGANGSSTØNAD).contains(ytelseType);
    }

    protected static boolean gjelderForeldrepenger(String ytelseType) {
        return YTELSE_TYPER.get(KEY_FORELDREPENGER).contains(ytelseType);
    }
}
