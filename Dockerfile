FROM ubuntu

RUN apt-get update -y
RUN apt-get install openjdk-8-jdk -y

WORKDIR /repainter
ADD build/libs/Reskinner.jar .
ADD configs configs

CMD java -jar Reskinner.jar