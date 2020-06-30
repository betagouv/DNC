# Présentation

Le micro-service Démarche MS a pour objectif :
* La gestion des démarches au sein du DNC
* L'aggrégation des statuts des démarches en cours chez les partenaires du DNC 


# Installation

Le projet est un projet Maven classique s'installant via la commande :
```
mvn clean install
```

# Démarrage

## Prérequis
L'application SpringCloudConfigServer doit être démarré.
Elle est disponible [ici](../spring-cloud-config-server/README.md)

## Démarrage
Le serveur se lance de façon standard par la commande :
```
java -jar target/demarche-ms-1.0-SNAPSHOT.jar
```

# Utilisation (Dockerfile)

Après un avoir installé le projet avec la commande :
```
maven clean install
```
L'image Docker peut être créée avec la commande Docker suivante :
```
docker build -t demarche-ms .
```
