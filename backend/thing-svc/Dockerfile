FROM eclipse-temurin:17.0.4_8-jre
VOLUME /tmp

RUN apt-get update

COPY target/*.jar /thing-svc.jar
COPY src/main/resources/start.sh /
RUN chmod +x start.sh

RUN bash -c 'touch /thing-svc.jar'

CMD ["/start.sh"]

EXPOSE 9010