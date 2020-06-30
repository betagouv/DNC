import { compose, pure } from 'recompose';
import { makeStyles, useTheme } from '@material-ui/core/styles';

import Button from 'react/components/buttons/button/Button';
import DownloadDocumentSummary from 'react/components/DownloadDocumentSummary';
import FixedFooter from 'react/components/FixedFooter';
import PDFViewerDialog from 'react/components/dialogs/PDFViewerDialog';
import PropTypes from 'prop-types';
import React from 'react';
import ShareDocumentDialog from 'react/components/dialogs/ShareDocumentDialog';
import colors from 'style/config.variables.scss';
import { connect } from 'react-redux';
import { useMediaQuery } from '@material-ui/core';
import { withRouter } from 'react-router-dom';

const enhancer = compose(pure);

const useStyles = makeStyles(theme => ({
  pdfCard: {
    display: 'flex',
    alignItems: 'center',
    width: '100%',
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

    '& .MuiButton-root': {
      fontSize: '0.875rem',
      fontWeight: 'bold',
      color: colors.darkSlateBlue,
      textTransform: 'initial',
    },

    '& #share_button': {
      marginLeft: 'auto',
    },
  },
  button: {
    marginTop: 'auto',
    alignSelf: 'center',
    minWidth: '15.625rem',
    marginBottom: '2rem',

    [theme.breakpoints.down('xs')]: {
      marginTop: '0.5rem',
      marginBottom: '0.5rem',
    },
  },
  title: {
    alignSelf: 'center',
    fontSize: '1.125rem',
    textAlign: 'center',
    fontWeight: 'bold',
    color: '#4f4f4f',
    maxWidth: '39.375rem',
    marginTop: '2rem',
    marginBottom: '1.5rem',
  },
}));

const JustificatifsPersonnalisesFinalisation = (props) => {
  const classes = useStyles();
  const theme = useTheme();
  const xsDown = useMediaQuery(theme.breakpoints.down('xs'));
  const [pdfDialogOpen, setPdfDialogOpen] = React.useState(false);
  const [shareDialogOpen, setShareDialogOpen] = React.useState(false);

  const handleClosePdfDialog = () => {
    setPdfDialogOpen(false);
  };

  const handleCloseShareDialog = () => {
    setShareDialogOpen(false);
  };

  let validationButton = (
    <Button
      size="small"
      color={colors.darkishBlue}
      className={classes.button}
      onClick={() => {
        props.history.push('/justificatifs');
      }}
    >
      Retourner à mes justificatifs
    </Button>
  );

  if (xsDown) {
    validationButton = <FixedFooter>{validationButton}</FixedFooter>;
  }

  return (
    <>
      <div
        style={{
          flexGrow: 1,
          paddingBottom: '2rem',
          display: 'flex',
          flexDirection: 'column',
        }}
      >
        <p className={classes.title}>Télécharger votre justificatif personnalisé</p>

        <DownloadDocumentSummary lienPdf={props.pdf} />
      </div>
      {validationButton}

      <PDFViewerDialog handleClose={handleClosePdfDialog} open={pdfDialogOpen} pdf={props.pdf} />

      <ShareDocumentDialog onClose={handleCloseShareDialog} open={shareDialogOpen} />
    </>
  );
};

JustificatifsPersonnalisesFinalisation.propTypes = {
  pdf: PropTypes.string,
  history: PropTypes.shape({ push: PropTypes.func.isRequired }).isRequired,
};

JustificatifsPersonnalisesFinalisation.defaultProps = {
  pdf: '',
};

const mapStateToProps = state => ({
  fsRedirectUri: state.demarches.fsRedirectUri,
});

const mapDispatchToProps = dispatch => ({
  initialState: () => dispatch({
    type: 'DEMARCHES_INITIAL_STATE',
  }),
});

export default withRouter(
  connect(mapStateToProps, mapDispatchToProps)(enhancer(JustificatifsPersonnalisesFinalisation)),
);
