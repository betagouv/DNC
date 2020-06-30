import React from 'react';
import PropTypes from 'prop-types';
import { Route, Redirect } from 'react-router-dom';
import { connect } from 'react-redux';

const PrivateRoute = (props) => {
  React.useEffect(() => {
    if (props.demarche) {
      props.startEmbeddedWorkflow();
      props.hideHeader();
    } else {
      props.startStandaloneWorkflow();
      props.showHeader();
    }

    document.documentElement.style.fontSize = '';
  }, []);

  const { authenticated, component } = props;

  const componentToRender = (p) => {
    if (authenticated) {
      return React.createElement(component, p);
    }
    return (
      <Redirect
        to={{
          pathname: '/home',
          state: { from: p.location },
        }}
      />
    );
  };

  return (
    <Route
      exact={props.exact}
      path={props.path}
      render={p => componentToRender({ ...props, ...p })}
    />
  );
};

PrivateRoute.propTypes = {
  component: PropTypes.func.isRequired,
  exact: PropTypes.bool,
  path: PropTypes.oneOfType([PropTypes.string, PropTypes.arrayOf(PropTypes.string)]).isRequired,
  authenticated: PropTypes.bool,
  location: PropTypes.shape({}),
  hideHeader: PropTypes.func.isRequired,
  showHeader: PropTypes.func.isRequired,
  demarche: PropTypes.bool,
  startEmbeddedWorkflow: PropTypes.func.isRequired,
  startStandaloneWorkflow: PropTypes.func.isRequired,
};

PrivateRoute.defaultProps = {
  exact: false,
  authenticated: false,
  location: null,
  demarche: false,
};

const mapDispatchToProps = dispatch => ({
  showHeader: () => dispatch({
    type: 'SHOW_HEADER',
    showHeader: true,
  }),
  hideHeader: () => dispatch({
    type: 'SHOW_HEADER',
    showHeader: false,
  }),
  startStandaloneWorkflow: () => dispatch({
    type: 'START_STANDALONE_WORKFLOW',
  }),
  startEmbeddedWorkflow: () => dispatch({
    type: 'START_EMBEDDED_WORKFLOW',
  }),
});

export default connect(null, mapDispatchToProps)(PrivateRoute);
