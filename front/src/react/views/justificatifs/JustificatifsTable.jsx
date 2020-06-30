import {
  Checkbox,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  useMediaQuery,
} from '@material-ui/core';
import { compose, pure } from 'recompose';
import { makeStyles, useTheme } from '@material-ui/core/styles';

import DownloadButton from 'react/components/buttons/DownloadButton';
import PropTypes from 'prop-types';
import React from 'react';
import colors from 'style/config.variables.scss';
import { connect } from 'react-redux';
import JustificatifRow from './JustificatifRow';
import EmptyFamilleJustificatifRow from './EmptyFamilleJustificatifRow';

const enhancer = compose(pure);

const useStyles = makeStyles({
  root: {
    maxWidth: '1000px',
    overflow: 'auto',
    '&::-webkit-scrollbar': {
      display: 'none',
    },
    '-ms-overflow-style': 'none',
  },
  button: {
    padding: '10px',
    alignSelf: 'center',
    fontSize: '1rem',
  },
  headerCell: {
    fontSize: '0.75rem',
    fontWeight: 'bold',
    color: colors.darkishBlue,
    height: '80px',
    marginTop: '16px',
    backgroundColor: 'white',
  },
  checkBoxRoot: {
    color: colors.darkishBlue,
    '&:hover': {
      backgroundColor: colors.darkishBlueAlpha,
    },
    alignSelf: 'flex-end',
  },
  checkBoxChecked: {
    color: `${colors.darkishBlue} !important`,
    '&:hover': {
      backgroundColor: `${colors.darkishBlueAlpha} !important`,
    },
    alignSelf: 'flex-end',
  },
});

const JustificatifsTable = (props) => {
  const classes = useStyles();
  const theme = useTheme();
  const xsDown = useMediaQuery(theme.breakpoints.down('xs'));
  // const smDown = useMediaQuery(theme.breakpoints.down('sm'));

  const [selectAllChecked, setSelectAllChecked] = React.useState(false);
  const [checkedRows, setCheckedRows] = React.useState([]);
  /* const [showBottomShadow, setShowBottomShadow] = React.useState(true);
  const [showTopShadow, setShowTopShadow] = React.useState(false); */

  const container = React.createRef();

  /* React.useEffect(() => {
    container.current.addEventListener('scroll', event => {
      if (
        showBottomShadow &&
        event.target.offsetHeight + event.target.scrollTop >= event.target.scrollHeight
      ) {
        setShowBottomShadow(false);
      } else {
        setShowBottomShadow(true);
      }

      if (event.target.scrollTop <= 0) {
        setShowTopShadow(false);
      } else {
        setShowTopShadow(true);
      }
    });
  }, []); */

  const { justificatifs: justificatifsList } = props;

  const onCheckChange = (checked, id) => {
    const index = checkedRows.indexOf(id);

    if (checked && index === -1) {
      setCheckedRows([...checkedRows, id]);
    } else if (!checked && index !== -1) {
      setCheckedRows(checkedRows.filter(rowId => rowId !== id));
    }
  };

  const handleSelectAllChange = (event) => {
    const { checked } = event.target;
    setSelectAllChecked(checked);

    if (checked) {
      setCheckedRows(justificatifsList.filter(row => !row.disabled).map(row => row.id));
      return;
    }

    setCheckedRows([]);
  };

  /**
   * Télécharge tous les PDFs sélectionnés et les fusionne en 1 seul fichier.
   */
  const groupDownload = async () => {
    const justificatifsToDownload = justificatifsList.filter(
      ({ id }) => checkedRows.indexOf(id) !== -1,
    );

    props.multipleFilesDownload(justificatifsToDownload);
  };

  return (
    <>
      <TableContainer
        className={classes.root}
        ref={container}
        style={{
          boxShadow: /* !xsDown && showBottomShadow ? 'inset 0 -6px 6px -7px black' : */ '',
        }}
      >
        {/* {!xsDown && showTopShadow && (
          <div
            style={{
              height: '0',
              position: 'sticky',
              top: '80px',
              width: '100%',
              boxShadow: 'black 0 -5px 9px 2px',
              background: 'red',
            }}
          />
        )} */}
        <Table stickyHeader={!xsDown}>
          <TableHead>
            <TableRow>
              <TableCell className={classes.headerCell} />

              <TableCell className={classes.headerCell}>
                <div style={{ display: 'flex', flexDirection: 'row', alignItems: 'center' }}>
                  <Checkbox
                    checked={selectAllChecked}
                    onChange={handleSelectAllChange}
                    classes={{
                      root: classes.checkBoxRoot,
                      checked: classes.checkBoxChecked,
                    }}
                  />
                  <p style={{ flexGrow: 1 }}>{'Tout sélectionner'.toUpperCase()}</p>

                  {xsDown && (
                    <DownloadButton onClick={groupDownload} disabled={checkedRows.length < 1}>
                      Télécharger la sélection
                    </DownloadButton>
                  )}
                </div>

                {/* {smDown && (
                  <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                    <DownloadButton onClick={groupDownload} disabled={checkedRows.length < 1}>
                      Télécharger la sélection
                    </DownloadButton>
                  </div>
                )} */}
              </TableCell>

              {!xsDown && (
                <TableCell className={classes.headerCell}>
                  <div
                    style={{ display: 'flex', alignItems: 'center', justifyContent: 'flex-end' }}
                  >
                    <DownloadButton onClick={groupDownload} disabled={checkedRows.length < 1}>
                      Télécharger la sélection
                    </DownloadButton>
                  </div>
                </TableCell>
              )}
            </TableRow>
          </TableHead>
          <TableBody>
            {justificatifsList.map((justificatif) => {
              let row = (
                <JustificatifRow
                  {...justificatif}
                  onCheckChange={onCheckChange}
                  checked={checkedRows.indexOf(justificatif.id) !== -1}
                />
              );

              if (
                justificatif.label === 'Attestation de quotient familial'
                && !props.numeroAllocataire
              ) {
                row = (
                  <EmptyFamilleJustificatifRow
                    {...justificatif}
                    onCheckChange={onCheckChange}
                    checked={checkedRows.indexOf(justificatif.id) !== -1}
                  />
                );
              }
              return row;
            })}
          </TableBody>
        </Table>
      </TableContainer>
    </>
  );
};

JustificatifsTable.propTypes = {
  justificatifs: PropTypes.arrayOf(PropTypes.any),
  numeroAllocataire: PropTypes.string.isRequired,
  multipleFilesDownload: PropTypes.func,
};

JustificatifsTable.defaultProps = {
  justificatifs: [],
  multipleFilesDownload: () => {},
};

const mapStateToProps = state => ({
  numeroAllocataire: state.identity.numeroAllocataire,
});

export default connect(mapStateToProps)(enhancer(JustificatifsTable));
