package no.nav.foreldrepenger.info.tjenester.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import no.nav.foreldrepenger.info.domene.Behandling;
import no.nav.foreldrepenger.info.felles.datatyper.BehandlingTema;
import no.nav.foreldrepenger.info.felles.rest.ResourceLink;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;

public class BehandlingDto implements AbacDto {

    private Long behandlingId;
    private String status;
    private String type;
    private String tema;
    private String årsak;
    private String behandlendeEnhet;
    private String behandlendeEnhetNavn;
    private List<String> inntektsmeldinger;
    private List<ResourceLink> lenker;
    private LocalDateTime opprettetTidspunkt;
    private LocalDateTime endretTidspunkt;
    private String behandlingResultat;

    public BehandlingDto() {
        lenker = new ArrayList<>();
    }

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

    public String getÅrsak() {
        return årsak;
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

    public List<ResourceLink> getLenker() {
        return lenker;
    }

    public void leggTilLenke(String href, String rel) {
        if (href != null) {
            lenker.add(ResourceLink.get(href, rel, null));
        }
    }

    public static BehandlingDto fraDomene(Behandling behandling) {
        BehandlingDto dto = new BehandlingDto();
        dto.behandlingId = behandling.getBehandlingId();
        dto.status = behandling.getBehandlingStatus();
        dto.årsak = behandling.getBehandlingÅrsak();
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
        return AbacDataAttributter.opprett().leggTilBehandlingsId(behandlingId);
    }
}
