CREATE OR REPLACE VIEW SOEKNAD_GR
AS
select
    dbms_random.random          as RANDOM_ID,
    gryf.id                     as GRYF_ID,
    f.saksnummer                as SAKSNUMMER,
    b.id                        as BEHANDLING_ID,
    h.ANTALL_BARN               as ANTALL_BARN,
    h.FAMILIE_HENDELSE_TYPE     as FAMILIE_HENDELSE_TYPE,
    a.OMSORGSOVERTAKELSE_DATO   as OMSORGSOVERTAKELSE_DATO,
    tb.TERMINDATO               as TERMINDATO,
    ub.FOEDSEL_DATO             as FOEDSEL_DATO,
    f.BRUKER_ROLLE              as BRUKER_ROLLE,
    fr.DEKNINGSGRAD             as DEKNINGSGRAD,
    fp.MORS_AKTIVITET           as MORS_AKTIVITET_UFOERE,
    yf.ANNENFORELDERERINFORMERT as ANNENFORELDERERINFORMERT,
    yf.OENSKER_JUSTERT_VED_FOEDSEL as OENSKER_JUSTERT_UTTAK_VED_FOEDSEL
from fpsak.BEHANDLING b
         join fpsak.GR_FAMILIE_HENDELSE grfh on grfh.BEHANDLING_ID = b.ID and aktiv = 'J'
         join fpsak.FH_FAMILIE_HENDELSE h on h.ID = coalesce(grfh.OVERSTYRT_FAMILIE_HENDELSE_ID, grfh.BEKREFTET_FAMILIE_HENDELSE_ID, grfh.SOEKNAD_FAMILIE_HENDELSE_ID)
         join fpsak.GR_YTELSES_FORDELING gryf on gryf.BEHANDLING_ID = b.ID and gryf.AKTIV = 'J'
         join fpsak.YF_FORDELING yf ON gryf.SO_FORDELING_ID = yf.ID
         join fpsak.FAGSAK f on B.FAGSAK_ID = f.ID
         join fpsak.FAGSAK_RELASJON fr on f.ID in (fr.FAGSAK_EN_ID, fr.FAGSAK_TO_ID) and fr.AKTIV = 'J'
         left join fpsak.YF_FORDELING_PERIODE fp on fp.FORDELING_ID = coalesce(gryf.overstyrt_fordeling_id, gryf.justert_fordeling_id, gryf.SO_FORDELING_ID) and fp.MORS_AKTIVITET = 'UFØRE'
         left join fpsak.FH_ADOPSJON a on a.FAMILIE_HENDELSE_ID = h.ID
         left join fpsak.FH_TERMINBEKREFTELSE tb on tb.FAMILIE_HENDELSE_ID = h.ID
         left join fpsak.FH_UIDENTIFISERT_BARN ub on h.ID = ub.FAMILIE_HENDELSE_ID and ub.barn_nummer = '1' -- velger tilfeldig
where f.ytelse_type = 'FP';

GRANT SELECT ON SOEKNAD_GR TO fpinfo;
