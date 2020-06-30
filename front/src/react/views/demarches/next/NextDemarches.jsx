import React from 'react';
import { compose, pure } from 'recompose';
import { CardContent, Grid, CardActionArea } from '@material-ui/core';
import Card from 'react/components/card/Card';
import Button from 'react/components/buttons/button/Button';
import PropTypes from 'prop-types';
import { makeStyles } from '@material-ui/core/styles';
import colors from 'style/config.variables.scss';
import bebeSvg from 'assets/images/ic-bebe.svg';
import documentSvg from 'assets/images/ic-document.svg';
import { withRouter } from 'react-router-dom';
import carSvg from 'assets/images/car.svg';
import styles from './NextDemarches.module.scss';

const enhancer = compose(pure);

const useStyles = makeStyles({
  cardAction: {
    color: colors.darkishBlue,
    '&:hover': {
      backgroundColor: colors.darkishBlueAlpha,
    },
    height: '100%',
  },
  cardContent: {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    padding: 0,
    height: '100%',
    paddingBottom: '10px',
  },
  cardIcon: { width: '50px', height: '50px', margin: '15px' },
  button: {
    marginBottom: '50px',
  },
  gridContainer: { padding: '1rem', margin: 0, maxWidth: '100%' },
  gridItem: { minWidth: '190px', minHeight: '190px' },
});

const NextDemarches = (props) => {
  const classes = useStyles();

  const demarches = [
    {
      label: "Obtenir un extrait d'acte de naissance",
      icon: bebeSvg,
      margin: '20px',
      onClick: () => {
        window.open('https://www.service-public.fr/particuliers/vosdroits/F1427');
      },
    },
    {
      label: 'Obtenir une carte de stationnement',
      icon: carSvg,
      margin: '20px',
      onClick: () => {
        props.history.push({
          pathname: '/justificatifs/personnalises/selection',
          default: 'carte_stationnement',
        });
      },
    },
    {
      label: 'Obtenir un certificat de non imposition',
      icon: documentSvg,
      margin: '10px',
      onClick: () => {},
    },
  ];

  return (
    <>
      <p className={styles.title}>Mes prochaines démarches ?</p>

      <Grid container spacing={5} justify="center" className={classes.gridContainer}>
        {demarches.map(({
          label, icon, margin, onClick,
        }) => (
          <Grid item xs={1} className={classes.gridItem}>
            <Card>
              <CardActionArea className={classes.cardAction}>
                <CardContent className={classes.cardContent} onClick={onClick}>
                  <img src={icon} className={classes.cardIcon} alt={label} />

                  <p
                    className={styles.cardLabel}
                    style={{ marginRight: margin, marginLeft: margin }}
                  >
                    {label}
                  </p>
                </CardContent>
              </CardActionArea>
            </Card>
          </Grid>
        ))}
      </Grid>

      <Button
        size="small"
        color={colors.darkishBlue}
        className={classes.button}
        onClick={() => {
          props.history.push('/justificatifs/personnalises');
        }}
      >
        Voir toutes les démarches
      </Button>
    </>
  );
};

NextDemarches.propTypes = {
  history: PropTypes.shape({ push: PropTypes.func.isRequired }).isRequired,
};

NextDemarches.defaultProps = {};

export default withRouter(enhancer(NextDemarches));
