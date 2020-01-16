export JAVA_OPTS="${JAVA_OPTS:-} -Djava.security.egd=file:/dev/./urandom"

export STARTUP_CLASS=${STARTUP_CLASS:-"no.nav.foreldrepenger.info.web.server.JettyServer"}
export CLASSPATH=app.jar:lib/*
export LOGBACK_CONFIG=${LOGBACK_CONFIG:-"./conf/logback.xml"}

exec java -cp ${CLASSPATH:-"app.jar:lib/*"} ${DEFAULT_JAVA_OPTS:-} ${JAVA_OPTS} -Dlogback.configurationFile=${LOGBACK_CONFIG?} -Dwebapp=${WEBAPP:-"./webapp"} -Dapplication.name=FPINFO ${STARTUP_CLASS?} ${SERVER_PORT:-8080} $@
