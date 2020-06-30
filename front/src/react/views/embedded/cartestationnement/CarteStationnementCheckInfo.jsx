import { compose, pure } from 'recompose';

import GenericDemarcheCheckInfo from 'react/components/embedded/GenericDemarcheCheckInfo';
import PropTypes from 'prop-types';
import React from 'react';
import { connect } from 'react-redux';

const enhancer = compose(pure);

/**
 * Retourne une liste d'objet pour un affichage avec libellée/valeur pour l'écran CheckInfo.
 *
 * @param {*} props - Les props du composant.
 * @returns {any[]} - Les infos de l'usager formatées.
 */
function formatUsagerInfos(props) {
  if (!props.carteStationnementInfo) {
    return [];
  }

  return [
    {
      label: 'nom',
      value: props.carteStationnementInfo.nom,
    },
    {
      label: 'prénom',
      value: props.carteStationnementInfo.prenoms,
    },
    {
      label: 'siret',
      value: props.carteStationnementInfo.siret,
    },
    {
      label: 'raison sociale',
      value: props.carteStationnementInfo.raisonSociale,
    },
    {
      label: 'adresse',
      value: props.adresse,
    },
    {
      label: 'immatriculation',
      value: props.carteStationnementInfo.immatriculation,
    },
    {
      label: 'modèle',
      value: props.carteStationnementInfo.modele,
    },
    {
      label: 'véhicule électrique',
      value: props.carteStationnementInfo.vehiculeElectrique,
    },
  ];
}

const RestaurantScolaireCheckInfo = props => (
  <>
    <GenericDemarcheCheckInfo
      formatedUsagerInfos={formatUsagerInfos(props)}
      fetchDataFunction={() => {
        props.fetchCarteStationnementInfo(props, props.embeddedData.siret, 'token');
      }}
    />
  </>
);
RestaurantScolaireCheckInfo.propTypes = {
  fetchCarteStationnementInfo: PropTypes.func.isRequired,
  embeddedData: PropTypes.objectOf(
    PropTypes.oneOf(PropTypes.string, PropTypes.number, PropTypes.bool),
  ),
};

RestaurantScolaireCheckInfo.defaultProps = {
  embeddedData: {},
};

const mapStateToProps = state => ({
  carteStationnementInfo: state.demarches.carteStationnementInfo,
  embeddedData: state.demarches.embeddedData,
  vehicule: state.demarches.vehicule,
  adresse: state.demarches.adresse,
});

const mapDispatchToProps = dispatch => ({
  fetchCarteStationnementInfo: (props, vehiculeSociete, token) => dispatch({
    type: 'FETCH_CARTE_STATIONNEMENT',
    props,
    vehiculeSociete,
    token,
  }),
});

export default connect(mapStateToProps, mapDispatchToProps)(enhancer(RestaurantScolaireCheckInfo));
