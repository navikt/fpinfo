package no.nav.foreldrepenger.info.web.app.tjenester.dto;

import static no.nav.vedtak.sikkerhet.abac.StandardAbacAttributtType.AKTØR_ID;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import no.nav.foreldrepenger.info.domene.Sak;
import no.nav.foreldrepenger.info.web.app.ResourceLink;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;

public class SakDto implements AbacDto {

    private String saksnummer;
    private String fagsakStatus;
    private String behandlingTema;
    private String aktørId;
    private String aktørIdAnnenPart;
    private Set<String> aktørIdBarna = new HashSet<>();
    private List<ResourceLink> lenker = new ArrayList<>();
    private LocalDateTime opprettetTidspunkt;
    private LocalDateTime endretTidspunkt;
    private boolean mottattEndringssøknad;

    public SakDto() {
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

    public boolean isMottattEndringssøknad() {
        return mottattEndringssøknad;
    }

    public static SakDto fraDomene(Sak sak, boolean mottattEndringssøknad) {
        SakDto dto = new SakDto();
        dto.saksnummer = sak.getSaksnummer();
        dto.fagsakStatus = sak.getFagsakStatus();
        dto.aktørId = sak.getAktørId();
        dto.aktørIdAnnenPart = sak.getAktørIdAnnenPart();
        dto.behandlingTema = sak.getBehandlingstema();
        dto.opprettetTidspunkt = sak.getOpprettetTidspunkt();
        dto.endretTidspunkt = sak.getEndretTidspunkt();
        dto.mottattEndringssøknad = mottattEndringssøknad;
        return dto;
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett().leggTil(AKTØR_ID, aktørId);
    }
}
