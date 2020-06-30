# Présentation
## Présentation technique
Ce projet héberge le POM Parent de tous les micro-services du DNC :
* Centralisation de toutes les versions des frameworks utilisés dans les micro-service (majoritairement du Spring)
* Définition des "modules" (i.e. des micro-services) composant le back-office du DNC

# Installation

Le projet est un projet Maven classique s'installant via la commande :
```
mvn clean install
```

# Utilisation (standard)

Le projet est importé dans les modules en tant que Parent au sens [Maven](https://maven.apache.org/guides/introduction/introduction-to-the-pom.html#Project_Inheritance).

Ce projet n'est pas un exécutable.
