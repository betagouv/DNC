import React from 'react';
import { compose, pure } from 'recompose';
import PropTypes from 'prop-types';
import { makeStyles } from '@material-ui/core/styles';

const enhancer = compose(pure);

const useStyles = makeStyles(() => ({
  footer: {
    background: 'white',
    width: '100%',
    position: 'sticky',
    bottom: 0,
    boxShadow: '0 -4px 20px 0 rgba(0, 0, 0, 0.15)',
    display: 'flex',
    justifyContent: 'center',
  },
}));

const FixedFooter = (props) => {
  const classes = useStyles(props);

  return (
    <div className={classes.footer} style={props.style}>
      {props.children}
    </div>
  );
};

FixedFooter.propTypes = {
  children: PropTypes.node,
  style: PropTypes.objectOf(PropTypes.oneOfType([PropTypes.string, PropTypes.number])),
};

FixedFooter.defaultProps = {
  children: null,
  style: {},
};

export default enhancer(FixedFooter);
