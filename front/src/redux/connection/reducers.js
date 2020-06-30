const connectionReducer = (state = {}, action) => {
  switch (action.type) {
    case 'FETCH_TOKEN_SUCCEEDED':
      return {
        ...state,
        tokenInfo: action.tokenInfo,
        authenticated: action.authenticated,
      };
    case 'SHOW_HEADER':
      return { ...state, showHeader: action.showHeader };
    case 'INITIAL_STATE':
      return {};
    case 'RESET_ALERTES':
      return {
        ...state,
        alertes: 0,
      };
    default:
      return state;
  }
};

export default connectionReducer;
