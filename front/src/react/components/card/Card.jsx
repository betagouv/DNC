import React from 'react';
import PropTypes from 'prop-types';
import { compose, pure } from 'recompose';

import { Card as MuiCard } from '@material-ui/core';
import { withStyles } from '@material-ui/core/styles';

const enhancer = compose(pure);

const stylized = withStyles(() => ({
  root: {
    height: '100%',
    borderRadius: '20px',
    boxShadow: ' 0 4px 20px 0 rgba(0, 0, 0, 0.15)',
  },
}));

const Card = props => (
  <>
    <MuiCard {...props}>{props.children}</MuiCard>
  </>
);

Card.propTypes = {
  children: PropTypes.node,
};

Card.defaultProps = {
  children: null,
};

export default enhancer(stylized(Card));
