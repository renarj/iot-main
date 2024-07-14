FROM eclipse-temurin:21-jre-jammy
VOLUME /tmp

RUN apt-get update && apt-get install -y netcat

ARG BSH_SCRIPT=src/main/resources/start.sh
ADD ${BSH_SCRIPT} start.sh
RUN chmod +x start.sh

ARG JAR_FILE=target/iot-ui*.jar
ADD ${JAR_FILE} iot-ui.jar

RUN bash -c 'touch /iot-ui.jar'

CMD ["/start.sh"]

EXPOSE 9001