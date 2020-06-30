import React from 'react';
import { compose, pure } from 'recompose';
import PropTypes from 'prop-types';
import colorUtils from 'utils/ColorUtils';
import MuiButton from '@material-ui/core/Button';
import { makeStyles } from '@material-ui/core/styles';

const enhancer = compose(pure);

const useStyles = makeStyles({
  button: props => ({
    padding: '1.25rem',
    borderColor: props.color,
    color: props.color,
    borderRadius: '29px',
    textTransform: 'initial',
    fontFamily: 'Roboto',
    fontSize: '1.125rem',
    fontWeight: 'bold',
    fontStretch: 'normal',
    fontStyle: 'normal',
    lineHeight: 1,
    letterSpacing: 'normal',
    textAlign: 'center',
    '&:hover': {
      backgroundColor: colorUtils.addAlphaToColor(props.color),
    },
  }),
});

const Button = (props) => {
  const classes = useStyles(props);

  return (
    <MuiButton variant="outlined" {...props} className={`${classes.button} ${props.className}`}>
      {props.children}
    </MuiButton>
  );
};

Button.propTypes = {
  className: PropTypes.string,
  children: PropTypes.node,
  color: PropTypes.string,
};

Button.defaultProps = {
  className: '',
  children: null,
  color: '',
};

export default enhancer(Button);
