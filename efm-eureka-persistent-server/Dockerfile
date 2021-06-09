FROM openjdk:8-jre-slim

EXPOSE 8762

ENV JAVA_OPTS="" \
    APP_DIR=/opt/digdir \
    APP_FILE_NAME=efm-eureka-server.jar

RUN addgroup --system --gid 1001 spring && adduser --system --uid 1001 --group spring

ADD --chown=spring:spring target/$APP_FILE_NAME ${APP_DIR}/$APP_FILE_NAME

RUN chmod -R +x $APP_DIR
RUN mkdir $CACHE_DIR && chown -R spring:spring $CACHE_DIR
RUN mkdir $SSH_DIR && chown -R spring:spring $SSH_DIR
USER spring
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar ${APP_DIR}/${APP_FILE_NAME} ${0} ${@}"]

HEALTHCHECK --interval=30s --timeout=2s --retries=3 \
CMD wget -qO- "http://localhost:8762/discovery/manage/health" || exit 1