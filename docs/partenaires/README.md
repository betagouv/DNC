# Devenir partenaire du DNC

## URLs du DNC
* URL de la plateforme de test: [https://dnc.apps.ocp-sbg1.dfp.ovh](https://dnc.apps.ocp-sbg1.dfp.ovh)
* URL complète à intégrer (bouton ou lien): [https://dnc.apps.ocp-sbg1.dfp.ovh/Redirect](https://dnc.apps.ocp-sbg1.dfp.ovh/Redirect)

## En tant que fournisseur de service au citoyen
Dans le cadre d'une démarche ou d'un formulaire nécessitant des pièces justificatives du citoyen, 
sur votre formulaire, vous pouvez simplement ajouter un lien vers le DNC ou intégrer un des boutons DNC ci-dessous.

En phase d'expérimentation, le lien mène à la plateforme de dévelopement du [DNC](https://dnc.apps.ocp-sbg1.dfp.ovh/).  

L'équipe DNC met à disposition 3 types de boutons :
* Un bouton simple qui permet de simplement se connecter au DNC
* Un bouton contextuelle qui permet de se connecter au DNC dans un contexte prédéfini par l'équipe

Peu importe le mode d'implémentation choisi, nous vous invitons à paramètrer 
l'URL du DNC afin de facilement mettre à jour votre plateforme. 

### Bouton simple

Ci-dessous, le code HTML du bouton simple du DNC:
```html
<div class="tooltip">
    <span class="tooltiptext">Connectez vous avec FranceConnect pour accéder à vos justificatifs</span>
    <img class="btn" onclick="window.open('https://dnc.apps.ocp-sbg1.dfp.ovh/Redirect', '_blank');">
        <img style="width: 1.65rem" alt="DNC" src="LogoDNC.svg"/> 
        <span class="text">Mes Justificatifs</span> 
    </button>
</div>
```
Le code complet du bouton simple est disponible [ici](bouton.html).

### Bouton contextualisé

La différence entre le bouton contextualisé et le bouton simple est l'ajout du paramètre _scope_ dans l'URL.

Ci-dessous, un exemple de code HTML pour le bouton contextualisé du DNC:
```html
<div class="tooltip">
    <span class="tooltiptext">Connectez vous avec FranceConnect pour accéder à vos justificatifs liés à vos revenus</span>
    <img class="btn" onclick="window.open('https://dnc.apps.ocp-sbg1.dfp.ovh/Redirect/?scope=DeclarationDesRevenus', '_blank');">
        <img style="width: 1.65rem" alt="DNC" src="LogoDNC.svg"/> 
        <span class="text">Mes Justificatifs liés à mes revenus</span> 
    </button>
</div>
```

Le paramètre _scope_ a 2 effets possibles pour la navigation de l'usager vers le DNC:
* **Présélection** : l'usager arrive sur une liste présélectionnée de justificatifs qu'il peut simplement télécharger
* **Attestation personnalisé** : l'usager peut générer une attestation personnalisée et spécifique en quelques clics

La liste des valeurs pour une présélection est:
* **DeclarationDesRevenus** : une présélection de ses justificatifs liés à ses revenus et à sa déclaration aux impôts

La liste des valeurs pour une attestation personnalisée : 
Le paramètre _scope_ peut avoir les valeurs possibles suivantes :
* **IDENTITE_FRANCECONNECT** : Une attestation personnalisée justifiant son identité numérique à partir de ses informations FranceConnect
* **CARTE_STATIONNEMENT** : Une attestation personnalisée pour une demande de carte de stationnement
* **AUTORISATION_STATIONNEMENT_DEMENAGEMENT** : Une attestation personnalisée pour une demande d'autorisation de stationnement pour un deménagement
* **INSCRIPTION_CRECHE** : Une attestation personnalisée pour une inscription d'un enfant, né ou à naitre, à la crèche
* **INSCRIPTION_ECOLE** : Une attestation personnalisée pour une inscription d'un enfant à l'école
* **DEMANDE_TRANSPORT_SCOLAIRE** : Une attestation personnalisée pour une demande d'inscription au transport scolaire
* **RESTAURATION_SCOLAIRE** : Une attestation personnalisée pour une demande d'inscription à la restauration scolaire
* **DOSSIER_MARIAGE** : Une attestation personnalisée pour un dossier de mariage
* **DECLARATION_MINI_MOTO** : Une attestation personnalisée pour une déclaration de mini-moto ou de mini-quad

### Archive et exemples
Un zip contenant le code HTML (incluant le style) et l'image du bouton est également disponible :
[Zip du bouton](boutonDNC.zip).

Un exemple d'affichage est le suivant :
![Exemple d'affichage](exemple_boutonDNC.png)



Pour les boutons contextualisés 2 exemples complets sont disponibles :
* [Présélection des justificatifs liés aux revenus](bouton_justificatif.html)
* [Demande de carte de stationnement](bouton_attestation.html)

