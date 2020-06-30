import React from 'react';
import { compose, pure } from 'recompose';
import { connect } from 'react-redux';
import Aide from 'react/views/aide/Aide';
import PropTypes from 'prop-types';

const enhancer = compose(pure);

const AidePage = (props) => {
  React.useEffect(() => {
    document.title = 'Aide';
    props.updateSelectedDrawerItem(props.drawerIndex);
  }, []);

  return <Aide />;
};

AidePage.propTypes = {
  drawerIndex: PropTypes.number.isRequired,
  updateSelectedDrawerItem: PropTypes.func.isRequired,
};

AidePage.defaultProps = {};

const mapDispatchToProps = dispatch => ({
  updateSelectedDrawerItem: selectedKey => dispatch({
    type: 'UPDATE_SELECTED_DRAWER_ITEM',
    selectedKey,
  }),
});

export default connect(null, mapDispatchToProps)(enhancer(AidePage));
