import React from 'react';
import { compose, pure } from 'recompose';
import { connect } from 'react-redux';
import Annuaire from 'react/views/annuaire/Annuaire';
import PropTypes from 'prop-types';

const enhancer = compose(pure);

const AnnuairePage = (props) => {
  React.useEffect(() => {
    document.title = 'Annuaire services FranceConnect';
    props.updateSelectedDrawerItem(props.drawerIndex);
  }, []);

  return <Annuaire />;
};

AnnuairePage.propTypes = {
  drawerIndex: PropTypes.number.isRequired,
  updateSelectedDrawerItem: PropTypes.func.isRequired,
};

AnnuairePage.defaultProps = {};

const mapDispatchToProps = dispatch => ({
  updateSelectedDrawerItem: selectedKey => dispatch({
    type: 'UPDATE_SELECTED_DRAWER_ITEM',
    selectedKey,
  }),
});

export default connect(null, mapDispatchToProps)(enhancer(AnnuairePage));
