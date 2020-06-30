import { GridList, GridListTile } from '@material-ui/core';
import { compose, pure } from 'recompose';

import PropTypes from 'prop-types';
import React from 'react';
import { makeStyles } from '@material-ui/core/styles';
import styles from './InformationCard.module.scss';

const enhancer = compose(pure);

const useStyles = makeStyles({
  gridList: {
    margin: '0 !important',
    marginTop: '1rem !important',

    '& .MuiGridListTile-tile': {
      display: 'flex',
      flexDirection: 'column',
    },
  },
});

const FamilleInformationCardData = (props) => {
  const classes = useStyles();

  return (
    <>
      <GridList cols={5} cellHeight="auto" spacing={12} className={classes.gridList}>
        {props.data.map(({ label, value }) => {
          if (!value || value === '') {
            return null;
          }

          // Affichage spécial pour la liste des enfants

          if (label === 'enfants') {
            return [
              <GridListTile cols={2}>
                <p className={styles.informationLabel}>{label}</p>
              </GridListTile>,
              <GridListTile cols={3}>
                {value.map(enfant => (
                  <p className={styles.informationValue} style={{ fontSize: '1rem' }}>
                    {enfant}
                  </p>
                ))}
              </GridListTile>,
            ];
          }

          return [
            <GridListTile cols={2}>
              <p className={styles.informationLabel}>{label}</p>
            </GridListTile>,
            <GridListTile cols={3}>
              <p className={styles.informationValue}>{value}</p>
            </GridListTile>,
          ];
        })}
      </GridList>
    </>
  );
};

FamilleInformationCardData.propTypes = {
  data: PropTypes.arrayOf(
    PropTypes.shape({
      label: PropTypes.string,
      value: PropTypes.string,
    }),
  ),
};

FamilleInformationCardData.defaultProps = {
  data: [],
};

export default enhancer(FamilleInformationCardData);
