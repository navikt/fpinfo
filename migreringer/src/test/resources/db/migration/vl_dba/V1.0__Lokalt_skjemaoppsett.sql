-- ##################################################
-- ### Opplegg for enhetstester (lokal + jenkins) ###
-- ##################################################

DECLARE

  FUNCTION f_schema_exists(schema_navn VARCHAR2)
    RETURN BOOLEAN IS
    userexists INTEGER := 0;
    BEGIN
      SELECT count(*)
          INTO userexists FROM SYS.ALL_USERS WHERE USERNAME = upper(schema_navn);
      IF userexists > 0
      THEN
        RETURN TRUE;
      ELSE
        RETURN FALSE;
      END IF;
    END;

  PROCEDURE p_opprett_hvis_ikke_funnet(schema_navn IN VARCHAR2) IS
    BEGIN
      IF (NOT f_schema_exists(schema_navn))
      THEN
        EXECUTE IMMEDIATE ('CREATE USER ' || schema_navn || ' IDENTIFIED BY ' || schema_navn);
        EXECUTE IMMEDIATE (
          'GRANT CONNECT, RESOURCE, CREATE JOB, CREATE TABLE, CREATE SYNONYM, CREATE VIEW, CREATE MATERIALIZED VIEW TO '
          || schema_navn);
      END IF;
    END;

  PROCEDURE p_opprett_permissions IS
    BEGIN
        EXECUTE IMMEDIATE ('GRANT CONNECT TO ${fpinfo.fpsak.schema.navn}');
        EXECUTE IMMEDIATE ('GRANT SELECT ON ${fpinfo.fpsak.schema.navn}.FAGSAK                      TO  ${fpinfoschema.schema.navn} WITH GRANT OPTION');
        EXECUTE IMMEDIATE ('GRANT SELECT ON ${fpinfo.fpsak.schema.navn}.FH_FAMILIE_HENDELSE         TO  ${fpinfoschema.schema.navn} WITH GRANT OPTION');
        EXECUTE IMMEDIATE ('GRANT SELECT ON ${fpinfo.fpsak.schema.navn}.GR_PERSONOPPLYSNING         TO  ${fpinfoschema.schema.navn} WITH GRANT OPTION');
        EXECUTE IMMEDIATE ('GRANT SELECT ON ${fpinfo.fpsak.schema.navn}.PO_INFORMASJON              TO  ${fpinfoschema.schema.navn} WITH GRANT OPTION');
        EXECUTE IMMEDIATE ('GRANT SELECT ON ${fpinfo.fpsak.schema.navn}.PO_RELASJON                 TO  ${fpinfoschema.schema.navn} WITH GRANT OPTION');
        EXECUTE IMMEDIATE ('GRANT SELECT ON ${fpinfo.fpsak.schema.navn}.SO_ANNEN_PART               TO  ${fpinfoschema.schema.navn} WITH GRANT OPTION');
        EXECUTE IMMEDIATE ('GRANT SELECT ON ${fpinfo.fpsak.schema.navn}.GR_FAMILIE_HENDELSE         TO  ${fpinfoschema.schema.navn} WITH GRANT OPTION');
        EXECUTE IMMEDIATE ('GRANT SELECT ON ${fpinfo.fpsak.schema.navn}.BRUKER                      TO  ${fpinfoschema.schema.navn} WITH GRANT OPTION');
        EXECUTE IMMEDIATE ('GRANT SELECT ON ${fpinfo.fpsak.schema.navn}.BEHANDLING                  TO  ${fpinfoschema.schema.navn} WITH GRANT OPTION');
        EXECUTE IMMEDIATE ('GRANT SELECT ON ${fpinfo.fpsak.schema.navn}.MOTTATT_DOKUMENT            TO  ${fpinfoschema.schema.navn} WITH GRANT OPTION');
        EXECUTE IMMEDIATE ('GRANT SELECT ON ${fpinfo.fpsak.schema.navn}.AKSJONSPUNKT                TO  ${fpinfoschema.schema.navn} WITH GRANT OPTION');
        EXECUTE IMMEDIATE ('GRANT SELECT ON ${fpinfo.fpsak.schema.navn}.BEHANDLING_RESULTAT         TO  ${fpinfoschema.schema.navn} WITH GRANT OPTION');
        EXECUTE IMMEDIATE ('GRANT SELECT ON ${fpinfo.fpsak.schema.navn}.BEHANDLING_ARSAK            TO  ${fpinfoschema.schema.navn} WITH GRANT OPTION');
        EXECUTE IMMEDIATE ('GRANT SELECT ON ${fpinfo.fpsak.schema.navn}.BEHANDLING_VEDTAK           TO  ${fpinfoschema.schema.navn} WITH GRANT OPTION');
        EXECUTE IMMEDIATE ('GRANT SELECT ON ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE      TO  ${fpinfoschema.schema.navn} WITH GRANT OPTION');
        EXECUTE IMMEDIATE ('GRANT SELECT ON ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE_AKT  TO  ${fpinfoschema.schema.navn} WITH GRANT OPTION');
        EXECUTE IMMEDIATE ('GRANT SELECT ON ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT              TO  ${fpinfoschema.schema.navn} WITH GRANT OPTION');
        EXECUTE IMMEDIATE ('GRANT SELECT ON ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODER     TO  ${fpinfoschema.schema.navn} WITH GRANT OPTION');
        EXECUTE IMMEDIATE ('GRANT SELECT ON ${fpinfo.fpsak.schema.navn}.FAGSAK_RELASJON             TO  ${fpinfoschema.schema.navn} WITH GRANT OPTION');
        EXECUTE IMMEDIATE ('GRANT SELECT ON ${fpinfo.fpsak.schema.navn}.FH_ADOPSJON                 TO  ${fpinfoschema.schema.navn} WITH GRANT OPTION');
        EXECUTE IMMEDIATE ('GRANT SELECT ON ${fpinfo.fpsak.schema.navn}.FH_TERMINBEKREFTELSE        TO  ${fpinfoschema.schema.navn} WITH GRANT OPTION');
        EXECUTE IMMEDIATE ('GRANT SELECT ON ${fpinfo.fpsak.schema.navn}.FH_UIDENTIFISERT_BARN       TO  ${fpinfoschema.schema.navn} WITH GRANT OPTION');
        EXECUTE IMMEDIATE ('GRANT SELECT ON ${fpinfo.fpsak.schema.navn}.SO_SOEKNAD                  TO  ${fpinfoschema.schema.navn} WITH GRANT OPTION');
        EXECUTE IMMEDIATE ('GRANT SELECT ON ${fpinfo.fpsak.schema.navn}.SO_DEKNINGSGRAD             TO  ${fpinfoschema.schema.navn} WITH GRANT OPTION');
        EXECUTE IMMEDIATE ('GRANT SELECT ON ${fpinfo.fpsak.schema.navn}.SO_RETTIGHET                TO  ${fpinfoschema.schema.navn} WITH GRANT OPTION');
        EXECUTE IMMEDIATE ('GRANT SELECT ON ${fpinfo.fpsak.schema.navn}.YF_DOKUMENTASJON_PERIODE    TO  ${fpinfoschema.schema.navn} WITH GRANT OPTION');
        EXECUTE IMMEDIATE ('GRANT SELECT ON ${fpinfo.fpsak.schema.navn}.YF_FORDELING_PERIODE        TO  ${fpinfoschema.schema.navn} WITH GRANT OPTION');
        EXECUTE IMMEDIATE ('GRANT SELECT ON ${fpinfo.fpsak.schema.navn}.GR_YTELSES_FORDELING        TO  ${fpinfoschema.schema.navn} WITH GRANT OPTION');
        EXECUTE IMMEDIATE ('GRANT SELECT ON ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE_SOKNAD        TO ${fpinfoschema.schema.navn} WITH GRANT OPTION');
        EXECUTE IMMEDIATE ('GRANT SELECT ON ${fpinfo.fpsak.schema.navn}.UTTAK_AKTIVITET             TO ${fpinfoschema.schema.navn} WITH GRANT OPTION');
        EXECUTE IMMEDIATE ('GRANT SELECT ON ${fpinfo.fpsak.schema.navn}.YF_FORDELING                TO ${fpinfoschema.schema.navn} WITH GRANT OPTION');
      -- Ved endring i grants her må tilsvarende endringer gjøres i migreringen 'R__Grants_for_fpinfo_og_fpinfo-intern.sql' i fpsak-prosjektet
      EXCEPTION
      WHEN OTHERS
      THEN
        -- Lokalt skjemaoppsett kjøres to ganger.
        --  Første gangen vil denne alltid feile, da migrering for fpsak ikke har kjørt
        NULL;
    END;

BEGIN
  -- Opprett alle schema
  p_opprett_hvis_ikke_funnet('${fpinfo.fpsak.schema.navn}'); -- fpsak-skjema
  p_opprett_hvis_ikke_funnet('${fpinfoschema.schema.navn}'); -- fpinfoSchema-skjema
  p_opprett_hvis_ikke_funnet('${fpinfo.schema.navn}'); -- fpinfo-skjema

  --  fpinfoSchema må kunne opprette views
  EXECUTE IMMEDIATE ('GRANT CREATE VIEW TO ${fpinfoschema.schema.navn} WITH ADMIN OPTION');

  -- Må ha dette, da fpinfoSchema skal opprette views mot fpsak-tabeller
  p_opprett_permissions;
END;
