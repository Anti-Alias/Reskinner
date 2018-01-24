# Uses Ubuntu as base image
FROM ubuntu

# Runs these shell commands to install jdk on this image
RUN apt-get update -y
RUN apt-get install openjdk-8-jdk -y

# Creates working dir in image, and changes to that directory.
WORKDIR /reskinner

# Add application files to working directory. (Executable, configs, etc)
ADD ./build/libs/Reskinner.jar /reskinner
ADD configs-docker configs
ADD db db
ADD templates templates
ADD images images
ADD css css
ADD js js

# Sets the shell command to invoke when a container is started.
CMD java -jar Reskinner.jar