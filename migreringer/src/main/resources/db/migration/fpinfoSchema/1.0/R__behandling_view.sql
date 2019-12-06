CREATE OR REPLACE VIEW BEHANDLING
AS SELECT
     beh.ID AS BEHANDLING_ID
    ,beh.BEHANDLING_STATUS AS BEHANDLING_STATUS
    ,br.BEHANDLING_RESULTAT_TYPE AS BEHANDLING_RESULTAT_TYPE
    ,fs.YTELSE_TYPE AS FAGSAK_YTELSE_TYPE
    ,fs.SAKSNUMMER AS SAKSNUMMER
    ,fh.FAMILIE_HENDELSE_TYPE AS FAMILIE_HENDELSE_TYPE
    ,beh.BEHANDLENDE_ENHET AS BEHANDLENDE_ENHET
    ,beh.BEHANDLENDE_ENHET_NAVN AS BEHANDLENDE_ENHET_NAVN
    ,ba.BEHANDLING_ARSAK_TYPE AS BEHANDLING_ARSAK
    ,beh.OPPRETTET_AV AS OPPRETTET_AV
    ,beh.OPPRETTET_TID AS OPPRETTET_TID
    ,beh.ENDRET_AV AS ENDRET_AV
    ,beh.ENDRET_TID AS ENDRET_TID
  FROM
    ${fpinfo.fpsak.schema.navn}.BEHANDLING beh
    INNER JOIN ${fpinfo.fpsak.schema.navn}.FAGSAK fs ON beh.FAGSAK_ID = fs.id
    LEFT OUTER JOIN ${fpinfo.fpsak.schema.navn}.BEHANDLING_RESULTAT br ON beh.id = br.BEHANDLING_ID
    LEFT OUTER JOIN ${fpinfo.fpsak.schema.navn}.GR_FAMILIE_HENDELSE gfh ON beh.id = gfh.BEHANDLING_ID
    LEFT OUTER JOIN ${fpinfo.fpsak.schema.navn}.FH_FAMILIE_HENDELSE fh ON fh.id = NVL(gfh.OVERSTYRT_FAMILIE_HENDELSE_ID, NVL(gfh.BEKREFTET_FAMILIE_HENDELSE_ID, gfh.SOEKNAD_FAMILIE_HENDELSE_ID))
    LEFT OUTER JOIN ${fpinfo.fpsak.schema.navn}.BEHANDLING_ARSAK ba ON beh.id = ba.BEHANDLING_ID
  WHERE
    gfh.AKTIV = 'J' AND  fs.til_infotrygd = 'N';

GRANT SELECT ON BEHANDLING TO ${fpinfo.schema.navn};