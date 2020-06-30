import {
  IconButton, MenuItem, Select, InputLabel,
} from '@material-ui/core';
import { makeStyles } from '@material-ui/core/styles';
import downSvg from 'assets/images/ic-down.svg';
import React from 'react';
import { compose, pure } from 'recompose';
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
  endIcon: {
    width: '1.875rem',
    height: '1.875rem',
  },
  endIconSeparator: {
    width: '1px',
    borderRadius: '0.5px',
    background: '#bebebe',
    margin: '0 0.3125rem',
    height: '2rem',
  },
  select: {
    all: 'unset',
    textOverflow: 'ellipsis',
    overflow: 'hidden',
    whiteSpace: 'nowrap',
    fontSize: '0.9375rem',
    color: '#4f4f4f',
    marginRight: '0.25rem',
    flex: '2 1',

    '& .MuiSelect-root': {
      all: 'unset',
    },
  },
  iconButton: {
    padding: 0,
    cursor: 'pointer',
    '&:hover': {
      backgroundColor: 'rgba(0, 0, 0, 0.24)',
    },
    marginLeft: 'auto',
  },
  startIcon: {
    width: '1.1875rem',
    height: '1.1875rem',
    marginRight: '0.625rem',
  },
  popover: {
    [theme.breakpoints.down('xs')]: {
      backgroundColor: 'rgba(0, 0, 0, 0.35)',
    },
  },
}));

const JustificatifsPersonnalisesAdditionalDataSelect = (props) => {
  const [open, setOpen] = React.useState(false);
  const [value, setValue] = React.useState(props.defaultValue || '');
  const classes = useStyles(/* { value, source: props.source } */);

  const handleOpen = (e) => {
    e.stopPropagation();
    setOpen(true);
  };
  const handleClose = (e) => {
    e.stopPropagation();

    setOpen(false);
  };
  const handleChange = (e) => {
    setValue(e.target.value);

    props.onChange(props.options.filter(({ id }) => e.target.value === id)[0]);
  };

  let { source } = props.options.filter(({ id }) => value === id)[0];

  source = props.source || source;

  const endButton = (
    <IconButton
      className={classes.iconButton}
      onClick={handleOpen}
      style={{ marginLeft: source ? '0' : 'auto' }}
    >
      <img className={classes.endIcon} id="end_icon" alt="Icon" src={downSvg} />
    </IconButton>
  );

  const label = (
    <InputLabel className={classes.label} id="label">
      <b>{value && value !== '' ? `${props.label} :` : props.label}&nbsp;</b>
    </InputLabel>
  );

  return (
    <JustificatifsPersonnalisesAdditionalDataComponent
      label={label}
      source={source}
      endButton={endButton}
      onClick={handleOpen}
      style={{
        backgroundColor: open ? '#e3e9f3' : '',
        boxShadow: '0 2px 15px 0 rgba(0, 0, 0, 0.25)',
      }}
    >
      {/* <img className={classes.startIcon} id="start_icon" alt="Icon" src={addressSvg} /> */}

      <Select
        className={classes.select}
        error={false}
        open={open}
        onClose={handleClose}
        onOpen={handleOpen}
        value={value}
        onChange={handleChange}
        labelId="label"
        MenuProps={{
          PopoverClasses: {
            root: classes.popover,
          },
        }}
      >
        {props.options.map(option => (
          <MenuItem value={option.id}>{option.value}</MenuItem>
        ))}
      </Select>
    </JustificatifsPersonnalisesAdditionalDataComponent>
  );
};

JustificatifsPersonnalisesAdditionalDataSelect.propTypes = {
  source: PropTypes.string,
  label: PropTypes.string,
  defaultValue: PropTypes.objectOf(PropTypes.string),
  onChange: PropTypes.func,
  options: PropTypes.arrayOf(PropTypes.objectOf(PropTypes.string)),
};

JustificatifsPersonnalisesAdditionalDataSelect.defaultProps = {
  source: undefined,
  label: undefined,
  defaultValue: undefined,
  onChange: () => {},
  options: [],
};
export default enhancer(JustificatifsPersonnalisesAdditionalDataSelect);
