const justificatifsReducers = (state = {}, action) => {
  switch (action.type) {
    case 'SET_JUSTIFICATIF_PERSONNALISE_INITIAL':
      return { ...state, justificatifInitial: action.justificatifInitial };
    case 'INITIAL_STATE':
      return {};
    default:
      return state;
  }
};

export default justificatifsReducers;
