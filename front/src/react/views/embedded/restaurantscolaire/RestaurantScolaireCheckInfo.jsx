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
  if (!props.restaurantScolaireInfo) {
    return [];
  }

  return [
    {
      label: "numero d'allocataire",
      value: props.restaurantScolaireInfo.numeroAllocataire,
    },
    {
      label: 'quotient familial',
      value: props.restaurantScolaireInfo.quotientFamilial,
    },
    {
      label: 'Revenu fiscal de référence',
      value: props.restaurantScolaireInfo.revenuFiscalRef,
    },
    {
      label: 'code postal',
      value: props.restaurantScolaireInfo.codePostal,
    },
    {
      label: "nom et prénom de l'enfant",
      value: `${props.restaurantScolaireInfo.nomEnfant} ${props.restaurantScolaireInfo.prenomsEnfant}`,
    },
    /* {
      label: "prénom de l'enfant",
      value: props.restaurantScolaireInfo.prenomsEnfant,
    }, */
    {
      label: "date de naissance de l'enfant",
      value: props.restaurantScolaireInfo.dateNaissanceEnfant,
    },
    {
      label: "adresse de l'enfant",
      value: props.restaurantScolaireInfo.adresseEnfant,
    },
  ];
}

const RestaurantScolaireCheckInfo = props => (
  /* if (!props.enfant || !props.adresse) {
    return <Redirect to="select" />;
  } */

  <>
    <GenericDemarcheCheckInfo
      formatedUsagerInfos={formatUsagerInfos(props)}
      fetchDataFunction={() => {
        props.fetchRestaurantScolaireInfo(
          props.numeroAllocataire,
          props.codePostal,
          props,
          'token',
        );
      }}
    />
  </>
);
RestaurantScolaireCheckInfo.propTypes = {
  numeroAllocataire: PropTypes.string,
  codePostal: PropTypes.string,
  /* enfant: PropTypes.string,
  adresse: PropTypes.string, */
  fetchRestaurantScolaireInfo: PropTypes.func.isRequired,
};

RestaurantScolaireCheckInfo.defaultProps = {
  numeroAllocataire: '',
  codePostal: '',
  /* enfant: {},
  adresse: {}, */
};

const mapStateToProps = state => ({
  numeroAllocataire: state.demarches.numeroAllocataire,
  codePostal: state.demarches.codePostal,
  enfant: state.demarches.enfant,
  adresse: state.demarches.adresse,
  restaurantScolaireInfo: state.demarches.restaurantScolaireInfo,
});

const mapDispatchToProps = dispatch => ({
  fetchRestaurantScolaireInfo: (numeroAllocataire, codePostal, token) => dispatch({
    type: 'FETCH_RESTAURANT_SCOLAIRE',
    numeroAllocataire,
    codePostal,
    token,
  }),
});

export default connect(mapStateToProps, mapDispatchToProps)(enhancer(RestaurantScolaireCheckInfo));
