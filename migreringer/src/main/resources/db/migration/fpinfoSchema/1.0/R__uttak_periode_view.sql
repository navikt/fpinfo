CREATE OR REPLACE VIEW UTTAK_PERIODE
AS SELECT
       dbms_random.random         as RANDOM_ID,
       br.BEHANDLING_ID           as BEHANDLING_ID,
       rp.PERIODE_RESULTAT_TYPE   AS PERIODE_RESULTAT_TYPE,
       rp.GRADERING_AVSLAG_AARSAK as GRADERING_AVSLAG_AARSAK,
       rp.UTTAK_UTSETTELSE_TYPE   as UTTAK_UTSETTELSE_TYPE,
       rp.FLERBARNSDAGER          as FLERBARNSDAGER,
       rp.GRADERING_INNVILGET     as GRADERING_INNVILGET,
       rp.MANUELT_BEHANDLET       as MANUELT_BEHANDLET,
       rp.SAMTIDIG_UTTAK          as SAMTIDIG_UTTAK,
       rp.FOM                     as FOM,
       rp.TOM                     as TOM,
       pa.TREKKONTO               as TREKKONTO,
       pa.TREKKDAGER_DESIMALER    as TREKKDAGER,
       pa.ARBEIDSTIDSPROSENT      as ARBEIDSTIDSPROSENT,
       pa.UTBETALINGSPROSENT      as UTBETALINGSPROSENT,
       rp.SAMTIDIG_UTTAKSPROSENT  as SAMTIDIG_UTTAKSPROSENT,
       rp.OPPHOLD_AARSAK          as OPPHOLD_AARSAK,
       rp.OVERFOERING_AARSAK      as OVERFOERING_AARSAK,
       urps.MORS_AKTIVITET        as MORS_AKTIVITET,
       ua.UTTAK_ARBEID_TYPE       as UTTAK_ARBEID_TYPE,
       ua.ARBEIDSGIVER_AKTOR_ID   as ARBEIDSGIVER_AKTOR_ID,
       ua.ARBEIDSGIVER_ORGNR      as ARBEIDSGIVER_ORGNR
   FROM ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE rp
            LEFT JOIN ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE_AKT pa ON rp.ID = pa.UTTAK_RESULTAT_PERIODE_ID
            LEFT JOIN ${fpinfo.fpsak.schema.navn}.UTTAK_AKTIVITET ua on pa.UTTAK_AKTIVITET_ID = ua.ID
            left JOIN ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODER rps ON rp.UTTAK_RESULTAT_PERIODER_ID = rps.ID
            LEFT JOIN ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE_SOKNAD urps on rp.PERIODE_SOKNAD_ID = urps.ID
            LEFT JOIN ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT ur on rps.id = coalesce(ur.OVERSTYRT_PERIODER_ID, ur.OPPRINNELIG_PERIODER_ID)
            LEFT JOIN ${fpinfo.fpsak.schema.navn}.BEHANDLING_RESULTAT br ON ur.BEHANDLING_RESULTAT_ID = br.ID
   WHERE ur.AKTIV = 'J';

GRANT SELECT ON UTTAK_PERIODE TO ${fpinfo.schema.navn};

