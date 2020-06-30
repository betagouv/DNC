import { compose, pure } from 'recompose';

import DemarcheManager from 'services/DemarcheManager';
import GenericDemarcheSelectData from 'react/components/embedded/GenericDemarcheSelectData';
import PropTypes from 'prop-types';
import React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router-dom';

const enhancer = compose(pure);

class CarteStationnementSelectData extends React.PureComponent {
  // const CarteStationnementSelectData = (props) => {
  state = {
    listeVehicule: [],
    selects: [],
    listeAdresse: [],
  };

  /*
  React.useEffect(() => {
    fetchVehicules(props.fetchVehiculesInfo);
    if (props.embeddedData) {
      fetchSocieteAdresses(props.fetchSocieteAdressesInfo, props.embeddedData.siret);
    }
    fetchSocietes(props.fetchSocietesInfo);
  }, []);
  */
  /**
   * Constructeur de l'objet, va chercher l'adresse.
   *
   * @param {*} props - Les props.
   */
  constructor(props) {
    super(props);
    document.addEventListener('RECEIVED_DATA_FROM_BACK', (event) => {
      this.setState({ listeVehicule: JSON.parse(event.detail[0].data.vehicules) });
      this.setState({ listeAdresse: event.detail[0].data.adressesConnues });
      this.setState({ idSession: event.detail[1] });
      this.setState({ idDemarche: event.detail[0].data.idDemarche });
      this.selects = [
        {
          label: 'Véhicule',
          id: 'vehicule',
          options: this.state.listeVehicule.map(vehicule => ({
            id: vehicule.id,
            value: `${vehicule.immatriculation} ${vehicule.modele}`,
          })),
        },
        {
          label: 'Adresse',
          id: 'adresse',
          options: [
            { id: 'adresse_1', value: this.state.listeAdresse },
          ],
        },
      ];
      this.setState({ selects: this.selects });
    });
    // this.fetchSocieteAdresses();
  }

  /* if (props.embeddedData.vehiculeSociete) {
    selects.push({
      label: 'société',
      id: 'societe',
      options: props.societesInfo.map(societe => ({
        id: societe.id,
        value: `${societe.raisonSociale} - ${societe.adresse}`,
      })),
    });
  } else {
    selects.push({
      label: 'Adresse',
      id: 'adresse',
      options: props.adresses,
    });
  } */

  /**
   * Rend l'UI.
   *
   * @returns {*} UI.
   */
  render() {
    return (
      <>
        {this.state.selects.length > 0
          ? (
            <GenericDemarcheSelectData
              title="Indiquez le véhicule que vous souhaitez choisir."
              selects={this.state.selects}
              saveDataCallback={(data) => {
                this.props.saveVehicule(
                  this.state.listeVehicule.filter(vehicule => vehicule.id === data.vehicule)[0],
                );
                this.props.saveAdresse(this.state.listeAdresse);
                DemarcheManager.fetchUpdateDemarche(
                  this.state.idSession,
                  this.state.idDemarche,
                  '1',
                  this.state.listeVehicule.filter(vehicule => vehicule.id === data.vehicule)[0],
                  this.state.listeAdresse,
                );
                this.props.history.push('check');
              }}
            />
          ) : (
            <GenericDemarcheSelectData
              title="Indiquez le véhicule que vous souhaitez choisir."
              saveDataCallback={this.saveSelectData}
            />
          )}

      </>
    );
  }
}

CarteStationnementSelectData.propTypes = {
  history: PropTypes.shape({ push: PropTypes.func.isRequired }).isRequired,
  // vehiculesInfo: PropTypes.arrayOf(PropTypes.objectOf(PropTypes.string)),
  saveVehicule: PropTypes.func.isRequired,
  saveAdresse: PropTypes.func.isRequired,
  // fetchVehiculesInfo: PropTypes.func.isRequired,
};

CarteStationnementSelectData.defaultProps = {
};

const mapStateToProps = state => ({
  numeroAllocataire: state.demarches.numeroAllocataire,
  codePostal: state.demarches.codePostal,
  vehiculesInfo: state.demarches.vehiculesInfo,
  societesInfo: state.demarches.societesInfo,
  societeAdresses: state.demarches.societeAdresses,
  embeddedData: state.demarches.embeddedData,
});

const mapDispatchToProps = dispatch => ({
  fetchVehiculesInfo: token => dispatch({
    type: 'FETCH_VEHICULES',
    token,
  }),
  fetchSocieteAdressesInfo: (token, siret) => dispatch({
    type: 'FETCH_SOCIETE_ADRESSES',
    token,
    siret,
  }),
  fetchSocietesInfo: token => dispatch({
    type: 'FETCH_SOCIETES',
    token,
  }),
  saveVehicule: vehicule => dispatch({
    type: 'SAVE_VEHICULE',
    vehicule,
  }),
  saveAdresse: adresse => dispatch({
    type: 'SAVE_ADRESSE',
    adresse,
  }),
  saveSociete: societe => dispatch({
    type: 'SAVE_SOCIETE',
    societe,
  }),
});

export default connect(
  mapStateToProps,
  mapDispatchToProps,
)(withRouter(enhancer(CarteStationnementSelectData)));
