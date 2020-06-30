import React from 'react';
import { compose, pure } from 'recompose';
import { connect } from 'react-redux';
import { makeStyles } from '@material-ui/core/styles';
import Card from 'react/components/card/Card';
import Button from 'react/components/buttons/button/Button';
import { Grid, CardContent } from '@material-ui/core';
import colors from 'style/config.variables.scss';
import PropTypes from 'prop-types';
import justifA from 'assets/pdf/justif_A.pdf';
import justifB from 'assets/pdf/justif_B.pdf';
import justifC from 'assets/pdf/justif_C.pdf';
import justifD from 'assets/pdf/justif_D.pdf';
import justifE from 'assets/pdf/justif_E.pdf';
import PDFViewerDialog from 'react/components/dialogs/PDFViewerDialog';

const enhancer = compose(pure);

const useStyles = makeStyles(theme => ({
  cardContent: {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    height: '100%',
    padding: '24px',
  },
  grid: {
    padding: '1rem',
    margin: 0,
    height: '100%',
  },
  gridItem: { maxWidth: '40.625rem' },
  button: {
    width: '80%',
  },
  card: {
    [theme.breakpoints.down('xs')]: {
      boxShadow: 'none',
    },
  },
}));

const TestsJustificatifs = (props) => {
  const classes = useStyles();

  const [pdfDialogOpen, setPdfDialogOpen] = React.useState(false);
  const [currentPdf, setCurrentPdf] = React.useState(null);

  const handleOpenPdfDialog = () => {
    setPdfDialogOpen(true);
  };

  const handleClosePdfDialog = () => {
    setPdfDialogOpen(false);
  };

  React.useEffect(() => {
    props.hideHeader();
  }, []);

  const justificatifs = [
    {
      id: 'a',
      pdf: justifA,
    },
    {
      id: 'b',
      pdf: justifB,
    },
    {
      id: 'c',
      pdf: justifC,
    },
    {
      id: 'd',
      pdf: justifD,
    },
    {
      id: 'E',
      pdf: justifE,
    },
  ];

  return (
    <>
      <Grid container justify="center" alignItems="center" className={classes.grid}>
        <Grid item xs className={classes.gridItem}>
          <Card className={classes.card}>
            <CardContent className={classes.cardContent}>
              {justificatifs.map(justif => (
                <Button
                  color={colors.darkishBlue}
                  className={classes.button}
                  style={{ marginBottom: '30px' }}
                  onClick={() => {
                    setCurrentPdf(justif.pdf);
                    handleOpenPdfDialog();
                  }}
                >
                  Justificatif {justif.id.toUpperCase()}
                </Button>
              ))}
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      <PDFViewerDialog handleClose={handleClosePdfDialog} open={pdfDialogOpen} pdf={currentPdf} />
    </>
  );
};

TestsJustificatifs.propTypes = {
  hideHeader: PropTypes.func.isRequired,
  history: PropTypes.shape({ push: PropTypes.func.isRequired }).isRequired,
};

TestsJustificatifs.defaultProps = {};

const mapStateToProps = () => ({});

const mapDispatchToProps = dispatch => ({
  hideHeader: () => dispatch({
    type: 'SHOW_HEADER',
    showHeader: false,
  }),
});

export default connect(mapStateToProps, mapDispatchToProps)(enhancer(TestsJustificatifs));
