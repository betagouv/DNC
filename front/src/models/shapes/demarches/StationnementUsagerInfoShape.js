import PropTypes from 'prop-types';

export default PropTypes.shape({
  prenoms: PropTypes.string.isRequired,
  nomFamille: PropTypes.string.isRequired,
  adresse: PropTypes.string.isRequired,
  communeReference: PropTypes.string.isRequired,
  immatriculationVehicule: PropTypes.string.isRequired,
  typeVehicule: PropTypes.string.isRequired,
  adresseJustifiee: PropTypes.bool.isRequired,
});
