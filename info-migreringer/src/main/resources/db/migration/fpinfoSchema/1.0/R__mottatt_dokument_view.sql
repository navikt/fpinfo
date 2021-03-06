CREATE OR REPLACE VIEW MOTTATT_DOKUMENT
  AS SELECT
            beh.ID AS BEHANDLING_ID
         ,beh.BEHANDLING_STATUS AS BEHANDLING_STATUS
         ,beh.OPPRETTET_AV AS OPPRETTET_AV
         ,beh.OPPRETTET_TID AS OPPRETTET_TID
         ,beh.ENDRET_AV AS ENDRET_AV
         ,beh.ENDRET_TID AS ENDRET_TID
         ,md.ID AS MOTTATT_DOKUMENT_ID
         ,md.FORSENDELSE_ID AS FORSENDELSE_ID
         ,md.XML_PAYLOAD AS SOEKNAD_XML
         ,md.JOURNALPOST_ID AS JOURNALPOST_ID
         ,md.TYPE AS TYPE
         ,fs.SAKSNUMMER AS SAKSNUMMER
     FROM
          ${fpinfo.fpsak.schema.navn}.MOTTATT_DOKUMENT md
            left join  ${fpinfo.fpsak.schema.navn}.BEHANDLING beh on md.BEHANDLING_ID = beh.ID
            left JOIN ${fpinfo.fpsak.schema.navn}.FAGSAK fs ON md.FAGSAK_ID = fs.ID;

GRANT SELECT ON MOTTATT_DOKUMENT TO ${fpinfo.schema.navn};
