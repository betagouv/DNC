import React from 'react';
import { compose, pure } from 'recompose';
import { connect } from 'react-redux';
import Justificatifs from 'react/views/justificatifs/Justificatifs';
import PropTypes from 'prop-types';

const enhancer = compose(pure);

const JustificatifsPage = (props) => {
  React.useEffect(() => {
    document.title = 'Mes justificatifs';
    props.updateSelectedDrawerItem(props.drawerIndex);
  }, []);

  return <Justificatifs {...props} />;
};

JustificatifsPage.propTypes = {
  drawerIndex: PropTypes.number.isRequired,
  updateSelectedDrawerItem: PropTypes.func.isRequired,
};

JustificatifsPage.defaultProps = {};

const mapDispatchToProps = dispatch => ({
  updateSelectedDrawerItem: selectedKey => dispatch({
    type: 'UPDATE_SELECTED_DRAWER_ITEM',
    selectedKey,
  }),
});

export default connect(null, mapDispatchToProps)(enhancer(JustificatifsPage));
