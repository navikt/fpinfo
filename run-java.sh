#!/usr/bin/env sh
set -eu

export STARTUP_CLASS=${STARTUP_CLASS:-"no.nav.foreldrepenger.info.web.server.JettyServer"}
export CLASSPATH=app.jar:lib/*

exec java -cp ${CLASSPATH:-"app.jar:lib/*"}  {DEFAULT_JVM_OPTS}  ${JAVA_OPTS}  -Dwebapp=${WEBAPP:-"./webapp"} -Dapplication.name=FPINFO ${STARTUP_CLASS?} ${SERVER_PORT:-8080} $@
