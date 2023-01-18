CREATE
OR REPLACE VIEW UFORE_GRUNNLAG
AS
select b.id as BEHANDLING_ID,
       gruf.register_ufore as REGISTER_UFORE
from fpsak.BEHANDLING b
left outer join fpsak.GR_UFORETRYGD gruf on b.id = gruf.behandling_id and gruf.aktiv = 'J';

GRANT SELECT ON UFORE_GRUNNLAG TO fpinfo;
