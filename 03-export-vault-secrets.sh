#!/usr/bin/env bash

if test -f /var/run/secrets/nais.io/fpinfoSchema/username;
then
   export  FPINFOSCHEMA_USERNAME=$(cat /var/run/secrets/nais.io/fpinfoSchema/username)
   echo "Setting FPINFOSCHEMA_USERNAME to $FPINFOSCHEMA_USERNAME"

fi

if test -f /var/run/secrets/nais.io/fpinfoSchema/password;
then
    export  FPINFOSCHEMA_PASSWORD=$(cat /var/run/secrets/nais.io/fpinfoSchema/password)
    echo "Setting FPINFOSCHEMA_PASSWORD"
fi

if test -f /var/run/secrets/nais.io/fpinfoSchemaDSconfig/jdbc_url;
then
    export  FPINFOSCHEMA_URL=$(cat /var/run/secrets/nais.io/fpinfoSchemaDSconfig/jdbc_url)
    echo "Setting FPINFOSCHEMA_URL til $FPINFOSCHEMA_URL"
fi

if test -f /var/run/secrets/nais.io/srvfpinfo/password;
then
    export  SYSTEMBRUKER_PASSWORD=$(cat /var/run/secrets/nais.io/srvfpinfo/password)
    echo "Setting SYSTEMBRUKER_PASSWORD"
fi

if test -f /var/run/secrets/nais.io/srvfpinfo/username;
then
    export  SYSTEMBRUKER_USERNAME=$(cat /var/run/secrets/nais.io/srvfpinfo/username)
    echo "Setting SYSTEMBRUKER_USERNAME"
fi
