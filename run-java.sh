export JAVA_OPTS="${JAVA_OPTS:-} -Djava.security.egd=file:/dev/./urandom"

export STARTUP_CLASS=${STARTUP_CLASS:-"no.nav.foreldrepenger.info.web.server.JettyServer"}
export CLASSPATH=app.jar:lib/*

exec java -cp ${CLASSPATH:-"app.jar:lib/*"} ${DEFAULT_JAVA_OPTS:-} ${JAVA_OPTS} -Dwebapp=${WEBAPP:-"./webapp"} ${STARTUP_CLASS?} ${SERVER_PORT:-8080} $@
