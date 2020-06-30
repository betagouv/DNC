import { compose, pure } from 'recompose';

import GenericDemarcheProvideIdentity from 'react/components/embedded/GenericDemarcheProvideIdentity';
import PropTypes from 'prop-types';
import React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router-dom';

const enhancer = compose(pure);

/**
 * Créer un objet avec les données nécessaire à un textfield pour l'écran ProvideIdentity.
 *
 * @param {string} label - Le label du textfield.
 * @param {string} id - L'id du textfield.
 * @param {string} placeholder - Le placeholder du textfield.
 * @param value
 * @returns {*} - Un object avec name, id et placeholder.
 */
function createProvideIdentityTextField(label, id, placeholder, value) {
  return {
    label,
    id,
    placeholder,
    value,
  };
}

const RestaurantScolaireProvideIdentity = (props) => {
  const saveProvideIdentityData = (data) => {
    props.saveNumeroAllocataire(data.numero_allocataire);
    props.saveCodePostal(data.code_postal);

    props.history.push('select');
  };

  return (
    <>
      <GenericDemarcheProvideIdentity
        title="Indiquez votre numéro d’allocataire et votre code postal."
        textFields={[
          createProvideIdentityTextField(
            "Numéro d' allocataire",
            'numero_allocataire',
            '123456789',
            '123456789',
          ),
          createProvideIdentityTextField('Code postal', 'code_postal', '75000', '75000'),
        ]}
        saveDataCallback={saveProvideIdentityData}
      />
    </>
  );
};

RestaurantScolaireProvideIdentity.propTypes = {
  history: PropTypes.shape({ push: PropTypes.func.isRequired }).isRequired,
  saveNumeroAllocataire: PropTypes.func.isRequired,
  saveCodePostal: PropTypes.func.isRequired,
};

RestaurantScolaireProvideIdentity.defaultProps = {};

const mapDispatchToProps = dispatch => ({
  saveNumeroAllocataire: numeroAllocataire => dispatch({
    type: 'SAVE_NUMERO_ALLOCATAIRE',
    numeroAllocataire,
  }),
  saveCodePostal: codePostal => dispatch({
    type: 'SAVE_CODE_POSTAL',
    codePostal,
  }),
});

export default connect(
  null,
  mapDispatchToProps,
)(withRouter(enhancer(RestaurantScolaireProvideIdentity)));
