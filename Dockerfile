FROM openjdk:8-jre-alpine
MAINTAINER Johannes Molland <johannes.molland@digdir.no>
LABEL package="no.difi.move" artifact="eureka" version="1.0" description="Digitaliseringsdirektoratet (Digdir)">

VOLUME /tmp/config_cache

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

EXPOSE 8761

ENV APP_DIR=/var/lib/difi
ENV JAVA_OPTS=""

ARG JAR_PATH
COPY ${JAR_PATH} ${APP_DIR}/app.jar

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar ${APP_DIR}/app.jar ${0} ${@}"]

HEALTHCHECK --interval=30s --timeout=2s --retries=3 CMD wget -qO- "http://localhost:8761/discovery/actuator/health" || exit 1