CREATE OR REPLACE VIEW GJELDENDE_VEDTATT_BEHANDLING
AS select rader.* from
    ( select
          row_number() over ( PARTITION BY fs.saksnummer ORDER BY
              CASE WHEN bv.vedtak_dato = to_timestamp(trunc(bv.vedtak_dato)) THEN bv.opprettet_tid
                   ELSE bv.vedtak_dato
                  END desc
              ) as row_number, b.id as behandling_id, fs.saksnummer
      from fpsak.behandling b
      JOIN fpsak.fagsak fs ON fs.id = b.fagsak_id
      JOIN fpsak.behandling_resultat br ON b.id = br.behandling_id
      JOIN fpsak.behandling_vedtak bv ON bv.behandling_resultat_id = br.id
      WHERE b.BEHANDLING_STATUS in ('AVSLU', 'IVED')
        AND b.BEHANDLING_TYPE in ('BT-002', 'BT-004')
    ) rader
   where rader.row_number = 1;

GRANT SELECT ON GJELDENDE_VEDTATT_BEHANDLING TO ${fpinfo.schema.navn};
