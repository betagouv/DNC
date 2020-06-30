import React from 'react';
import { compose, pure } from 'recompose';

import { NavLink } from 'react-router-dom';

import { Container, Fab, Grid } from '@material-ui/core';
import { connect } from 'react-redux';

const enhancer = compose(pure);
/* <Document file={pdf}>
        <Page pageNumber={1} scale={0.5} />
      </Document> */
const StationnementSummary = () => (
  <>
    <Container
      maxWidth="md"
      style={{
        backgroundColor: '#D8D8D8',
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
      }}
    />

    <Grid justify="center" container style={{ marginTop: '1rem' }}>
      <NavLink to={{ pathname: '/stationnement/summary' }}>
        <Fab
          variant="extended"
          size="medium"
          aria-label="add"
          style={{ backgroundColor: '#4F4F4F', color: '#FFFFFF' }}
        >
          <b>Valider</b>
        </Fab>
      </NavLink>
    </Grid>
  </>
);

StationnementSummary.propTypes = {};

StationnementSummary.defaultProps = {};

const mapStateToProps = () => ({});

export default connect(mapStateToProps)(enhancer(StationnementSummary));
