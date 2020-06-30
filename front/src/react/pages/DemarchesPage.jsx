import React from 'react';
import { compose, pure } from 'recompose';
import { connect } from 'react-redux';
import Demarches from 'react/views/demarches/Demarches';
import PropTypes from 'prop-types';

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
