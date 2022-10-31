FROM eclipse-temurin:17.0.4_8-jre
VOLUME /tmp

RUN apt-get update && apt-get install -y netcat

ARG BSH_SCRIPT=src/main/resources/start.sh
ADD ${BSH_SCRIPT} start.sh
RUN chmod +x start.sh

ARG JAR_FILE=target/command-svc*.jar
ADD ${JAR_FILE} command-svc.jar

RUN bash -c 'touch /command-svc.jar'

CMD ["/start.sh"]

EXPOSE 9004