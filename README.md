FP-INFO
===============
[![Bygg og deploy](https://github.com/navikt/fpinfo/actions/workflows/build.yml/badge.svg?branch=master)](https://github.com/navikt/fpinfo/actions/workflows/build.yml)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=navikt_fpinfo&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=navikt_fpinfo)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=navikt_fpinfo&metric=coverage)](https://sonarcloud.io/summary/new_code?id=navikt_fpinfo)

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=navikt_fpinfo&metric=alert_status)](https://sonarcloud.io/dashboard?id=navikt_fpinfo)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=navikt_fpinfo&metric=bugs)](https://sonarcloud.io/dashboard?id=navikt_fpinfo)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=navikt_fpinfo&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=navikt_fpinfo)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=navikt_fpinfo&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=navikt_fpinfo)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=navikt_fpinfo&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=navikt_fpinfo)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=navikt_fpinfo&metric=sqale_index)](https://sonarcloud.io/dashboard?id=navikt_fpinfo)

Rest-grensesnitt for å slå opp behandlinger i fpsak. Går dessverre mot views i fpsak.   

Brukes av selvbetjening. 

### Sikkerhet
Det er mulig å kalle tjenesten med bruk av følgende tokens
- Azure CC
- Azure OBO med følgende rettigheter:
    - fpsak-drift
- TokenX
