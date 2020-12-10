FROM openjdk:8-jre-slim

EXPOSE 8761

ENV JAVA_OPTS="" \
    APP_DIR=/opt/digdir \
    CACHE_DIR=/tmp/config_cache \
    APP_FILE_NAME=eureka.jar

RUN addgroup --system --gid 1001 spring && adduser --system --uid 1001 --group spring

ARG JAR_PATH
ADD --chown=spring:spring ${JAR_PATH} ${APP_DIR}/$APP_FILE_NAME

RUN chmod -R +x $APP_DIR
RUN mkdir $CACHE_DIR && chown  -R spring:spring $CACHE_DIR
USER spring
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar ${APP_DIR}/${APP_FILE_NAME} ${0} ${@}"]

HEALTHCHECK --interval=30s --timeout=2s --retries=3 \
CMD wget -qO- "http://localhost:8761/discovery/actuator/health" || exit 1