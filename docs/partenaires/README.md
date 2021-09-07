# Devenir partenaire du DNC
## 1.	URLs du DNC

 - URL de la plateforme de test : [https://dnc.apps.ocp-sbg1.dfp.ovh](https://dnc.apps.ocp-sbg1.dfp.ovh)
 - URL complète à intégrer (bouton ou lien) : [https://dnc.apps.ocp-sbg1.dfp.ovh/Redirect](https://dnc.apps.ocp-sbg1.dfp.ovh/Redirect)
 - URL de test des boutons : [https://dnc.apps.ocp-sbg1.dfp.ovh/test-button](https://dnc.apps.ocp-sbg1.dfp.ovh/test-button)

## 2.	En tant que fournisseur de service

Dans le cadre d'une démarche ou d'un formulaire nécessitant des pièces justificatives du citoyen, sur votre formulaire, vous pouvez simplement ajouter un lien vers le DNC ou intégrer un des boutons DNC ci-dessous.

L'équipe DNC met à disposition 2 types de boutons :

 - Un bouton **générique** : l’usager se connecte simplement au DNC
 - Un bouton de **présélection** d’un ou plusieurs justificatifs : l'usager arrive sur une liste présélectionnée de justificatifs qu'il peut simplement télécharger. 
 
 
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


### 2.2.	Bouton de présélection d’un ou plusieurs justificatifs

Le bouton de présélection permet à l'usager d’arriver sur une liste de justificatifs présélectionnés qu'il peut simplement télécharger.

La différence entre le bouton de présélection et le bouton générique **est l'ajout du paramètre scope dans l'URL.** Celui-ci peut avoir différentes valeurs :

*BOUTON N°1*

**Paramètre scope :** Bouton1

**Utilisation :** 
![Exemple d'utilisation](https://github.com/betagouv/DNC/blob/master/docs/images/Diapositive13.JPG)

**Justificatif téléchargeable :**  
 o Justificatif d’informations personnelles FranceConnect  

**Code HTML :**

    <div class="tooltip">
        <span class="tooltiptext">Connectez-vous avec FranceConnect pour accéder à vos justificatifs</span>
        <img class="btn" onclick="window.open('https://dnc.apps.ocp-sbg1.dfp.ovh/Redirect/?scope=Bouton1', '_blank');">
            <img style="width: 1.65rem" alt="DNC" src="LogoDNC.svg"/> 
            <span class="text">Mes Justificatifs</span> 
        </button>
    </div>

*BOUTON N°2*

**Paramètre scope :** Bouton2

**Utilisation :** ![Exemple d'utilisation](https://github.com/betagouv/DNC/blob/master/docs/images/Diapositive14.JPG)

**Justificatif téléchargeable :**  
 o	Justificatif d’informations personnelles FranceConnect  
 o	Justificatif d’adresse  

**Code HTML :**

    <div class="tooltip">
        <span class="tooltiptext">Connectez-vous avec FranceConnect pour accéder à vos justificatifs</span>
        <img class="btn" onclick="window.open('https://dnc.apps.ocp-sbg1.dfp.ovh/Redirect/?scope=Bouton2', '_blank');">
            <img style="width: 1.65rem" alt="DNC" src="LogoDNC.svg"/> 
            <span class="text">Mes Justificatifs</span> 
        </button>
    </div>
    
*BOUTON N°3*

**Paramètre scope :** Bouton3

**Utilisation :** 
![Exemple d'utilisation](https://github.com/betagouv/DNC/blob/master/docs/images/Diapositive15.JPG)

**Justificatif téléchargeable :**  
o	Justificatif d’informations personnelles FranceConnect  
o	Justificatif d’inscription Pôle Emploi / Justificatif étudiant  

**Code HTML :**

    <div class="tooltip">
        <span class="tooltiptext">Connectez-vous avec FranceConnect pour accéder à vos justificatifs</span>
        <img class="btn" onclick="window.open('https://dnc.apps.ocp-sbg1.dfp.ovh/Redirect/?scope=Bouton3', '_blank');">
            <img style="width: 1.65rem" alt="DNC" src="LogoDNC.svg"/> 
            <span class="text">Mes Justificatifs</span> 
        </button>
    </div>

*BOUTON N°4*

**Paramètre scope :** Bouton4

**Utilisation :** 
![Exemple d'utilisation](https://github.com/betagouv/DNC/blob/master/docs/images/Diapositive16.JPG)

**Justificatif téléchargeable :**  
o	Justificatif d’informations personnelles FranceConnect  
o	Justificatif de droits CNAM  

**Code HTML :**

    <div class="tooltip">
        <span class="tooltiptext">Connectez-vous avec FranceConnect pour accéder à vos justificatifs</span>
        <img class="btn" onclick="window.open('https://dnc.apps.ocp-sbg1.dfp.ovh/Redirect/?scope=Bouton4', '_blank');">
            <img style="width: 1.65rem" alt="DNC" src="LogoDNC.svg"/> 
            <span class="text">Mes Justificatifs</span> 
        </button>
    </div>
    
*BOUTON N°5*

**Paramètre scope :** Bouton5

**Utilisation :** 
![Exemple d'utilisation](https://github.com/betagouv/DNC/blob/master/docs/images/Diapositive17.JPG)

**Justificatif téléchargeable :**  
o	Justificatif d’informations personnelles FranceConnect  
o	Justificatif de quotient familial  

**Code HTML :**

    <div class="tooltip">
        <span class="tooltiptext">Connectez-vous avec FranceConnect pour accéder à vos justificatifs</span>
        <img class="btn" onclick="window.open('https://dnc.apps.ocp-sbg1.dfp.ovh/Redirect/?scope=Bouton5', '_blank');">
            <img style="width: 1.65rem" alt="DNC" src="LogoDNC.svg"/> 
            <span class="text">Mes Justificatifs</span> 
        </button>
    </div>

*BOUTON N°6*

**Paramètre scope :** Bouton6

**Utilisation :** 
![Exemple d'utilisation](https://github.com/betagouv/DNC/blob/master/docs/images/Diapositive18.JPG)

**Justificatif téléchargeable :**  
o	Justificatif d’informations personnelles FranceConnect  
o	Justificatif de revenus  

**Code HTML :**

    <div class="tooltip">
        <span class="tooltiptext">Connectez-vous avec FranceConnect pour accéder à vos justificatifs</span>
        <img class="btn" onclick="window.open('https://dnc.apps.ocp-sbg1.dfp.ovh/Redirect/?scope=Bouton6', '_blank');">
            <img style="width: 1.65rem" alt="DNC" src="LogoDNC.svg"/> 
            <span class="text">Mes Justificatifs</span> 
        </button>
    </div>
    
*BOUTON N°7*

**Paramètre scope :** Bouton7

**Utilisation :** 
![Exemple d'utilisation](https://github.com/betagouv/DNC/blob/master/docs/images/Diapositive19.JPG)

**Justificatif téléchargeable :**  
o	Justificatif d’informations personnelles FranceConnect  
o	Justificatif de revenus  
o	Justificatif d’adresse  


**Code HTML :**

    <div class="tooltip">
        <span class="tooltiptext">Connectez-vous avec FranceConnect pour accéder à vos justificatifs</span>
        <img class="btn" onclick="window.open('https://dnc.apps.ocp-sbg1.dfp.ovh/Redirect/?scope=Bouton7', '_blank');">
            <img style="width: 1.65rem" alt="DNC" src="LogoDNC.svg"/> 
            <span class="text">Mes Justificatifs</span> 
        </button>
    </div>
    
*BOUTON N°8*

**Paramètre scope :** Bouton8

**Utilisation :** 
![Exemple d'utilisation](https://github.com/betagouv/DNC/blob/master/docs/images/Diapositive20.JPG)

**Justificatif téléchargeable :**  
o	Justificatif d’informations personnelles FranceConnect  
o	Justificatif d’adresse  
o	Justificatifs de droits CNAM  
o	Justificatif de diplôme  

**Code HTML :**

    <div class="tooltip">
        <span class="tooltiptext">Connectez-vous avec FranceConnect pour accéder à vos justificatifs</span>
        <img class="btn" onclick="window.open('https://dnc.apps.ocp-sbg1.dfp.ovh/Redirect/?scope=Bouton8', '_blank');">
            <img style="width: 1.65rem" alt="DNC" src="LogoDNC.svg"/> 
            <span class="text">Mes Justificatifs</span> 
        </button>
    </div>

*BOUTON N°9*

**Paramètre scope :** Bouton9

**Utilisation :** 
![Exemple d'utilisation](https://github.com/betagouv/DNC/blob/master/docs/images/Diapositive21.JPG)

**Justificatif téléchargeable :**  
o	Justificatif d’informations personnelles FranceConnect  
o	Justificatifs de droits CNAM  
o	Justificatif de quotient familial  

**Code HTML :**

    <div class="tooltip">
        <span class="tooltiptext">Connectez-vous avec FranceConnect pour accéder à vos justificatifs</span>
        <img class="btn" onclick="window.open('https://dnc.apps.ocp-sbg1.dfp.ovh/Redirect/?scope=Bouton9', '_blank');">
            <img style="width: 1.65rem" alt="DNC" src="LogoDNC.svg"/> 
            <span class="text">Mes Justificatifs</span> 
        </button>
    </div>
        
    
