# Présentation

Le micro-service Situation Usager MS a pour objectif :
* L'aggrégation des données propres à un usager sur sa stituation personnelle, familiale et fiscale
* La mise à disposition des données récupérées au fournisseur de service maître de la démarche
* La gestion des justificatifs
* La vérification des données chiffrés du QR Code d'un justificatif

# Installation

Le projet est un projet Maven classique s'installant via la commande :
```
mvn clean install
```

# Démarrage

Le serveur se lance de façon standard par la commande :
```
java -jar target/situation-usager-ms-1.0-SNAPSHOT.jar
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
