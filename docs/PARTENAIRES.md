# Devenir partenaire du DNC

## URLs du DNC
* URL de la plateforme de test: [http://dnc-dev.cloudapps.dfp.ovh](http://dnc-dev.cloudapps.dfp.ovh)
* URL à intégrer dans le bouton: [http://dnc-dev.cloudapps.dfp.ovh/Redirect](http://dnc-dev.cloudapps.dfp.ovh/Redirect)

## En tant que fournisseur de service au citoyen
Dans le cadre d'une démarche ou d'un formulaire nécessitant des pièces justificatives du citoyen, 
sur votre formulaire, vous pouvez simplement ajouter un lien vers le DNC ou intégrer le bouton DNC ci-dessous.

En phase d'expérimentation, le lien mène à la plateforme de dévelopement du [DNC](http://dnc-dev.cloudapps.dfp.ovh/).  

Ci-dessous, le code HTML du bouton du DNC:
```html
<div class="tooltip">
    <span class="tooltiptext">Connectez vous avec FranceConnect pour accéder à vos justificatifs</span>
    <img class="btn" onclick="window.open('http://dnc-dev.cloudapps.dfp.ovh/Redirect', '_blank');">
        <img style="width: 1.65rem" alt="DNC" src="LogoDNC.svg"/> 
        <span class="text">Mes Justificatifs</span> 
    </button>
</div>
```
Un zip contenant le code HTML (incluant le style) et l'image du bouton est également disponible :
[Zip du bouton](partenaires/boutonDNC.zip).

Un exemple d'affichage est le suivant :
![Exemple d'affichage](partenaires/exemple_boutonDNC.png)


Nous vous invitons à paramètrer l'URL du DNC afin de facilement mettre à jour votre plateforme.

 
