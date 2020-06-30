import {
  IconButton, InputLabel, MenuItem, Select,
} from '@material-ui/core';
import { makeStyles } from '@material-ui/core/styles';
import addSvg from 'assets/images/ic-add.svg';
import downSvg from 'assets/images/ic-down.svg';
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
  downIcon: {
    width: '1.875rem',
    height: '1.875rem',
  },
  clearIcon: ({ value }) => ({
    width: '1.875rem',
    height: '1.875rem',
    transition: theme.transitions.create(['transform'], { duration: '0.3s' }),
    transform: value && value !== '' ? 'rotate(-45deg)' : 'rotate(0deg)',
  }),
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
  const classes = useStyles({ value, source: props.source });

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

    props.onChange(props.id, {
      label: props.label,
      img: props.img || props.options.filter(({ id }) => e.target.value === id)[0].sourceImg,
      value: props.options.filter(({ id }) => e.target.value === id)[0].value,
    });
  };

  const clearValue = (e) => {
    e.stopPropagation();
    setValue('');

    props.onChange(props.id, undefined);
  };

  const endIcon = <img className={classes.clearIcon} id="end_icon" alt="Icon" src={addSvg} />;

  let { source } = props;

  if (!source && value && value !== '') {
    source = props.options.filter(({ id }) => value === id)[0].source;
  }

  const endButton = (
    <>
      {value && value !== '' && (
        <>
          <IconButton
            className={classes.iconButton}
            onClick={handleOpen}
            style={{ marginLeft: source && source !== '' ? '' : 'auto' }}
          >
            <img className={classes.downIcon} alt="Icon" src={downSvg} />
          </IconButton>
          <div className={classes.endIconSeparator} />
        </>
      )}
      <IconButton
        className={classes.iconButton}
        onClick={value && value !== '' ? clearValue : handleOpen}
        style={{
          marginLeft: ((source && source) || (value && value !== '')) !== '' ? '' : 'auto',
        }}
      >
        {endIcon}
      </IconButton>
    </>
  );

  const label = (
    <InputLabel className={classes.label} id="label">
      {value && value !== '' ? `${props.label} :` : props.label}&nbsp;
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
        border: value && value !== '' ? `solid 1px ${colors.darkishBlue}` : '',
        boxShadow: value && value !== '' ? '' : '0 2px 15px 0 rgba(0, 0, 0, 0.25)',
      }}
    >
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
  img: PropTypes.string,
  label: PropTypes.string,
  source: PropTypes.string,
  id: PropTypes.string,
  defaultValue: PropTypes.objectOf(PropTypes.string),
  onChange: PropTypes.func,
  options: PropTypes.arrayOf(PropTypes.objectOf(PropTypes.string)),
};

JustificatifsPersonnalisesAdditionalDataSelect.defaultProps = {
  img: undefined,
  label: undefined,
  source: undefined,
  id: undefined,
  defaultValue: undefined,
  onChange: () => {},
  options: [],
};

export default enhancer(JustificatifsPersonnalisesAdditionalDataSelect);
