# Présentation

Le micro-service partenaire-MS a pour objectif :
* La gestion des partenaires et de leurs configurations au sein du DNC 

# Installation

Le projet est un projet Maven classique s'installant via la commande :
```
mvn clean install
```

# Utilisation (standard)

## Prérequis
L'application SpringCloudConfigServer doit être démarré.
Elle est disponible [ici](../spring-cloud-config-server/README.md)

## Démarrage
Le serveur se lance de façon standard par la commande :
```
java -jar target/partenaire-ms-1.0-SNAPSHOT.jar
```

## Utilisation (Dockerfile)

Après un avoir installé le projet avec la commande :
```
maven clean install
```
L'image Docker peut être créée avec la commande Docker suivante :
```
docker build -t demarche-ms .
```