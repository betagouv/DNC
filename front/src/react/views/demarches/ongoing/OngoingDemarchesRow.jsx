import {
  Button,
  TableCell,
  TableRow,
  useMediaQuery,
} from '@material-ui/core';
import { compose, pure } from 'recompose';
import { makeStyles, useTheme } from '@material-ui/core/styles';

import PropTypes from 'prop-types';
import React from 'react';
import alertSvg from 'assets/images/ic-alert.svg';
import arrowSvg from 'assets/images/ic-arrow.svg';
import colors from 'style/config.variables.scss';
import OngoingDemarchesDocuments from './OngoingDemarchesDocuments';

const enhancer = compose(pure);

const useStyles = makeStyles(() => ({
  dataCell: {
    padding: '1rem 0.5rem 1rem 0.5rem',
    color: '#4f4f4f',
    // height: '80px',
    fontSize: '1rem',
  },
  dataRow: {
    '&:last-child th, &:last-child td': {
      borderBottom: 0,
    },
  },
  documentsContainer: {
    display: 'flex',
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    maxWidth: '200px',
  },
  stateContainer: {
    display: 'flex',
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    maxWidth: '130px',
  },
  stateAlert: {
    width: '1.875rem',
    marginLeft: '0.625rem',
  },
  dossierButton: {
    marginLeft: '0.625rem',
    padding: '5px',
    textTransform: 'initial',
    color: colors.darkishBlue,
  },
  arrow: { width: '1.25rem', marginLeft: '0.625rem' },
  headerCell: {
    fontSize: '0.75rem',
    fontWeight: 'bold',
    fontStretch: 'normal',
    fontStyle: 'normal',
    lineHeight: 'normal',
    letterSpacing: 'normal',
    color: colors.lightNavyBlue,
    // height: '80px',
  },
}));

const OngoingDemarchesRow = (props) => {
  const classes = useStyles();
  const theme = useTheme();
  const xsDown = useMediaQuery(theme.breakpoints.down('xs'));

  const cellStyle = {
    fontWeight: props.stateAlert || props.documentsAlert ? 'bold' : 'normal',
  };

  const cells = [
    <TableCell className={classes.dataCell} style={cellStyle}>
      {props.id}
    </TableCell>,
    <TableCell className={classes.dataCell} style={cellStyle}>
      {props.demarche}
    </TableCell>,
    <TableCell className={classes.dataCell} style={cellStyle}>
      {props.date}
    </TableCell>,
    <TableCell className={classes.dataCell} style={cellStyle}>
      {props.sources}
    </TableCell>,
    <TableCell className={classes.dataCell} style={cellStyle}>
      <div className={classes.stateContainer}>
        {props.state}
        <img src={props.stateAlert || props.documentsAlert ? alertSvg : ''} className={classes.stateAlert} alt="" />
      </div>
    </TableCell>,
    <TableCell className={classes.dataCell} style={cellStyle}>
      <div className={classes.documentsContainer}>
        <OngoingDemarchesDocuments documents={props.documents} />
      </div>
    </TableCell>,
    <TableCell className={classes.dataCell} style={{ ...cellStyle, padding: 0 }}>
      <Button
        className={classes.dossierButton}
        onClick={() => {
          window.open(props.link, '_blank');
        }}
      >
        <u>Accéder à mon dossier</u>
        <img src={arrowSvg} className={classes.arrow} alt="" />
      </Button>
    </TableCell>,
  ];

  let content = <TableRow className={classes.dataRow}> {cells} </TableRow>;

  if (xsDown) {
    content = cells.map((cell, index) => (
      <TableRow className={classes.dataRow}>
        <TableCell
          variant="head"
          className={classes.headerCell}
          style={{ textTransform: 'uppercase' }}
        >
          {props.headers[index]}
        </TableCell>
        {cell}
      </TableRow>
    ));
  }

  return content;
};

OngoingDemarchesRow.propTypes = {
  id: PropTypes.number,
  date: PropTypes.string,
  state: PropTypes.string,
  demarche: PropTypes.string,
  sources: PropTypes.string,
  documents: PropTypes.arrayOf(PropTypes.string),
  stateAlert: PropTypes.bool,
  documentsAlert: PropTypes.bool,
  link: PropTypes.string,
};

OngoingDemarchesRow.defaultProps = {
  id: 0,
  date: '',
  state: '',
  demarche: '',
  sources: '',
  documents: [],
  stateAlert: false,
  documentsAlert: false,
  link: '',
  headers: [],
};

export default enhancer(OngoingDemarchesRow);
