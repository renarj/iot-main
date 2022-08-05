FROM openjdk:10
VOLUME /tmp

RUN apt-get update && apt-get install -y netcat

ARG BSH_SCRIPT
ADD ${BSH_SCRIPT} start.sh
RUN chmod +x start.sh

ARG JAR_FILE
ADD ${JAR_FILE} command-svc.jar

RUN bash -c 'touch /command-svc.jar'

CMD ["/start.sh"]

EXPOSE 8080