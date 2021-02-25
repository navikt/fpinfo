CREATE OR REPLACE VIEW SAK_STATUS
AS SELECT
       dbms_random.random as RANDOM_ID,
       fs.saksnummer AS SAKSNUMMER,
       br.aktoer_id as HOVED_SOEKER_AKTOER_ID,
       beh.id as BEHANDLING_ID,
       fs.fagsak_status AS FAGSAK_STATUS,
       fs.ytelse_type as YTELSE_TYPE,
       por.TIL_AKTOER_ID as BARN_AKTOER_ID,
       sa.aktoer_id as ANPA_AKTØRID,
       ffh.familie_hendelse_type AS familie_hendelse_type,
       beh.OPPRETTET_AV AS OPPRETTET_AV,
       beh.OPPRETTET_TID AS OPPRETTET_TID,
       beh.ENDRET_AV AS ENDRET_AV,
       beh.ENDRET_TID AS ENDRET_TID
   FROM
       ${fpinfo.fpsak.schema.navn}.FAGSAK fs
           JOIN ${fpinfo.fpsak.schema.navn}.BRUKER br on br.id = fs.BRUKER_ID
           LEFT JOIN ${fpinfo.fpsak.schema.navn}.BEHANDLING beh ON fs.id = beh.fagsak_id
           LEFT JOIN ${fpinfo.fpsak.schema.navn}.GR_PERSONOPPLYSNING grp ON beh.id = grp.behandling_id and grp.aktiv = 'J'
           LEFT JOIN ${fpinfo.fpsak.schema.navn}.PO_INFORMASJON poi ON poi.id = NVL(grp.OVERSTYRT_INFORMASJON_ID, grp.REGISTRERT_INFORMASJON_ID)
           LEFT JOIN ${fpinfo.fpsak.schema.navn}.PO_RELASJON por ON por.po_informasjon_id = poi.id AND por.relasjonsrolle = 'BARN' AND por.FRA_AKTOER_ID = br.AKTOER_ID
           LEFT JOIN ${fpinfo.fpsak.schema.navn}.SO_ANNEN_PART sa ON grp.so_annen_part_id = sa.id
           LEFT JOIN ${fpinfo.fpsak.schema.navn}.GR_FAMILIE_HENDELSE gfh ON gfh.behandling_id  = beh.id and gfh.aktiv = 'J'
           LEFT JOIN ${fpinfo.fpsak.schema.navn}.FH_FAMILIE_HENDELSE ffh on ffh.id = NVL(gfh.OVERSTYRT_FAMILIE_HENDELSE_ID, NVL(gfh.BEKREFTET_FAMILIE_HENDELSE_ID, gfh.SOEKNAD_FAMILIE_HENDELSE_ID))
   WHERE
           fs.til_infotrygd = 'N';


GRANT SELECT ON SAK_STATUS TO ${fpinfo.schema.navn};
