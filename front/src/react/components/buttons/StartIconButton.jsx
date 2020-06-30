import React from 'react';
import { compose, pure } from 'recompose';
import PropTypes from 'prop-types';
import { makeStyles, useTheme } from '@material-ui/core/styles';
import { IconButton, Button as MuiButton, useMediaQuery } from '@material-ui/core';
import colors from 'style/config.variables.scss';

const enhancer = compose(pure);

const useStyles = makeStyles(() => ({
  button: {
    fontSize: '0.875rem',
    fontWeight: 'bold',
    color: colors.darkSlateBlue,
    textTransform: 'initial',

    '& path': {
      fill: 'rgba(0, 0, 0, 0.26)',
    },
  },
}));

const StartIconButton = (props) => {
  const classes = useStyles();
  const theme = useTheme();
  const xsDown = useMediaQuery(theme.breakpoints.down('xs'));

  let icon;

  if (props.icon) {
    icon = {
      ...props.icon,
      props: {
        ...props.icon.props,
      },
    };

    if (props.disabled && props.icon.props && props.icon.props.src) {
      icon.props.src = props.icon.props.src.replace(/fill='(%23.\S+)'/g, "fill='%2300000042'");
    }
  }

  if (xsDown) {
    return (
      <IconButton className={classes.button} {...props}>
        {icon}
      </IconButton>
    );
  }

  return (
    <MuiButton startIcon={icon} color="default" className={classes.button} {...props}>
      {props.children}
    </MuiButton>
  );
};

StartIconButton.propTypes = {
  icon: PropTypes.element,
  children: PropTypes.node,
  disabled: PropTypes.bool,
};

StartIconButton.defaultProps = {
  icon: null,
  children: null,
  disabled: false,
};

export default enhancer(StartIconButton);
