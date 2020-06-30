const navigationReducers = (state = { selectedKey: 0 }, action) => {
  switch (action.type) {
    case 'UPDATE_SELECTED_DRAWER_ITEM':
      return { ...state, selectedKey: action.selectedKey };
    case 'START_EMBEDDED_WORKFLOW':
      return { ...state, embedded: true };
    case 'START_STANDALONE_WORKFLOW':
      return { ...state, embedded: false };
    case 'TOGGLE_MOBILE_DRAWER':
      return { ...state, mobileDrawerVisible: action.mobileDrawerVisible };
    case 'INITIAL_STATE':
      return {};
    default:
      return state;
  }
};

export default navigationReducers;
