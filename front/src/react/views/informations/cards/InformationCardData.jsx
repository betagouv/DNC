import React from 'react';
import { compose, pure } from 'recompose';
import PropTypes from 'prop-types';
import { GridList } from '@material-ui/core';
import { makeStyles } from '@material-ui/core/styles';
import InformationCardDataRow from './InformationCardDataRow';

const enhancer = compose(pure);

const useStyles = makeStyles({
  gridList: {
    margin: '0 !important',
    marginTop: '1rem !important',
  },
});

const InformationCardData = (props) => {
  const classes = useStyles();

  return (
    <>
      <GridList cols={5} cellHeight="auto" spacing={12} className={classes.gridList}>
        {props.data.map(({ label, value }) => {
          if ((!value && value !== 0) || value === '') {
            return null;
          }

          return <InformationCardDataRow label={label} value={value} />;
        })}
      </GridList>
    </>
  );
};

InformationCardData.propTypes = {
  data: PropTypes.arrayOf(
    PropTypes.shape({
      label: PropTypes.string,
      value: PropTypes.string,
    }),
  ),
};

InformationCardData.defaultProps = {
  data: [],
};

export default enhancer(InformationCardData);
