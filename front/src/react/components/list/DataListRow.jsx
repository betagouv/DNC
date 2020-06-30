import React from 'react';
import { compose, pure } from 'recompose';
import PropTypes from 'prop-types';
import { GridListTile } from '@material-ui/core';
import { makeStyles } from '@material-ui/core/styles';
import styles from './DataList.module.scss';

const enhancer = compose(pure);

const useStyles = makeStyles({
  gridListeTileTile: {
    display: 'flex',
    flexDirection: 'column',
  },
});

const DataListRow = (props) => {
  const classes = useStyles();

  return (
    <>
      <GridListTile
        cols={2}
        style={{ ...props.style, width: props.labelWidth ? props.labelWidth : '50%' }}
        classes={{
          tile: props.labelTileClass ? props.labelTileClass : classes.gridListeTileTile,
        }}
      >
        <p className={styles.informationLabel}>{props.label}</p>
      </GridListTile>
      <GridListTile
        cols={3}
        style={{ ...props.style, width: props.valueWidth ? props.valueWidth : '50%' }}
        classes={{
          tile: props.valueTileClass ? props.valueTileClass : classes.gridListeTileTile,
        }}
      >
        <p className={styles.informationValue}>{props.value}</p>
      </GridListTile>
    </>
  );
};

DataListRow.propTypes = {
  style: PropTypes.objectOf(PropTypes.oneOfType([PropTypes.string, PropTypes.number])),
  label: PropTypes.string,
  value: PropTypes.string,
  labelTileClass: PropTypes.string,
  valueTileClass: PropTypes.string,
  labelWidth: PropTypes.string,
  valueWidth: PropTypes.string,
};

DataListRow.defaultProps = {
  style: {},
  label: '',
  value: '',
  labelTileClass: undefined,
  valueTileClass: undefined,
  labelWidth: undefined,
  valueWidth: undefined,
};

export default enhancer(DataListRow);
