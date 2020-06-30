export default class StationnementUsagerInfoFactory {
  /**
   * Créé un objet StationnementUsagerInfo.
   *
   */
  constructor() {
    const usagerInfo = {};

    usagerInfo.prenoms = '';
    usagerInfo.nomFamille = '';
    usagerInfo.adresse = '';
    usagerInfo.communeReference = '';
    usagerInfo.immatriculationVehicule = '';
    usagerInfo.typeVehicule = '';
    usagerInfo.adresseJustifiee = false;

    return usagerInfo;
  }
}
