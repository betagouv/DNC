# Présentation

Ce projet est un projet minimal mettant à disposition des utilisateurs un serveur de configuration Spring Cloud Config Server.
Une fois le projet Spring Boot démarré, il expose sur le port 8888 un endpoint permettant à tout projet Spring Boot utilisant le client Spring Cloud Config de récupérer leur configuration selon leur profil Spring utilisé.

La documentation associée à Spring Cloud Config peut être trouvée [ici](https://spring.io/guides/gs/centralized-configuration/).

Dans cette version du serveur Spring Cloud Config, les fichiers de configuration sont versionnés dans ce repository dans le répertoire `central-config`, suivant le modèle déjà en vigueur à la CNAF.

Les clients récupérent leur conf en spécifiant l'URL du serveur Spring Cloud Config via l'argument `--spring.cloud.config.uri=http://<host>:<port>/config`

# Installation

Le projet est un projet Maven classique s'installant via la commande :
```
mvn clean install
```

# Démarrage

Le serveur se lance de façon standard par la commande :
```
java -jar target/spring-cloud-config-server-1.0-SNAPSHOT.jar --server.port=8888
```

 :mag: Le paramètre `server.port` permet d'indiquer le port utilisé par le serveur Tomcat Spring Boot. Ce paramètre est **facultatif**, par défaut il prend la valeur `8888`


# Utilisation (Dockerfile)

Après un avoir installé le projet avec la commande :
```
maven clean install
```
L'image Docker peut être créée avec la commande Docker suivante :
```
docker build -t demarche-ms .
```
