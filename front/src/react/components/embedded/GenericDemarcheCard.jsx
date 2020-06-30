import React from 'react';
import { compose, pure } from 'recompose';
import PropTypes from 'prop-types';
import colors from 'style/config.variables.scss';
import Button from 'react/components/buttons/button/Button';
import { makeStyles } from '@material-ui/core/styles';

const enhancer = compose(pure);

const useStyles = makeStyles(theme => ({
  button: {
    marginTop: 'auto',
    maxWidth: '15.625rem',
  },
  title: {
    fontSize: '1.125rem',
    textAlign: 'center',
    fontWeight: 'bold',
    color: '#4f4f4f',
    maxWidth: '39.375rem',
    marginBottom: '2.8125rem',

    [theme.breakpoints.down('xs')]: {
      marginBottom: '1rem',
    },
  },
}));

const GenericDemarcheCard = (props) => {
  const classes = useStyles();

  let button = (
    <Button
      size="small"
      color={colors.darkishBlue}
      onClick={props.buttonClickHandler}
      className={classes.button}
    >
      {props.buttonLabel}
    </Button>
  );

  if (props.formId) {
    button = (
      <Button
        type="submit"
        form={props.formId}
        size="small"
        color={colors.darkishBlue}
        className={classes.button}
      >
        {props.buttonLabel}
      </Button>
    );
  }

  return (
    <>
      <p className={classes.title}>{props.title}</p>

      {props.children}

      {button}
    </>
  );
};

GenericDemarcheCard.propTypes = {
  children: PropTypes.node.isRequired,
  buttonClickHandler: PropTypes.func,
  buttonLabel: PropTypes.string,
  formId: PropTypes.string,
  title: PropTypes.string,
};

GenericDemarcheCard.defaultProps = {
  buttonClickHandler: () => {},
  buttonLabel: '',
  formId: undefined,
  title: '',
};

export default enhancer(GenericDemarcheCard);
