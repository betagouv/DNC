const identityReducers = (state = {}, action) => {
  switch (action.type) {
    case 'FETCH_IDENTITE_PIVOT_SUCCEEDED':
      return { ...state, identitePivot: action.identitePivot };
    case 'FETCH_INFOS_FISCALES_SUCCEEDED':
      return { ...state, infosFiscales: action.infosFiscales };
    case 'FETCH_INFOS_FAMILLE_SUCCEEDED':
      return {
        ...state,
        infosFamille: action.infosFamille,
        numeroAllocataire: action.numeroAllocataire,
        codePostal: action.codePostal,
      };
    case 'FETCH_INFOS_SECU_SUCCEEDED':
      return { ...state, infosSecu: action.infosSecu };
    case 'FETCH_INFOS_POLE_EMPLOI_SUCCEEDED':
      return { ...state, infosPoleEmploi: action.infosPoleEmploi };
    case 'FETCH_INFOS_DEMARCHES_SUCCEEDED':
      return { ...state, infosDemarches: action.infosDemarches };
    case 'UPDATE_NUMERO_ALLOCATAIRE':
      return {
        ...state,
        numeroAllocataire: action.numeroAllocataire,
      };
    case 'INITIAL_STATE':
      return {};
    default:
      return state;
  }
};

export default identityReducers;
