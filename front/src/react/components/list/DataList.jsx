import React from 'react';
import { compose, pure } from 'recompose';
import PropTypes from 'prop-types';
import { GridList } from '@material-ui/core';
import { makeStyles } from '@material-ui/core/styles';
import DataListRow from './DataListRow';

const enhancer = compose(pure);

const useStyles = makeStyles({
  gridList: {
    margin: '0 !important',
    marginTop: '1rem !important',
  },
});

const DataList = (props) => {
  const classes = useStyles();

  return (
    <>
      <GridList
        cols={5}
        cellHeight="auto"
        spacing={12}
        className={`${classes.gridList} ${props.className}`}
      >
        {props.data.map(({ label, value }) => {
          if ((!value && value !== 0) || value === '') {
            return null;
          }

          return (
            <DataListRow
              label={label}
              value={value}
              valueWidth={props.valueWidth}
              labelWidth={props.labelWidth}
              labelTileClass={props.labelTileClass}
              valueTileClass={props.valueTileClass}
            />
          );
        })}
      </GridList>
    </>
  );
};

DataList.propTypes = {
  data: PropTypes.arrayOf(
    PropTypes.shape({
      label: PropTypes.string,
      value: PropTypes.string,
    }),
  ),
  className: PropTypes.string,
  labelTileClass: PropTypes.string,
  valueTileClass: PropTypes.string,
  valueWidth: PropTypes.string,
  labelWidth: PropTypes.string,
};

DataList.defaultProps = {
  data: [],
  className: '',
  labelTileClass: '',
  valueTileClass: '',
  valueWidth: '50%',
  labelWidth: '50%',
};

export default enhancer(DataList);
