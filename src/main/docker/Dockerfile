FROM fabric8/java-alpine-openjdk8-jre

ENV JAVA_OPTIONS="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager -Xss256m -Xms256m -Xmx3G -XX:MetaspaceSize=256m -XX:MaxMetaspaceSize=3G -XX:+UseAdaptiveSizePolicy -XX:-UseGCOverheadLimit -Duser.timezone=GMT-3"

ENV AB_ENABLED=jmx_exporter

RUN apk add --no-cache nss

COPY lib/* /deployments/lib/
COPY *-runner.jar /deployments/app.jar

ENV HOST 0.0.0.0
EXPOSE 8080

ENTRYPOINT [ "/deployments/run-java.sh" ]