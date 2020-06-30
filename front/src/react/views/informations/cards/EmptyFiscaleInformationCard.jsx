import React from 'react';
import { compose, pure } from 'recompose';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { Grid, CardContent, TextField } from '@material-ui/core';
import Button from 'react/components/buttons/button/Button';
import { makeStyles } from '@material-ui/core/styles';
import colors from 'style/config.variables.scss';
import Card from 'react/components/card/Card';
import styles from './InformationCard.module.scss';
import EmptyFiscaleHelpDialog from './EmptyFiscaleHelpDialog';

const enhancer = compose(pure);

const useStyles = makeStyles({
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
    maxWidth: '340px',
    marginTop: '1rem',
    width: '9.375rem',
  },
  input: {
    color: colors.darkSlateBlue,
  },
  label: {
    color: colors.darkSlateBlue,
  },
  helpButton: {
    all: 'unset',
    marginTop: '5px',
    textAlign: 'center',
    cursor: 'pointer',
    textDecoration: 'underline',
    fontStyle: 'italic',
    color: colors.darkishBlue,
    fontSize: '0.875rem',
    '&:hover': {
      fontWeight: 'bold',
    },
  },
});

const EmptyFiscaleInformationCard = (props) => {
  const classes = useStyles();

  const [numeroFiscal, setNumeroFiscal] = React.useState('');
  const [numeroFiscalError, setNumeroFiscalError] = React.useState(false);

  const [referenceAvisFiscal, setReferenceAvisFiscal] = React.useState('');
  const [referenceAvisFiscalError, setReferenceAvisFiscalError] = React.useState(false);

  const [dialogOpen, setDialogOpen] = React.useState(false);

  const fetchInfosFiscales = async (event) => {
    event.preventDefault();
    event.stopPropagation();

    let error = false;

    if (!numeroFiscal || numeroFiscal === '') {
      setNumeroFiscalError(true);
      error = true;
    }

    if (!referenceAvisFiscal || referenceAvisFiscal === '') {
      setReferenceAvisFiscalError(true);
      error = true;
    }

    if (error) {
      return;
    }

    props.fetchDataCallback(numeroFiscal, referenceAvisFiscal);
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
              onSubmit={fetchInfosFiscales}
              id="fiscales-form"
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
                <b>numéro fiscal</b> et votre <b>référence d&apos;avis fiscal</b>
              </p>

              <TextField
                label="Numéro fiscal"
                margin="normal"
                style={{ width: '75%', color: colors.darkSlateBlue }}
                InputLabelProps={{
                  className: classes.input,
                }}
                InputProps={{
                  className: classes.input,
                }}
                type="number"
                onChange={e => setNumeroFiscal(e.target.value)}
                error={numeroFiscalError}
                onClick={() => setNumeroFiscalError(false)}
              />

              <TextField
                label="Référence d'avis fiscal"
                margin="normal"
                style={{ width: '75%', color: colors.darkSlateBlue, whiteSpace: 'nowrap' }}
                InputLabelProps={{
                  className: classes.input,
                }}
                InputProps={{
                  className: classes.input,
                }}
                type="number"
                onChange={e => setReferenceAvisFiscal(e.target.value)}
                error={referenceAvisFiscalError}
                onClick={() => setReferenceAvisFiscalError(false)}
              />

              <button
                onClick={() => {
                  setDialogOpen(true);
                }}
                className={classes.helpButton}
              >
                Où trouver la référence d&apos;avis fiscal ?
              </button>
            </form>
          </CardContent>
          <Button
            type="submit"
            size="small"
            color={colors.darkSlateBlue}
            className={classes.button}
            form="fiscales-form"
          >
            Valider
          </Button>
        </Card>

        <EmptyFiscaleHelpDialog
          onClose={() => {
            setDialogOpen(false);
          }}
          open={dialogOpen}
        />
      </Grid>
    </>
  );
};

EmptyFiscaleInformationCard.propTypes = {
  logoSrc: PropTypes.string,
  source: PropTypes.string,
  title: PropTypes.string,
  fetchDataCallback: PropTypes.func,
  minWidth: PropTypes.number,
};

EmptyFiscaleInformationCard.defaultProps = {
  logoSrc: '',
  source: '',
  title: '',
  fetchDataCallback: () => {},
  minWidth: 400,
};

const mapDispatchToProps = dispatch => ({
  fetchInfosFiscales: (token, numeroFiscal, referenceAvisFiscal) => dispatch({
    type: 'FETCH_INFOS_FISCALES',
    token,
    numeroFiscal,
    referenceAvisFiscal,
  }),
});

export default connect(null, mapDispatchToProps)(enhancer(EmptyFiscaleInformationCard));
