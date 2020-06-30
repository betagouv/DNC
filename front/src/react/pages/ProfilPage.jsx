import { Grid, Hidden } from '@material-ui/core';
import { compose, pure } from 'recompose';
import { makeStyles, useTheme } from '@material-ui/core/styles';

import React from 'react';
import ReactResizeDetector from 'react-resize-detector';
import indisponibleImg from 'assets/images/ic-indisponible.png';
import useMediaQuery from '@material-ui/core/useMediaQuery';
import InformationCard from '../views/informations/cards/InformationCard';

const enhancer = compose(pure);

const useStyles = makeStyles({
  grid: {
    margin: 0,
    maxWidth: '110%',
    height: '200%',
  },
  emptyGridItem: {
    padding: '1.5rem',
  },
  img: {
    height: '5rem',
    display: 'block',
    marginLeft: 'auto',
    marginRight: 'auto',
    marginBottom: '1rem',
  },
  iframe: {
    width: '100%',
    height: '100%',
    border: '0',
  },
  wrapper: {
    height: '100%',
    width: '100%',
  },
});

const ProfilPage = () => {
  const matches = useMediaQuery('(min-width:1280px)');

  const classes = useStyles({ matches });
  const theme = useTheme();
  const xsDown = useMediaQuery(theme.breakpoints.down('sm'));
  React.useEffect(() => {
    document.title = 'Page de profil';
  }, []);

  // Le nombre de cartes vides à ajouter
  const [emptyCards, setEmptyCards] = React.useState(0);

  const content = [];

  // Le nombre total de cartes (utiliser pour calculer le nombre de cartes vides)
  let cardsNb = 0;

  // La largeur min de chaque cartes (en rem)
  let cardMinWidth = 25;

  const iframe = (
    <iframe
      title="Historique FranceConnect"
      sandbox="allow-scripts allow-same-origin"
      src="https://fcp.integ01.dev-franceconnect.fr/traces"
      className={classes.iframe}
    />
  );

  // Si taille écran < 600px => on change la largeur min des cartes (de 25rem à 18.75rem)
  if (xsDown) {
    cardMinWidth = 18.75;
  }

  content.push(
    <InformationCard
      id="preferences"
      title="Mes préférences"
      minWidth={`${cardMinWidth}rem`}
    >
      <img src={indisponibleImg} alt="" className={classes.img} />
    </InformationCard>,
  );

  content.push(
    <InformationCard
      id="historique"
      title="Mon historique France Connect"
      minWidth={`${cardMinWidth}rem`}
    >
      {iframe}
    </InformationCard>,
  );

  cardsNb = content.length;

  // On rajouter un certain nombre de cartes vides pour l'affichage
  for (let i = 0; i < emptyCards; i++) {
    content.push(
      <Grid item xs className={classes.emptyGridItem} implementation="css" component={Hidden} />,
    );
  }

  /**
   * Callback appelée lorsque la largeur du composant Grid change.
   * Cette fonction permet de calculer le nombre de Card vide à ajouter
   * afin que toutes les Card (même seules sur une ligne) aient la même largeur.
   *
   * @param {*} width - Largeur du composant Grid.
   */
  const onResize = (width) => {
    let oneRemValuePx = 16; // Par défaut 1rem = 16px

    if (window.innerWidth < 1600 && window.innerWidth >= 1280) {
      // (0.64625 * 16 + 0.11000000000000014 * window.innerWidth)
      // formule pour calculer la valeur actuelle en px de 1rem.
      // Voir fichier main.scss
      // oneRemValuePx = 0.64625 * 16 + (0.11000000000000014 * window.innerWidth) / 100;
      oneRemValuePx = -0.27000000000000046 * 16 + (1.2750000000000006 * window.innerWidth) / 100;
    }

    if (window.innerWidth < 1280 && window.innerWidth >= 600) {
      // (0.64625 * 16 + 0.11000000000000014 * window.innerWidth)
      // formule pour calculer la valeur actuelle en px de 1rem.
      // Voir fichier main.scss
      // oneRemValuePx = 0.64625 * 16 + (0.11000000000000014 * window.innerWidth) / 100;
      oneRemValuePx = 0.6329044117647059 * 16 + (0.14558823529411768 * window.innerWidth) / 100;
    }

    // Nombre de carte par ligne = Taille de la Grid / min-width d'une carte
    const cardsNbByLines = Math.trunc(width / (cardMinWidth * oneRemValuePx)) + 1;

    // Nombre de ligne = nombre de carte / nombre de carte par ligne
    const lineNb = cardsNb / cardsNbByLines;

    if (lineNb % 1 !== 0 && cardsNbByLines < cardsNb) {
      // Si lineNb est pas rond => lineNb % 1 = 0
      // Nombre de carte vide à ajouter =
      // nombre de ligne (arrondi au plus haut) * nombre de cartes par ligne - nombre de cartes
      setEmptyCards(Math.ceil(lineNb) * cardsNbByLines - cardsNb);
    } else if (emptyCards > 0) {
      setEmptyCards(0);
    }
  };

  return (
    <>
      <Grid handleWidth container justify="center" className={classes.grid}>
        <ReactResizeDetector handleWidth handleHeight onResize={onResize} />

        {content}
      </Grid>
    </>
  );
};

ProfilPage.propTypes = {};

ProfilPage.defaultProps = {};

export default enhancer(ProfilPage);
