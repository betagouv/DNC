import { compose, pure } from 'recompose';

import Demarches from 'react/views/demarches/DemarchesAlerte';
import PropTypes from 'prop-types';
import React from 'react';
import { connect } from 'react-redux';

const enhancer = compose(pure);

const DemarchesPage = (props) => {
  React.useEffect(() => {
    document.title = 'Mes démarches en cours';
    props.updateSelectedDrawerItem(props.drawerIndex);
  }, []);

  return <Demarches />;
};

DemarchesPage.propTypes = {
  drawerIndex: PropTypes.number.isRequired,
  updateSelectedDrawerItem: PropTypes.func.isRequired,
};

DemarchesPage.defaultProps = {};

const mapDispatchToProps = dispatch => ({
  updateSelectedDrawerItem: selectedKey => dispatch({
    type: 'UPDATE_SELECTED_DRAWER_ITEM',
    selectedKey,
  }),
});

export default connect(null, mapDispatchToProps)(enhancer(DemarchesPage));
