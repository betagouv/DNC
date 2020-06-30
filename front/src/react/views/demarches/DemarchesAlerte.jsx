import './ongoing/OngoingDemarches.module.scss';

import { compose, pure } from 'recompose';

import { Grid } from '@material-ui/core';
import PropTypes from 'prop-types';
import React from 'react';
import { connect } from 'react-redux';
import { makeStyles } from '@material-ui/core/styles';
import { resetAlertes } from 'redux/connection/sagas';
import { sessionService } from 'redux-react-session';
import OngoingDemarches from './ongoing/OngoingDemarches';

const enhancer = compose(pure);

const useStyles = makeStyles({
  grid: { padding: '1.5rem', margin: 0, maxWidth: '100%' },
});
/**
 * @param {*} props - Les props du composant.
 */
async function fetchDemarches(props) {
  const session = await sessionService.loadSession();

  if (!session.tokenInfo) {
    return;
  }

  props.fetchInfosDemarches(session.tokenInfo.access_token);
}

const Demarches = (props) => {
  const classes = useStyles();

  React.useEffect(() => {
    props.updateSelectedDrawerItem(props.drawerIndex);
    resetAlertes();
    fetchDemarches(props);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  return (
    <>
      <Grid container spacing={6} justify="center" className={classes.grid}>
        <OngoingDemarches
          {...props}
          demarches={
            props.infosDemarches
              ? props.infosDemarches.filter(demarche => demarche.alerte_etat
              || demarche.alerte_documents)
              : []
          }
        />

        {/* On supprime (temporairement) la partie "Mes prochaines démarches" */}
        {/* <NextDemarches {...props} /> */}
      </Grid>
    </>
  );
};

Demarches.displayName = 'Demarches';

Demarches.propTypes = {
  identitePivot: PropTypes.shape({ alerte: PropTypes.bool }),
  drawerIndex: PropTypes.number.isRequired,
  updateSelectedDrawerItem: PropTypes.func.isRequired,
  updateCookies: PropTypes.func.isRequired,
  infosDemarches: PropTypes.arrayOf(
    PropTypes.shape({
      id: PropTypes.number,
      updated_at: PropTypes.string,
      state: PropTypes.string,
      initiated_at: PropTypes.string,
      libelle_demarche: PropTypes.string,
      sources: PropTypes.string,
      documents_en_attente: PropTypes.arrayOf(PropTypes.string),
      alerte_etat: PropTypes.bool,
      alerte_documents: PropTypes.bool,
    }),
  ),
};

Demarches.defaultProps = {
  infosDemarches: null,
  identitePivot: { alerte: false },
};

const mapStateToProps = state => ({
  isHeaderVisible: state.connection.showHeader,
  infosDemarches: state.identity.infosDemarches,
});

const mapDispatchToProps = dispatch => ({
  updateSelectedDrawerItem: selectedKey => dispatch({
    type: 'UPDATE_SELECTED_DRAWER_ITEM',
    selectedKey,
  }),
  fetchInfosDemarches: token => dispatch({
    type: 'FETCH_INFOS_DEMARCHES',
    token,
  }),
});

export default connect(mapStateToProps, mapDispatchToProps)(enhancer(Demarches));
