const embeddedReducers = (state = {}, action) => {
  switch (action.type) {
    case 'FETCH_CURRENT_DEMARCHE_SCOPES_SUCCEEDED':
      return { ...state, currentDemarcheScopes: action.currentDemarcheScopes };
    case 'FETCH_ABONNEMENT_STATIONNEMENT_SUCCEEDED':
      return { ...state, stationnementUsagerInfo: action.stationnementUsagerInfo };
    case 'FETCH_RESTAURANT_SCOLAIRE_SUCCEEDED':
      return { ...state, restaurantScolaireInfo: action.restaurantScolaireInfo };
    case 'FETCH_ENFANTS_SUCCEEDED':
      return { ...state, enfantsInfo: action.enfantsInfo };
    case 'FETCH_ADRESSES_SUCCEEDED':
      return { ...state, adresses: action.adresses };

    case 'SAVE_NUMERO_ALLOCATAIRE':
      return { ...state, numeroAllocataire: action.numeroAllocataire };
    case 'SAVE_CODE_POSTAL':
      return { ...state, codePostal: action.codePostal };
    case 'SAVE_ENFANT':
      return { ...state, enfant: action.enfant };
    case 'SAVE_ADRESSE':
      return { ...state, adresse: action.adresse };

    // Démarche carte stationnement
    case 'FETCH_VEHICULES_SUCCEEDED':
      return { ...state, vehiculesInfo: action.vehiculesInfo };
    case 'FETCH_SOCIETE_ADRESSES_SUCCEEDED':
      return { ...state, societeAdresses: action.societeAdresses };
    case 'FETCH_USER_SESSION_SUCCEEDED':
      return { ...state, userSession: action.userSession };
    case 'SAVE_VEHICULE':
      return { ...state, vehicule: action.vehicule };
    case 'FETCH_SOCIETES_SUCCEEDED':
      return { ...state, societesInfo: action.societesInfo };
    case 'SAVE_SOCIETE':
      return { ...state, societe: action.societe };
    case 'FETCH_CARTE_STATIONNEMENT_SUCCEEDED':
      return { ...state, carteStationnementInfo: action.carteStationnementInfo };

    case 'SET_FS_REDIRECT_URI':
      return { ...state, fsRedirectUri: action.fsRedirectUri };
    case 'SET_EMBEDDED_DATA':
      return { ...state, embeddedData: action.embeddedData };
    case 'DEMARCHES_INITIAL_STATE':
      return {};
    default:
      return state;
  }
};

export default embeddedReducers;
