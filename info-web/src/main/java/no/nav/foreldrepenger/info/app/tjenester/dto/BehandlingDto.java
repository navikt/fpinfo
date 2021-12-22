package no.nav.foreldrepenger.info.app.tjenester.dto;

import static no.nav.foreldrepenger.sikkerhet.abac.domene.StandardAbacAttributtType.BEHANDLING_ID;

import java.time.LocalDateTime;
import java.util.List;

import no.nav.foreldrepenger.info.Behandling;
import no.nav.foreldrepenger.info.datatyper.BehandlingTema;
import no.nav.foreldrepenger.sikkerhet.abac.AbacDto;
import no.nav.foreldrepenger.sikkerhet.abac.domene.AbacDataAttributter;

public class BehandlingDto implements AbacDto {

    private Long behandlingId;
    private String status;
    private String type;
    private String tema;
    private String behandlendeEnhet;
    private String behandlendeEnhetNavn;
    private List<String> inntektsmeldinger;
    private LocalDateTime opprettetTidspunkt;
    private LocalDateTime endretTidspunkt;
    private String behandlingResultat;

    public String getBehandlingResultat() {
        return behandlingResultat;
    }

    public String getStatus() {
        return status;
    }

    public String getType() {
        return type;
    }

    public String getTema() {
        return tema;
    }

    public String getBehandlendeEnhet() {
        return behandlendeEnhet;
    }

    public String getBehandlendeEnhetNavn() {
        return behandlendeEnhetNavn;
    }

    public LocalDateTime getOpprettetTidspunkt() {
        return opprettetTidspunkt;
    }

    public LocalDateTime getEndretTidspunkt() {
        return endretTidspunkt;
    }

    public List<String> getInntektsmeldinger() {
        return inntektsmeldinger;
    }

    public void setInntektsmeldinger(List<String> inntektsmeldinger) {
        this.inntektsmeldinger = inntektsmeldinger;
    }

    public static BehandlingDto fraDomene(Behandling behandling) {
        BehandlingDto dto = new BehandlingDto();
        dto.behandlingId = behandling.getBehandlingId();
        dto.status = behandling.getBehandlingStatus();
        dto.type = behandling.getFagsakYtelseType();
        dto.behandlendeEnhet = behandling.getBehandlendeEnhet();
        dto.behandlendeEnhetNavn = behandling.getBehandlendeEnhetNavn();
        dto.tema = BehandlingTema.fraYtelse(behandling.getFagsakYtelseType(), behandling.getFamilieHendelseType());
        dto.opprettetTidspunkt = behandling.getOpprettetTidspunkt();
        dto.endretTidspunkt = behandling.getEndretTidspunkt();
        dto.behandlingResultat = behandling.getBehandlingResultatType();

        return dto;
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett().leggTil(BEHANDLING_ID, behandlingId);
    }
}
