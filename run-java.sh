#!/usr/bin/env sh
set -eu

hostname=$(hostname)

export JAVA_OPTS="${JAVA_OPTS:-} -Djava.security.egd=file:/dev/./urandom"

# hvor skal gc log, heap dump etc kunne skrives til med Docker?
export todo_JAVA_OPTS="${JAVA_OPTS} -XX:ErrorFile=./hs_err_pid<pid>.log -XX:HeapDumpPath=./java_pid<pid>.hprof -XX:-HeapDumpOnOutOfMemoryError -Xloggc:<filename>"

export STARTUP_CLASS=${STARTUP_CLASS:-"no.nav.foreldrepenger.info.web.server.JettyServer"}
export CLASSPATH=app.jar:lib/*
export LOGBACK_CONFIG=${LOGBACK_CONFIG:-"./conf/logback.xml"}

exec java -cp ${CLASSPATH:-"app.jar:lib/*"} ${DEFAULT_JAVA_OPTS:-} ${JAVA_OPTS} -Dlogback.configurationFile=${LOGBACK_CONFIG?} -Dwebapp=${WEBAPP:-"./webapp"} -Dapplication.name=FPINFO ${STARTUP_CLASS?} ${SERVER_PORT:-8080} $@
