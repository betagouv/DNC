# Devenir partenaire du DNC
## 1.	URLs du DNC

 - URL de la plateforme de test : [https://dnc.apps.ocp-sbg1.dfp.ovh](https://dnc.apps.ocp-sbg1.dfp.ovh)
 - URL complète à intégrer (bouton ou lien) : [https://dnc.apps.ocp-sbg1.dfp.ovh/Redirect](https://dnc.apps.ocp-sbg1.dfp.ovh/Redirect)
 - URL de test des boutons : [https://dnc.apps.ocp-sbg1.dfp.ovh/test-button](https://dnc.apps.ocp-sbg1.dfp.ovh/test-button)

## 2.	En tant que fournisseur de service au citoyen

Dans le cadre d'une démarche ou d'un formulaire nécessitant des pièces justificatives du citoyen, sur votre formulaire, vous pouvez simplement ajouter un lien vers le DNC ou intégrer un des boutons DNC ci-dessous.

*En phase d'expérimentation, le lien mène à la plateforme de développement du DNC.*

L'équipe DNC met à disposition 3 types de boutons :

 - Un bouton **générique** : l’usager se connecte simplement au DNC
 - Un bouton de **présélection** d’une ou plusieurs attestations : l'usager arrive sur une liste présélectionnée d’attestations qu'il peut simplement télécharger. 
 
 
![Exemple d'affichage](exemple_boutonDNC.png)

Peu importe le mode d'implémentation choisi, nous vous invitons à paramétrer l'URL du DNC afin de facilement mettre à jour votre plateforme.
### 2.1.	Bouton générique
Le bouton générique permet à l’usager de se connecter simplement au DNC.
Ci-dessous, le code HTML du bouton générique du DNC :

    <div class="tooltip">
        <span class="tooltiptext">Connectez vous avec FranceConnect pour accéder à vos justificatifs</span>
        <img class="btn" onclick="window.open('https://dnc.apps.ocp-sbg1.dfp.ovh/Redirect', '_blank');">
            <img style="width: 1.65rem" alt="DNC" src="LogoDNC.svg"/> 
            <span class="text">Mes Justificatifs</span> 
        </button>
    </div>

Le code complet du bouton générique est disponible ici : [https://github.com/betagouv/DNC/blob/master/docs/partenaires/bouton.html](https://github.com/betagouv/DNC/blob/master/docs/partenaires/bouton.html)


### 2.2.	Bouton de présélection d’une ou plusieurs attestations

Le bouton de présélection permet à l'usager d’arriver sur une liste de d’attestations présélectionnés qu'il peut simplement télécharger.

La différence entre le bouton de présélection et le bouton générique **est l'ajout du paramètre scope dans l'URL.** Celui-ci peut avoir différentes valeurs :

*a.	DECLARATION DES REVENUS*

**Paramètre scope :** DeclarationDesRevenus

**Utilisation :** permet de présélectionner les attestations liés à ses revenus et à sa déclaration aux impôts par exemple.

**Attestations téléchargeables :**  
o	Attestation de quotient familial
o	Attestation de revenus

**Code HTML :**

    <div class="tooltip">
        <span class="tooltiptext">Connectez-vous avec FranceConnect pour accéder à vos justificatifs liés à vos revenus</span>
        <img class="btn" onclick="window.open('https://dnc.apps.ocp-sbg1.dfp.ovh/Redirect/?scope=DeclarationDesRevenus', '_blank');">
            <img style="width: 1.65rem" alt="DNC" src="LogoDNC.svg"/> 
            <span class="text">Mes Justificatifs liés à mes revenus</span> 
        </button>
    </div>

*b.	NOUVELLE EMBAUCHE*

**Paramètre scope :** NouvelleEmbauche

**Utilisation :** permet de présélectionner les attestations nécessaires dans le cadre d’une nouvelle embauche par exemple.

**Attestations téléchargeables :**  
o	Informations personnelles FranceConnect
o	Attestation de droits

**Code HTML :**

    <div class="tooltip">
        <span class="tooltiptext">Connectez-vous avec FranceConnect pour accéder à vos justificatifs liés à votre nouvelle embauche</span>
        <img class="btn" onclick="window.open('https://dnc.apps.ocp-sbg1.dfp.ovh/Redirect/?scope= NouvelleEmbauche, '_blank');">
            <img style="width: 1.65rem" alt="DNC" src="LogoDNC.svg"/> 
            <span class="text">Mes Justificatifs liés à ma nouvelle embauche</span> 
        </button>
    </div>


