import { compose, pure } from 'recompose';

import DataList from 'react/components/list/DataList';
import DemarcheManager from 'services/DemarcheManager';
import PropTypes from 'prop-types';
import React from 'react';
import { connect } from 'react-redux';
import { makeStyles } from '@material-ui/core/styles';
import { withRouter } from 'react-router-dom';
import GenericDemarcheCard from './GenericDemarcheCard';

const enhancer = compose(pure);

const useStyles = makeStyles({
  gridList: {
    margin: '0 0 2vh 0 !important',
  },
  tile: {
    display: 'flex',
    flexDirection: 'column',
    justifyContent: 'center',
  },
  valueTile: {
    extend: 'tile',
    maxWidth: '12.5rem',
  },
  labelTile: {
    extend: 'tile',
    marginRight: '30px',
  },
  verifyButton: {
    borderRadius: '5px',
    fontSize: '0.9375rem',
    padding: '6px',
  },
});

const GenericDemarcheCheckInfo = (props) => {
  const classes = useStyles();
  let idSession = '';
  let idDemarche = '';
  document.addEventListener('PUT_DATA_TO_BACK', (event) => {
    [idDemarche, idSession] = event.detail; // array destructuring
  });

  React.useEffect(() => {
    props.fetchDataFunction();
  }, []);

  const gotToNextStep = () => {
    DemarcheManager.fetchFinalizeDemarche(idSession, idDemarche);
    let pdf;
    let pdfBase64;
    document.addEventListener('FETCH_JUSTIFICATIF', (event) => {
      [pdf, pdfBase64] = event.detail;
      props.history.push('summary', { pdf, pdfBase64 });
    });
    if (idSession === '' && idDemarche === '') {
      props.history.push('summary');
    }
  };

  return (
    <>
      <GenericDemarcheCard
        buttonClickHandler={gotToNextStep}
        buttonLabel="Valider et continuer"
        title="Vérifiez et confirmez vos informations."
      >
        <DataList
          data={props.formatedUsagerInfos}
          className={classes.gridList}
          labelTileClass={classes.labelTile}
          valueTileClass={classes.valueTile}
        />
      </GenericDemarcheCard>
    </>
  );
};

GenericDemarcheCheckInfo.propTypes = {
  history: PropTypes.shape({ push: PropTypes.func.isRequired }).isRequired,
  fetchDataFunction: PropTypes.func,
  // eslint-disable-next-line react/forbid-prop-types
  formatedUsagerInfos: PropTypes.any,
};

GenericDemarcheCheckInfo.defaultProps = {
  fetchDataFunction: () => {},
  formatedUsagerInfos: {},
};

const mapStateToProps = () => ({});

export default connect(mapStateToProps)(withRouter(enhancer(GenericDemarcheCheckInfo)));
