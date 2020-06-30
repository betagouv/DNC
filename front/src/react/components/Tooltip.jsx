import React from 'react';
import { compose, pure } from 'recompose';
import PropTypes from 'prop-types';
import { Tooltip as MuiTooltip } from '@material-ui/core';
import { makeStyles } from '@material-ui/core/styles';
import colors from 'style/config.variables.scss';

const enhancer = compose(pure);

const useStyles = makeStyles({
  tooltip: {
    background: colors.lightNavyBlue,
    color: 'white',
    fontStyle: 'italic',
    fontSize: '1rem',
    maxWidth: '25rem',
    padding: '1.25rem 0.625rem 1.25rem 0.625rem',
    textAlign: 'center',
    borderRadius: '8px',
    '& .MuiTooltip-arrow': {
      color: colors.lightNavyBlue,
    },
  },
});

const Tooltip = (props) => {
  const classes = useStyles();

  return (
    <MuiTooltip title={props.title} placement="top" arrow classes={{ tooltip: classes.tooltip }}>
      {props.children}
    </MuiTooltip>
  );
};

Tooltip.propTypes = {
  title: PropTypes.string,
  children: PropTypes.node,
};

Tooltip.defaultProps = {
  title: '',
  children: null,
};

export default enhancer(Tooltip);
