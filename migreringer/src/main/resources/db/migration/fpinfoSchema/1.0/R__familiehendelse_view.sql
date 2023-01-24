CREATE OR REPLACE VIEW FAMILIE_HENDELSE
AS
select
    b.id                        as BEHANDLING_ID,
    h.ANTALL_BARN               as ANTALL_BARN,
    h.FAMILIE_HENDELSE_TYPE     as FAMILIE_HENDELSE_TYPE,
    a.OMSORGSOVERTAKELSE_DATO   as OMSORGSOVERTAKELSE_DATO,
    tb.TERMINDATO               as TERMINDATO,
    ub.FOEDSEL_DATO             as FOEDSEL_DATO
from fpsak.BEHANDLING b
         join fpsak.GR_FAMILIE_HENDELSE grfh on grfh.BEHANDLING_ID = b.ID and grfh.aktiv = 'J'
         join fpsak.FH_FAMILIE_HENDELSE h on h.ID = coalesce(grfh.OVERSTYRT_FAMILIE_HENDELSE_ID, grfh.BEKREFTET_FAMILIE_HENDELSE_ID, grfh.SOEKNAD_FAMILIE_HENDELSE_ID)
         left join fpsak.FH_ADOPSJON a on a.FAMILIE_HENDELSE_ID = h.ID
         left join fpsak.FH_TERMINBEKREFTELSE tb on tb.FAMILIE_HENDELSE_ID = h.ID
         left join fpsak.FH_UIDENTIFISERT_BARN ub on h.ID = ub.FAMILIE_HENDELSE_ID and ub.barn_nummer = '1' -- velger tilfeldig
;

GRANT SELECT ON FAMILIE_HENDELSE TO fpinfo;
