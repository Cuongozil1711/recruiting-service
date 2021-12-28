FROM registry.nspace.vn:5000/openjdk11:jre-11.0.10_9-alpine
LABEL maintainer="ducta286@gmail.com"
COPY build/libs/*.jar /ngs/app.jar
COPY build/resources/main/* /ngs/config/

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom","-jar","/ngs/app.jar"]
