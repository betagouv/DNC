import React from 'react';
import { compose, pure } from 'recompose';
import PropTypes from 'prop-types';
import { GridListTile } from '@material-ui/core';
import { makeStyles } from '@material-ui/core/styles';
import styles from './InformationCard.module.scss';

const enhancer = compose(pure);

const useStyles = makeStyles({
  gridListeTileTile: {
    display: 'flex',
    flexDirection: 'column',
  },
});

const InformationCardDataRow = (props) => {
  const classes = useStyles();

  return (
    <>
      <GridListTile
        cols={2}
        style={{ ...props.style, width: '40%' }}
        classes={{
          tile: classes.gridListeTileTile,
        }}
      >
        <p className={styles.informationLabel}>{props.label}</p>
      </GridListTile>
      <GridListTile
        cols={3}
        style={{ ...props.style, width: '60%' }}
        classes={{
          tile: classes.gridListeTileTile,
        }}
      >
        <p className={styles.informationValue}>{props.value}</p>
      </GridListTile>
    </>
  );
};

InformationCardDataRow.propTypes = {
  style: PropTypes.objectOf(PropTypes.oneOfType([PropTypes.string, PropTypes.number])),
  label: PropTypes.string,
  value: PropTypes.string,
};

InformationCardDataRow.defaultProps = {
  style: {},
  label: '',
  value: '',
};

export default enhancer(InformationCardDataRow);
