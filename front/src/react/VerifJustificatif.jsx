import { Grid } from '@material-ui/core';
import { makeStyles, useTheme } from '@material-ui/core/styles';
import useMediaQuery from '@material-ui/core/useMediaQuery';
import PropTypes from 'prop-types';
import React from 'react';
import { connect } from 'react-redux';
import Card from 'react/components/card/Card';
import { compose, pure } from 'recompose';
import justificatifManager from 'services/JustificatifsManager';

const enhancer = compose(pure);

const useStyles = makeStyles(() => ({
  cardContent: {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    height: '100%',
    padding: '24px',
  },
  grid: {
    padding: '1rem',
    margin: 0,
    height: '100%',
  },
  gridItem: { maxWidth: '40.625rem' },
  button: {
    width: '80%',
  },
  card: {
    /* [theme.breakpoints.down('xs')]: {
      boxShadow: 'none',
    }, */
    padding: '1.5rem',
  },
}));

const VerifJustificatif = (props) => {
  const theme = useTheme();
  const xsScreen = useMediaQuery(theme.breakpoints.down('xs'));
  const classes = useStyles({ xsScreen });

  const [justifData, setJustifData] = React.useState([]);
  const [error, setError] = React.useState(null);

  React.useEffect(() => {
    /**
     *
     */
    async function verifData() {
      const data = await justificatifManager.verifJustificatif(props.location.search);
      setJustifData(data);

      if (!data || data.length <= 0 || data.filter(justif => justif === null).length > 0) {
        setError(
          <p style={{ color: 'red', textAlign: 'center' }}>
            <b>Le lien fourni est invalide.</b>
          </p>,
        );
      }
    }

    props.hideHeader();
    verifData();
  }, []);

  return (
    <>
      <Grid container justify="center" alignItems="center" className={`${classes.grid}`}>
        <Grid item xs className={classes.gridItem}>
          <Card className={classes.card}>
            {error
              || justifData.map(data => (
                <p style={{ color: 'black', textAlign: 'center' }}>
                  <b>{data}</b>
                </p>
              ))}
          </Card>
        </Grid>
      </Grid>
    </>
  );
};

VerifJustificatif.propTypes = {
  hideHeader: PropTypes.func.isRequired,
  history: PropTypes.shape({ push: PropTypes.func.isRequired }).isRequired,
  location: PropTypes.shape({
    search: PropTypes.string.isRequired,
  }).isRequired,
};

VerifJustificatif.defaultProps = {};

const mapStateToProps = () => ({});

const mapDispatchToProps = dispatch => ({
  hideHeader: () => dispatch({
    type: 'SHOW_HEADER',
    showHeader: false,
  }),
});

export default connect(mapStateToProps, mapDispatchToProps)(enhancer(VerifJustificatif));
