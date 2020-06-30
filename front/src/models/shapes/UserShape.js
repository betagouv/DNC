import PropTypes from 'prop-types';

export const userShape = PropTypes.shape({
  identitePivot: PropTypes.shape({
    prenoms: PropTypes.string.isRequired,
    nomFamille: PropTypes.string.isRequired,
    dateNaissance: PropTypes.string.isRequired,
    lieuNaissance: PropTypes.string.isRequired,
    paysNaissance: PropTypes.string.isRequired,
    adresse: PropTypes.string,
    codePostal: PropTypes.string,
    genre: PropTypes.string,
  }),
  dgip: PropTypes.shape({
    revenuFiscalReference: PropTypes.number.isRequired,
    nombres: PropTypes.number.isRequired,
    adresseFiscale: PropTypes.string.isRequired,
  }),
  cnaf: PropTypes.shape({
    dateNaissance: PropTypes.number.isRequired,
  }),
});
