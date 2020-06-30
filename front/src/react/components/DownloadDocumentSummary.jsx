import { compose, pure } from 'recompose';
import { makeStyles, useTheme } from '@material-ui/core/styles';

import DownloadButton from 'react/components/buttons/DownloadButton';
import PDFViewerDialog from 'react/components/dialogs/PDFViewerDialog';
import PreviewButton from 'react/components/buttons/PreviewButton';
import PropTypes from 'prop-types';
import React from 'react';
import ShareButton from 'react/components/buttons/ShareButton';
import ShareDocumentDialog from 'react/components/dialogs/ShareDocumentDialog';
import documentSvg from 'assets/images/ic-document.svg';
import { saveAs } from 'file-saver';
import { useMediaQuery } from '@material-ui/core';
import { withRouter } from 'react-router-dom';

const enhancer = compose(pure);

const useStyles = makeStyles(theme => ({
  pdfCard: {
    display: 'flex',
    alignItems: 'center',
    width: '80%',
    maxWidth: '800px',
    height: '50px',
    borderRadius: '20px',
    boxShadow: '0 4px 20px 0 rgba(0, 0, 0, 0.15)',
    backgroundColor: '#ffffff',
    paddingRight: '4vw',
    alignSelf: 'center',

    '& #document_icon': {
      width: '1.1875rem',
      marginLeft: '1.875rem',
    },

    '& p': {
      marginLeft: '2vw',
      fontSize: '0.9375rem',
      color: '#4f4f4f',
    },

    '& #share_button': {
      marginLeft: 'auto',
    },

    [theme.breakpoints.down('xs')]: {
      width: 'initial',
      margin: '0 1.5rem',
    },
  },
}));

const DownloadDocumentSummary = (props) => {
  const classes = useStyles();
  const theme = useTheme();
  const xsDown = useMediaQuery(theme.breakpoints.down('xs'));
  // const file = new Blob({ data: props.pdf }, { type: 'application/pdf' });
  const [pdfDialogOpen, setPdfDialogOpen] = React.useState(false);
  const [shareDialogOpen, setShareDialogOpen] = React.useState(false);

  const pdfName = `Justificatif-${new Date().toLocaleDateString().replace(/\//g, '_')}.pdf`;

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

  return (

    <>
      <div className={classes.pdfCard}>
        <img id="document_icon" alt="Document" src={documentSvg} />
        <p style={{ textOverflow: 'ellipsis', overflow: 'hidden', whiteSpace: 'nowrap' }}>
          {pdfName}
        </p>

        <ShareButton id="share_button" onClick={handleOpenShareDialog} />

        {!xsDown && (
          <PreviewButton
            style={{ marginLeft: '10px' }}
            id="preview_button"
            onClick={handleOpenPdfDialog}
          />
        )}
        {props.lienPdf === ''
          ? (
            <DownloadButton
              style={{ marginLeft: '10px' }}
              onClick={() => {
                const linkSource = `data:application/pdf;base64,${props.pdfBase64}`;
                const downloadLink = document.createElement('a');
                const fileName = pdfName;

                downloadLink.href = linkSource;
                downloadLink.download = fileName;
                downloadLink.click();
              }}
            />
          )
          : (
            <DownloadButton
              style={{ marginLeft: '10px' }}
              onClick={() => {
                saveAs(props.lienPdf, pdfName);
              }}
            />
          )}
      </div>
      {props.lienPdf === ''
        ? (
          <PDFViewerDialog
            handleClose={handleClosePdfDialog}
            open={pdfDialogOpen}
            pdfdata={props.pdf}
          />
        )
        : (
          <PDFViewerDialog
            handleClose={handleClosePdfDialog}
            open={pdfDialogOpen}
            pdf={props.lienPdf}
          />
        )}

      <ShareDocumentDialog onClose={handleCloseShareDialog} open={shareDialogOpen} />
    </>
  );
};

DownloadDocumentSummary.propTypes = {
  pdf: PropTypes.string,
  pdfBase64: PropTypes.string,
  lienPdf: PropTypes.string,
};

DownloadDocumentSummary.defaultProps = {
  pdf: '',
  pdfBase64: '',
  lienPdf: '',
};

export default withRouter(enhancer(DownloadDocumentSummary));
