import React from 'react';
import { compose, pure } from 'recompose';
import PropTypes from 'prop-types';
import { Button, IconButton } from '@material-ui/core';
import { makeStyles, useTheme } from '@material-ui/core/styles';
import useMediaQuery from '@material-ui/core/useMediaQuery';
import colors from 'style/config.variables.scss';

const enhancer = compose(pure);

const useStyles = makeStyles(theme => ({
  button: {
    textTransform: 'initial',
    paddingLeft: '1rem',
    color: colors.darkishBlue,
    fontFamily: 'Roboto',
    fontSize: '1rem',
    fontWeight: 'bold',
    fontStretch: 'normal',
    fontStyle: 'normal',
    lineHeight: 1,
    letterSpacing: 'normal',
    textAlign: 'center',
    [theme.breakpoints.down('xs')]: {
      flexGrow: 1,
    },
    '&:hover': {
      backgroundColor: colors.darkishBlueAlpha,
    },
  },
}));

const HeaderItem = (props) => {
  const classes = useStyles();
  const theme = useTheme();
  const xsScreen = useMediaQuery(theme.breakpoints.down('xs'));

  const onClick = (itemClickHandler) => {
    if (itemClickHandler) {
      itemClickHandler();
    }
  };

  let button = (
    <Button
      size="large"
      color="default"
      startIcon={props.icon}
      className={classes.button}
      onClick={() => onClick(props.onClick)}
      style={{ ...props.style }}
    >
      {props.children}
    </Button>
  );

  if (xsScreen) {
    button = (
      <IconButton onClick={() => onClick(props.onClick)} style={{ ...props.style }}>
        {props.icon}
      </IconButton>
    );
  }

  return button;
};

HeaderItem.propTypes = {
  icon: PropTypes.element,
  onClick: PropTypes.func,
  children: PropTypes.node,
};

HeaderItem.defaultProps = {
  icon: '',
  onClick: null,
  children: null,
};

export default enhancer(HeaderItem);
