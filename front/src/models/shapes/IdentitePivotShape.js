import PropTypes from 'prop-types';

export const identitePivotShape = PropTypes.shape({
  prenoms: PropTypes.string.isRequired,
  nomFamille: PropTypes.string.isRequired,
  dateNaissance: PropTypes.string.isRequired,
  lieuNaissance: PropTypes.string.isRequired,
  paysNaissance: PropTypes.string.isRequired,
  adresse: PropTypes.string,
  codePostal: PropTypes.string,
  mail: PropTypes.string,
  genre: PropTypes.string,
});
