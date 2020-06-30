import React from 'react';
import { compose, pure } from 'recompose';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import Card from 'react/components/card/Card';
import { Grid, CardContent } from '@material-ui/core';
import { makeStyles } from '@material-ui/core/styles';
import StationnementCheckInfo from './StationnementCheckInfo';
import StationnementSummary from './StationnementSummary';
import StationnementHeader from './StationnementHeader';

const enhancer = compose(pure);

const useStyles = makeStyles({
  cardContent: {
    padding: '3.4375rem',
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
  },
  gridList: {
    margin: '0 !important',
    marginTop: '1rem !important',
  },
  gridListeTileTile: {
    display: 'flex',
    flexDirection: 'column',
    justifyContent: 'center',
  },
});

const AbonnementStationnement = (props) => {
  const classes = useStyles();
  let content = null;

  switch (props.match.params.step) {
    case 'summary':
      content = (
        <>
          <StationnementHeader step={1} />

          <StationnementSummary {...props} />
        </>
      );
      break;
    default:
      props.fetchUsagerInfo(1, 2);

      content = (
        <>
          <StationnementHeader step={0} />
          <StationnementCheckInfo {...props} />
        </>
      );
  }

  return (
    <div style={{ width: '100%' }}>
      <Grid
        container
        spacing={6}
        justify="center"
        style={{ padding: '1rem', margin: 0, maxWidth: '100%' }}
      >
        <Grid item xs={12}>
          <Card>
            <CardContent className={classes.cardContent}>{content}</CardContent>
          </Card>
        </Grid>
      </Grid>
    </div>
  );
};

AbonnementStationnement.propTypes = {
  match: PropTypes.shape({
    params: PropTypes.shape({
      step: PropTypes.string.isRequired,
    }).isRequired,
  }).isRequired,
  fetchUsagerInfo: PropTypes.func.isRequired,
};

AbonnementStationnement.defaultProps = {};

const mapStateToProps = () => ({});

const mapDispatchToProps = dispatch => ({
  fetchUsagerInfo: (idDemarche, token) => dispatch({
    type: 'FETCH_ABONNEMENT_STATIONNEMENT',
    idDemarche,
    token,
  }),
});

export default connect(mapStateToProps, mapDispatchToProps)(enhancer(AbonnementStationnement));
