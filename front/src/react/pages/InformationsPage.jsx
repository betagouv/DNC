import React from 'react';
import { compose, pure } from 'recompose';
import { connect } from 'react-redux';
import PropTypes from 'prop-types';
import Informations from 'react/views/informations/Informations';

const enhancer = compose(pure);

const InformationsPage = (props) => {
  React.useEffect(() => {
    document.title = 'Mes informations';
    props.updateSelectedDrawerItem(props.drawerIndex);
  }, []);

  return <Informations />;
};

InformationsPage.propTypes = {
  drawerIndex: PropTypes.number.isRequired,
  updateSelectedDrawerItem: PropTypes.func.isRequired,
};

InformationsPage.defaultProps = {};

const mapDispatchToProps = dispatch => ({
  updateSelectedDrawerItem: selectedKey => dispatch({
    type: 'UPDATE_SELECTED_DRAWER_ITEM',
    selectedKey,
  }),
});

export default connect(null, mapDispatchToProps)(enhancer(InformationsPage));
