package no.nav.foreldrepenger.info.web.app.tjenester.dto;

import static no.nav.foreldrepenger.sikkerhet.abac.domene.StandardAbacAttributtType.SAKSNUMMER;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import no.nav.foreldrepenger.info.domene.Saksnummer;
import no.nav.foreldrepenger.info.domene.UttakPeriode;
import no.nav.foreldrepenger.sikkerhet.abac.AbacDto;
import no.nav.foreldrepenger.sikkerhet.abac.domene.AbacDataAttributter;

public class UttaksPeriodeDto implements AbacDto {

    @JsonIgnore
    private String saksnummer;

    private String periodeResultatType;
    private Boolean graderingInnvilget;
    private Boolean samtidigUttak;
    private LocalDate fom;
    private LocalDate tom;
    private String trekkonto;
    private BigDecimal trekkdager;
    private Long arbeidstidprosent;
    private Long utbetalingsprosent;
    private Boolean gjelderAnnenPart;
    private Boolean flerbarnsdager;
    private Boolean manueltBehandlet;
    private String morsAktivitet;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String utsettelsePeriodeType;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String graderingAvslagAarsak;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long samtidigUttaksprosent;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String oppholdAarsak;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String overfoeringAarsak;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String uttakArbeidType;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String arbeidsgiverAktoerId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String arbeidsgiverOrgnr;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String periodeResultatÅrsak;

    public String getPeriodeResultatType() {
        return periodeResultatType;
    }

    public UttaksPeriodeDto setPeriodeResultatType(String periodeResultatType) {
        this.periodeResultatType = periodeResultatType;
        return this;
    }

    public static UttaksPeriodeDto fraDomene(Saksnummer saksnummer, UttakPeriode uttakPeriode,
            boolean gjelderAnnenPart) {
        UttaksPeriodeDto dto = new UttaksPeriodeDto();
        dto.gjelderAnnenPart = gjelderAnnenPart;
        dto.saksnummer = saksnummer.saksnummer();
        dto.periodeResultatType = dash2null(uttakPeriode.getPeriodeResultatType());
        dto.fom = uttakPeriode.getFom();
        dto.tom = uttakPeriode.getTom();
        dto.trekkonto = dash2null(uttakPeriode.getTrekkonto());
        dto.trekkdager = uttakPeriode.getTrekkdager();
        dto.utbetalingsprosent = uttakPeriode.getUtbetalingsprosent();
        dto.utsettelsePeriodeType = dash2null(uttakPeriode.getUttakUtsettelseType());
        dto.flerbarnsdager = uttakPeriode.getFlerbarnsdager();
        dto.manueltBehandlet = uttakPeriode.getManueltBehandlet();
        dto.samtidigUttak = uttakPeriode.getSamtidigUttak();
        dto.samtidigUttaksprosent = uttakPeriode.getSamtidigUttaksprosent();
        if (!uttakPeriode.getSamtidigUttak()) { // frontend vil ikke ha detaljer om gradering ved samtidigUttak
            dto.graderingInnvilget = uttakPeriode.getGraderingInnvilget();
            dto.arbeidstidprosent = uttakPeriode.getArbeidstidprosent();
            dto.graderingAvslagAarsak = uttakPeriode.getGraderingAvslagAarsak();
        }
        dto.arbeidsgiverAktoerId = uttakPeriode.getArbeidsgiverAktørId();
        dto.arbeidsgiverOrgnr = uttakPeriode.getArbeidsgiverOrgnr();
        dto.oppholdAarsak = dash2null(uttakPeriode.getOppholdÅrsak());
        dto.overfoeringAarsak = dash2null(uttakPeriode.getOverføringÅrsak());
        dto.uttakArbeidType = uttakPeriode.getUttakArbeidType();
        dto.morsAktivitet = dash2null(uttakPeriode.getMorsAktivitet());
        dto.periodeResultatÅrsak = dash2null(uttakPeriode.getPeriodeResultatÅrsak());
        return dto;
    }

    private static String dash2null(String domeneString) {
        return "-".equals(domeneString) ? null : domeneString;
    }

    public Boolean getGraderingInnvilget() {
        return graderingInnvilget;
    }

    public Boolean getSamtidigUttak() {
        return samtidigUttak;
    }

    public LocalDate getFom() {
        return fom;
    }

    public LocalDate getTom() {
        return tom;
    }

    public String getTrekkonto() {
        return trekkonto;
    }

    public BigDecimal getTrekkdager() {
        return trekkdager;
    }

    public Long getArbeidstidprosent() {
        return arbeidstidprosent;
    }

    public Long getUtbetalingsprosent() {
        return utbetalingsprosent;
    }

    public Boolean getGjelderAnnenPart() {
        return gjelderAnnenPart;
    }

    public String getUtsettelsePeriodeType() {
        return utsettelsePeriodeType;
    }

    public Boolean getFlerbarnsdager() {
        return flerbarnsdager;
    }

    public Boolean getManueltBehandlet() {
        return manueltBehandlet;
    }

    public String getGraderingAvslagAarsak() {
        return graderingAvslagAarsak;
    }

    public Long getSamtidigUttaksprosent() {
        return samtidigUttaksprosent;
    }

    public String getOppholdAarsak() {
        return oppholdAarsak;
    }

    public String getOverfoeringAarsak() {
        return overfoeringAarsak;
    }

    public String getUttakArbeidType() {
        return uttakArbeidType;
    }

    public String getArbeidsgiverAktoerId() {
        return arbeidsgiverAktoerId;
    }

    public String getArbeidsgiverOrgnr() {
        return arbeidsgiverOrgnr;
    }

    public String getMorsAktivitet() {
        return morsAktivitet;
    }

    public String getPeriodeResultatÅrsak() {
        return periodeResultatÅrsak;
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett().leggTil(SAKSNUMMER, saksnummer);
    }
}
