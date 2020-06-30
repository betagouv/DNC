import React from 'react';
import { compose, pure } from 'recompose';
import { connect } from 'react-redux';
import Historique from 'react/views/historique/Historique';
import PropTypes from 'prop-types';

const enhancer = compose(pure);

const HistoriquePage = (props) => {
  React.useEffect(() => {
    document.title = 'Mon historique';
    props.updateSelectedDrawerItem(props.drawerIndex);
  }, []);

  return <Historique />;
};

HistoriquePage.propTypes = {
  drawerIndex: PropTypes.number.isRequired,
  updateSelectedDrawerItem: PropTypes.func.isRequired,
};

HistoriquePage.defaultProps = {};

HistoriquePage.displayName = 'Mon historique';

const mapDispatchToProps = dispatch => ({
  updateSelectedDrawerItem: selectedKey => dispatch({
    type: 'UPDATE_SELECTED_DRAWER_ITEM',
    selectedKey,
  }),
});

export default connect(null, mapDispatchToProps)(enhancer(HistoriquePage));
