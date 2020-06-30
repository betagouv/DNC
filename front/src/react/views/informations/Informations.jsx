import React from 'react';
import { compose, pure } from 'recompose';
import { connect } from 'react-redux';
import PropTypes from 'prop-types';
import { sessionService } from 'redux-react-session';
import InformationsGrid from './InformationsGrid';

import './cards/InformationCard.module.scss';

const enhancer = compose(pure);

/**
 * @param {*} props - Les props du composant.
 */
async function fetchData(props) {
  const session = await sessionService.loadSession();

  if (!session.tokenInfo) {
    return;
  }

  if (!props.identitePivot) {
    props.fetchIdentitePivot(session.tokenInfo.access_token);
  }

  if (!props.infosSecu) {
    props.fetchInfosSecu(session.tokenInfo.access_token);
  }

  if (!props.infosPoleEmploi) {
    props.fetchInfosPoleEmploi(session.tokenInfo.access_token);
  }

  if (!props.infosDemarches) {
    props.fetchInfosDemarches(session.tokenInfo.access_token);
  }

  if (!props.infosFamille && props.numeroAllocataire && props.codePostal) {
    props.fetchInfosFamille(
      session.tokenInfo.access_token,
      props.numeroAllocataire,
      props.codePostal,
    );
  }

  if (!props.infosFiscales && props.numeroFiscal && props.referenceAvisFiscal) {
    props.fetchInfosFiscales(
      session.tokenInfo.access_token,
      props.numeroFiscal,
      props.referenceAvisFiscal,
    );
  }
}

/**
 * @param numeroFiscal
 * @param referenceAvisFiscal
 */

const Informations = (props) => {
  React.useEffect(() => {
    props.updateSelectedDrawerItem(props.drawerIndex);
    fetchData(props);
  }, []);

  const fetchInfosFamille = async (numeroAllocataire, codePostal) => {
    const session = await sessionService.loadSession();

    if (!session.tokenInfo) {
      return;
    }

    props.fetchInfosFamille(session.tokenInfo.access_token, numeroAllocataire, codePostal);
  };

  const fetchInfosFiscales = async (numeroFiscal, referenceAvisFiscal) => {
    const session = await sessionService.loadSession();

    if (!session.tokenInfo) {
      return;
    }

    props.fetchInfosFiscales(session.tokenInfo.access_token, numeroFiscal, referenceAvisFiscal);
  };

  return (
    <>
      <InformationsGrid
        fetchInfosFiscales={fetchInfosFiscales}
        fetchInfosFamille={fetchInfosFamille}
      />
    </>
  );
};

Informations.propTypes = {
  drawerIndex: PropTypes.number.isRequired,
  updateSelectedDrawerItem: PropTypes.func.isRequired,
  fetchInfosFamille: PropTypes.func.isRequired,
  fetchInfosFiscales: PropTypes.func.isRequired,
};

Informations.defaultProps = {};

const mapStateToProps = state => ({
  identitePivot: state.identity.identitePivot,
  infosFiscales: state.identity.infosFiscales,
  infosFamille: state.identity.infosFamille,
  infosSecu: state.identity.infosSecu,
  infosPoleEmploi: state.identity.infosPoleEmploi,
  infosDemarches: state.identity.infosDemarches,
  numeroAllocataire: state.identity.numeroAllocataire,
  codePostal: state.identity.codePostal,
});

const mapDispatchToProps = dispatch => ({
  fetchIdentitePivot: token => dispatch({
    type: 'FETCH_IDENTITE_PIVOT',
    token,
  }),
  fetchInfosFiscales: (token, numeroFiscal, referenceAvisFiscal) => dispatch({
    type: 'FETCH_INFOS_FISCALES',
    token,
    numeroFiscal,
    referenceAvisFiscal,
  }),
  fetchInfosFamille: (token, numeroAllocataire, codePostal) => dispatch({
    type: 'FETCH_INFOS_FAMILLE',
    token,
    numeroAllocataire,
    codePostal,
  }),
  fetchInfosSecu: token => dispatch({
    type: 'FETCH_INFOS_SECU',
    token,
  }),
  fetchInfosPoleEmploi: token => dispatch({
    type: 'FETCH_INFOS_POLE_EMPLOI',
    token,
  }),
  fetchInfosDemarches: token => dispatch({
    type: 'FETCH_INFOS_DEMARCHES',
    token,
  }),
  updateSelectedDrawerItem: selectedKey => dispatch({
    type: 'UPDATE_SELECTED_DRAWER_ITEM',
    selectedKey,
  }),
});

export default connect(mapStateToProps, mapDispatchToProps)(enhancer(Informations));
