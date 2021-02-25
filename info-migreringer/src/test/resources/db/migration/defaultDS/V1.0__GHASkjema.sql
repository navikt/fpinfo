



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
   

create table ${fpinfo.fpsak.schema.navn}.FAGSAK
(
   ID NUMBER(19) not null
      constraint PK_FAGSAK
         primary key,
   FAGSAK_STATUS VARCHAR2(100 char) not null,
   BRUKER_ID NUMBER(19) not null
      constraint FK_FAGSAK_3
         references ${fpinfo.fpsak.schema.navn}.BRUKER,
   BRUKER_ROLLE VARCHAR2(100 char) not null,
   VERSJON NUMBER(19) default 0 not null,
   OPPRETTET_AV VARCHAR2(20 char) default 'VL' not null,
   OPPRETTET_TID TIMESTAMP(3) default systimestamp not null,
   ENDRET_AV VARCHAR2(20 char),
   ENDRET_TID TIMESTAMP(3),
   YTELSE_TYPE VARCHAR2(100 char),
   SAKSNUMMER VARCHAR2(19 char),
   TIL_INFOTRYGD VARCHAR2(1 char) default 'N' not null
);

create table ${fpinfo.fpsak.schema.navn}.FAGSAK_RELASJON
(
   ID NUMBER(19) not null
      constraint PK_FAGSAK_RELASJON
         primary key,
   VERSJON NUMBER(19) default 0 not null,
   OPPRETTET_AV VARCHAR2(20 char) default 'VL' not null,
   OPPRETTET_TID TIMESTAMP(3) default systimestamp not null,
   ENDRET_AV VARCHAR2(20 char),
   ENDRET_TID TIMESTAMP(3),
   FAGSAK_EN_ID NUMBER(19) not null
      constraint FK_FAGSAK_RELASJON_1
         references ${fpinfo.fpsak.schema.navn}.FAGSAK,
   FAGSAK_TO_ID NUMBER(19)
      constraint FK_FAGSAK_RELASJON_2
         references ${fpinfo.fpsak.schema.navn}.FAGSAK,
   KONTO_BEREGNING_ID NUMBER(19),
   AKTIV VARCHAR2(1 char) default 'J' not null
      constraint CHK_AKTIV12
         check (aktiv IN ('J', 'N')),
   DEKNINGSGRAD NUMBER(3) default 100 not null,
   OVERSTYRT_KONTO_BEREGNING_ID NUMBER(19),
   OVERSTYRT_DEKNINGSGRAD NUMBER(3),
   AVSLUTTNINGSDATO DATE
);


create table ${fpinfo.fpsak.schema.navn}.BEHANDLING
(
   ID NUMBER(19) not null
      constraint PK_BEHANDLING
         primary key,
   FAGSAK_ID NUMBER(19) not null
      constraint FK_BEHANDLING_1
         references ${fpinfo.fpsak.schema.navn}.FAGSAK,
   BEHANDLING_STATUS VARCHAR2(100 char) not null,
   BEHANDLING_TYPE VARCHAR2(100 char) not null,
   OPPRETTET_DATO DATE default sysdate not null,
   AVSLUTTET_DATO DATE,
   VERSJON NUMBER(19) default 0 not null,
   OPPRETTET_AV VARCHAR2(20 char) default 'VL' not null,
   OPPRETTET_TID TIMESTAMP(3) default systimestamp not null,
   ENDRET_AV VARCHAR2(20 char),
   ENDRET_TID TIMESTAMP(3),
   ANSVARLIG_SAKSBEHANDLER VARCHAR2(100 char),
   ANSVARLIG_BESLUTTER VARCHAR2(100 char),
   BEHANDLENDE_ENHET VARCHAR2(10 char),
   BEHANDLENDE_ENHET_NAVN VARCHAR2(320 char),
   BEHANDLENDE_ENHET_ARSAK VARCHAR2(800 char),
   BEHANDLINGSTID_FRIST DATE not null,
   STARTPUNKT_TYPE VARCHAR2(50 char) default '-' not null,
   SIST_OPPDATERT_TIDSPUNKT TIMESTAMP(3),
   AAPNET_FOR_ENDRING VARCHAR2(1 char) default 'N' not null,
   TOTRINNSBEHANDLING VARCHAR2(1 char) default 'N' not null
      constraint CHK_TOTRINNSBEHANDLING1
         check (totrinnsbehandling IN ('J', 'N')),
   UUID RAW(16),
   MIGRERT_KILDE VARCHAR2(100 char) default '-' not null
);

CREATE TABLE ${fpinfo.fpsak.schema.navn}.STOENADSKONTOBEREGNING (
  ID                        NUMBER(19, 0)                                       NOT NULL,
  FAGSAK_RELASJON_ID        NUMBER(19, 0)                                       NOT NULL,
  BEHANDLING_ID             NUMBER(19, 0)                                       NOT NULL,
  REGEL_INPUT               CLOB                                                NOT NULL,
  REGEL_EVALUERING          CLOB                                                NOT NULL,
  OPPRETTET_AV              VARCHAR2(20 CHAR) DEFAULT 'VL'                      NOT NULL,
  OPPRETTET_TID             TIMESTAMP(3)      DEFAULT systimestamp              NOT NULL,
  ENDRET_AV                 VARCHAR2(20 CHAR),
  ENDRET_TID                TIMESTAMP(3),
  CONSTRAINT PK_STOENADSKONTOBEREGNING PRIMARY KEY (ID),
  CONSTRAINT FK_STOENADSKONTOBEREGNING_2 FOREIGN KEY (BEHANDLING_ID) REFERENCES  ${fpinfo.fpsak.schema.navn}.BEHANDLING(ID)
);


create index ${fpinfo.fpsak.schema.navn}.IDX_FAGSAK_RELASJON_1
   on ${fpinfo.fpsak.schema.navn}.FAGSAK_RELASJON (FAGSAK_EN_ID);

create index ${fpinfo.fpsak.schema.navn}.IDX_FAGSAK_RELASJON_2
   on ${fpinfo.fpsak.schema.navn}.FAGSAK_RELASJON (FAGSAK_TO_ID);

create index ${fpinfo.fpsak.schema.navn}.IDX_FAGSAK_RELASJON_3
   on ${fpinfo.fpsak.schema.navn}.FAGSAK_RELASJON (KONTO_BEREGNING_ID);

create index ${fpinfo.fpsak.schema.navn}.IDX_FAGSAK_RELASJON_4
   on ${fpinfo.fpsak.schema.navn}.FAGSAK_RELASJON (OVERSTYRT_KONTO_BEREGNING_ID);

create index ${fpinfo.fpsak.schema.navn}.IDX_FAGSAK_RELASJON_5
   on ${fpinfo.fpsak.schema.navn}.FAGSAK_RELASJON (AVSLUTTNINGSDATO);

create index ${fpinfo.fpsak.schema.navn}.IDX_FAGSAK_1
   on ${fpinfo.fpsak.schema.navn}.FAGSAK (FAGSAK_STATUS);

create index ${fpinfo.fpsak.schema.navn}.IDX_FAGSAK_2
   on ${fpinfo.fpsak.schema.navn}.FAGSAK (BRUKER_ROLLE);

create index ${fpinfo.fpsak.schema.navn}.IDX_FAGSAK_3
   on ${fpinfo.fpsak.schema.navn}.FAGSAK (BRUKER_ID);

create index ${fpinfo.fpsak.schema.navn}.IDX_FAGSAK_7
   on ${fpinfo.fpsak.schema.navn}.FAGSAK (YTELSE_TYPE);

create unique index ${fpinfo.fpsak.schema.navn}.UIDX_FAGSAK_1
   on ${fpinfo.fpsak.schema.navn}.FAGSAK (SAKSNUMMER);



create index ${fpinfo.fpsak.schema.navn}.IDX_BEHANDLING_1
   on ${fpinfo.fpsak.schema.navn}.BEHANDLING (FAGSAK_ID);

create index ${fpinfo.fpsak.schema.navn}.IDX_BEHANDLING_2
   on ${fpinfo.fpsak.schema.navn}.BEHANDLING (BEHANDLING_STATUS);
create index ${fpinfo.fpsak.schema.navn}.IDX_BEHANDLING_3
   on ${fpinfo.fpsak.schema.navn}.BEHANDLING (BEHANDLING_TYPE);

create index ${fpinfo.fpsak.schema.navn}.IDX_BEHANDLING_6
   on ${fpinfo.fpsak.schema.navn}.BEHANDLING (STARTPUNKT_TYPE);

create unique index ${fpinfo.fpsak.schema.navn}.UIDX_BEHANDLING_03
   on ${fpinfo.fpsak.schema.navn}.BEHANDLING (UUID);
   
   

 
   

create table ${fpinfo.fpsak.schema.navn}.SO_SOEKNAD
(
   ID NUMBER(19) not null
      constraint PK_SOEKNAD
         primary key,
   SOEKNADSDATO DATE not null,
   TILLEGGSOPPLYSNINGER VARCHAR2(4000 char),
   VERSJON NUMBER(19) default 0 not null,
   OPPRETTET_AV VARCHAR2(20 char) default 'VL' not null,
   OPPRETTET_TID TIMESTAMP(3) default systimestamp not null,
   ENDRET_AV VARCHAR2(20 char),
   ENDRET_TID TIMESTAMP(3),
   FAR_SOEKER_TYPE VARCHAR2(100 char) not null,
   ELEKTRONISK_REGISTRERT VARCHAR2(1 char) default 'N' not null,
   MOTTATT_DATO DATE,
   BEGRUNNELSE_FOR_SEN_INNSENDING VARCHAR2(2000 char),
   ER_ENDRINGSSOEKNAD VARCHAR2(1 char) default 'N' not null,
   BRUKER_ROLLE VARCHAR2(100 char) default '-' not null,
   SPRAK_KODE VARCHAR2(100 char) default 'NB' not null
);

create index ${fpinfo.fpsak.schema.navn}.IDX_SOEKNAD_10
   on ${fpinfo.fpsak.schema.navn}.SO_SOEKNAD (FAR_SOEKER_TYPE);

create index ${fpinfo.fpsak.schema.navn}.IDX_SOEKNAD_11
   on ${fpinfo.fpsak.schema.navn}.SO_SOEKNAD (BRUKER_ROLLE);

create index ${fpinfo.fpsak.schema.navn}.IDX_SOEKNAD_12
   on ${fpinfo.fpsak.schema.navn}.SO_SOEKNAD (SPRAK_KODE);

create table ${fpinfo.fpsak.schema.navn}.BEHANDLING_RESULTAT
(
   ID NUMBER(19) not null
      constraint PK_BEHANDLING_RESULTAT
         primary key,
   BEHANDLING_ID NUMBER(19) not NULL,
   INNGANGSVILKAR_RESULTAT_ID NUMBER(19),
   BEREGNING_RESULTAT_ID NUMBER(19),
   VERSJON NUMBER(19) default 0 not null,
   OPPRETTET_AV VARCHAR2(20 char) default 'VL' not null,
   OPPRETTET_TID TIMESTAMP(3) default systimestamp not null,
   ENDRET_AV VARCHAR2(20 char),
   ENDRET_TID TIMESTAMP(3),
   BEHANDLING_RESULTAT_TYPE VARCHAR2(100 char) default 'IKKE_FASTSATT' not null,
   AVSLAG_ARSAK VARCHAR2(100 char),
   AVSLAG_ARSAK_FRITEKST VARCHAR2(1500 char),
   RETTEN_TIL VARCHAR2(100 char) not null,
   VEDTAKSBREV VARCHAR2(100 char) not null,
   OVERSKRIFT VARCHAR2(200 char),
   FRITEKSTBREV CLOB,
   ENDRET_DEKNINGSGRAD CHAR default 'N' not null
      check (endret_dekningsgrad IN ('J', 'N')),
   ENDRET_STOENADSKONTO CHAR default 'N' not null
      check (endret_stoenadskonto IN ('J', 'N'))
);

create index ${fpinfo.fpsak.schema.navn}.IDX_BEHANDLING_RESULTAT_1
   on ${fpinfo.fpsak.schema.navn}.BEHANDLING_RESULTAT (INNGANGSVILKAR_RESULTAT_ID);

create index ${fpinfo.fpsak.schema.navn}.IDX_BEHANDLING_RESULTAT_2
   on ${fpinfo.fpsak.schema.navn}.BEHANDLING_RESULTAT (BEREGNING_RESULTAT_ID);

create index ${fpinfo.fpsak.schema.navn}.IDX_BEHANDLING_RESULTAT_3
   on ${fpinfo.fpsak.schema.navn}.BEHANDLING_RESULTAT (BEHANDLING_ID);

create index ${fpinfo.fpsak.schema.navn}.IDX_BEHANDLING_RESULTAT_4
   on ${fpinfo.fpsak.schema.navn}.BEHANDLING_RESULTAT (BEHANDLING_RESULTAT_TYPE);

create index ${fpinfo.fpsak.schema.navn}.IDX_BEHANDLING_RESULTAT_6
   on ${fpinfo.fpsak.schema.navn}.BEHANDLING_RESULTAT (AVSLAG_ARSAK);
   
   
   
   
create table ${fpinfo.fpsak.schema.navn}.AKSJONSPUNKT
(
   ID NUMBER(19) not null
      constraint PK_AKSJONSPUNKT
         primary key,
   PERIODE_FOM DATE,
   PERIODE_TOM DATE,
   BEGRUNNELSE VARCHAR2(4000 char),
   TOTRINN_BEHANDLING VARCHAR2(1 char) not null
      constraint CHK_TOTRINNSBEHANDLING
         check ("TOTRINN_BEHANDLING"='J' OR "TOTRINN_BEHANDLING"='N'),
   BEHANDLING_STEG_FUNNET VARCHAR2(100 char),
   AKSJONSPUNKT_STATUS VARCHAR2(100 char) not null,
   AKSJONSPUNKT_DEF VARCHAR2(100 char) not null,
   VERSJON NUMBER(19) default 0 not null,
   FRIST_TID TIMESTAMP(3),
   OPPRETTET_AV VARCHAR2(20 char) default 'VL' not null,
   OPPRETTET_TID TIMESTAMP(3) default systimestamp not null,
   ENDRET_AV VARCHAR2(20 char),
   ENDRET_TID TIMESTAMP(3),
   VENT_AARSAK VARCHAR2(100 char) default '-' not null,
   BEHANDLING_ID NUMBER(19) not null
      constraint FK_AKSJONSPUNKT_2
         references ${fpinfo.fpsak.schema.navn}.BEHANDLING
);

create unique index ${fpinfo.fpsak.schema.navn}.IDX_AKSJONSPUNKT_1
   on ${fpinfo.fpsak.schema.navn}.AKSJONSPUNKT (BEHANDLING_ID, AKSJONSPUNKT_DEF);

create index ${fpinfo.fpsak.schema.navn}.IDX_AKSJONSPUNKT_6
   on ${fpinfo.fpsak.schema.navn}.AKSJONSPUNKT (BEHANDLING_STEG_FUNNET);

create index ${fpinfo.fpsak.schema.navn}.IDX_AKSJONSPUNKT_7
   on ${fpinfo.fpsak.schema.navn}.AKSJONSPUNKT (AKSJONSPUNKT_DEF);

create index ${fpinfo.fpsak.schema.navn}.IDX_AKSJONSPUNKT_8
   on ${fpinfo.fpsak.schema.navn}.AKSJONSPUNKT (VENT_AARSAK);

create index ${fpinfo.fpsak.schema.navn}.IDX_AKSJONSPUNKT_9
   on ${fpinfo.fpsak.schema.navn}.AKSJONSPUNKT (AKSJONSPUNKT_STATUS);

alter table ${fpinfo.fpsak.schema.navn}.AKSJONSPUNKT
   add constraint CHK_UNIQUE_BEH_AD
      unique (BEHANDLING_ID, AKSJONSPUNKT_DEF);

create table ${fpinfo.fpsak.schema.navn}.BEHANDLING_VEDTAK
(
   ID NUMBER(19) not null
      constraint PK_BEHANDLING_VEDTAK
         primary key,
   VEDTAK_DATO TIMESTAMP(3) not null,
   ANSVARLIG_SAKSBEHANDLER VARCHAR2(40 char) not null,
   BEHANDLING_RESULTAT_ID NUMBER(19) not null
      constraint FK_BEHANDLING_VEDTAK_1
         references ${fpinfo.fpsak.schema.navn}.BEHANDLING_RESULTAT,
   VEDTAK_RESULTAT_TYPE VARCHAR2(100 char) not null,
   VERSJON NUMBER(19) default 0 not null,
   OPPRETTET_AV VARCHAR2(20 char) default 'VL' not null,
   OPPRETTET_TID TIMESTAMP(3) default systimestamp not null,
   ENDRET_AV VARCHAR2(20 char),
   ENDRET_TID TIMESTAMP(3),
   IVERKSETTING_STATUS VARCHAR2(100 char) default 'IKKE_IVERKSATT' not null,
   BESLUTNING VARCHAR2(1 char) default 'N' not null
      constraint CHK_BESLUTNING
         check (BESLUTNING IN ('J', 'N'))
);

create index ${fpinfo.fpsak.schema.navn}.IDX_VEDTAK_1
   on ${fpinfo.fpsak.schema.navn}.BEHANDLING_VEDTAK (VEDTAK_RESULTAT_TYPE);

create index ${fpinfo.fpsak.schema.navn}.IDX_VEDTAK_2
   on ${fpinfo.fpsak.schema.navn}.BEHANDLING_VEDTAK (ANSVARLIG_SAKSBEHANDLER);

create index ${fpinfo.fpsak.schema.navn}.IDX_VEDTAK_3
   on ${fpinfo.fpsak.schema.navn}.BEHANDLING_VEDTAK (VEDTAK_DATO);

create unique index ${fpinfo.fpsak.schema.navn}.UIDX_BEHANDLING_VEDTAK_1
   on ${fpinfo.fpsak.schema.navn}.BEHANDLING_VEDTAK (BEHANDLING_RESULTAT_ID);

create index ${fpinfo.fpsak.schema.navn}.IDX_BEHANDLING_VEDTAK_6
   on ${fpinfo.fpsak.schema.navn}.BEHANDLING_VEDTAK (IVERKSETTING_STATUS);
   
   create table ${fpinfo.fpsak.schema.navn}.MOTTATT_DOKUMENT
(
   ID NUMBER(19) not null
      constraint PK_MOTTATTE_DOKUMENT
         primary key,
   JOURNALPOST_ID VARCHAR2(20 char),
   TYPE VARCHAR2(100 char) not null,
   OPPRETTET_AV VARCHAR2(20 char) default 'VL' not null,
   OPPRETTET_TID TIMESTAMP(3) default systimestamp not null,
   ENDRET_AV VARCHAR2(20 char),
   ENDRET_TID TIMESTAMP(3),
   BEHANDLING_ID NUMBER(19)
      constraint FK_MOTTATT_DOKUMENT_02
         references ${fpinfo.fpsak.schema.navn}.BEHANDLING,
   MOTTATT_DATO DATE,
   ELEKTRONISK_REGISTRERT VARCHAR2(1 char) default 'N' not null,
   FAGSAK_ID NUMBER(19) not null
      constraint FK_MOTTATT_DOKUMENT_04
         references ${fpinfo.fpsak.schema.navn}.FAGSAK,
   FORSENDELSE_ID RAW(16),
   XML_PAYLOAD CLOB,
   DOKUMENT_KATEGORI VARCHAR2(100 char) not null,
   JOURNAL_ENHET VARCHAR2(10 char),
   MOTTATT_TIDSPUNKT TIMESTAMP(3),
   KANALREFERANSE VARCHAR2(100 char)
);

create index ${fpinfo.fpsak.schema.navn}.IDX_MOTTATTE_DOKUMENT_1
   on ${fpinfo.fpsak.schema.navn}.MOTTATT_DOKUMENT (TYPE);

create index ${fpinfo.fpsak.schema.navn}.IDX_MOTTATT_DOKUMENT_6
   on ${fpinfo.fpsak.schema.navn}.MOTTATT_DOKUMENT (BEHANDLING_ID);

create index ${fpinfo.fpsak.schema.navn}.IDX_MOTTATT_DOKUMENT_2
   on ${fpinfo.fpsak.schema.navn}.MOTTATT_DOKUMENT (DOKUMENT_KATEGORI);

create index ${fpinfo.fpsak.schema.navn}.IDX_MOTTATT_DOKUMENT_7
   on ${fpinfo.fpsak.schema.navn}.MOTTATT_DOKUMENT (FAGSAK_ID);

create index ${fpinfo.fpsak.schema.navn}.IDX_MOTTATT_DOKUMENT_8
   on ${fpinfo.fpsak.schema.navn}.MOTTATT_DOKUMENT (FORSENDELSE_ID);

create index ${fpinfo.fpsak.schema.navn}.IDX_MOTTATT_DOKUMENT_9
   on ${fpinfo.fpsak.schema.navn}.MOTTATT_DOKUMENT (JOURNALPOST_ID);

create table ${fpinfo.fpsak.schema.navn}.SO_ANNEN_PART
(
   ID NUMBER(19) not null
      constraint PK_SOEKNAD_ANNEN_PART
         primary key,
   UTL_PERSON_IDENT VARCHAR2(250 char),
   UTL_PERSON_IDENT_LAND VARCHAR2(100 char) not null,
   ARSAK VARCHAR2(20 char),
   BEGRUNNELSE VARCHAR2(2000 char),
   TYPE VARCHAR2(100 char) not null,
   OPPRETTET_AV VARCHAR2(20 char) default 'VL' not null,
   OPPRETTET_TID TIMESTAMP(3) default systimestamp not null,
   ENDRET_AV VARCHAR2(20 char),
   ENDRET_TID TIMESTAMP(3),
   NAVN VARCHAR2(100 char),
   AKTOER_ID VARCHAR2(50 char)
);


create index ${fpinfo.fpsak.schema.navn}.IDX_SOEKNAD_ANNEN_PART_1
   on ${fpinfo.fpsak.schema.navn}.SO_ANNEN_PART (UTL_PERSON_IDENT_LAND);

create index ${fpinfo.fpsak.schema.navn}.IDX_SOEKNAD_ANNEN_PART_2
   on ${fpinfo.fpsak.schema.navn}.SO_ANNEN_PART (TYPE);

create index ${fpinfo.fpsak.schema.navn}.IDX_SO_ANNEN_PART_3
   on ${fpinfo.fpsak.schema.navn}.SO_ANNEN_PART (AKTOER_ID);

create index ${fpinfo.fpsak.schema.navn}.IDX_SO_ANNEN_PART_4
   on ${fpinfo.fpsak.schema.navn}.SO_ANNEN_PART (UTL_PERSON_IDENT);

create table ${fpinfo.fpsak.schema.navn}.BEHANDLING_ARSAK
(
   ID NUMBER(19) not null
      constraint PK_BEHANDLING_ARSAK
         primary key,
   BEHANDLING_ID NUMBER(19) not null
      constraint FK_BEHANDLING_ARSAK_1
         references ${fpinfo.fpsak.schema.navn}.BEHANDLING,
   BEHANDLING_ARSAK_TYPE VARCHAR2(20 char) not null,
   VERSJON NUMBER(19) default 0 not null,
   OPPRETTET_AV VARCHAR2(20 char) default 'VL' not null,
   OPPRETTET_TID TIMESTAMP(3) default systimestamp not null,
   ENDRET_AV VARCHAR2(20 char),
   ENDRET_TID TIMESTAMP(3),
   ORIGINAL_BEHANDLING_ID NUMBER(19)
      constraint FK_BEHANDLING_ARSAK_2
         references ${fpinfo.fpsak.schema.navn}.BEHANDLING,
   MANUELT_OPPRETTET VARCHAR2(1 char) default 'N' not null
);



create index ${fpinfo.fpsak.schema.navn}.IDX_BEHANDLING_ARSAK_6
   on ${fpinfo.fpsak.schema.navn}.BEHANDLING_ARSAK (BEHANDLING_ID);

create index ${fpinfo.fpsak.schema.navn}.IDX_BEHANDLING_ARSAK_7
   on ${fpinfo.fpsak.schema.navn}.BEHANDLING_ARSAK (ORIGINAL_BEHANDLING_ID);

create table ${fpinfo.fpsak.schema.navn}.FH_FAMILIE_HENDELSE
(
   ID NUMBER(19) not null
      constraint PK_FAMILIE_HENDELSE
         primary key,
   ANTALL_BARN NUMBER(3),
   FAMILIE_HENDELSE_TYPE VARCHAR2(100 char) not null,
   VERSJON NUMBER(19) default 0 not null,
   OPPRETTET_AV VARCHAR2(20 char) default 'VL' not null,
   OPPRETTET_TID TIMESTAMP(3) default systimestamp not null,
   ENDRET_AV VARCHAR2(20 char),
   ENDRET_TID TIMESTAMP(3),
   MOR_FOR_SYK_VED_FODSEL VARCHAR2(1 char) default 'N'
      constraint CHK_MOR_SYK_FODSEL
         check (mor_for_syk_ved_fodsel IN ('J', 'N'))
);


create table ${fpinfo.fpsak.schema.navn}.FH_TERMINBEKREFTELSE
(
   ID NUMBER(19) not null
      constraint PK_TERMINBEKREFTELSE
         primary key,
   TERMINDATO DATE not null,
   UTSTEDT_DATO DATE,
   VERSJON NUMBER(19) default 0 not null,
   OPPRETTET_AV VARCHAR2(20 char) default 'VL' not null,
   OPPRETTET_TID TIMESTAMP(3) default systimestamp not null,
   ENDRET_AV VARCHAR2(20 char),
   ENDRET_TID TIMESTAMP(3),
   FAMILIE_HENDELSE_ID NUMBER(19) not null
      constraint FK_TERMINBEKREFTELSE_1
         references ${fpinfo.fpsak.schema.navn}.FH_FAMILIE_HENDELSE,
   NAVN VARCHAR2(256 char)
);

create index ${fpinfo.fpsak.schema.navn}.IDX_TERMINBEKREFTELSE_1
   on ${fpinfo.fpsak.schema.navn}.FH_TERMINBEKREFTELSE (FAMILIE_HENDELSE_ID);

create table ${fpinfo.fpsak.schema.navn}.FH_ADOPSJON
(
   ID NUMBER(19) not null
      constraint PK_ADOPSJON
         primary key,
   OMSORGSOVERTAKELSE_DATO DATE,
   EKTEFELLES_BARN VARCHAR2(1 char)
      constraint CHK_EKTEFELLES_BARN
         check (EKTEFELLES_BARN in ('J', 'N')),
   ADOPTERER_ALENE VARCHAR2(1 char)
      constraint CHK_ADOPTERER_ALENE
         check (ADOPTERER_ALENE in ('J', 'N')),
   VERSJON NUMBER(19) default 0 not null,
   OPPRETTET_AV VARCHAR2(20 char) default 'VL' not null,
   OPPRETTET_TID TIMESTAMP(3) default systimestamp not null,
   ENDRET_AV VARCHAR2(20 char),
   ENDRET_TID TIMESTAMP(3),
   FAMILIE_HENDELSE_ID NUMBER(19) not null
      constraint FK_ADOPSJON_1
         references ${fpinfo.fpsak.schema.navn}.FH_FAMILIE_HENDELSE,
   OMSORG_VILKAAR_TYPE VARCHAR2(100 char) default '-' not null,
   FORELDREANSVAR_OPPFYLT_DATO DATE,
   ANKOMST_NORGE_DATO DATE
);


create index ${fpinfo.fpsak.schema.navn}.IDX_ADOPSJON_1
   on ${fpinfo.fpsak.schema.navn}.FH_ADOPSJON (FAMILIE_HENDELSE_ID);

create index ${fpinfo.fpsak.schema.navn}.IDX_FH_ADOPSJON_6
   on ${fpinfo.fpsak.schema.navn}.FH_ADOPSJON (OMSORG_VILKAAR_TYPE);

create table ${fpinfo.fpsak.schema.navn}.FH_UIDENTIFISERT_BARN
(
   ID NUMBER(19) not null
      constraint PK_SOEKNAD_BARN
         primary key,
   FOEDSEL_DATO DATE not null,
   OPPRETTET_AV VARCHAR2(20 char) default 'VL' not null,
   OPPRETTET_TID TIMESTAMP(3) default systimestamp not null,
   ENDRET_AV VARCHAR2(20 char),
   ENDRET_TID TIMESTAMP(3),
   BARN_NUMMER NUMBER(19),
   FAMILIE_HENDELSE_ID NUMBER(19) not null
      constraint FK_UIDENTIFISERT_BARN_1
         references ${fpinfo.fpsak.schema.navn}.FH_FAMILIE_HENDELSE,
   DOEDSDATO DATE
);


create unique index ${fpinfo.fpsak.schema.navn}.UIDX_UIDENTIFISERT_BARN_1
   on ${fpinfo.fpsak.schema.navn}.FH_UIDENTIFISERT_BARN (FAMILIE_HENDELSE_ID, BARN_NUMMER);

create index ${fpinfo.fpsak.schema.navn}.UIDX_UIDENTIFISERT_BARN_2
   on ${fpinfo.fpsak.schema.navn}.FH_UIDENTIFISERT_BARN (FAMILIE_HENDELSE_ID);

create index ${fpinfo.fpsak.schema.navn}.IDX_FH_FAMILIE_HENDELSE_6
   on ${fpinfo.fpsak.schema.navn}.FH_FAMILIE_HENDELSE (FAMILIE_HENDELSE_TYPE);

create table ${fpinfo.fpsak.schema.navn}.GR_FAMILIE_HENDELSE
(
   ID NUMBER(19) not null
      constraint PK_GR_FAMILIE_HENDELSE
         primary key,
   BEHANDLING_ID NUMBER(19) not null
      constraint FK_GR_FAMILIE_HENDELSE_1
         references ${fpinfo.fpsak.schema.navn}.BEHANDLING,
   SOEKNAD_FAMILIE_HENDELSE_ID NUMBER(19) not null
      constraint FK_GR_FAMILIE_HENDELSE_2
         references ${fpinfo.fpsak.schema.navn}.FH_FAMILIE_HENDELSE,
   BEKREFTET_FAMILIE_HENDELSE_ID NUMBER(19)
      constraint FK_GR_FAMILIE_HENDELSE_3
         references ${fpinfo.fpsak.schema.navn}.FH_FAMILIE_HENDELSE,
   OVERSTYRT_FAMILIE_HENDELSE_ID NUMBER(19)
      constraint FK_GR_FAMILIE_HENDELSE_4
         references ${fpinfo.fpsak.schema.navn}.FH_FAMILIE_HENDELSE,
   AKTIV VARCHAR2(1 char) default 'N' not null
      constraint CHK_GR_FAMILIE_HENDELSE
         check (AKTIV IN ('J', 'N')),
   VERSJON NUMBER(19) default 0 not null,
   OPPRETTET_AV VARCHAR2(20 char) default 'VL' not null,
   OPPRETTET_TID TIMESTAMP(3) default systimestamp not null,
   ENDRET_AV VARCHAR2(20 char),
   ENDRET_TID TIMESTAMP(3)
);

create unique index ${fpinfo.fpsak.schema.navn}.UIDX_GR_FAMILIE_HENDELSE_01
   on ${fpinfo.fpsak.schema.navn}.GR_FAMILIE_HENDELSE (CASE "AKTIV" WHEN 'J' THEN "BEHANDLING_ID" ELSE NULL END , CASE "AKTIV" WHEN 'J' THEN "AKTIV" ELSE NULL END );

create index ${fpinfo.fpsak.schema.navn}.IDX_FAMILIE_HENDELSE_1
   on ${fpinfo.fpsak.schema.navn}.GR_FAMILIE_HENDELSE (SOEKNAD_FAMILIE_HENDELSE_ID);

create index ${fpinfo.fpsak.schema.navn}.IDX_FAMILIE_HENDELSE_2
   on ${fpinfo.fpsak.schema.navn}.GR_FAMILIE_HENDELSE (BEKREFTET_FAMILIE_HENDELSE_ID);

create index ${fpinfo.fpsak.schema.navn}.IDX_FAMILIE_HENDELSE_3
   on ${fpinfo.fpsak.schema.navn}.GR_FAMILIE_HENDELSE (OVERSTYRT_FAMILIE_HENDELSE_ID);

create index ${fpinfo.fpsak.schema.navn}.IDX_GR_FAMILIE_HENDELSE_6
   on ${fpinfo.fpsak.schema.navn}.GR_FAMILIE_HENDELSE (BEHANDLING_ID);

create table ${fpinfo.fpsak.schema.navn}.SO_RETTIGHET
(
   ID NUMBER(19) not null
      constraint PK_SO_RETTIGHET
         primary key,
   ANNEN_FORELDRE_RETT VARCHAR2(1 char),
   OMSORG_I_HELE_PERIODEN VARCHAR2(1 char),
   VERSJON NUMBER(19) default 0 not null,
   OPPRETTET_AV VARCHAR2(20 char) default 'VL' not null,
   OPPRETTET_TID TIMESTAMP(3) default systimestamp not null,
   ENDRET_AV VARCHAR2(20 char),
   ENDRET_TID TIMESTAMP(3),
   ALENEOMSORG VARCHAR2(1 char) default 'N'
      constraint CHK_SO_RETTIGHET_03
         check (aleneomsorg IS NULL OR aleneomsorg IN ('J', 'N')),
   constraint CHK_SO_RETTIGHET_01
      check (omsorg_i_hele_perioden IS NULL OR aleneomsorg IN ('J', 'N')),
   constraint CHK_SO_RETTIGHET_02
      check (annen_foreldre_rett IS NULL OR aleneomsorg IN ('J', 'N'))
);


create table ${fpinfo.fpsak.schema.navn}.SO_DEKNINGSGRAD
(
   ID NUMBER(19) not null
      constraint PK_SO_DEKNINGSGRAD
         primary key,
   DEKNINGSGRAD NUMBER(3) not null,
   VERSJON NUMBER(19) default 0 not null,
   OPPRETTET_AV VARCHAR2(20 char) default 'VL' not null,
   OPPRETTET_TID TIMESTAMP(3) default systimestamp not null,
   ENDRET_AV VARCHAR2(20 char),
   ENDRET_TID TIMESTAMP(3)
);



create table ${fpinfo.fpsak.schema.navn}.YF_FORDELING
(
   ID NUMBER(19) not null
      constraint PK_SO_FORDELING
         primary key,
   VERSJON NUMBER(19) default 0 not null,
   OPPRETTET_AV VARCHAR2(20 char) default 'VL' not null,
   OPPRETTET_TID TIMESTAMP(3) default systimestamp not null,
   ENDRET_AV VARCHAR2(20 char),
   ENDRET_TID TIMESTAMP(3),
   ANNENFORELDERERINFORMERT VARCHAR2(1 char) default 'N' not null
      constraint CHK_ANNENFORELDERERINFORMERT
         check (annenForelderErInformert IN ('J', 'N'))
);


create table ${fpinfo.fpsak.schema.navn}.YF_FORDELING_PERIODE
(
   ID NUMBER(19) not null
      constraint PK_SO_FORDELING_PERIODE
         primary key,
   FORDELING_ID NUMBER(19) not null
      constraint FK_SO_FORDELING_PERIODE_4
         references ${fpinfo.fpsak.schema.navn}.YF_FORDELING,
   FOM DATE not null,
   TOM DATE not null,
   PERIODE_TYPE VARCHAR2(100 char) not null,
   AARSAK_TYPE VARCHAR2(100 char) not null,
   KL_AARSAK_TYPE VARCHAR2(100 char) not null,
   ARBEIDSPROSENT NUMBER(5,2) default NULL,
   VERSJON NUMBER(19) default 0 not null,
   OPPRETTET_AV VARCHAR2(20 char) default 'VL' not null,
   OPPRETTET_TID TIMESTAMP(3) default systimestamp not null,
   ENDRET_AV VARCHAR2(20 char),
   ENDRET_TID TIMESTAMP(3),
   MORS_AKTIVITET VARCHAR2(100 char),
   BEGRUNNELSE VARCHAR2(4000 char),
   VURDERING_TYPE VARCHAR2(100 char) default 'PERIODE_IKKE_VURDERT',
   FORDELING_PERIODE_KILDE VARCHAR2(100 char) default 'SØKNAD' not null,
   ARBEIDSTAKER VARCHAR2(1 char) default 'N' not null
      constraint CHK_ARBEIDSTAKER
         check (arbeidstaker IN ('J', 'N')),
   SAMTIDIG_UTTAK VARCHAR2(1 char) default 'N' not null
      constraint CHK_SAMTIDIG_UTTAK2
         check (samtidig_uttak IN ('J', 'N')),
   FLERBARNSDAGER VARCHAR2(1 char) default 'N' not null
      constraint CHK_FLERBARNSDAGER2
         check (flerbarnsdager IN ('J', 'N')),
   SAMTIDIG_UTTAKSPROSENT NUMBER(5,2) default NULL,
   ARBEIDSGIVER_AKTOR_ID VARCHAR2(100 char),
   FRILANSER VARCHAR2(1 char) default 'N' not null,
   SELVSTENDIG VARCHAR2(1 char) default 'N' not null,
   ARBEIDSGIVER_ORGNR VARCHAR2(100 char)
);


create index ${fpinfo.fpsak.schema.navn}.IDX_YF_FORDELING_PERIODE_1
   on ${fpinfo.fpsak.schema.navn}.YF_FORDELING_PERIODE (MORS_AKTIVITET);

create index ${fpinfo.fpsak.schema.navn}.IDX_YF_FORDELING_PERIODE_2
   on ${fpinfo.fpsak.schema.navn}.YF_FORDELING_PERIODE (PERIODE_TYPE);

create index ${fpinfo.fpsak.schema.navn}.IDX_YF_FORDELING_PERIODE_3
   on ${fpinfo.fpsak.schema.navn}.YF_FORDELING_PERIODE (AARSAK_TYPE);

create index ${fpinfo.fpsak.schema.navn}.IDX_YF_FORDELING_PERIODE_4
   on ${fpinfo.fpsak.schema.navn}.YF_FORDELING_PERIODE (FORDELING_ID);

create index ${fpinfo.fpsak.schema.navn}.IDX_YF_FORDELING_PERIODE_6
   on ${fpinfo.fpsak.schema.navn}.YF_FORDELING_PERIODE (VURDERING_TYPE);

create index ${fpinfo.fpsak.schema.navn}.IDX_YF_FORDELING_PERIODE_7
   on ${fpinfo.fpsak.schema.navn}.YF_FORDELING_PERIODE (FORDELING_PERIODE_KILDE);

create index ${fpinfo.fpsak.schema.navn}.IDX_YF_FORDELING_PERIODE_9
   on ${fpinfo.fpsak.schema.navn}.YF_FORDELING_PERIODE (ARBEIDSGIVER_ORGNR);

create table ${fpinfo.fpsak.schema.navn}.GR_YTELSES_FORDELING
(
   ID NUMBER(19) not null
      constraint PK_GR_YTELSES_FORDELING
         primary key,
   BEHANDLING_ID NUMBER(19) not null
      constraint FK_GR_YTELSES_FORDELING_1
         references ${fpinfo.fpsak.schema.navn}.BEHANDLING,
   SO_RETTIGHET_ID NUMBER(19)
      constraint FK_GR_YTELSES_FORDELING_2
         references ${fpinfo.fpsak.schema.navn}.SO_RETTIGHET,
   SO_FORDELING_ID NUMBER(19)
      constraint FK_GR_YTELSES_FORDELING_4
         references ${fpinfo.fpsak.schema.navn}.YF_FORDELING,
   SO_DEKNINGSGRAD_ID NUMBER(19)
      constraint FK_GR_YTELSES_FORDELING_3
         references ${fpinfo.fpsak.schema.navn}.SO_DEKNINGSGRAD,
   VERSJON NUMBER(19) default 0 not null,
   OPPRETTET_AV VARCHAR2(20 char) default 'VL' not null,
   OPPRETTET_TID TIMESTAMP(3) default systimestamp not null,
   ENDRET_AV VARCHAR2(20 char),
   ENDRET_TID TIMESTAMP(3),
   OVERSTYRT_FORDELING_ID NUMBER
      constraint FK_GR_YTELSES_FORDELING_7
         references ${fpinfo.fpsak.schema.navn}.YF_FORDELING,
   UTENOMSORG_ID NUMBER,
   ALENEOMSORG_ID NUMBER,
   UTTAK_DOKUMENTASJON_ID NUMBER,
   YF_AVKLART_DATO_ID NUMBER(19),
   AKTIV VARCHAR2(1 char) default 'J' not null
      constraint CHK_AKTIV1
         check (aktiv IN ('J', 'N')),
   ANNEN_FORELDER_HAR_RETT_ID NUMBER,
   JUSTERT_FORDELING_ID NUMBER(19)
      constraint FK_GR_YTELSES_FORDELING_10
         references ${fpinfo.fpsak.schema.navn}.YF_FORDELING
);



create index ${fpinfo.fpsak.schema.navn}.IDX_GR_YTELSES_FORDELING_01
   on ${fpinfo.fpsak.schema.navn}.GR_YTELSES_FORDELING (UTTAK_DOKUMENTASJON_ID);

create index ${fpinfo.fpsak.schema.navn}.IDX_GR_YTELSES_FORDELING_02
   on ${fpinfo.fpsak.schema.navn}.GR_YTELSES_FORDELING (UTENOMSORG_ID);

create index ${fpinfo.fpsak.schema.navn}.IDX_GR_YTELSES_FORDELING_04
   on ${fpinfo.fpsak.schema.navn}.GR_YTELSES_FORDELING (OVERSTYRT_FORDELING_ID);

create index ${fpinfo.fpsak.schema.navn}.IDX_GR_YTELSES_FORDELING_06
   on ${fpinfo.fpsak.schema.navn}.GR_YTELSES_FORDELING (ALENEOMSORG_ID);

create index ${fpinfo.fpsak.schema.navn}.IDX_GR_YTELSES_FORDELING_07
   on ${fpinfo.fpsak.schema.navn}.GR_YTELSES_FORDELING (SO_FORDELING_ID);

create index ${fpinfo.fpsak.schema.navn}.IDX_GR_YTELSES_FORDELING_08
   on ${fpinfo.fpsak.schema.navn}.GR_YTELSES_FORDELING (SO_DEKNINGSGRAD_ID);

create index ${fpinfo.fpsak.schema.navn}.IDX_GR_YTELSES_FORDELING_09
   on ${fpinfo.fpsak.schema.navn}.GR_YTELSES_FORDELING (SO_RETTIGHET_ID);

create index ${fpinfo.fpsak.schema.navn}.IDX_GR_YTELSES_FORDELING_10
   on ${fpinfo.fpsak.schema.navn}.GR_YTELSES_FORDELING (BEHANDLING_ID);

create index ${fpinfo.fpsak.schema.navn}.IDX_GR_YTELSES_FORDELING_11
   on ${fpinfo.fpsak.schema.navn}.GR_YTELSES_FORDELING (YF_AVKLART_DATO_ID);

create index ${fpinfo.fpsak.schema.navn}.IDX_GR_YTELSES_FORDELING_12
   on ${fpinfo.fpsak.schema.navn}.GR_YTELSES_FORDELING (ANNEN_FORELDER_HAR_RETT_ID);

create index ${fpinfo.fpsak.schema.navn}.IDX_GR_YTELSES_FORDELING_15
   on ${fpinfo.fpsak.schema.navn}.GR_YTELSES_FORDELING (JUSTERT_FORDELING_ID);

create table ${fpinfo.fpsak.schema.navn}.YF_DOKUMENTASJON_PERIODE
(
   ID NUMBER(19) not null
      constraint PK_YF_DOKUMENTASJON_PERIODE
         primary key,
   PERIODER_ID NUMBER(19) not NULL,
   FOM DATE not null,
   TOM DATE not null,
   DOKUMENTASJON_TYPE VARCHAR2(100 char) not null,
   DOKUMENTASJON_KLASSE VARCHAR2(100 char) not null,
   OPPRETTET_AV VARCHAR2(20 char) default 'VL' not null,
   OPPRETTET_TID TIMESTAMP(3) default systimestamp not null,
   ENDRET_AV VARCHAR2(20 char),
   ENDRET_TID TIMESTAMP(3)
);

create index ${fpinfo.fpsak.schema.navn}.IDX_YF_DOKUMENTASJON_PERIODE_1
   on ${fpinfo.fpsak.schema.navn}.YF_DOKUMENTASJON_PERIODE (PERIODER_ID);

create index ${fpinfo.fpsak.schema.navn}.IDX_YF_DOK_PERIODE_2
   on ${fpinfo.fpsak.schema.navn}.YF_DOKUMENTASJON_PERIODE (DOKUMENTASJON_TYPE);

create index ${fpinfo.fpsak.schema.navn}.IDX_YF_DOK_PERIODE_3
   on ${fpinfo.fpsak.schema.navn}.YF_DOKUMENTASJON_PERIODE (DOKUMENTASJON_KLASSE);

create table ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODER
(
   ID NUMBER(19) not null
      constraint PK_UTTAK_RESULTAT_PERIODER
         primary key,
   OPPRETTET_AV VARCHAR2(20 char) default 'VL' not null,
   OPPRETTET_TID TIMESTAMP(3) default systimestamp not null,
   ENDRET_AV VARCHAR2(20 char),
   ENDRET_TID TIMESTAMP(3)
);


create table ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT
(
   ID NUMBER(19) not null
      constraint PK_UTTAK_RESULTAT
         primary key,
   BEHANDLING_RESULTAT_ID NUMBER(19) not NULL,
   OPPRINNELIG_PERIODER_ID NUMBER(19)
      constraint FK_UTTAK_RESULTAT_2
         references ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODER,
   OVERSTYRT_PERIODER_ID NUMBER(19)
      constraint FK_UTTAK_RESULTAT_3
         references ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODER,
   VERSJON NUMBER(19) default 0 not null,
   OPPRETTET_AV VARCHAR2(20 char) default 'VL' not null,
   OPPRETTET_TID TIMESTAMP(3) default systimestamp not null,
   ENDRET_AV VARCHAR2(20 char),
   ENDRET_TID TIMESTAMP(3),
   AKTIV VARCHAR2(1 char) default 'J' not null
      constraint CHK_AKTIV4
         check (aktiv IN ('J', 'N'))
      constraint CHK_AKTIV6
         check (aktiv IN ('J', 'N'))
);

create index ${fpinfo.fpsak.schema.navn}.IDX_UTTAK_RESULTAT_02
   on ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT (BEHANDLING_RESULTAT_ID);

create index ${fpinfo.fpsak.schema.navn}.IDX_UTTAK_RESULTAT_03
   on ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT (OPPRINNELIG_PERIODER_ID);

create index ${fpinfo.fpsak.schema.navn}.IDX_UTTAK_RESULTAT_04
   on ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT (OVERSTYRT_PERIODER_ID);

create table ${fpinfo.fpsak.schema.navn}.PO_INFORMASJON
(
   ID NUMBER(19) not null
      constraint PK_PO_INFORMASJON
         primary key,
   VERSJON NUMBER(19) default 0 not null,
   OPPRETTET_AV VARCHAR2(20 char) default 'VL' not null,
   OPPRETTET_TID TIMESTAMP(3) default systimestamp not null,
   ENDRET_AV VARCHAR2(20 char),
   ENDRET_TID TIMESTAMP(3)
);


create table ${fpinfo.fpsak.schema.navn}.GR_PERSONOPPLYSNING
(
   ID NUMBER(19) not null
      constraint PK_GR_PERSONOPPLYSNING
         primary key,
   BEHANDLING_ID NUMBER(19) not null
      constraint FK_GR_PERSONOPPL_BEH
         references ${fpinfo.fpsak.schema.navn}.BEHANDLING,
   VERSJON NUMBER(19) default 0 not null,
   OPPRETTET_AV VARCHAR2(20 char) default 'VL' not null,
   OPPRETTET_TID TIMESTAMP(3) default systimestamp not null,
   ENDRET_AV VARCHAR2(20 char),
   ENDRET_TID TIMESTAMP(3),
   SO_ANNEN_PART_ID NUMBER(19)
      constraint FK_GR_PERSONOPPL_ANNE_PRT
         references ${fpinfo.fpsak.schema.navn}.SO_ANNEN_PART,
   REGISTRERT_INFORMASJON_ID NUMBER(19)
      constraint FK_GR_PERSONOPPLYSNING_03
         references ${fpinfo.fpsak.schema.navn}.PO_INFORMASJON,
   OVERSTYRT_INFORMASJON_ID NUMBER(19)
      constraint FK_GR_PERSONOPPLYSNING_04
         references ${fpinfo.fpsak.schema.navn}.PO_INFORMASJON,
   AKTIV VARCHAR2(1 char) default 'J' not null
      constraint CHK_AKTIV14
         check (aktiv IN ('J', 'N'))
);

create index ${fpinfo.fpsak.schema.navn}.IDX_GR_PERSONOPPLYSNING_01
   on ${fpinfo.fpsak.schema.navn}.GR_PERSONOPPLYSNING (BEHANDLING_ID);

create index ${fpinfo.fpsak.schema.navn}.IDX_GR_PERSONOPPLYSNING_03
   on ${fpinfo.fpsak.schema.navn}.GR_PERSONOPPLYSNING (REGISTRERT_INFORMASJON_ID);

create index ${fpinfo.fpsak.schema.navn}.IDX_GR_PERSONOPPLYSNING_04
   on ${fpinfo.fpsak.schema.navn}.GR_PERSONOPPLYSNING (OVERSTYRT_INFORMASJON_ID);

create index ${fpinfo.fpsak.schema.navn}.IDX_GR_PERSONOPPLYSNING_6
   on ${fpinfo.fpsak.schema.navn}.GR_PERSONOPPLYSNING (SO_ANNEN_PART_ID);

create table ${fpinfo.fpsak.schema.navn}.PO_RELASJON
(
   ID NUMBER(19) not null
      constraint PK_PO_RELASJON
         primary key,
   OPPRETTET_AV VARCHAR2(20 char) default 'VL' not null,
   OPPRETTET_TID TIMESTAMP(3) default systimestamp not null,
   ENDRET_AV VARCHAR2(20 char),
   ENDRET_TID TIMESTAMP(3),
   PO_INFORMASJON_ID NUMBER(19) not null
      constraint FK_PO_RELASJON_2
         references ${fpinfo.fpsak.schema.navn}.PO_INFORMASJON,
   RELASJONSROLLE VARCHAR2(100 char) not null,
   HAR_SAMME_BOSTED VARCHAR2(1 char),
   FRA_AKTOER_ID VARCHAR2(50 char),
   TIL_AKTOER_ID VARCHAR2(50 char)
);
 
create index ${fpinfo.fpsak.schema.navn}.IDX_PO_RELASJON_1
   on ${fpinfo.fpsak.schema.navn}.PO_RELASJON (PO_INFORMASJON_ID);

create index ${fpinfo.fpsak.schema.navn}.IDX_PO_RELASJON_5
   on ${fpinfo.fpsak.schema.navn}.PO_RELASJON (RELASJONSROLLE);

create index ${fpinfo.fpsak.schema.navn}.IDX_PO_RELASJON_2
   on ${fpinfo.fpsak.schema.navn}.PO_RELASJON (FRA_AKTOER_ID);

create index ${fpinfo.fpsak.schema.navn}.IDX_PO_RELASJON_3
   on ${fpinfo.fpsak.schema.navn}.PO_RELASJON (TIL_AKTOER_ID);

create table ${fpinfo.fpsak.schema.navn}.UTTAK_AKTIVITET
(
   ID NUMBER(19) not null
      constraint PK_UTTAK_AKTIVITET_1
         primary key,
   UTTAK_ARBEID_TYPE VARCHAR2(100 char) not null,
   VERSJON NUMBER(19) default 0 not null,
   OPPRETTET_AV VARCHAR2(20 char) default 'VL' not null,
   OPPRETTET_TID TIMESTAMP(3) default systimestamp not null,
   ENDRET_AV VARCHAR2(20 char),
   ENDRET_TID TIMESTAMP(3),
   ARBEIDSGIVER_AKTOR_ID VARCHAR2(100 char),
   ARBEIDSGIVER_ORGNR VARCHAR2(100 char),
   ARBEIDSFORHOLD_INTERN_ID RAW(16)
);


create index ${fpinfo.fpsak.schema.navn}.IDX_UTTAK_AKTIVITET_1
   on ${fpinfo.fpsak.schema.navn}.UTTAK_AKTIVITET (UTTAK_ARBEID_TYPE);
   
create index ${fpinfo.fpsak.schema.navn}.IDX_UTTAK_AKTIVITET_4
   on ${fpinfo.fpsak.schema.navn}.UTTAK_AKTIVITET (ARBEIDSGIVER_ORGNR);

create index ${fpinfo.fpsak.schema.navn}.IDX_UTTAK_AKTIVITET_11
   on ${fpinfo.fpsak.schema.navn}.UTTAK_AKTIVITET (ARBEIDSFORHOLD_INTERN_ID);

create table ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE_SOKNAD
(
   ID NUMBER(19) not null
      constraint PK_UTTAK_RES_PER_SOKNAD_1
         primary key,
   PERIODE_TYPE VARCHAR2(100 char) not null,
   GRADERING_ARBEIDSTIDSPROSENT NUMBER(5,2),
   VERSJON NUMBER(19) default 0 not null,
   OPPRETTET_AV VARCHAR2(20 char) default 'VL' not null,
   OPPRETTET_TID TIMESTAMP(3) default systimestamp not null,
   ENDRET_AV VARCHAR2(20 char),
   ENDRET_TID TIMESTAMP(3),
   MORS_AKTIVITET VARCHAR2(100 char) default '-',
   MOTTATT_DATO DATE,
   SAMTIDIG_UTTAK VARCHAR2(1 char) default 'N' not null
      constraint CHK_SAMTIDIG_UTTAK1
         check (samtidig_uttak IN ('J', 'N')),
   SAMTIDIG_UTTAKSPROSENT NUMBER(5,2)
);


create table ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE
(
   ID NUMBER(19) not null
      constraint PK_UTTAK_RESULTAT_PERIODE_1
         primary key,
   UTTAK_RESULTAT_PERIODER_ID NUMBER(19) not null
      constraint FK_UTTAK_RESULTAT_PERIODE_01
         references ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODER,
   PERIODE_RESULTAT_TYPE VARCHAR2(100 char) not null,
   PERIODE_RESULTAT_AARSAK VARCHAR2(100 char) not null,
   GRADERING_AVSLAG_AARSAK VARCHAR2(100 char) not null,
   BEGRUNNELSE VARCHAR2(4000 char),
   KL_PERIODE_RESULTAT_AARSAK VARCHAR2(100 char) not null,
   VERSJON NUMBER(19) default 0 not null,
   OPPRETTET_AV VARCHAR2(20 char) default 'VL' not null,
   OPPRETTET_TID TIMESTAMP(3) default systimestamp not null,
   ENDRET_AV VARCHAR2(20 char),
   ENDRET_TID TIMESTAMP(3),
   PERIODE_SOKNAD_ID NUMBER(19)
      constraint FK_UTTAK_RESULTAT_PERIODE_02
         references ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE_SOKNAD,
   UTTAK_UTSETTELSE_TYPE VARCHAR2(100 char) not null,
   FLERBARNSDAGER VARCHAR2(1 char) default 'N' not null
      constraint CHK_FLERBARNSDAGER
         check (flerbarnsdager IN ('J', 'N')),
   GRADERING_INNVILGET VARCHAR2(1 char) default 'N' not null
      constraint CHK_GRADERING_INNVILGET
         check (gradering_innvilget IN ('J', 'N')),
   MANUELT_BEHANDLET VARCHAR2(1 char) default 'N' not null
      constraint CHK_MANUELT_BEHANDLET
         check (manuelt_behandlet IN ('J', 'N')),
   SAMTIDIG_UTTAK VARCHAR2(1 char) default 'N' not null
      constraint CHK_SAMTIDIG_UTTAK
         check (samtidig_uttak IN ('J', 'N')),
   FOM DATE not null,
   TOM DATE not null,
   SAMTIDIG_UTTAKSPROSENT NUMBER(5,2),
   OPPHOLD_AARSAK VARCHAR2(100 char) default '-' not null,
   OVERFOERING_AARSAK VARCHAR2(100 char) default '-' not null
);


create index ${fpinfo.fpsak.schema.navn}.IDX_UTTAK_RESULTAT_PERIODE
   on ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE (UTTAK_RESULTAT_PERIODER_ID);

create index ${fpinfo.fpsak.schema.navn}.IDX_UTTAK_RESULTAT_PERIODE_2
   on ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE (PERIODE_RESULTAT_AARSAK);

create index ${fpinfo.fpsak.schema.navn}.IDX_UTTAK_RESULTAT_PERIODE_5
   on ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE (GRADERING_AVSLAG_AARSAK);

create index ${fpinfo.fpsak.schema.navn}.IDX_UTTAK_RESULTAT_PERIODE_6
   on ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE (PERIODE_RESULTAT_TYPE);

create index ${fpinfo.fpsak.schema.navn}.IDX_UTTAK_RESULTAT_PERIODE_9
   on ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE (PERIODE_SOKNAD_ID);

create index ${fpinfo.fpsak.schema.navn}.IDX_UTTAK_RESULTAT_PERIODE_10
   on ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE (OPPHOLD_AARSAK);

create index ${fpinfo.fpsak.schema.navn}.IDX_UTTAK_RESULTAT_PERIODE_11
   on ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE (OVERFOERING_AARSAK);

create index ${fpinfo.fpsak.schema.navn}.IDX_UTTAK_RES_PER_SOKNAD_1
   on ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE_SOKNAD (PERIODE_TYPE);

create index ${fpinfo.fpsak.schema.navn}.IDX_UTTAK_RESULTAT_SOKNAD_2
   on ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE_SOKNAD (MORS_AKTIVITET);

create table ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE_AKT
(
   ID NUMBER(19) not null
      constraint PK_UTTAK_RES_PERIODE_AKT_1
         primary key,
   UTTAK_RESULTAT_PERIODE_ID NUMBER(19) not null
      constraint FK_UTTAK_RES_PERIODE_AKT_01
         references ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE,
   UTTAK_AKTIVITET_ID NUMBER(19) not null
      constraint FK_UTTAK_RES_PERIODE_AKT_02
         references ${fpinfo.fpsak.schema.navn}.UTTAK_AKTIVITET,
   TREKKONTO VARCHAR2(100 char) not null,
   ARBEIDSTIDSPROSENT NUMBER(5,2) not null
      check (ARBEIDSTIDSPROSENT >= 0),
   UTBETALINGSPROSENT NUMBER(5,2)
      check ("UTBETALINGSPROSENT">=0 AND "UTBETALINGSPROSENT"<=100),
   VERSJON NUMBER(19) default 0 not null,
   OPPRETTET_AV VARCHAR2(20 char) default 'VL' not null,
   OPPRETTET_TID TIMESTAMP(3) default systimestamp not null,
   ENDRET_AV VARCHAR2(20 char),
   ENDRET_TID TIMESTAMP(3),
   GRADERING VARCHAR2(1 char) default 'N' not null
      constraint CHK_GRADERING
         check (gradering IN ('J', 'N')),
   TREKKDAGER_DESIMALER NUMBER(4,1) not null
      check (trekkdager_desimaler >= 0)
);

create index ${fpinfo.fpsak.schema.navn}.IDX_UTTAK_RES_PERIODE_AKT_1
   on ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE_AKT (UTTAK_RESULTAT_PERIODE_ID);

create index ${fpinfo.fpsak.schema.navn}.IDX_UTTAK_RES_PERIODE_AKT_2
   on ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE_AKT (UTTAK_AKTIVITET_ID);

create index ${fpinfo.fpsak.schema.navn}.IDX_UTTAK_RES_PERIODE_AKT_3
   on ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE_AKT (TREKKONTO);
