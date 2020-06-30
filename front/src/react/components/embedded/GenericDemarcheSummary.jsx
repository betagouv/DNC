import { compose, pure } from 'recompose';

import DownloadDocumentSummary from 'react/components/DownloadDocumentSummary';
import PropTypes from 'prop-types';
import React from 'react';
import { connect } from 'react-redux';
import justifRestoScolaire from 'assets/pdf/justificatif_restauration_scolaire.pdf';
import { useLocation } from 'react-router-dom';
import GenericDemarcheCard from './GenericDemarcheCard';

const enhancer = compose(pure);

const GenericDemarcheSummary = (props) => {
  const location = useLocation();
  // let pdf;
  let pdf = '';
  let pdfBase64 = '';
  let pdfLink = '';
  if (location.state && location.state.pdf) {
    pdf = location.state.pdf;
  }
  if (location.state && location.state.pdfBase64) {
    pdfBase64 = location.state.pdfBase64;
  }

  if (pdf === '' && pdfBase64 === '') {
    pdfLink = justifRestoScolaire;
  }

  const redirectToFS = () => {
    props.initialState();
    window.location.href = `${props.fsRedirectUri}?tokenDemarche=${props.tokenDemarche}&data=${JSON.stringify(props.data)}`;
  };
  // pdf au lieu de props.pdf
  return (
    <>
      <GenericDemarcheCard
        buttonClickHandler={redirectToFS}
        buttonLabel="Finaliser ma démarche"
        title="Transmettez vos données pour finaliser votre démarche."
      >
        <DownloadDocumentSummary pdf={pdf} pdfBase64={pdfBase64} lienPdf={pdfLink} />
      </GenericDemarcheCard>
    </>
  );
};

GenericDemarcheSummary.propTypes = {
  fsRedirectUri: PropTypes.string.isRequired,
  // eslint-disable-next-line react/forbid-prop-types
  data: PropTypes.any,
  initialState: PropTypes.func.isRequired,
  tokenDemarche: PropTypes.string,
};

GenericDemarcheSummary.defaultProps = {
  data: {},
  tokenDemarche: '6aa3252d-6fac-4b46-b68f-705845841acc',
};

const mapStateToProps = state => ({
  fsRedirectUri: state.demarches.fsRedirectUri,
});

const mapDispatchToProps = dispatch => ({
  initialState: () => dispatch({
    type: 'DEMARCHES_INITIAL_STATE',
  }),
});

export default connect(mapStateToProps, mapDispatchToProps)(enhancer(GenericDemarcheSummary));
