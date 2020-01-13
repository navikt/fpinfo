package no.nav.foreldrepenger.info.tjenester.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import no.nav.foreldrepenger.info.abac.AppAbacAttributtType;
import no.nav.foreldrepenger.info.domene.SakStatus;
import no.nav.foreldrepenger.info.felles.rest.ResourceLink;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;

public class SakStatusDto implements AbacDto {

    private String saksnummer;
    private String fagsakStatus;
    private String behandlingTema;
    private String aktørId;
    private String aktørIdAnnenPart;
    private Set<String> aktørIdBarna = new HashSet<>();
    private List<ResourceLink> lenker = new ArrayList<>();
    private LocalDateTime opprettetTidspunkt;
    private LocalDateTime endretTidspunkt;

    public SakStatusDto() {
    }

    public String getSaksnummer() {
        return saksnummer;
    }

    public String getFagsakStatus() {
        return fagsakStatus;
    }

    public String getAktørId() {
        return aktørId;
    }

    public String getAktørIdAnnenPart() {
        return aktørIdAnnenPart;
    }

    public Set<String> getAktørIdBarna() {
        return aktørIdBarna;
    }

    public String getBehandlingTema() {
        return behandlingTema;
    }

    public LocalDateTime getOpprettetTidspunkt() {
        return opprettetTidspunkt;
    }

    public LocalDateTime getEndretTidspunkt() {
        return endretTidspunkt;
    }

    public List<ResourceLink> getLenker() {
        return lenker;
    }

    public void leggTilLenke(String href, String rel) {
        if (href != null) {
            lenker.add(ResourceLink.get(href, rel, null));
        }
    }

    public void leggTilBarn(String barnAktørId) {
        if (barnAktørId != null) {
            aktørIdBarna.add(barnAktørId);
        }
    }

    public static SakStatusDto fraDomene(SakStatus sakStatus) {
        SakStatusDto dto = new SakStatusDto();
        dto.saksnummer = sakStatus.getSaksnummer();
        dto.fagsakStatus = sakStatus.getFagsakStatus();
        dto.aktørId = sakStatus.getAktørId();
        dto.aktørIdAnnenPart = sakStatus.getAktørIdAnnenPart();
        dto.behandlingTema = sakStatus.getBehandlingstema();
        dto.opprettetTidspunkt = sakStatus.getOpprettetTidspunkt();
        dto.endretTidspunkt = sakStatus.getEndretTidspunkt();
        return dto;
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett().leggTil(AppAbacAttributtType.AKTØR_ID, aktørId);
    }
}
