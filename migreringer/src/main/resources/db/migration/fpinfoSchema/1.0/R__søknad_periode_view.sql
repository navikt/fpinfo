CREATE OR REPLACE VIEW SOEKNAD_PERIODE
AS
select
    fp.id                       as ID,
    fp.fom                      as FOM,
    fp.tom                      as TOM,
    fp.periode_type             as TREKKONTO,
    fp.arbeidsprosent           as ARBEIDSTIDPROSENT,
    fp.aarsak_type              as AARSAK,
    fp.kl_aarsak_type           as AARSAK_KODEVERK,
    fp.arbeidstaker             as ARBEIDSTAKER,
    fp.frilanser                as FRILANSER,
    fp.selvstendig              as SELVSTENDIG,
    fp.arbeidsgiver_orgnr       as ARBEIDSGIVER_ORGNR,
    fp.arbeidsgiver_aktor_id    as ARBEIDSGIVER_AKTOR_ID,
    fp.flerbarnsdager           as FLERBARNSDAGER,
    fp.mors_aktivitet           as MORS_AKTIVITET,
    fp.samtidig_uttaksprosent   as SAMTIDIG_UTTAKSPROSENT,
    b.id                        as BEHANDLING_ID
from fpsak.YF_FORDELING_PERIODE fp
    join fpsak.GR_YTELSES_FORDELING gryf on gryf.so_fordeling_id = fp.fordeling_id and gryf.aktiv = 'J'
    join fpsak.BEHANDLING b on b.id = gryf.behandling_id
;

GRANT SELECT ON SOEKNAD_PERIODE TO fpinfo;
