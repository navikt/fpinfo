CREATE OR REPLACE VIEW SOEKNADS_GR_RETTIGHETER
AS
select gryf.id                  as GRYF_ID,
       (
           select coalesce(
                          (select CASE
                                      WHEN count(1) > 0
                                          THEN 'J' --rett dersom det eksisterer dokumentasjonsperioder tilknyttet gryf.annen_forelder_har_rett_id
                                      ELSE null
                                      END
                           from fpsak.YF_DOKUMENTASJON_PERIODE dp
                           where dp.perioder_id = gryf.annen_forelder_har_rett_id
                          ), (
                              select 'N' -- dersom annen_forelder_har_rett er vurdert, og det ikke eksisterer noen innslag i dokumentasjon_periode (i.e. avslag)
                              from dual
                              where gryf.annen_forelder_har_rett_id is not null
                                and not exists(
                                      select 1
                                      from fpsak.yf_dokumentasjon_periode yfdp
                                      where yfdp.perioder_id = gryf.annen_forelder_har_rett_id
                                  )
                          ))
           from dual)           as SAKSB_ANNEN_FORELDER_HAR_RETT,
       SORE.ANNEN_FORELDRE_RETT as SO_ANNEN_FORELDER_RETT,
       (
           select coalesce(
                          (select CASE
                                      WHEN count(1) > 0
                                          THEN 'J' -- rett dersom det eksisterer dokumentasjonsperioder tilknyttet gryf.aleneomsorg_id
                                      ELSE null
                                      END
                           from fpsak.YF_DOKUMENTASJON_PERIODE dp
                           where dp.perioder_id = GRYF.ALENEOMSORG_ID
                          ), (
                              select 'N' -- ikke rett dersom gryf.aleneomsorg_id er satt, men det ikke eksisterer innslag i yf_dokumentasjon_periode
                              from dual
                              where GRYF.ALENEOMSORG_ID is not null
                                and not exists(
                                      select 1
                                      from fpsak.yf_dokumentasjon_periode yfdp
                                      where yfdp.perioder_id = GRYF.ALENEOMSORG_ID
                                  )
                          ))
           from dual)           as SAKSB_ALENEOMSORG,
       SORE.aleneomsorg         as SO_ALENEOMSORG,
       SORE.mor_uforetrygd      as SO_UFORETRYGD
from fpsak.gr_ytelses_fordeling gryf
         join fpsak.so_rettighet sore
              on sore.id = gryf.so_rettighet_id
where gryf.aktiv = 'J';


GRANT SELECT ON SOEKNADS_GR_RETTIGHETER TO ${fpinfo.schema.navn};
