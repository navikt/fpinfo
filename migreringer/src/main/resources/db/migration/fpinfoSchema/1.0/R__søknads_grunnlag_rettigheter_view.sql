CREATE OR REPLACE VIEW SOEKNADS_GR_RETTIGHETER
AS
select gryf.id                  as GRYF_ID,
       SOSB.ANNEN_FORELDRE_RETT as SAKSB_ANNEN_FORELDER_HAR_RETT,
       SORE.ANNEN_FORELDRE_RETT as SO_ANNEN_FORELDER_RETT,
       SOSB.aleneomsorg         as SAKSB_ALENEOMSORG,
       SORE.aleneomsorg         as SO_ALENEOMSORG,
       SOSB.mor_uforetrygd      as SAKSB_UFORETRYGD,
       SORE.mor_uforetrygd      as SO_UFORETRYGD,
       SOSB.annen_forelder_rett_eos as SAKSB_ANNEN_FORELDER_RETT_EOS,
       SORE.annen_forelder_rett_eos as SO_ANNEN_FORELDER_RETT_EOS
from fpsak.gr_ytelses_fordeling gryf
         join fpsak.so_rettighet sore
              on sore.id = gryf.so_rettighet_id
         left outer join fpsak.so_rettighet sosb
              on sosb.id = gryf.overstyrt_rettighet_id
where gryf.aktiv = 'J';


GRANT SELECT ON SOEKNADS_GR_RETTIGHETER TO fpinfo;
