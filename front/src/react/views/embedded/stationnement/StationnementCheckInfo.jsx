import { GridList, GridListTile, Tooltip } from '@material-ui/core';
import { compose, pure } from 'recompose';

import Button from 'react/components/buttons/button/Button';
import CheckCircleOutlineIcon from '@material-ui/icons/CheckCircleOutline';
import PropTypes from 'prop-types';
import React from 'react';
import StationnementUsagerInfoFactory from 'models/factories/demarches/StationnementUsagerInfoFactory';
import colors from 'style/config.variables.scss';
import { connect } from 'react-redux';
import { makeStyles } from '@material-ui/core/styles';
import stationnementUsagerInfoShape from 'models/shapes/demarches/StationnementUsagerInfoShape';
import styles from './AbonnementStationnement.module.scss';

const enhancer = compose(pure);

const useStyles = makeStyles({
  gridList: {
    margin: '0 !important',
    marginTop: '1rem !important',
  },
  gridListeTileTile: {
    display: 'flex',
    flexDirection: 'column',
    justifyContent: 'center',
  },
  verifyButton: {
    borderRadius: '5px',
    fontSize: '0.9375rem',
    padding: '6px',
  },
  button: {
    marginTop: '10px',
  },
});

const StationnementCheckInfo = (props) => {
  const classes = useStyles();
  let addressVerified = null;
  let verifyAddressComponent = null;
  let verifyVehicleComponent = null;
  let vehicleVerified = null;

  const [isAddressVerified, setAddressVerified] = React.useState(false);
  const [isVehicleVerified, setVehicleVerified] = React.useState(false);
  const [error, setError] = React.useState('');

  const verifyAddress = () => {
    setAddressVerified(true);
  };

  const verifyVehicle = () => {
    setVehicleVerified(true);
  };

  const gotToNextStep = () => {
    if (!isAddressVerified || !isVehicleVerified) {
      return setError(
        'Vous devez justifier votre adresse et votre immatriculation avant de pouvoir continuer.',
      );
    }

    return props.history.push('/stationnement/summary');
  };

  if (!isAddressVerified) {
    verifyAddressComponent = (
      <Button
        size="medium"
        className={classes.verifyButton}
        color={colors.darkishBlue}
        onClick={verifyAddress}
      >
        Justifier mon adresse
      </Button>
    );
  } else {
    addressVerified = (
      <Tooltip title="Adresse vérifiée par Engie" placement="top">
        <CheckCircleOutlineIcon htmlColor="green" />
      </Tooltip>
    );
  }

  if (!isVehicleVerified) {
    verifyVehicleComponent = (
      <Button
        size="medium"
        className={classes.verifyButton}
        color={colors.darkishBlue}
        onClick={verifyVehicle}
      >
        Justifier mon immatriculation
      </Button>
    );
  } else {
    vehicleVerified = (
      <Tooltip
        title="Immatriculation vérifiée par Système d'Immatriculation des Véhicules"
        placement="top"
      >
        <CheckCircleOutlineIcon htmlColor="green" />
      </Tooltip>
    );
  }

  const formatUsagerInfos = (stationnementUsagerInfo) => {
    if (!stationnementUsagerInfo) {
      return [];
    }
    return [
      {
        label: 'nom',
        value: stationnementUsagerInfo.nomFamille,
      },
      {
        label: 'prénom',
        value: stationnementUsagerInfo.prenoms,
      },
      {
        label: 'adresse',
        value: stationnementUsagerInfo.adresse,
        verifyButton: verifyAddressComponent,
        verified: addressVerified,
      },
      {
        label: 'commune de référence',
        value: stationnementUsagerInfo.communeReference,
      },
      {
        label: 'type de véhicule',
        value: stationnementUsagerInfo.typeVehicule,
      },
      {
        label: 'Immatriculation du véhicule',
        value: stationnementUsagerInfo.immatriculationVehicule,
        verifyButton: verifyVehicleComponent,
        verified: vehicleVerified,
      },
    ];
  };

  const dataComponent = formatUsagerInfos(props.stationnementUsagerInfo).map(
    ({
      label, value, verifyButton, verified,
    }) => {
      if (!value || value === '') {
        return null;
      }

      return [
        <GridListTile
          classes={{
            tile: classes.gridListeTileTile,
          }}
        >
          <p className={styles.informationLabel}>{label}</p>
        </GridListTile>,
        <GridListTile
          classes={{
            tile: classes.gridListeTileTile,
          }}
        >
          <p className={styles.informationValue}>
            {value} {verified}
          </p>
        </GridListTile>,
        verifyButton ? [<GridListTile />, <GridListTile>{verifyButton}</GridListTile>] : null,
      ];
    },
  );

  return (
    <>
      <p className={styles.cardSubTitle}>Mes informations</p>
      <p className={styles.cardSource}>Source : Système d&apos;Immatriculation des Véhicules</p>

      <GridList cols={2} cellHeight="auto" spacing={12} className={classes.gridList}>
        {dataComponent}
      </GridList>

      <p className={styles.editInformations}>
        <u>Modifier mes informations</u>
      </p>

      <p style={{ color: 'red', marginTop: '10px' }}>{error}</p>

      <Button color={colors.darkishBlue} className={classes.button} onClick={gotToNextStep}>
        Valider ces informations
      </Button>
    </>
  );
};

StationnementCheckInfo.propTypes = {
  stationnementUsagerInfo: stationnementUsagerInfoShape,
  history: PropTypes.shape({ push: PropTypes.func.isRequired }).isRequired,
};

StationnementCheckInfo.defaultProps = {
  stationnementUsagerInfo: new StationnementUsagerInfoFactory(),
};

const mapStateToProps = state => ({
  stationnementUsagerInfo: state.demarches.stationnementUsagerInfo,
});

export default connect(mapStateToProps)(enhancer(StationnementCheckInfo));
