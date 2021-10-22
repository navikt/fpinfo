CREATE
OR REPLACE VIEW BEHANDLING_ARSAK
  AS
SELECT DISTINCT ba.ID                    AS BEHANDLING_ARSAK_ID
              , ba.BEHANDLING_ARSAK_TYPE AS BEHANDLING_ARSAK_TYPE
              , ba.BEHANDLING_ID         AS BEHANDLING_ID
FROM fpsak.BEHANDLING_ARSAK ba;

GRANT SELECT ON BEHANDLING_ARSAK TO ${fpinfo.schema.navn};
