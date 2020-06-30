import { IconButton, InputLabel } from '@material-ui/core';
import { makeStyles } from '@material-ui/core/styles';
import addSvg from 'assets/images/ic-add.svg';
import React from 'react';
import { compose, pure } from 'recompose';
import colors from 'style/config.variables.scss';
import PropTypes from 'prop-types';
import JustificatifsPersonnalisesAdditionalDataComponent from './JustificatifsPersonnalisesAdditionalDataComponent';

const enhancer = compose(pure);

const useStyles = makeStyles(theme => ({
  label: {
    fontSize: '0.9375rem',
    color: '#4f4f4f',
    whiteSpace: 'nowrap',
    overflow: 'hidden',
    textOverflow: 'ellipsis',
  },
  clearIcon: ({ values }) => ({
    width: '1.875rem',
    height: '1.875rem',
    transition: theme.transitions.create(['transform'], { duration: '0.3s' }),
    transform: values.length > 0 ? 'rotate(-45deg)' : 'rotate(0deg)',
  }),
  iconButton: ({ source }) => ({
    padding: 0,
    cursor: 'pointer',
    '&:hover': {
      backgroundColor: 'rgba(0, 0, 0, 0.24)',
    },
    marginLeft: source && source !== '' ? '' : 'auto',
  }),
}));

const JustificatifsPersonnalisesAdditionalDataButton = (props) => {
  const [values, setValues] = React.useState([]);
  const classes = useStyles({ values, source: props.source });

  const handleClick = () => {
    setValues(values.length > 0 ? [] : props.values);

    props.onClick(
      props.values.map(value => ({
        id: value.id,
        data: values.length > 0 ? undefined : { ...value, img: props.img },
      })),
    );
  };

  const endIcon = <img className={classes.clearIcon} id="end_icon" alt="Icon" src={addSvg} />;

  const endButton = (
    <>
      <IconButton className={classes.iconButton} onClick={handleClick}>
        {endIcon}
      </IconButton>
    </>
  );

  const label = (
    <InputLabel className={classes.label} id="label">
      {values.length > 0 ? null : props.label}
    </InputLabel>
  );

  return (
    <JustificatifsPersonnalisesAdditionalDataComponent
      label={label}
      source={props.source}
      endButton={endButton}
      onClick={handleClick}
      style={{
        border: values.length > 0 ? `solid 1px ${colors.darkishBlue}` : '',
        boxShadow: values.length > 0 ? '' : '0 2px 15px 0 rgba(0, 0, 0, 0.25)',
      }}
    >
      <p className={classes.label}>
        {values.map(value => (
          <>
            {value.label} : {value.value}
            <br />
          </>
        ))}
      </p>
    </JustificatifsPersonnalisesAdditionalDataComponent>
  );
};

JustificatifsPersonnalisesAdditionalDataButton.propTypes = {
  values: PropTypes.arrayOf(PropTypes.objectOf(PropTypes.string)),
  onClick: PropTypes.func,
  img: PropTypes.string,
  label: PropTypes.string,
  source: PropTypes.string,
};

JustificatifsPersonnalisesAdditionalDataButton.defaultProps = {
  values: [],
  onClick: () => {},
  img: undefined,
  label: undefined,
  source: undefined,
};

export default enhancer(JustificatifsPersonnalisesAdditionalDataButton);
