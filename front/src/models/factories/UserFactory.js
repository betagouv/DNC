/**
 * Classe de création d'un nouvel utilisateur.
 */
export default class UserFactory {
  /**
   * Créé un objet User.
   *
   */
  constructor() {
    const user = {};

    user.identitePivot = {
      prenoms: '',
      nomFamille: '',
      dateNaissance: '',
      genre: '',
      lieuNaissance: '',
      paysNaissance: '',
      adresse: '',
      codePostal: '',
      mail: 'donnée indisponible',
      telephone: 'donnée indisponible',
    };
    user.dgfip = {
      revenuFiscalReference: -1,
      nombreParts: -1,
      adresseFiscale: '',
    };
    user.cnaf = {
      quotient_familial: -1,
    };

    return user;
  }
}
