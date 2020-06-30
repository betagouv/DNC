import React from 'react';
import { compose, pure } from 'recompose';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { Grid, CardContent, TextField } from '@material-ui/core';
import Button from 'react/components/buttons/button/Button';
import { makeStyles } from '@material-ui/core/styles';
import colors from 'style/config.variables.scss';
import Card from 'react/components/card/Card';
import { identitePivotShape } from 'models/shapes/IdentitePivotShape';
import styles from './InformationCard.module.scss';

const enhancer = compose(pure);

const useStyles = makeStyles({
  gridListeTileTile: {
    display: 'flex',
    flexDirection: 'column',
    justifyContent: 'center',
  },
  img: {
    height: '5rem',
    display: 'block',
    marginLeft: 'auto',
    marginRight: 'auto',
    marginBottom: '1rem',
  },
  card: {
    display: 'flex',
    flexDirection: 'column',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  button: {
    marginBottom: '1.5rem',
    marginLeft: '2rem',
    marginRight: '2rem',
    marginTop: '1rem',
    maxWidth: '340px',
    width: '9.375rem',
  },
  input: {
    color: colors.darkSlateBlue,
  },
  label: {
    color: colors.darkSlateBlue,
  },
});

const EmptyFamilleInformationCard = (props) => {
  const classes = useStyles();

  const [numeroAllocataire, setNumeroAllocataire] = React.useState('');
  const [numeroAllocataireError, setNumeroAllocataireError] = React.useState(false);
  const [numeroAllocataireDisabled, setNumeroAllocataireDisabled] = React.useState(false);

  const [codePostal, setCodePostal] = React.useState('');
  const [codePostalError, setCodePostalError] = React.useState(false);

  React.useEffect(() => {
    if (
      props.identitePivot
      && props.identitePivot.codePostal
      && props.identitePivot.codePostal !== ''
    ) {
      setCodePostal(props.identitePivot.codePostal);
    }

    if (props.numeroAllocataire && props.numeroAllocataire !== '') {
      setNumeroAllocataire(props.numeroAllocataire);
      setNumeroAllocataireDisabled(true);
    }
  }, []);

  const fetchInfosFamille = async (event) => {
    event.preventDefault();

    let error = false;

    if (!numeroAllocataire || numeroAllocataire === '') {
      setNumeroAllocataireError(true);
      error = true;
    }

    if (!codePostal || codePostal === '') {
      setCodePostalError(true);
      error = true;
    }

    if (error) {
      return;
    }

    props.fetchDataCallback(numeroAllocataire, codePostal);
  };

  return (
    <>
      <Grid item xs style={{ minWidth: props.minWidth, padding: '1.5rem' }}>
        <Card className={classes.card}>
          <CardContent
            className={styles.content}
            style={{
              display: 'flex',
              flexDirection: 'column',
              alignItems: 'center',
              height: '100%',
            }}
          >
            <img src={props.logoSrc} alt={props.source} className={classes.img} />

            <p className={styles.title}>{props.title}</p>
            <p className={styles.source}>Source : {props.source}</p>

            <form
              onSubmit={fetchInfosFamille}
              id="famille-form"
              style={{
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
                justifyContent: 'center',
                height: '100%',
                marginTop: '15px',
              }}
            >
              <p
                className={styles.informationLabel}
                style={{
                  textAlign: 'center',
                  fontSize: '1.125rem',
                  fontWeight: 'normal',
                  textTransform: 'initial',
                }}
              >
                Afin de pouvoir récupérer vos données, vous devez entrer votre&nbsp;
                <b>numéro d&apos;allocataire</b> et votre <b>code postal</b>
              </p>

              <TextField
                label="Numéro d'allocataire"
                margin="normal"
                disabled={numeroAllocataireDisabled}
                style={{ width: '75%', color: colors.darkSlateBlue }}
                InputLabelProps={{
                  className: classes.input,
                }}
                InputProps={{
                  className: classes.input,
                }}
                type="number"
                value={numeroAllocataire}
                onChange={e => setNumeroAllocataire(e.target.value)}
                error={numeroAllocataireError}
                onClick={() => setNumeroAllocataireError(false)}
              />

              <TextField
                label="Code postal"
                margin="normal"
                style={{ width: '75%', color: colors.darkSlateBlue }}
                InputLabelProps={{
                  className: classes.input,
                }}
                InputProps={{
                  className: classes.input,
                }}
                type="number"
                onChange={e => setCodePostal(e.target.value)}
                value={codePostal}
                error={codePostalError}
                onClick={() => setCodePostalError(false)}
              />
            </form>
          </CardContent>
          <Button
            type="submit"
            size="small"
            color={colors.darkSlateBlue}
            className={classes.button}
            form="famille-form"
          >
            Valider
          </Button>
        </Card>
      </Grid>
    </>
  );
};

EmptyFamilleInformationCard.propTypes = {
  logoSrc: PropTypes.string,
  source: PropTypes.string,
  title: PropTypes.string,
  fetchDataCallback: PropTypes.func,
  identitePivot: identitePivotShape.isRequired,
  minWidth: PropTypes.number,
  numeroAllocataire: PropTypes.string.isRequired,
};

EmptyFamilleInformationCard.defaultProps = {
  logoSrc: '',
  source: '',
  title: '',
  fetchDataCallback: () => {},
  minWidth: 400,
};

const mapStateToProps = state => ({
  identitePivot: state.identity.identitePivot,
  numeroAllocataire: state.identity.numeroAllocataire,
});

export default connect(mapStateToProps)(enhancer(EmptyFamilleInformationCard));
