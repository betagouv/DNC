import React from 'react';
import { compose, pure } from 'recompose';
import { sessionService } from 'redux-react-session';
import { Redirect } from 'react-router-dom';
import { connect } from 'react-redux';
import PropTypes from 'prop-types';

const enhancer = compose(pure);

const LogoutCallback = (props) => {
  const [redirect, setRedirect] = React.useState(false);

  React.useEffect(() => {
    // eslint-disable-next-line jsdoc/require-jsdoc
    async function invalidateSession() {
      props.hideHeader();

      await sessionService.invalidateSession();

      props.initialState();

      setRedirect(true);
    }

    invalidateSession();
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  if (redirect) {
    return <Redirect to="/" />;
  }

  return null;
};

LogoutCallback.propTypes = {
  initialState: PropTypes.func.isRequired,
  hideHeader: PropTypes.func.isRequired,
};

const mapDispatchToProps = dispatch => ({
  initialState: () => dispatch({ type: 'INITIAL_STATE' }),
  hideHeader: () => dispatch({
    type: 'SHOW_HEADER',
    showHeader: false,
  }),
});

export default connect(null, mapDispatchToProps)(enhancer(LogoutCallback));
