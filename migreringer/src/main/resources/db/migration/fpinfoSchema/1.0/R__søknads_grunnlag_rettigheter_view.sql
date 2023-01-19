CREATE OR REPLACE VIEW SOEKNADS_GR_RETTIGHETER
AS
select gryf.id                  as GRYF_ID,
       SBRE.ANNEN_FORELDRE_RETT as SB_ANNEN_FORELDER_RETT,
       SORE.ANNEN_FORELDRE_RETT as SO_ANNEN_FORELDER_RETT,
       SBRE.aleneomsorg         as SB_ALENEOMSORG,
       SORE.aleneomsorg         as SO_ALENEOMSORG,
       SBRE.mor_uforetrygd      as SB_UFORETRYGD,
       SORE.mor_uforetrygd      as SO_UFORETRYGD,
       SBRE.annen_forelder_rett_eos as SB_ANNEN_FORELDER_RETT_EOS,
       SORE.annen_forelder_rett_eos as SO_ANNEN_FORELDER_RETT_EOS
from fpsak.gr_ytelses_fordeling gryf
         join fpsak.so_rettighet sore
              on sore.id = gryf.so_rettighet_id
         left outer join fpsak.so_rettighet sbre
                         on sbre.id = gryf.overstyrt_rettighet_id
where gryf.aktiv = 'J';


GRANT SELECT ON SOEKNADS_GR_RETTIGHETER TO fpinfo;
