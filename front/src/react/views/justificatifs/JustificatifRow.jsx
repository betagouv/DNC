import {
  Checkbox,
  Grid,
  TableCell,
  TableRow,
  useMediaQuery,
} from '@material-ui/core';
import { compose, pure } from 'recompose';
import { makeStyles, useTheme } from '@material-ui/core/styles';

import Button from 'react/components/buttons/button/Button';
import DownloadButton from 'react/components/buttons/DownloadButton';
import PDFViewerDialog from 'react/components/dialogs/PDFViewerDialog';
import Popper from '@material-ui/core/Popper';
import PreviewButton from 'react/components/buttons/PreviewButton';
import PropTypes from 'prop-types';
import React from 'react';
import ShareButton from 'react/components/buttons/ShareButton';
import ShareDocumentDialog from 'react/components/dialogs/ShareDocumentDialog';
import colors from 'style/config.variables.scss';
import { connect } from 'react-redux';
import { saveAs } from 'file-saver';

const enhancer = compose(pure);

const useStyles = makeStyles(theme => ({
  checkBoxRoot: {
    color: colors.darkishBlue,
    '&:hover': {
      backgroundColor: colors.darkishBlueAlpha,
    },
  },
  checkBoxChecked: {
    color: `${colors.darkishBlue} !important`,
    '&:hover': {
      backgroundColor: `${colors.darkishBlueAlpha} !important`,
    },
  },
  globalPopover: {
    border: 'solid',
    borderRadius: '23px',
    backgroundColor: 'white',
  },
  dataCell2: {
    fontSize: '1rem',
    color: '#4f4f4f',
    borderBottom: '0px !important',
  },
  dataCell: {
    fontSize: '1rem',
    color: '#4f4f4f',
  },

  popover: {
    padding: '10px',
  },
  input: {
    color: colors.darkSlateBlue,
  },
  label: {
    color: colors.darkSlateBlue,
  },
  button: {
    flex: '1 0 33%',
    maxWidth: '20rem',
    alignSelf: 'flex-end',

    [theme.breakpoints.down('xs')]: {
      marginTop: '1.5rem',
      marginBottom: '1.5rem',
      alignSelf: 'center',
      flex: '',
      maxWidth: '',
    },
  },
}));

const JustificatifRow = (props) => {
  const classes = useStyles();
  const theme = useTheme();
  const xsDown = useMediaQuery(theme.breakpoints.down('xs'));

  const [pdfDialogOpen, setPdfDialogOpen] = React.useState(false);
  const [shareDialogOpen, setShareDialogOpen] = React.useState(false);

  const handleChange = (event) => {
    props.onCheckChange(event.target.checked, props.id, props.downloadUri);
  };

  const handleOpenPdfDialog = () => {
    setPdfDialogOpen(true);
  };

  const handleClosePdfDialog = () => {
    setPdfDialogOpen(false);
  };

  const handleOpenShareDialog = () => {
    setShareDialogOpen(true);
  };

  const handleCloseShareDialog = () => {
    setShareDialogOpen(false);
  };

  const downloadPdf = async () => {
    if (!props.downloadUri || props.downloadUri === '') {
      return;
    }

    saveAs(props.downloadUri, `${props.id}.pdf`);
  };

  const viewPdf = () => {
    handleOpenPdfDialog();
  };

  const [anchorEl, setAnchorEl] = React.useState(null);

  const handleClick = (event) => {
    setAnchorEl(anchorEl ? null : event.currentTarget);
  };

  const open = Boolean(anchorEl);
  const id = open ? 'simple-popper' : undefined;

  const buttons = (
    <>
      <ShareButton
        style={{ marginRight: '10px' }}
        disabled={!props.downloadUri || props.disabled}
        onClick={handleOpenShareDialog}
      />

      {!xsDown && (
        <PreviewButton onClick={viewPdf} disabled={!props.downloadUri || props.disabled} />
      )}

      <DownloadButton onClick={downloadPdf} disabled={!props.downloadUri || props.disabled} />
    </>
  );

  return (
    <>
      <ShareDocumentDialog onClose={handleCloseShareDialog} open={shareDialogOpen} />

      <PDFViewerDialog
        handleClose={handleClosePdfDialog}
        open={pdfDialogOpen}
        pdf={props.downloadUri}
      />

      <TableRow className={classes.dataRow}>
        <TableCell
          className={classes.dataCell}
          style={{
            textAlign: 'center',
          }}
        >
          {props.icon}
        </TableCell>
        <TableCell className={classes.dataCell}>
          <div
            style={{
              display: 'flex',
              flexDirection: 'row',
              alignItems: 'center',
            }}
          >
            <Checkbox
              checked={props.checked && props.downloadUri}
              onChange={handleChange}
              classes={{ root: classes.checkBoxRoot, checked: classes.checkBoxChecked }}
              disabled={!props.downloadUri || props.disabled}
            />
            {props.label === "avis d'imposition dernière année connue"
              ? (
                <Grid container>
                  <Grid item>
                    <p style={{ flexGrow: 1, padding: '15px 20px 0 0' }}>{props.label}</p>
                  </Grid>
                  <Grid item>
                    <Button
                      size="small"
                      color={colors.darkishBlue}
                      className={classes.button}
                      aria-describedby={id}
                      onClick={handleClick}
                    >
                      Autres années
                    </Button>
                    <Popper
                      id={id}
                      open={open}
                      style={{ padding: '5px' }}
                      anchorEl={anchorEl}
                    >
                      <div className={classes.globalPopover}>
                        <TableRow style={{
                          textAlign: 'center',
                          borderBottom: '0px !important',
                        }}
                        >
                          <TableCell
                            className={classes.dataCell2}
                            style={{
                              textAlign: 'center',
                              borderBottom: '0px !important',
                            }}
                          >
                            {props.icon}
                          </TableCell>
                          <TableCell
                            className={classes.dataCell2}
                            style={{
                              textAlign: 'center',
                              borderBottom: '0px !important',
                            }}
                          >
                            <div
                              style={{
                                display: 'flex',
                                flexDirection: 'row',
                                alignItems: 'center',
                              }}
                            >
                              <div
                                className={classes.popover}
                              >
                                Me rendre sur le site de la DGFIP -&gt;
                              </div>
                            </div>
                          </TableCell>
                          <TableCell
                            className={classes.dataCell2}
                            style={{
                              textAlign: 'center',
                              borderBottom: '0px !important',
                            }}
                          >
                            <Button onClick={handleClick}>X</Button>
                          </TableCell>
                        </TableRow>
                      </div>
                    </Popper>
                  </Grid>
                </Grid>
              ) : <p style={{ flexGrow: 1 }}>{props.label}</p>}

            {xsDown && <>{buttons}</>}
          </div>

          {/*  {xsDown && (
            <div
              style={{
                display: 'flex',
                flexDirection: 'row',
                justifyContent: 'center',
                marginTop: '10px',
              }}
            >
              {buttons}
            </div>
          )} */}
        </TableCell>

        {!xsDown && (
          <TableCell className={classes.dataCell}>
            <div
              style={{
                display: 'flex',
                flexDirection: 'row',
                alignItems: 'center',
                justifyContent: 'flex-end',
              }}
            >
              {buttons}
            </div>
          </TableCell>
        )}
      </TableRow>
    </>
  );
};

JustificatifRow.displayName = 'JustificatifRow';

JustificatifRow.propTypes = {
  disabled: PropTypes.bool,
  downloadUri: PropTypes.string,
  icon: PropTypes.element,
  label: PropTypes.string,
  checked: PropTypes.bool,
  id: PropTypes.string,
  onCheckChange: PropTypes.func.isRequired,
};

JustificatifRow.defaultProps = {
  disabled: false,
  downloadUri: null,
  icon: null,
  label: '-',
  checked: false,
  id: '',
};

const mapStateToProps = () => ({});

export default connect(mapStateToProps)(enhancer(JustificatifRow));
