FROM openjdk:12-jdk-alpine

#WORKAROUND - L'usage de Jasper et de polices particulière oblige à installer freetype & fontconifg et recréer un lien symbolique vers une librairie
#https://github.com/AdoptOpenJDK/openjdk-docker/issues/75
RUN apk add freetype fontconfig ttf-dejavu
RUN ln -s /usr/lib/libfontconfig.so.1 /usr/lib/libfontconfig.so && \
    ln -s /lib/libuuid.so.1 /usr/lib/libuuid.so.1 && \
    ln -s /lib/libc.musl-x86_64.so.1 /usr/lib/libc.musl-x86_64.so.1
ENV LD_LIBRARY_PATH /usr/lib

RUN addgroup -S spring && adduser -S spring -G spring && mkdir /app && chown spring:spring -R /app
USER spring:spring

#Volumes des certificats : Tous les certificats à ajouter doivent y être
#VOLUME /app/certificats
#Ajout du Certificat au Keystore de l'image
#RUN keytool -keystore $JAVA_HOME/lib/security/cacerts -storepass changeit -noprompt -trustcacerts -importcert -alias certToAdd -file /opt/openjdk-12/lib/security/certificats/certToAdd.crt
#Copie du répertoire des certificats

ARG JAR_FILE=target/*.jar
COPY --chown=spring:spring ${JAR_FILE} /app/app.jar
WORKDIR /app



ENV PORT 8084
EXPOSE $PORT

ENTRYPOINT ["java","-jar","app.jar"]