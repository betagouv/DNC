import React from 'react';
import { compose, pure } from 'recompose';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import colors from 'style/config.variables.scss';
import Button from 'react/components/buttons/button/Button';
import styles from './AbonnementStationnement.module.scss';

const enhancer = compose(pure);

const StationnementSummary = (props) => {
  const redirectToFS = () => {
    window.location.href = `${props.demarcheParams.redirectUri}?id_technique=123456789`;
  };

  return (
    <>
      <p className={styles.cardSubTitle}>
        Vous pouvez dès à présent télécharger votre fiche résumé.
      </p>

      <Button
        size="small"
        color={colors.darkishBlue}
        style={{ marginTop: '10px', marginBottom: '50px' }}
      >
        Télécharger ma fiche résumé
      </Button>

      <p style={{ color: 'red', fontSize: '0.9375rem' }}>
        <b>Attention: Afin de finaliser votre démarche veuillez cliquer sur le bouton ci-dessous</b>
      </p>

      <Button
        size="small"
        color={colors.darkishBlue}
        onClick={redirectToFS}
        style={{ marginTop: '10px', marginBottom: '30px' }}
      >
        Finaliser et retourner sur Ville de Nantes
      </Button>
    </>
  );
};

StationnementSummary.propTypes = {
  redirectUri: PropTypes.string.isRequired,
  demarcheParams: PropTypes.shape({
    redirectUri: PropTypes.string.isRequired,
  }),
};

StationnementSummary.defaultProps = {
  demarcheParams: {},
};

const mapStateToProps = state => ({
  demarcheParams: state.demarches.demarcheParams,
});

export default connect(mapStateToProps)(enhancer(StationnementSummary));
