create or replace view FAGSAK_RELASJON
as select
       b.id as behandling_id,
       fs.SAKSNUMMER as saksnummer,
       fs.ID as FAGSAK_ID,
       fr.FAGSAK_EN_ID,
       fr.FAGSAK_TO_ID,
       fs1.saksnummer as saksnummer_en,
       fs2.saksnummer as saksnummer_to,
       br.ENDRET_TID as ENDRET_TID
   from ${fpinfo.fpsak.schema.navn}.FAGSAK fs
            inner join ${fpinfo.fpsak.schema.navn}.BEHANDLING b on b.FAGSAK_ID = fs.ID and b.BEHANDLING_TYPE in ('BT-002', 'BT-004')
            inner join ${fpinfo.fpsak.schema.navn}.BEHANDLING_RESULTAT br on br.BEHANDLING_ID = b.ID and br.BEHANDLING_RESULTAT_TYPE = 'INNVILGET'
            left join ${fpinfo.fpsak.schema.navn}.FAGSAK_RELASJON fr ON fs.ID = fr.FAGSAK_EN_ID or fs.ID = fr.FAGSAK_TO_ID
            left join ${fpinfo.fpsak.schema.navn}.FAGSAK fs1 ON fs1.ID = fr.fagsak_en_id
            left join ${fpinfo.fpsak.schema.navn}.FAGSAK fs2 ON fs2.ID = fr.fagsak_to_id
   where fr.AKTIV='J';

GRANT SELECT ON FAGSAK_RELASJON TO ${fpinfo.schema.navn};
