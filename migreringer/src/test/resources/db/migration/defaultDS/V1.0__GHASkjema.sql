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
   AKTOER_ID VARCHAR2(50 char);
)


create index ${fpinfo.fpsak.schema.navn}.IDX_BRUKER_6
   on ${fpinfo.fpsak.schema.navn}.BRUKER (SPRAK_KODE);
/

create unique index ${fpinfo.fpsak.schema.navn}.UIDX_BRUKER_1
   on ${fpinfo.fpsak.schema.navn}.BRUKER (AKTOER_ID);
/

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
)
/

comment on table ${fpinfo.fpsak.schema.navn}.FAGSAK is 'Fagsak for engangsstønad og foreldrepenger. Alle behandling er koblet mot en fagsak.'
/

comment on column ${fpinfo.fpsak.schema.navn}.FAGSAK.ID is 'Primary Key'
/

comment on column ${fpinfo.fpsak.schema.navn}.FAGSAK.FAGSAK_STATUS is 'FK:FAGSAK_STATUS Fremmednøkkel til kodeverkstabellen som inneholder oversikten over fagstatuser'
/

comment on column ${fpinfo.fpsak.schema.navn}.FAGSAK.BRUKER_ID is 'FK:BRUKER Fremmednøkkel til brukertabellen'
/

comment on column ${fpinfo.fpsak.schema.navn}.FAGSAK.BRUKER_ROLLE is 'FK:BRUKER_ROLLE_TYPE Fremmednøkkel til tabellen som viser brukerens rolle i fagsaken'
/

comment on column ${fpinfo.fpsak.schema.navn}.FAGSAK.YTELSE_TYPE is 'Fremmednøkkel til kodeverkstabellen som inneholder oversikt over ytelser'
/

comment on column ${fpinfo.fpsak.schema.navn}.FAGSAK.SAKSNUMMER is 'Saksnummer (som GSAK har mottatt)'
/

comment on column ${fpinfo.fpsak.schema.navn}.FAGSAK.TIL_INFOTRYGD is 'J hvis saken må behandles av Infotrygd'
/

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
   KONTO_BEREGNING_ID NUMBER(19)
      constraint FK_FAGSAK_RELASJON_3
         references ${fpinfo.fpsak.schema.navn}.STOENADSKONTOBEREGNING,
   AKTIV VARCHAR2(1 char) default 'J' not null
      constraint CHK_AKTIV12
         check (aktiv IN ('J', 'N')),
   DEKNINGSGRAD NUMBER(3) default 100 not null,
   OVERSTYRT_KONTO_BEREGNING_ID NUMBER(19)
      constraint FK_FAGSAK_RELASJON_4
         references ${fpinfo.fpsak.schema.navn}.STOENADSKONTOBEREGNING,
   OVERSTYRT_DEKNINGSGRAD NUMBER(3),
   AVSLUTTNINGSDATO DATE
)
/

comment on table ${fpinfo.fpsak.schema.navn}.FAGSAK_RELASJON is 'Fagsaks relasjon'
/

comment on column ${fpinfo.fpsak.schema.navn}.FAGSAK_RELASJON.ID is 'Primærnøkkel'
/

comment on column ${fpinfo.fpsak.schema.navn}.FAGSAK_RELASJON.FAGSAK_EN_ID is 'FK:'
/

comment on column ${fpinfo.fpsak.schema.navn}.FAGSAK_RELASJON.FAGSAK_TO_ID is 'Fjernnøkkel til andre fagsaken i relasjonen.'
/

comment on column ${fpinfo.fpsak.schema.navn}.FAGSAK_RELASJON.KONTO_BEREGNING_ID is 'FK:'
/

comment on column ${fpinfo.fpsak.schema.navn}.FAGSAK_RELASJON.DEKNINGSGRAD is 'Dekningsgrad for begge søkere'
/

comment on column ${fpinfo.fpsak.schema.navn}.FAGSAK_RELASJON.OVERSTYRT_KONTO_BEREGNING_ID is 'Overstyrt versjon av stønadskontoberegning'
/

comment on column ${fpinfo.fpsak.schema.navn}.FAGSAK_RELASJON.OVERSTYRT_DEKNINGSGRAD is 'Overstyrte verdien av dekningsgrad'
/

comment on column ${fpinfo.fpsak.schema.navn}.FAGSAK_RELASJON.AVSLUTTNINGSDATO is 'Dato for avsluttning av fagsak som avsluttningsbatch bruker.'
/

create index ${fpinfo.fpsak.schema.navn}.IDX_FAGSAK_RELASJON_1
   on ${fpinfo.fpsak.schema.navn}.FAGSAK_RELASJON (FAGSAK_EN_ID)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_FAGSAK_RELASJON_2
   on ${fpinfo.fpsak.schema.navn}.FAGSAK_RELASJON (FAGSAK_TO_ID)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_FAGSAK_RELASJON_3
   on ${fpinfo.fpsak.schema.navn}.FAGSAK_RELASJON (KONTO_BEREGNING_ID)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_FAGSAK_RELASJON_4
   on ${fpinfo.fpsak.schema.navn}.FAGSAK_RELASJON (OVERSTYRT_KONTO_BEREGNING_ID)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_FAGSAK_RELASJON_5
   on ${fpinfo.fpsak.schema.navn}.FAGSAK_RELASJON (AVSLUTTNINGSDATO)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_FAGSAK_1
   on ${fpinfo.fpsak.schema.navn}.FAGSAK (FAGSAK_STATUS)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_FAGSAK_2
   on ${fpinfo.fpsak.schema.navn}.FAGSAK (BRUKER_ROLLE)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_FAGSAK_3
   on ${fpinfo.fpsak.schema.navn}.FAGSAK (BRUKER_ID)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_FAGSAK_7
   on ${fpinfo.fpsak.schema.navn}.FAGSAK (YTELSE_TYPE)
/

create unique index ${fpinfo.fpsak.schema.navn}.UIDX_FAGSAK_1
   on ${fpinfo.fpsak.schema.navn}.FAGSAK (SAKSNUMMER)
/

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
)
/

comment on table ${fpinfo.fpsak.schema.navn}.BEHANDLING is 'Behandling av fagsak'
/

comment on column ${fpinfo.fpsak.schema.navn}.BEHANDLING.ID is 'Primary Key'
/

comment on column ${fpinfo.fpsak.schema.navn}.BEHANDLING.FAGSAK_ID is 'FK: FAGSAK Fremmednøkkel for kobling til fagsak'
/

comment on column ${fpinfo.fpsak.schema.navn}.BEHANDLING.BEHANDLING_STATUS is 'FK: BEHANDLING_STATUS Fremmednøkkel til tabellen som viser status på behandlinger'
/

comment on column ${fpinfo.fpsak.schema.navn}.BEHANDLING.BEHANDLING_TYPE is 'FK: BEHANDLING_TYPE Fremmedøkkel til oversikten over hvilken behandlingstyper som finnes'
/

comment on column ${fpinfo.fpsak.schema.navn}.BEHANDLING.OPPRETTET_DATO is 'Dato når behandlingen ble opprettet.'
/

comment on column ${fpinfo.fpsak.schema.navn}.BEHANDLING.AVSLUTTET_DATO is 'Dato når behandlingen ble avsluttet.'
/

comment on column ${fpinfo.fpsak.schema.navn}.BEHANDLING.ANSVARLIG_SAKSBEHANDLER is 'Id til saksbehandler som oppretter forslag til vedtak ved totrinnsbehandling.'
/

comment on column ${fpinfo.fpsak.schema.navn}.BEHANDLING.ANSVARLIG_BESLUTTER is 'Beslutter som har fattet vedtaket'
/

comment on column ${fpinfo.fpsak.schema.navn}.BEHANDLING.BEHANDLENDE_ENHET is 'NAV-enhet som behandler behandlingen'
/

comment on column ${fpinfo.fpsak.schema.navn}.BEHANDLING.BEHANDLENDE_ENHET_NAVN is 'Navn på behandlende enhet'
/

comment on column ${fpinfo.fpsak.schema.navn}.BEHANDLING.BEHANDLENDE_ENHET_ARSAK is 'Fritekst for hvorfor behandlende enhet har blitt endret'
/

comment on column ${fpinfo.fpsak.schema.navn}.BEHANDLING.BEHANDLINGSTID_FRIST is 'Frist for når behandlingen skal være ferdig'
/

comment on column ${fpinfo.fpsak.schema.navn}.BEHANDLING.STARTPUNKT_TYPE is 'Fremmednøkkel til tabellen som forteller startpunktet slik det er gitt av forretningshendelsen'
/

comment on column ${fpinfo.fpsak.schema.navn}.BEHANDLING.SIST_OPPDATERT_TIDSPUNKT is 'Beskriver når grunnlagene til behandling ble sist innhentet'
/

comment on column ${fpinfo.fpsak.schema.navn}.BEHANDLING.AAPNET_FOR_ENDRING is 'Flagget settes når menyvalget "Åpne behandling for endringer" kjøres.'
/

comment on column ${fpinfo.fpsak.schema.navn}.BEHANDLING.TOTRINNSBEHANDLING is 'Indikerer at behandlingen skal totrinnsbehandles'
/

comment on column ${fpinfo.fpsak.schema.navn}.BEHANDLING.UUID is 'Unik UUID for behandling til utvortes bruk'
/

comment on column ${fpinfo.fpsak.schema.navn}.BEHANDLING.MIGRERT_KILDE is 'Hvilket fagsystem behandlingen er migrert fra'
/

create index ${fpinfo.fpsak.schema.navn}.IDX_BEHANDLING_1
   on ${fpinfo.fpsak.schema.navn}.BEHANDLING (FAGSAK_ID)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_BEHANDLING_2
   on ${fpinfo.fpsak.schema.navn}.BEHANDLING (BEHANDLING_STATUS)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_BEHANDLING_3
   on ${fpinfo.fpsak.schema.navn}.BEHANDLING (BEHANDLING_TYPE)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_BEHANDLING_6
   on ${fpinfo.fpsak.schema.navn}.BEHANDLING (STARTPUNKT_TYPE)
/

create unique index ${fpinfo.fpsak.schema.navn}.UIDX_BEHANDLING_03
   on ${fpinfo.fpsak.schema.navn}.BEHANDLING (UUID)
/

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
)
/

comment on table ${fpinfo.fpsak.schema.navn}.SO_SOEKNAD is 'Søknad om foreldrepenger'
/

comment on column ${fpinfo.fpsak.schema.navn}.SO_SOEKNAD.ID is 'Primary Key'
/

comment on column ${fpinfo.fpsak.schema.navn}.SO_SOEKNAD.SOEKNADSDATO is 'Søknadsdato'
/

comment on column ${fpinfo.fpsak.schema.navn}.SO_SOEKNAD.TILLEGGSOPPLYSNINGER is 'Tilleggsopplysninger'
/

comment on column ${fpinfo.fpsak.schema.navn}.SO_SOEKNAD.FAR_SOEKER_TYPE is 'FK:FAR_SOEKER_TYPE'
/

comment on column ${fpinfo.fpsak.schema.navn}.SO_SOEKNAD.ELEKTRONISK_REGISTRERT is 'Elektronisk registrert søknad'
/

comment on column ${fpinfo.fpsak.schema.navn}.SO_SOEKNAD.MOTTATT_DATO is 'Mottatt dato'
/

comment on column ${fpinfo.fpsak.schema.navn}.SO_SOEKNAD.BEGRUNNELSE_FOR_SEN_INNSENDING is 'Begrunnelse for sen innsending'
/

comment on column ${fpinfo.fpsak.schema.navn}.SO_SOEKNAD.ER_ENDRINGSSOEKNAD is 'Er endringssøknad'
/

comment on column ${fpinfo.fpsak.schema.navn}.SO_SOEKNAD.BRUKER_ROLLE is 'FK:RELASJONSROLLE_TYPE'
/

comment on column ${fpinfo.fpsak.schema.navn}.SO_SOEKNAD.SPRAK_KODE is 'Kode for hvilket språk søker sender søknad på'
/

create index ${fpinfo.fpsak.schema.navn}.IDX_SOEKNAD_10
   on ${fpinfo.fpsak.schema.navn}.SO_SOEKNAD (FAR_SOEKER_TYPE)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_SOEKNAD_11
   on ${fpinfo.fpsak.schema.navn}.SO_SOEKNAD (BRUKER_ROLLE)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_SOEKNAD_12
   on ${fpinfo.fpsak.schema.navn}.SO_SOEKNAD (SPRAK_KODE)
/

create table ${fpinfo.fpsak.schema.navn}.BEHANDLING_RESULTAT
(
   ID NUMBER(19) not null
      constraint PK_BEHANDLING_RESULTAT
         primary key,
   BEHANDLING_ID NUMBER(19) not null
      constraint FK_BEHANDLING_RESULTAT_3
         references ${fpinfo.fpsak.schema.navn}.BEHANDLING,
   INNGANGSVILKAR_RESULTAT_ID NUMBER(19)
      constraint FK_BEHANDLING_RESULTAT_1
         references ${fpinfo.fpsak.schema.navn}.VILKAR_RESULTAT,
   BEREGNING_RESULTAT_ID NUMBER(19)
      constraint FK_BEHANDLING_RESULTAT_2
         references ${fpinfo.fpsak.schema.navn}.BR_LEGACY_ES_BEREGNING_RES,
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
)
/

comment on table ${fpinfo.fpsak.schema.navn}.BEHANDLING_RESULTAT is 'Beregningsresultat. Knytter sammen beregning og behandling.'
/

comment on column ${fpinfo.fpsak.schema.navn}.BEHANDLING_RESULTAT.ID is 'Primary Key'
/

comment on column ${fpinfo.fpsak.schema.navn}.BEHANDLING_RESULTAT.BEHANDLING_ID is 'FK: BEHANDLING Fremmednøkkel for kobling til behandling'
/

comment on column ${fpinfo.fpsak.schema.navn}.BEHANDLING_RESULTAT.INNGANGSVILKAR_RESULTAT_ID is 'FK: INNGANGSVILKAR_RESULTAT Fremmednøkkel til tabellen som viser de avklarte inngangsvilkårene som er grunnlaget for behandlingsresultatet'
/

comment on column ${fpinfo.fpsak.schema.navn}.BEHANDLING_RESULTAT.BEREGNING_RESULTAT_ID is 'FK: BEREGNING_RESULTAT Fremmednøkkel til tabellen som inneholder beregningsresultatet'
/

comment on column ${fpinfo.fpsak.schema.navn}.BEHANDLING_RESULTAT.BEHANDLING_RESULTAT_TYPE is 'Resultat av behandlingen'
/

comment on column ${fpinfo.fpsak.schema.navn}.BEHANDLING_RESULTAT.AVSLAG_ARSAK is 'Årsakskode for avslag. Foreign key til AVSLAGSARSAK.'
/

comment on column ${fpinfo.fpsak.schema.navn}.BEHANDLING_RESULTAT.AVSLAG_ARSAK_FRITEKST is 'Begrunnelse for avslag av søknad.'
/

comment on column ${fpinfo.fpsak.schema.navn}.BEHANDLING_RESULTAT.RETTEN_TIL is 'FK: RETTEN_TIL'
/

comment on column ${fpinfo.fpsak.schema.navn}.BEHANDLING_RESULTAT.VEDTAKSBREV is 'FK: VEDTAKSBREV'
/

comment on column ${fpinfo.fpsak.schema.navn}.BEHANDLING_RESULTAT.OVERSKRIFT is 'Overskrift felt brukt som hovedoverskrift i frikestbrev'
/

comment on column ${fpinfo.fpsak.schema.navn}.BEHANDLING_RESULTAT.FRITEKSTBREV is 'Fritekstbrev felt brukt som hovedoverskrift i frikestbrev'
/

comment on column ${fpinfo.fpsak.schema.navn}.BEHANDLING_RESULTAT.ENDRET_DEKNINGSGRAD is 'Behandlingen har endret dekningsgrad'
/

comment on column ${fpinfo.fpsak.schema.navn}.BEHANDLING_RESULTAT.ENDRET_STOENADSKONTO is 'Behandlingen har endret stønadskonto'
/

create index ${fpinfo.fpsak.schema.navn}.IDX_BEHANDLING_RESULTAT_1
   on ${fpinfo.fpsak.schema.navn}.BEHANDLING_RESULTAT (INNGANGSVILKAR_RESULTAT_ID)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_BEHANDLING_RESULTAT_2
   on ${fpinfo.fpsak.schema.navn}.BEHANDLING_RESULTAT (BEREGNING_RESULTAT_ID)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_BEHANDLING_RESULTAT_3
   on ${fpinfo.fpsak.schema.navn}.BEHANDLING_RESULTAT (BEHANDLING_ID)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_BEHANDLING_RESULTAT_4
   on ${fpinfo.fpsak.schema.navn}.BEHANDLING_RESULTAT (BEHANDLING_RESULTAT_TYPE)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_BEHANDLING_RESULTAT_6
   on ${fpinfo.fpsak.schema.navn}.BEHANDLING_RESULTAT (AVSLAG_ARSAK)
/

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
)
/

comment on table ${fpinfo.fpsak.schema.navn}.AKSJONSPUNKT is 'Aksjoner som en saksbehandler må utføre manuelt.'
/

comment on column ${fpinfo.fpsak.schema.navn}.AKSJONSPUNKT.ID is 'Primary Key'
/

comment on column ${fpinfo.fpsak.schema.navn}.AKSJONSPUNKT.PERIODE_FOM is 'Angir starttidspunkt dersom aksjonspunktet gjelder en spesifikk periode. Brukes for aksjonspunkt som kan repteres flere ganger for en behandling.'
/

comment on column ${fpinfo.fpsak.schema.navn}.AKSJONSPUNKT.PERIODE_TOM is 'Angir sluttidspunkt dersom aksjonspunktet gjelder en spesifikk periode.'
/

comment on column ${fpinfo.fpsak.schema.navn}.AKSJONSPUNKT.BEGRUNNELSE is 'Begrunnelse for endringer gjort i forbindelse med aksjonspunktet.'
/

comment on column ${fpinfo.fpsak.schema.navn}.AKSJONSPUNKT.TOTRINN_BEHANDLING is 'Indikerer at aksjonspunkter krever en totrinnsbehandling'
/

comment on column ${fpinfo.fpsak.schema.navn}.AKSJONSPUNKT.BEHANDLING_STEG_FUNNET is 'Hvilket steg ble dette aksjonspunktet funnet i?'
/

comment on column ${fpinfo.fpsak.schema.navn}.AKSJONSPUNKT.AKSJONSPUNKT_STATUS is 'FK:AKSJONSPUNKT_STATUS Fremmednøkkel til tabellen som inneholder status på aksjonspunktene'
/

comment on column ${fpinfo.fpsak.schema.navn}.AKSJONSPUNKT.AKSJONSPUNKT_DEF is 'Aksjonspunkt kode'
/

comment on column ${fpinfo.fpsak.schema.navn}.AKSJONSPUNKT.FRIST_TID is 'Behandling blir automatisk gjenopptatt etter dette tidspunktet'
/

comment on column ${fpinfo.fpsak.schema.navn}.AKSJONSPUNKT.VENT_AARSAK is 'Årsak for at behandling er satt på vent'
/

comment on column ${fpinfo.fpsak.schema.navn}.AKSJONSPUNKT.BEHANDLING_ID is 'Fremmednøkkel for kobling til behandling'
/

create unique index ${fpinfo.fpsak.schema.navn}.IDX_AKSJONSPUNKT_1
   on ${fpinfo.fpsak.schema.navn}.AKSJONSPUNKT (BEHANDLING_ID, AKSJONSPUNKT_DEF)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_AKSJONSPUNKT_6
   on ${fpinfo.fpsak.schema.navn}.AKSJONSPUNKT (BEHANDLING_STEG_FUNNET)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_AKSJONSPUNKT_7
   on ${fpinfo.fpsak.schema.navn}.AKSJONSPUNKT (AKSJONSPUNKT_DEF)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_AKSJONSPUNKT_8
   on ${fpinfo.fpsak.schema.navn}.AKSJONSPUNKT (VENT_AARSAK)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_AKSJONSPUNKT_9
   on ${fpinfo.fpsak.schema.navn}.AKSJONSPUNKT (AKSJONSPUNKT_STATUS)
/

alter table ${fpinfo.fpsak.schema.navn}.AKSJONSPUNKT
   add constraint CHK_UNIQUE_BEH_AD
      unique (BEHANDLING_ID, AKSJONSPUNKT_DEF)
/

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
)
/

comment on table ${fpinfo.fpsak.schema.navn}.BEHANDLING_VEDTAK is 'Vedtak koblet til en behandling via et behandlingsresultat.'
/

comment on column ${fpinfo.fpsak.schema.navn}.BEHANDLING_VEDTAK.ID is 'Primary Key'
/

comment on column ${fpinfo.fpsak.schema.navn}.BEHANDLING_VEDTAK.VEDTAK_DATO is 'Vedtaksdato.'
/

comment on column ${fpinfo.fpsak.schema.navn}.BEHANDLING_VEDTAK.ANSVARLIG_SAKSBEHANDLER is 'Ansvarlig saksbehandler som godkjente vedtaket.'
/

comment on column ${fpinfo.fpsak.schema.navn}.BEHANDLING_VEDTAK.BEHANDLING_RESULTAT_ID is 'FK:BEHANDLING_RESULTAT Fremmednøkkel til tabellen som viser behandlingsresultatet'
/

comment on column ${fpinfo.fpsak.schema.navn}.BEHANDLING_VEDTAK.VEDTAK_RESULTAT_TYPE is 'FK:VEDTAK_RESULTAT_TYPE Fremmednøkkel til tabellen som viser innholdet i vedtaket'
/

comment on column ${fpinfo.fpsak.schema.navn}.BEHANDLING_VEDTAK.IVERKSETTING_STATUS is 'Status for iverksettingssteget'
/

comment on column ${fpinfo.fpsak.schema.navn}.BEHANDLING_VEDTAK.BESLUTNING is 'Hvorvidt vedtaket er et beslutningsvedtak. Et beslutningsvedtak er et vedtak med samme utfall som forrige vedtak'
/

create index ${fpinfo.fpsak.schema.navn}.IDX_VEDTAK_1
   on ${fpinfo.fpsak.schema.navn}.BEHANDLING_VEDTAK (VEDTAK_RESULTAT_TYPE)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_VEDTAK_2
   on ${fpinfo.fpsak.schema.navn}.BEHANDLING_VEDTAK (ANSVARLIG_SAKSBEHANDLER)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_VEDTAK_3
   on ${fpinfo.fpsak.schema.navn}.BEHANDLING_VEDTAK (VEDTAK_DATO)
/

create unique index ${fpinfo.fpsak.schema.navn}.UIDX_BEHANDLING_VEDTAK_1
   on ${fpinfo.fpsak.schema.navn}.BEHANDLING_VEDTAK (BEHANDLING_RESULTAT_ID)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_BEHANDLING_VEDTAK_6
   on ${fpinfo.fpsak.schema.navn}.BEHANDLING_VEDTAK (IVERKSETTING_STATUS)
/

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
)
/

comment on table ${fpinfo.fpsak.schema.navn}.MOTTATT_DOKUMENT is 'Mottatt dokument som er lagret i Joark'
/

comment on column ${fpinfo.fpsak.schema.navn}.MOTTATT_DOKUMENT.ID is 'Primary Key'
/

comment on column ${fpinfo.fpsak.schema.navn}.MOTTATT_DOKUMENT.JOURNALPOST_ID is 'FK: Journalpostens ID i JOARK'
/

comment on column ${fpinfo.fpsak.schema.navn}.MOTTATT_DOKUMENT.TYPE is 'FK:DOKUMENT_TYPE Fremmednøkkel for kobling til dokumenttype'
/

comment on column ${fpinfo.fpsak.schema.navn}.MOTTATT_DOKUMENT.BEHANDLING_ID is 'FK:BEHANDLING Fremmednøkkel for kobling til behandling'
/

comment on column ${fpinfo.fpsak.schema.navn}.MOTTATT_DOKUMENT.MOTTATT_DATO is 'Mottatt dato'
/

comment on column ${fpinfo.fpsak.schema.navn}.MOTTATT_DOKUMENT.ELEKTRONISK_REGISTRERT is 'Elektronisk registrert søknad'
/

comment on column ${fpinfo.fpsak.schema.navn}.MOTTATT_DOKUMENT.FAGSAK_ID is 'FK: Fremmednøkkel til Fagsak'
/

comment on column ${fpinfo.fpsak.schema.navn}.MOTTATT_DOKUMENT.FORSENDELSE_ID is 'Unik ID for forsendelsen'
/

comment on column ${fpinfo.fpsak.schema.navn}.MOTTATT_DOKUMENT.XML_PAYLOAD is 'Strukturert informasjon fra det mottatte dokumentet'
/

comment on column ${fpinfo.fpsak.schema.navn}.MOTTATT_DOKUMENT.DOKUMENT_KATEGORI is 'Dokumentkategori'
/

comment on column ${fpinfo.fpsak.schema.navn}.MOTTATT_DOKUMENT.JOURNAL_ENHET is 'Journalførende enhet fra forside dersom satt'
/

comment on column ${fpinfo.fpsak.schema.navn}.MOTTATT_DOKUMENT.MOTTATT_TIDSPUNKT is 'Arkiveringstidspunkt for journalposten'
/

comment on column ${fpinfo.fpsak.schema.navn}.MOTTATT_DOKUMENT.KANALREFERANSE is 'Kildereferanse for journalposten'
/

create index ${fpinfo.fpsak.schema.navn}.IDX_MOTTATTE_DOKUMENT_1
   on ${fpinfo.fpsak.schema.navn}.MOTTATT_DOKUMENT (TYPE)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_MOTTATT_DOKUMENT_6
   on ${fpinfo.fpsak.schema.navn}.MOTTATT_DOKUMENT (BEHANDLING_ID)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_MOTTATT_DOKUMENT_2
   on ${fpinfo.fpsak.schema.navn}.MOTTATT_DOKUMENT (DOKUMENT_KATEGORI)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_MOTTATT_DOKUMENT_7
   on ${fpinfo.fpsak.schema.navn}.MOTTATT_DOKUMENT (FAGSAK_ID)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_MOTTATT_DOKUMENT_8
   on ${fpinfo.fpsak.schema.navn}.MOTTATT_DOKUMENT (FORSENDELSE_ID)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_MOTTATT_DOKUMENT_9
   on ${fpinfo.fpsak.schema.navn}.MOTTATT_DOKUMENT (JOURNALPOST_ID)
/

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
)
/

comment on table ${fpinfo.fpsak.schema.navn}.SO_ANNEN_PART is 'Informasjon om annen part i en søknad'
/

comment on column ${fpinfo.fpsak.schema.navn}.SO_ANNEN_PART.ID is 'Primary Key'
/

comment on column ${fpinfo.fpsak.schema.navn}.SO_ANNEN_PART.UTL_PERSON_IDENT is 'Utenlandsk person ident'
/

comment on column ${fpinfo.fpsak.schema.navn}.SO_ANNEN_PART.UTL_PERSON_IDENT_LAND is 'Utenlandsk person ident landkode (ISO 3-bokstav kode)'
/

comment on column ${fpinfo.fpsak.schema.navn}.SO_ANNEN_PART.ARSAK is 'Årsak'
/

comment on column ${fpinfo.fpsak.schema.navn}.SO_ANNEN_PART.BEGRUNNELSE is 'Begrunnelse'
/

comment on column ${fpinfo.fpsak.schema.navn}.SO_ANNEN_PART.TYPE is 'Mor eller far'
/

comment on column ${fpinfo.fpsak.schema.navn}.SO_ANNEN_PART.NAVN is 'Navn på annen forelder i søknad'
/

comment on column ${fpinfo.fpsak.schema.navn}.SO_ANNEN_PART.AKTOER_ID is 'Akt�rid (fra NAV Akt�rregister)'
/

create index ${fpinfo.fpsak.schema.navn}.IDX_SOEKNAD_ANNEN_PART_1
   on ${fpinfo.fpsak.schema.navn}.SO_ANNEN_PART (UTL_PERSON_IDENT_LAND)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_SOEKNAD_ANNEN_PART_2
   on ${fpinfo.fpsak.schema.navn}.SO_ANNEN_PART (TYPE)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_SO_ANNEN_PART_3
   on ${fpinfo.fpsak.schema.navn}.SO_ANNEN_PART (AKTOER_ID)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_SO_ANNEN_PART_4
   on ${fpinfo.fpsak.schema.navn}.SO_ANNEN_PART (UTL_PERSON_IDENT)
/

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
)
/

comment on table ${fpinfo.fpsak.schema.navn}.BEHANDLING_ARSAK is 'Årsak for rebehandling'
/

comment on column ${fpinfo.fpsak.schema.navn}.BEHANDLING_ARSAK.ID is 'Primary Key'
/

comment on column ${fpinfo.fpsak.schema.navn}.BEHANDLING_ARSAK.BEHANDLING_ID is 'FK: BEHANDLING Fremmednøkkel for kobling til behandling'
/

comment on column ${fpinfo.fpsak.schema.navn}.BEHANDLING_ARSAK.BEHANDLING_ARSAK_TYPE is 'FK: BEHANDLING_ARSAK_TYPE Fremmednøkkel til oversikten over hvilke årsaker en behandling kan begrunnes med'
/

comment on column ${fpinfo.fpsak.schema.navn}.BEHANDLING_ARSAK.ORIGINAL_BEHANDLING_ID is 'FK: BEHANDLING Fremmednøkkel for kobling til behandlingen denne raden i tabellen hører til'
/

comment on column ${fpinfo.fpsak.schema.navn}.BEHANDLING_ARSAK.MANUELT_OPPRETTET is 'Angir om behandlingsårsaken oppstod når en behandling ble manuelt opprettet. Brukes til å utlede om behandlingen ble manuelt opprettet.'
/

create index ${fpinfo.fpsak.schema.navn}.IDX_BEHANDLING_ARSAK_6
   on ${fpinfo.fpsak.schema.navn}.BEHANDLING_ARSAK (BEHANDLING_ID)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_BEHANDLING_ARSAK_7
   on ${fpinfo.fpsak.schema.navn}.BEHANDLING_ARSAK (ORIGINAL_BEHANDLING_ID)
/

create table ${fpinfo.fpsak.schema.navn}.KODELISTE
(
   KODEVERK VARCHAR2(100 char) not null
      constraint FK_KODELISTE_01
         references ${fpinfo.fpsak.schema.navn}.KODEVERK,
   KODE VARCHAR2(100 char) not null,
   OFFISIELL_KODE VARCHAR2(1000 char),
   BESKRIVELSE VARCHAR2(4000 char),
   GYLDIG_FOM DATE default sysdate not null,
   GYLDIG_TOM DATE default to_date('31.12.9999','dd.mm.yyyy') not null,
   OPPRETTET_AV VARCHAR2(200 char) default 'VL' not null,
   OPPRETTET_TID TIMESTAMP(3) default systimestamp not null,
   ENDRET_AV VARCHAR2(200 char),
   ENDRET_TID TIMESTAMP(3)
)
/

comment on table ${fpinfo.fpsak.schema.navn}.KODELISTE is 'Inneholder lister av koder for alle Kodeverk som benyttes i applikasjonen.  Både offisielle (synkronisert fra sentralt hold i Nav) såvel som interne Kodeverk.  Offisielle koder skiller seg ut ved at nav_offisiell_kode er populert. Følgelig vil gyldig_tom/fom, navn, språk og beskrivelse lastes ned fra Kodeverkklienten eller annen kilde sentralt'
/

comment on column ${fpinfo.fpsak.schema.navn}.KODELISTE.KODEVERK is '(PK) og FK - kodeverk'
/

comment on column ${fpinfo.fpsak.schema.navn}.KODELISTE.KODE is '(PK) Unik kode innenfor kodeverk. Denne koden er alltid brukt internt'
/

comment on column ${fpinfo.fpsak.schema.navn}.KODELISTE.OFFISIELL_KODE is '(Optional) Offisiell kode hos kodeverkeier. Denne kan avvike fra kode der systemet har egne koder. Kan brukes til å veksle inn kode i offisiell kode når det trengs for integrasjon med andre systemer'
/

comment on column ${fpinfo.fpsak.schema.navn}.KODELISTE.BESKRIVELSE is 'Beskrivelse av koden'
/

comment on column ${fpinfo.fpsak.schema.navn}.KODELISTE.GYLDIG_FOM is 'Dato Kodeverket er gyldig fra og med'
/

comment on column ${fpinfo.fpsak.schema.navn}.KODELISTE.GYLDIG_TOM is 'Dato Kodeverket er gyldig til og med'
/

create index ${fpinfo.fpsak.schema.navn}.IDX_KODELISTE_1
   on ${fpinfo.fpsak.schema.navn}.KODELISTE (KODE)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_KODELISTE_2
   on ${fpinfo.fpsak.schema.navn}.KODELISTE (OFFISIELL_KODE)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_KODELISTE_3
   on ${fpinfo.fpsak.schema.navn}.KODELISTE (GYLDIG_FOM)
/

create unique index ${fpinfo.fpsak.schema.navn}.UIDX_KODELISTE_1
   on ${fpinfo.fpsak.schema.navn}.KODELISTE (KODE, KODEVERK)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_KODELISTE_6
   on ${fpinfo.fpsak.schema.navn}.KODELISTE (KODEVERK)
/

alter table ${fpinfo.fpsak.schema.navn}.KODELISTE
   add constraint PK_KODELISTE
      primary key (KODE, KODEVERK)
/

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
)
/

comment on table ${fpinfo.fpsak.schema.navn}.FH_FAMILIE_HENDELSE is 'Familie hendelse'
/

comment on column ${fpinfo.fpsak.schema.navn}.FH_FAMILIE_HENDELSE.ID is 'Primærnøkkel'
/

comment on column ${fpinfo.fpsak.schema.navn}.FH_FAMILIE_HENDELSE.ANTALL_BARN is 'Antall barn'
/

comment on column ${fpinfo.fpsak.schema.navn}.FH_FAMILIE_HENDELSE.FAMILIE_HENDELSE_TYPE is 'Type hendelse(Fødsel, Termin, Adopsjon eller omsorgsovertakelse)'
/

comment on column ${fpinfo.fpsak.schema.navn}.FH_FAMILIE_HENDELSE.MOR_FOR_SYK_VED_FODSEL is 'Om mor er syk ved fødsel'
/

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
)
/

comment on table ${fpinfo.fpsak.schema.navn}.FH_TERMINBEKREFTELSE is 'Informasjon om en bekreftet terminbekreftelse som er registrert av saksbehandler'
/

comment on column ${fpinfo.fpsak.schema.navn}.FH_TERMINBEKREFTELSE.ID is 'Primary Key'
/

comment on column ${fpinfo.fpsak.schema.navn}.FH_TERMINBEKREFTELSE.TERMINDATO is 'Termindato'
/

comment on column ${fpinfo.fpsak.schema.navn}.FH_TERMINBEKREFTELSE.UTSTEDT_DATO is 'Utstedt dato'
/

comment on column ${fpinfo.fpsak.schema.navn}.FH_TERMINBEKREFTELSE.FAMILIE_HENDELSE_ID is 'FK: Fremmednøkkel som knytter terminbekreftelsen til familiehendelse'
/

comment on column ${fpinfo.fpsak.schema.navn}.FH_TERMINBEKREFTELSE.NAVN is 'Legen som utstedete terminbekreftelsen.'
/

create index ${fpinfo.fpsak.schema.navn}.IDX_TERMINBEKREFTELSE_1
   on ${fpinfo.fpsak.schema.navn}.FH_TERMINBEKREFTELSE (FAMILIE_HENDELSE_ID)
/

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
)
/

comment on table ${fpinfo.fpsak.schema.navn}.FH_ADOPSJON is 'Adopsjon inneholder bekreftet informasjon angående en adopsjon.'
/

comment on column ${fpinfo.fpsak.schema.navn}.FH_ADOPSJON.ID is 'Primary Key'
/

comment on column ${fpinfo.fpsak.schema.navn}.FH_ADOPSJON.OMSORGSOVERTAKELSE_DATO is 'Angir datoen for når man overtok omsorgen for barnet/barna.'
/

comment on column ${fpinfo.fpsak.schema.navn}.FH_ADOPSJON.EKTEFELLES_BARN is 'Angir om det er adopsjon av ektefelles barn.'
/

comment on column ${fpinfo.fpsak.schema.navn}.FH_ADOPSJON.ADOPTERER_ALENE is 'Angir om man adopterer alene.'
/

comment on column ${fpinfo.fpsak.schema.navn}.FH_ADOPSJON.FAMILIE_HENDELSE_ID is 'FK: Fremmednøkkel som knytter adopsjonsinformasjonen til familiehendelse'
/

comment on column ${fpinfo.fpsak.schema.navn}.FH_ADOPSJON.OMSORG_VILKAAR_TYPE is 'Type foreldreansvar/omsorg ansvar (for omsorgovertakelse)'
/

comment on column ${fpinfo.fpsak.schema.navn}.FH_ADOPSJON.FORELDREANSVAR_OPPFYLT_DATO is 'Dato for når foreldreansvaret ble oppfylt.'
/

comment on column ${fpinfo.fpsak.schema.navn}.FH_ADOPSJON.ANKOMST_NORGE_DATO is 'Datoen barnet ankom norge.'
/

create index ${fpinfo.fpsak.schema.navn}.IDX_ADOPSJON_1
   on ${fpinfo.fpsak.schema.navn}.FH_ADOPSJON (FAMILIE_HENDELSE_ID)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_FH_ADOPSJON_6
   on ${fpinfo.fpsak.schema.navn}.FH_ADOPSJON (OMSORG_VILKAAR_TYPE)
/

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
)
/

comment on table ${fpinfo.fpsak.schema.navn}.FH_UIDENTIFISERT_BARN is 'Fødselsdatoene på barna'
/

comment on column ${fpinfo.fpsak.schema.navn}.FH_UIDENTIFISERT_BARN.ID is 'Primary Key'
/

comment on column ${fpinfo.fpsak.schema.navn}.FH_UIDENTIFISERT_BARN.FOEDSEL_DATO is 'Fødselsdato for barn'
/

comment on column ${fpinfo.fpsak.schema.navn}.FH_UIDENTIFISERT_BARN.BARN_NUMMER is 'Barn nummer i et barnekull'
/

comment on column ${fpinfo.fpsak.schema.navn}.FH_UIDENTIFISERT_BARN.FAMILIE_HENDELSE_ID is 'FK: Fremmednøkkel som knytter fødselsdato for barn til familiehendelse'
/

comment on column ${fpinfo.fpsak.schema.navn}.FH_UIDENTIFISERT_BARN.DOEDSDATO is 'Barnets dødsdato'
/

create unique index ${fpinfo.fpsak.schema.navn}.UIDX_UIDENTIFISERT_BARN_1
   on ${fpinfo.fpsak.schema.navn}.FH_UIDENTIFISERT_BARN (FAMILIE_HENDELSE_ID, BARN_NUMMER)
/

create index ${fpinfo.fpsak.schema.navn}.UIDX_UIDENTIFISERT_BARN_2
   on ${fpinfo.fpsak.schema.navn}.FH_UIDENTIFISERT_BARN (FAMILIE_HENDELSE_ID)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_FH_FAMILIE_HENDELSE_6
   on ${fpinfo.fpsak.schema.navn}.FH_FAMILIE_HENDELSE (FAMILIE_HENDELSE_TYPE)
/

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
)
/

comment on table ${fpinfo.fpsak.schema.navn}.GR_FAMILIE_HENDELSE is 'Grunnlag familie-hendelse'
/

comment on column ${fpinfo.fpsak.schema.navn}.GR_FAMILIE_HENDELSE.ID is 'Primærnøkkel'
/

comment on column ${fpinfo.fpsak.schema.navn}.GR_FAMILIE_HENDELSE.BEHANDLING_ID is 'FK: Fremmednøkkel for kobling til behandling'
/

comment on column ${fpinfo.fpsak.schema.navn}.GR_FAMILIE_HENDELSE.SOEKNAD_FAMILIE_HENDELSE_ID is 'FK: Fremmednøkkel til tabell som knytter beregningsgrunnlagforekomsten til behandling'
/

comment on column ${fpinfo.fpsak.schema.navn}.GR_FAMILIE_HENDELSE.BEKREFTET_FAMILIE_HENDELSE_ID is 'FK:'
/

comment on column ${fpinfo.fpsak.schema.navn}.GR_FAMILIE_HENDELSE.OVERSTYRT_FAMILIE_HENDELSE_ID is 'FK'
/

comment on column ${fpinfo.fpsak.schema.navn}.GR_FAMILIE_HENDELSE.AKTIV is 'Aktivflagg som sier om grunnlaget er aktiv eller ei.'
/

create unique index ${fpinfo.fpsak.schema.navn}.UIDX_GR_FAMILIE_HENDELSE_01
   on ${fpinfo.fpsak.schema.navn}.GR_FAMILIE_HENDELSE (CASE "AKTIV" WHEN 'J' THEN "BEHANDLING_ID" ELSE NULL END , CASE "AKTIV" WHEN 'J' THEN "AKTIV" ELSE NULL END )
/

create index ${fpinfo.fpsak.schema.navn}.IDX_FAMILIE_HENDELSE_1
   on ${fpinfo.fpsak.schema.navn}.GR_FAMILIE_HENDELSE (SOEKNAD_FAMILIE_HENDELSE_ID)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_FAMILIE_HENDELSE_2
   on ${fpinfo.fpsak.schema.navn}.GR_FAMILIE_HENDELSE (BEKREFTET_FAMILIE_HENDELSE_ID)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_FAMILIE_HENDELSE_3
   on ${fpinfo.fpsak.schema.navn}.GR_FAMILIE_HENDELSE (OVERSTYRT_FAMILIE_HENDELSE_ID)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_GR_FAMILIE_HENDELSE_6
   on ${fpinfo.fpsak.schema.navn}.GR_FAMILIE_HENDELSE (BEHANDLING_ID)
/

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
)
/

comment on table ${fpinfo.fpsak.schema.navn}.SO_RETTIGHET is 'Søkers oppgitte rettigheter'
/

comment on column ${fpinfo.fpsak.schema.navn}.SO_RETTIGHET.ID is 'Primærnøkkel'
/

comment on column ${fpinfo.fpsak.schema.navn}.SO_RETTIGHET.ANNEN_FORELDRE_RETT is 'Søker har oppgitt verdi om den andre foreldren har rett'
/

comment on column ${fpinfo.fpsak.schema.navn}.SO_RETTIGHET.OMSORG_I_HELE_PERIODEN is 'Søker har oppgitt verdi om omsorg hele perioden'
/

comment on column ${fpinfo.fpsak.schema.navn}.SO_RETTIGHET.ALENEOMSORG is 'Søker har oppgitt verdi om søkern har aleneomsorg'
/

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
)
/

comment on table ${fpinfo.fpsak.schema.navn}.SO_DEKNINGSGRAD is 'Søkers oppgitte dekningsgradsinformasjon'
/

comment on column ${fpinfo.fpsak.schema.navn}.SO_DEKNINGSGRAD.ID is 'Primærnøkkel'
/

comment on column ${fpinfo.fpsak.schema.navn}.SO_DEKNINGSGRAD.DEKNINGSGRAD is 'Søker har oppgitt følgende dekningsgrad'
/

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
)
/

comment on table ${fpinfo.fpsak.schema.navn}.YF_FORDELING is 'Mange til mange tabell mellom grunnlaget og periodene'
/

comment on column ${fpinfo.fpsak.schema.navn}.YF_FORDELING.ID is 'Primærnøkkel'
/

comment on column ${fpinfo.fpsak.schema.navn}.YF_FORDELING.ANNENFORELDERERINFORMERT is 'Om det er huket av for at den andre forelder er kjent med hvilke perioder det er søkt om'
/

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
)
/

comment on table ${fpinfo.fpsak.schema.navn}.YF_FORDELING_PERIODE is 'Mange til mange tabell mellom grunnlaget og periodene'
/

comment on column ${fpinfo.fpsak.schema.navn}.YF_FORDELING_PERIODE.ID is 'Primary key'
/

comment on column ${fpinfo.fpsak.schema.navn}.YF_FORDELING_PERIODE.FORDELING_ID is 'FK:YF_FORDELING Fremmednøkkel til tabell tabell som knytter grunnlag og perioder sammen'
/

comment on column ${fpinfo.fpsak.schema.navn}.YF_FORDELING_PERIODE.FOM is 'Fra-og-med dato for perioden'
/

comment on column ${fpinfo.fpsak.schema.navn}.YF_FORDELING_PERIODE.TOM is 'Til-og-med dato for perioden'
/

comment on column ${fpinfo.fpsak.schema.navn}.YF_FORDELING_PERIODE.PERIODE_TYPE is 'Hva det er søkt om (må sees i sammenheng med AARSAK-type ifbm utsettelse)'
/

comment on column ${fpinfo.fpsak.schema.navn}.YF_FORDELING_PERIODE.AARSAK_TYPE is 'Brukes ved søknad om utsettelse, opplyser årsak til dette'
/

comment on column ${fpinfo.fpsak.schema.navn}.YF_FORDELING_PERIODE.KL_AARSAK_TYPE is 'Referanse til KODEVERK-kolonnen i KODELISTE-tabellen'
/

comment on column ${fpinfo.fpsak.schema.navn}.YF_FORDELING_PERIODE.ARBEIDSPROSENT is 'Hvor mye søker har tenkt å arbeide i perioden. Brukes ved gradert uttak.'
/

comment on column ${fpinfo.fpsak.schema.navn}.YF_FORDELING_PERIODE.MORS_AKTIVITET is 'Hva mor skal gjøre i perioden (brukes ifbm aktivitetskrav)'
/

comment on column ${fpinfo.fpsak.schema.navn}.YF_FORDELING_PERIODE.BEGRUNNELSE is 'Saksbehandlers begrunnelse. Brukes når saksbehandler dokumenterer/endrer perioder'
/

comment on column ${fpinfo.fpsak.schema.navn}.YF_FORDELING_PERIODE.VURDERING_TYPE is 'Saksbehandlers vurdering av perioden ifbm avklaring av fakta.'
/

comment on column ${fpinfo.fpsak.schema.navn}.YF_FORDELING_PERIODE.FORDELING_PERIODE_KILDE is 'Kilden til denne perioden, fra søknad eller tidligere vedtak'
/

comment on column ${fpinfo.fpsak.schema.navn}.YF_FORDELING_PERIODE.ARBEIDSTAKER is 'Er arbeidstype arbeidstaker'
/

comment on column ${fpinfo.fpsak.schema.navn}.YF_FORDELING_PERIODE.SAMTIDIG_UTTAK is 'Om søker ønsker samtidig uttak'
/

comment on column ${fpinfo.fpsak.schema.navn}.YF_FORDELING_PERIODE.FLERBARNSDAGER is 'Angir om flerbarnsdager skal benyttes i perioden'
/

comment on column ${fpinfo.fpsak.schema.navn}.YF_FORDELING_PERIODE.SAMTIDIG_UTTAKSPROSENT is 'Hvor stor søker har tenkt å ha for samtidig uttak'
/

comment on column ${fpinfo.fpsak.schema.navn}.YF_FORDELING_PERIODE.ARBEIDSGIVER_AKTOR_ID is 'Aktør til personlig foretak.'
/

comment on column ${fpinfo.fpsak.schema.navn}.YF_FORDELING_PERIODE.FRILANSER is 'Er arbeidstype frilanser'
/

comment on column ${fpinfo.fpsak.schema.navn}.YF_FORDELING_PERIODE.SELVSTENDIG is 'Er arbeidstype selvstendig'
/

comment on column ${fpinfo.fpsak.schema.navn}.YF_FORDELING_PERIODE.ARBEIDSGIVER_ORGNR is 'Organisasjonsnummer for arbeidsgivere som er virksomheter'
/

create index ${fpinfo.fpsak.schema.navn}.IDX_YF_FORDELING_PERIODE_1
   on ${fpinfo.fpsak.schema.navn}.YF_FORDELING_PERIODE (MORS_AKTIVITET)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_YF_FORDELING_PERIODE_2
   on ${fpinfo.fpsak.schema.navn}.YF_FORDELING_PERIODE (PERIODE_TYPE)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_YF_FORDELING_PERIODE_3
   on ${fpinfo.fpsak.schema.navn}.YF_FORDELING_PERIODE (AARSAK_TYPE)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_YF_FORDELING_PERIODE_4
   on ${fpinfo.fpsak.schema.navn}.YF_FORDELING_PERIODE (FORDELING_ID)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_YF_FORDELING_PERIODE_6
   on ${fpinfo.fpsak.schema.navn}.YF_FORDELING_PERIODE (VURDERING_TYPE)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_YF_FORDELING_PERIODE_7
   on ${fpinfo.fpsak.schema.navn}.YF_FORDELING_PERIODE (FORDELING_PERIODE_KILDE)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_YF_FORDELING_PERIODE_9
   on ${fpinfo.fpsak.schema.navn}.YF_FORDELING_PERIODE (ARBEIDSGIVER_ORGNR)
/

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
   UTENOMSORG_ID NUMBER
      constraint FK_YF_DOKUMENTASJON_PERIODE_9
         references ${fpinfo.fpsak.schema.navn}.YF_DOKUMENTASJON_PERIODER,
   ALENEOMSORG_ID NUMBER
      constraint FK_YF_DOKUMENTASJON_PERIODE_10
         references ${fpinfo.fpsak.schema.navn}.YF_DOKUMENTASJON_PERIODER,
   UTTAK_DOKUMENTASJON_ID NUMBER
      constraint FK_YF_DOKUMENTASJON_PERIODE_11
         references ${fpinfo.fpsak.schema.navn}.YF_DOKUMENTASJON_PERIODER,
   YF_AVKLART_DATO_ID NUMBER(19)
      constraint FK_GR_YTELSES_FORDELING_9
         references ${fpinfo.fpsak.schema.navn}.YF_AVKLART_DATO,
   AKTIV VARCHAR2(1 char) default 'J' not null
      constraint CHK_AKTIV1
         check (aktiv IN ('J', 'N')),
   ANNEN_FORELDER_HAR_RETT_ID NUMBER
      constraint FK_YF_DOKUMENTASJON_PERIODE_12
         references ${fpinfo.fpsak.schema.navn}.YF_DOKUMENTASJON_PERIODER,
   JUSTERT_FORDELING_ID NUMBER(19)
      constraint FK_GR_YTELSES_FORDELING_10
         references ${fpinfo.fpsak.schema.navn}.YF_FORDELING
)
/

comment on table ${fpinfo.fpsak.schema.navn}.GR_YTELSES_FORDELING is 'Grunnlag med informasjon om perioder vedrørende behandlingen'
/

comment on column ${fpinfo.fpsak.schema.navn}.GR_YTELSES_FORDELING.ID is 'Primærnøkkel'
/

comment on column ${fpinfo.fpsak.schema.navn}.GR_YTELSES_FORDELING.BEHANDLING_ID is 'FK: BEHANDLING Fremmednøkkel for kobling til behandling'
/

comment on column ${fpinfo.fpsak.schema.navn}.GR_YTELSES_FORDELING.SO_RETTIGHET_ID is 'FK:'
/

comment on column ${fpinfo.fpsak.schema.navn}.GR_YTELSES_FORDELING.SO_FORDELING_ID is 'FK:'
/

comment on column ${fpinfo.fpsak.schema.navn}.GR_YTELSES_FORDELING.SO_DEKNINGSGRAD_ID is 'FK:'
/

comment on column ${fpinfo.fpsak.schema.navn}.GR_YTELSES_FORDELING.OVERSTYRT_FORDELING_ID is 'FK:'
/

comment on column ${fpinfo.fpsak.schema.navn}.GR_YTELSES_FORDELING.UTENOMSORG_ID is 'FK:'
/

comment on column ${fpinfo.fpsak.schema.navn}.GR_YTELSES_FORDELING.ALENEOMSORG_ID is 'FK:'
/

comment on column ${fpinfo.fpsak.schema.navn}.GR_YTELSES_FORDELING.UTTAK_DOKUMENTASJON_ID is 'FK:'
/

comment on column ${fpinfo.fpsak.schema.navn}.GR_YTELSES_FORDELING.ANNEN_FORELDER_HAR_RETT_ID is 'FK: Fremmednøkkel for kobling til YF_DOKUMENTASJON_PERIODER'
/

comment on column ${fpinfo.fpsak.schema.navn}.GR_YTELSES_FORDELING.JUSTERT_FORDELING_ID is 'FK: justert fordeling basert på familiehendelse-dato og vedtaksperioder'
/

create index ${fpinfo.fpsak.schema.navn}.IDX_GR_YTELSES_FORDELING_01
   on ${fpinfo.fpsak.schema.navn}.GR_YTELSES_FORDELING (UTTAK_DOKUMENTASJON_ID)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_GR_YTELSES_FORDELING_02
   on ${fpinfo.fpsak.schema.navn}.GR_YTELSES_FORDELING (UTENOMSORG_ID)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_GR_YTELSES_FORDELING_04
   on ${fpinfo.fpsak.schema.navn}.GR_YTELSES_FORDELING (OVERSTYRT_FORDELING_ID)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_GR_YTELSES_FORDELING_06
   on ${fpinfo.fpsak.schema.navn}.GR_YTELSES_FORDELING (ALENEOMSORG_ID)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_GR_YTELSES_FORDELING_07
   on ${fpinfo.fpsak.schema.navn}.GR_YTELSES_FORDELING (SO_FORDELING_ID)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_GR_YTELSES_FORDELING_08
   on ${fpinfo.fpsak.schema.navn}.GR_YTELSES_FORDELING (SO_DEKNINGSGRAD_ID)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_GR_YTELSES_FORDELING_09
   on ${fpinfo.fpsak.schema.navn}.GR_YTELSES_FORDELING (SO_RETTIGHET_ID)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_GR_YTELSES_FORDELING_10
   on ${fpinfo.fpsak.schema.navn}.GR_YTELSES_FORDELING (BEHANDLING_ID)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_GR_YTELSES_FORDELING_11
   on ${fpinfo.fpsak.schema.navn}.GR_YTELSES_FORDELING (YF_AVKLART_DATO_ID)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_GR_YTELSES_FORDELING_12
   on ${fpinfo.fpsak.schema.navn}.GR_YTELSES_FORDELING (ANNEN_FORELDER_HAR_RETT_ID)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_GR_YTELSES_FORDELING_15
   on ${fpinfo.fpsak.schema.navn}.GR_YTELSES_FORDELING (JUSTERT_FORDELING_ID)
/

create table ${fpinfo.fpsak.schema.navn}.YF_DOKUMENTASJON_PERIODE
(
   ID NUMBER(19) not null
      constraint PK_YF_DOKUMENTASJON_PERIODE
         primary key,
   PERIODER_ID NUMBER(19) not null
      constraint FK_YF_DOKUMENTASJON_PERIODE_1
         references ${fpinfo.fpsak.schema.navn}.YF_DOKUMENTASJON_PERIODER,
   FOM DATE not null,
   TOM DATE not null,
   DOKUMENTASJON_TYPE VARCHAR2(100 char) not null,
   DOKUMENTASJON_KLASSE VARCHAR2(100 char) not null,
   OPPRETTET_AV VARCHAR2(20 char) default 'VL' not null,
   OPPRETTET_TID TIMESTAMP(3) default systimestamp not null,
   ENDRET_AV VARCHAR2(20 char),
   ENDRET_TID TIMESTAMP(3)
)
/

comment on table ${fpinfo.fpsak.schema.navn}.YF_DOKUMENTASJON_PERIODE is 'Dokumentasjonsperiode. Brukes for å dokumentere fakta/vurderinger for søknadsperioder'
/

comment on column ${fpinfo.fpsak.schema.navn}.YF_DOKUMENTASJON_PERIODE.ID is 'Primary key'
/

comment on column ${fpinfo.fpsak.schema.navn}.YF_DOKUMENTASJON_PERIODE.PERIODER_ID is 'FK: YF_DOKUMENTASJON_PERIODER Fremmednøkkel til tabell som samler dokumentasjon for en periode'
/

comment on column ${fpinfo.fpsak.schema.navn}.YF_DOKUMENTASJON_PERIODE.FOM is 'Fra-og-med dato for perioden'
/

comment on column ${fpinfo.fpsak.schema.navn}.YF_DOKUMENTASJON_PERIODE.TOM is 'Tra-og-med dato for perioden'
/

comment on column ${fpinfo.fpsak.schema.navn}.YF_DOKUMENTASJON_PERIODE.DOKUMENTASJON_TYPE is 'Hvilken type dokumentasjon det er snakk om'
/

comment on column ${fpinfo.fpsak.schema.navn}.YF_DOKUMENTASJON_PERIODE.DOKUMENTASJON_KLASSE is 'Brukes av hibernate for å lage riktig java-klasse'
/

create index ${fpinfo.fpsak.schema.navn}.IDX_YF_DOKUMENTASJON_PERIODE_1
   on ${fpinfo.fpsak.schema.navn}.YF_DOKUMENTASJON_PERIODE (PERIODER_ID)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_YF_DOK_PERIODE_2
   on ${fpinfo.fpsak.schema.navn}.YF_DOKUMENTASJON_PERIODE (DOKUMENTASJON_TYPE)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_YF_DOK_PERIODE_3
   on ${fpinfo.fpsak.schema.navn}.YF_DOKUMENTASJON_PERIODE (DOKUMENTASJON_KLASSE)
/

create table ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODER
(
   ID NUMBER(19) not null
      constraint PK_UTTAK_RESULTAT_PERIODER
         primary key,
   OPPRETTET_AV VARCHAR2(20 char) default 'VL' not null,
   OPPRETTET_TID TIMESTAMP(3) default systimestamp not null,
   ENDRET_AV VARCHAR2(20 char),
   ENDRET_TID TIMESTAMP(3)
)
/

comment on table ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODER is 'Brukes for å samle rader fra UTTAK_RESULTAT_PERIODE som hører sammen'
/

comment on column ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODER.ID is 'Primærnøkkel'
/

create table ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT
(
   ID NUMBER(19) not null
      constraint PK_UTTAK_RESULTAT
         primary key,
   BEHANDLING_RESULTAT_ID NUMBER(19) not null
      constraint FK_UTTAK_RESULTAT_1
         references ${fpinfo.fpsak.schema.navn}.BEHANDLING_RESULTAT,
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
)
/

comment on table ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT is 'Peker på UTTAK_RESULTAT_PERIODER for å angi hvilke perioder som er aktive'
/

comment on column ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT.ID is 'Primærnøkkel'
/

comment on column ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT.BEHANDLING_RESULTAT_ID is 'FK:BEHANDLING_RESULTAT Behandlingsresultatet til uttaksresultatet'
/

comment on column ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT.OPPRINNELIG_PERIODER_ID is 'FK:UTTAK_RESULTAT_PERIODER Opprinnelige periodene til uttaksresultatet'
/

comment on column ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT.OVERSTYRT_PERIODER_ID is 'FK:UTTAK_RESULTAT_PERIODER Overstyrte periodene til uttaksresultatet. Settes når saksbehandler overstyrer de opprinnelige periodene'
/

create index ${fpinfo.fpsak.schema.navn}.IDX_UTTAK_RESULTAT_02
   on ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT (BEHANDLING_RESULTAT_ID)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_UTTAK_RESULTAT_03
   on ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT (OPPRINNELIG_PERIODER_ID)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_UTTAK_RESULTAT_04
   on ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT (OVERSTYRT_PERIODER_ID)
/

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
)
/

comment on table ${fpinfo.fpsak.schema.navn}.PO_INFORMASJON is 'Aggregering av informasjon om personopplysning'
/

comment on column ${fpinfo.fpsak.schema.navn}.PO_INFORMASJON.ID is 'Primærnøkkel'
/

comment on column ${fpinfo.fpsak.schema.navn}.PO_INFORMASJON.VERSJON is 'Angir versjon for optimistisk låsing'
/

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
)
/

comment on table ${fpinfo.fpsak.schema.navn}.GR_PERSONOPPLYSNING is 'Behandlingsgrunnlag for Personopplysning (aggregat) for søker med familie'
/

comment on column ${fpinfo.fpsak.schema.navn}.GR_PERSONOPPLYSNING.ID is 'Primærnøkkel'
/

comment on column ${fpinfo.fpsak.schema.navn}.GR_PERSONOPPLYSNING.BEHANDLING_ID is 'FK: BEHANDLING Fremmednøkkel for kobling til behandling'
/

comment on column ${fpinfo.fpsak.schema.navn}.GR_PERSONOPPLYSNING.SO_ANNEN_PART_ID is 'FK:'
/

comment on column ${fpinfo.fpsak.schema.navn}.GR_PERSONOPPLYSNING.REGISTRERT_INFORMASJON_ID is 'FK:'
/

comment on column ${fpinfo.fpsak.schema.navn}.GR_PERSONOPPLYSNING.OVERSTYRT_INFORMASJON_ID is 'FK:'
/

create index ${fpinfo.fpsak.schema.navn}.IDX_GR_PERSONOPPLYSNING_01
   on ${fpinfo.fpsak.schema.navn}.GR_PERSONOPPLYSNING (BEHANDLING_ID)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_GR_PERSONOPPLYSNING_03
   on ${fpinfo.fpsak.schema.navn}.GR_PERSONOPPLYSNING (REGISTRERT_INFORMASJON_ID)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_GR_PERSONOPPLYSNING_04
   on ${fpinfo.fpsak.schema.navn}.GR_PERSONOPPLYSNING (OVERSTYRT_INFORMASJON_ID)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_GR_PERSONOPPLYSNING_6
   on ${fpinfo.fpsak.schema.navn}.GR_PERSONOPPLYSNING (SO_ANNEN_PART_ID)
/

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
)
/

comment on table ${fpinfo.fpsak.schema.navn}.PO_RELASJON is 'Angir relasjon mellom to personer (som må ligge i PO_PERSONOPPLYSNING, selv om dette er ingen db-constraint)'
/

comment on column ${fpinfo.fpsak.schema.navn}.PO_RELASJON.ID is 'Primærnøkkel'
/

comment on column ${fpinfo.fpsak.schema.navn}.PO_RELASJON.PO_INFORMASJON_ID is 'FK:'
/

comment on column ${fpinfo.fpsak.schema.navn}.PO_RELASJON.RELASJONSROLLE is 'Type relasjon mellom to personer (eks. EKTE/BARN/MORA/FARA/MMOR, etc.)'
/

comment on column ${fpinfo.fpsak.schema.navn}.PO_RELASJON.HAR_SAMME_BOSTED is 'Indikerer om personene i relasjonen bor på samme adresse'
/

comment on column ${fpinfo.fpsak.schema.navn}.PO_RELASJON.FRA_AKTOER_ID is 'Hva betyr dette?'
/

comment on column ${fpinfo.fpsak.schema.navn}.PO_RELASJON.TIL_AKTOER_ID is 'Hva betyr dette?'
/

create index ${fpinfo.fpsak.schema.navn}.IDX_PO_RELASJON_1
   on ${fpinfo.fpsak.schema.navn}.PO_RELASJON (PO_INFORMASJON_ID)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_PO_RELASJON_5
   on ${fpinfo.fpsak.schema.navn}.PO_RELASJON (RELASJONSROLLE)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_PO_RELASJON_2
   on ${fpinfo.fpsak.schema.navn}.PO_RELASJON (FRA_AKTOER_ID)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_PO_RELASJON_3
   on ${fpinfo.fpsak.schema.navn}.PO_RELASJON (TIL_AKTOER_ID)
/

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
)
/

comment on table ${fpinfo.fpsak.schema.navn}.UTTAK_AKTIVITET is 'Arbeid eller annen aktivitet som gir uttak'
/

comment on column ${fpinfo.fpsak.schema.navn}.UTTAK_AKTIVITET.UTTAK_ARBEID_TYPE is 'Arbeidstype'
/

comment on column ${fpinfo.fpsak.schema.navn}.UTTAK_AKTIVITET.ARBEIDSGIVER_AKTOR_ID is 'Aktør til personlig foretak.'
/

comment on column ${fpinfo.fpsak.schema.navn}.UTTAK_AKTIVITET.ARBEIDSGIVER_ORGNR is 'Organisasjonsnummer for arbeidsgivere som er virksomheter'
/

comment on column ${fpinfo.fpsak.schema.navn}.UTTAK_AKTIVITET.ARBEIDSFORHOLD_INTERN_ID is 'Globalt unikt arbeidsforhold id generert for arbeidsgiver/arbeidsforhold. I motsetning til arbeidsforhold_ekstern_id som holder arbeidsgivers referanse'
/

create index ${fpinfo.fpsak.schema.navn}.IDX_UTTAK_AKTIVITET_1
   on ${fpinfo.fpsak.schema.navn}.UTTAK_AKTIVITET (UTTAK_ARBEID_TYPE)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_UTTAK_AKTIVITET_4
   on ${fpinfo.fpsak.schema.navn}.UTTAK_AKTIVITET (ARBEIDSGIVER_ORGNR)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_UTTAK_AKTIVITET_11
   on ${fpinfo.fpsak.schema.navn}.UTTAK_AKTIVITET (ARBEIDSFORHOLD_INTERN_ID)
/

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
)
/

comment on table ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE_SOKNAD is 'Perioder fra søknaden'
/

comment on column ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE_SOKNAD.ID is 'Primærnøkkel'
/

comment on column ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE_SOKNAD.PERIODE_TYPE is 'type periode i søknadsperioden'
/

comment on column ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE_SOKNAD.GRADERING_ARBEIDSTIDSPROSENT is 'Arbeidstidsprosent hvis søkt om gradering'
/

comment on column ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE_SOKNAD.MORS_AKTIVITET is 'Mors aktivitet i perioden.'
/

comment on column ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE_SOKNAD.MOTTATT_DATO is 'Mottatt dato for søknad'
/

comment on column ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE_SOKNAD.SAMTIDIG_UTTAK is 'Samtidig uttak eller ikke'
/

comment on column ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE_SOKNAD.SAMTIDIG_UTTAKSPROSENT is 'Samtidig uttaksprosent fra søknaden'
/

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
)
/

comment on table ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE is 'Uttakresultatperiode'
/

comment on column ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE.ID is 'Primærnøkkel'
/

comment on column ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE.UTTAK_RESULTAT_PERIODER_ID is 'FK:UTTAK_RESULTAT_PERIODER Refererer til hvilken uttaksperioder perioden ligger i'
/

comment on column ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE.PERIODE_RESULTAT_TYPE is 'Hvilket resultat uttaksperioden har fått'
/

comment on column ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE.PERIODE_RESULTAT_AARSAK is 'Årsak til resultat uttaksperioden har fått'
/

comment on column ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE.GRADERING_AVSLAG_AARSAK is 'Årsak til at gradering for uttaksperioden har blitt avslått'
/

comment on column ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE.BEGRUNNELSE is 'Saksbehandlers begrunnelse (brukes ved manuell fastsetting og overstyring)'
/

comment on column ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE.KL_PERIODE_RESULTAT_AARSAK is 'Referanse til KODEVERK-kolonnen i KODELISTE-tabellen'
/

comment on column ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE.UTTAK_UTSETTELSE_TYPE is 'Om utsettelse er typen til perioden'
/

comment on column ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE.FLERBARNSDAGER is 'Angir om flerbarnsdager skal benyttes i perioden'
/

comment on column ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE.GRADERING_INNVILGET is 'Om gradering ble innvilget'
/

comment on column ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE.MANUELT_BEHANDLET is 'Om perioden har blitt manuelt behandlet eller ikke'
/

comment on column ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE.SAMTIDIG_UTTAK is 'Uttaksperiode har samtidig uttak'
/

comment on column ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE.SAMTIDIG_UTTAKSPROSENT is 'Samtidig uttaksprosent fra perioden'
/

comment on column ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE.OPPHOLD_AARSAK is 'Årsak til opphold'
/

comment on column ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE.OVERFOERING_AARSAK is 'Årsak til overføring'
/

create index ${fpinfo.fpsak.schema.navn}.IDX_UTTAK_RESULTAT_PERIODE
   on ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE (UTTAK_RESULTAT_PERIODER_ID)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_UTTAK_RESULTAT_PERIODE_2
   on ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE (PERIODE_RESULTAT_AARSAK)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_UTTAK_RESULTAT_PERIODE_5
   on ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE (GRADERING_AVSLAG_AARSAK)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_UTTAK_RESULTAT_PERIODE_6
   on ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE (PERIODE_RESULTAT_TYPE)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_UTTAK_RESULTAT_PERIODE_9
   on ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE (PERIODE_SOKNAD_ID)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_UTTAK_RESULTAT_PERIODE_10
   on ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE (OPPHOLD_AARSAK)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_UTTAK_RESULTAT_PERIODE_11
   on ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE (OVERFOERING_AARSAK)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_UTTAK_RES_PER_SOKNAD_1
   on ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE_SOKNAD (PERIODE_TYPE)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_UTTAK_RESULTAT_SOKNAD_2
   on ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE_SOKNAD (MORS_AKTIVITET)
/

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
)
/

comment on table ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE_AKT is 'Uttakresultatperiode for arbeidsforhold'
/

comment on column ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE_AKT.ID is 'Primærnøkkel'
/

comment on column ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE_AKT.UTTAK_RESULTAT_PERIODE_ID is 'FK:UTTAK_RESULTAT_PERIODE Hvilken uttaksperiode aktiviteten tilhører'
/

comment on column ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE_AKT.UTTAK_AKTIVITET_ID is 'FK:UTTAK_AKTIVITET Hvilken uttak aktivitet periode aktiviteten har'
/

comment on column ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE_AKT.TREKKONTO is 'Hvilken stønadskonto det skal trekkes fra.'
/

comment on column ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE_AKT.ARBEIDSTIDSPROSENT is 'Hvor mange prosent bruker ønsker å arbeide i dette arbeidsforholdet.'
/

comment on column ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE_AKT.UTBETALINGSPROSENT is 'Overstyrt utbetalingsprosent'
/

comment on column ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE_AKT.GRADERING is 'Om aktiviteten er gradert'
/

comment on column ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE_AKT.TREKKDAGER_DESIMALER is 'Trekkdager med en desimal'
/

create index ${fpinfo.fpsak.schema.navn}.IDX_UTTAK_RES_PERIODE_AKT_1
   on ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE_AKT (UTTAK_RESULTAT_PERIODE_ID)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_UTTAK_RES_PERIODE_AKT_2
   on ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE_AKT (UTTAK_AKTIVITET_ID)
/

create index ${fpinfo.fpsak.schema.navn}.IDX_UTTAK_RES_PERIODE_AKT_3
   on ${fpinfo.fpsak.schema.navn}.UTTAK_RESULTAT_PERIODE_AKT (TREKKONTO)
/

