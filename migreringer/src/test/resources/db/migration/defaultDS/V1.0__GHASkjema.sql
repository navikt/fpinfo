create table ${fpinfo.fpsak.schema.navn}.BRUKER
(
   ID NUMBER(19) not null
      constraint PK_BRUKER
         primary key,
   VERSJON NUMBER(19) default 0 not null,
   OPPRETTET_AV VARCHAR2(20 char) default 'VL' not null,
   OPPRETTET_TID TIMESTAMP(3) default systimestamp not null,
   ENDRET_AV VARCHAR2(20 char),
   ENDRET_TID TIMESTAMP(3),
   SPRAK_KODE VARCHAR2(100 char) default 'NB' not null,
   AKTOER_ID VARCHAR2(50 char)
);


create index ${fpinfo.fpsak.schema.navn}.IDX_BRUKER_6
   on ${fpinfo.fpsak.schema.navn}.BRUKER (SPRAK_KODE);

create unique index ${fpinfo.fpsak.schema.navn}.UIDX_BRUKER_1
   on ${fpinfo.fpsak.schema.navn}.BRUKER (AKTOER_ID);
