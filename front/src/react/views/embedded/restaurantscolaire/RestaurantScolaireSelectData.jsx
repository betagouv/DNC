import React from 'react';
import { compose, pure } from 'recompose';
import { connect } from 'react-redux';
import PropTypes from 'prop-types';
import { sessionService } from 'redux-react-session';
import GenericDemarcheSelectData from 'react/components/embedded/GenericDemarcheSelectData';
import { withRouter } from 'react-router-dom';

const enhancer = compose(pure);

/**
 * Récupère la liste des enfants.
 *
 * @param {string} numeroAllocataire - Le numéro d'allocataire de l'usager.
 * @param {number} codePostal - Le code postal de l'usager.
 * @param {Function} fetchEnfantsInfo - La fonction redux pour lancer l'action FETCH_ENFANTS.
 */
async function fetchEnfants(numeroAllocataire, codePostal, fetchEnfantsInfo) {
  const session = await sessionService.loadSession();

  if (!session.tokenInfo) {
    return;
  }

  fetchEnfantsInfo(numeroAllocataire, codePostal, session.tokenInfo.access_token);
}

/**
 * Récupère la liste des adresses.
 *
 * @param {Function} fetchAdressesInfo - La fonction redux pour lancer l'action FETCH_ADRESSES.
 */
async function fetchAdresses(fetchAdressesInfo) {
  const session = await sessionService.loadSession();

  if (!session.tokenInfo) {
    return;
  }

  fetchAdressesInfo(session.tokenInfo.access_token);
}

const RestaurantScolaireSelectData = (props) => {
  React.useEffect(() => {
    fetchEnfants(props.numeroAllocataire, props.codePostal, props.fetchEnfantsInfo);
    fetchAdresses(props.fetchAdressesInfo);
  }, []);

  const saveSelectData = (data) => {
    props.saveEnfant(props.enfantsInfo.filter(enfant => enfant.id === data.enfant_beneficiaire)[0]);
    props.saveAdresse(props.adresses.filter(adresse => adresse.id === data.adresse_enfant)[0]);

    props.history.push('check');
  };

  const selects = [
    {
      label: 'Enfant bénéficiaire',
      id: 'enfant_beneficiaire',
      options: props.enfantsInfo.map(enfant => ({
        id: enfant.id,
        value: `${enfant.nom_famille} ${enfant.prenoms}`,
      })),
    },
    {
      label: "Adresse de l'enfant",
      id: 'adresse_enfant',
      options: props.adresses,
    },
  ];

  return (
    <>
      <GenericDemarcheSelectData
        title="Indiquez l’enfant que vous souhaitez inscrire."
        selects={selects}
        saveDataCallback={saveSelectData}
      />
    </>
  );
};

RestaurantScolaireSelectData.propTypes = {
  history: PropTypes.shape({ push: PropTypes.func.isRequired }).isRequired,
  numeroAllocataire: PropTypes.string,
  codePostal: PropTypes.string,
  enfantsInfo: PropTypes.arrayOf(PropTypes.objectOf(PropTypes.string)),
  adresses: PropTypes.arrayOf(PropTypes.objectOf(PropTypes.string)),

  saveEnfant: PropTypes.func.isRequired,
  saveAdresse: PropTypes.func.isRequired,
  fetchEnfantsInfo: PropTypes.func.isRequired,
  fetchAdressesInfo: PropTypes.func.isRequired,
};

RestaurantScolaireSelectData.defaultProps = {
  numeroAllocataire: '',
  codePostal: '',
  enfantsInfo: [],
  adresses: [],
};

const mapStateToProps = state => ({
  numeroAllocataire: state.demarches.numeroAllocataire,
  codePostal: state.demarches.codePostal,
  enfantsInfo: state.demarches.enfantsInfo,
  adresses: state.demarches.adresses,
});

const mapDispatchToProps = dispatch => ({
  fetchEnfantsInfo: (numeroAllocataire, codePostal, token) => dispatch({
    type: 'FETCH_ENFANTS',
    numeroAllocataire,
    codePostal,
    token,
  }),
  fetchAdressesInfo: token => dispatch({
    type: 'FETCH_ADRESSES',
    token,
  }),
  saveEnfant: enfant => dispatch({
    type: 'SAVE_ENFANT',
    enfant,
  }),
  saveAdresse: adresse => dispatch({
    type: 'SAVE_ADRESSE',
    adresse,
  }),
});

export default connect(
  mapStateToProps,
  mapDispatchToProps,
)(withRouter(enhancer(RestaurantScolaireSelectData)));
