# Projet DNC - Dossier Numérique du Citoyen
## Présentation du projet
Présentation disponible [ici](docs/PROJECT.md)

## Code du Front
Readme du projet Front [ici](front/README.md)

## Code du Backend
### Prérequis pour utiliser le backend
Pour utiliser le DNC en local, vous devez à minima avoir à disposition:
* Une base Postgres
* Une base Redis
* JDK 11

Vous pouvez créer ces éléments via les images dockers standards disponibles sur Docker Hub:
* [Postgres](https://hub.docker.com/_/postgres)
* [Redis](https://hub.docker.com/_/redis)

Exemple de démarrage :
* *docker run -d --name some-postgres -e POSTGRES_PASSWORD=<mot de passe Postgres> postgres*
* *docker run -d --name some-redis redis redis-server --appendonly yes --requirepass <mot de passe Redis>*

### Projet backcore 
Readme du projet Backcore [ici](backend/backcore/README.md)
### Projet common-ms 
Readme du projet common-ms [ici](backend/common-ms/README.md)
### Projet franceconnect-ms 
Readme du projet franceconnect-ms [ici](backend/franceconnect-ms/README.md)
### Projet partenaire-ms 
Readme du projet partenaire-ms [ici](backend/partenaire-ms/README.md)
### Projet demarche-ms 
Readme du projet demarche-ms [ici](backend/demarche-ms/README.md)
### Projet situation-usager-ms 
Readme du projet situation-usager-ms [ici](backend/situation-usager-ms/README.md)
### Projet spring-cloud-config-server
Readme du projet spring-cloud-config-server [ici](backend/spring-cloud-config-server/README.md)