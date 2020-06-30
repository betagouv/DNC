import React from 'react';
import { compose, pure } from 'recompose';
import { Grid } from '@material-ui/core';
import Card from 'react/components/card/Card';
import { makeStyles } from '@material-ui/core/styles';

const enhancer = compose(pure);

const useStyles = makeStyles({
  gridContainer: {
    padding: '1rem',
    margin: 0,
    maxWidth: '100%',
    height: '100%',
  },
  iframe: {
    width: '100%',
    height: '100%',
    border: 'none',
    padding: '30px',
  },
  card: {
    height: '100%',
  },
});

const Aide = () => {
  const classes = useStyles();

  return (
    <>
      <Grid container spacing={6} justify="center" className={classes.gridContainer}>
        <Grid item xs={12}>
          <Card className={classes.card} />
        </Grid>
      </Grid>
    </>
  );
};

Aide.propTypes = {};

Aide.defaultProps = {};

export default enhancer(Aide);
