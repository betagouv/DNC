import { Document, Page } from 'react-pdf/dist/entry.webpack';
import { compose, pure } from 'recompose';

import { CircularProgress } from '@material-ui/core';
import PropTypes from 'prop-types';
import React from 'react';
import { debounce } from 'lodash';
import { makeStyles } from '@material-ui/core/styles';
import Dialog from './Dialog';

const enhancer = compose(pure);

const useStyles = makeStyles(() => ({
  dialogPaper: ({ pageWidth }) => ({
    maxWidth: pageWidth,
  }),
  page: {
    '& .react-pdf__Page__svg': {
      borderRadius: '20px',
    },
  },
  progress: {
    top: 'calc(50% - 20px)',
    left: 'calc(50% - 20px)',
    position: 'absolute',
  },
  separator: {
    width: '100%',
    height: '1px',
    backgroundColor: 'black',
    margin: '5px 0 5px 0',
  },
}));

const PDFViewerDialog = (props) => {
  const [pageWidth, setPageWidth] = React.useState(Math.min(600, window.innerWidth * 0.8));

  const classes = useStyles({ pageWidth });
  const [loading, setLoading] = React.useState(true);
  const [pageNb, setPageNb] = React.useState(0);

  React.useEffect(() => {
    window.onresize = debounce(() => {
      setPageWidth(Math.min(600, window.innerWidth * 0.8));
    }, 300);
  }, []);

  const onDocumentLoadSuccess = (document) => {
    setPageNb(document.numPages);
  };

  const onPageLoadSuccess = () => {
    setLoading(false);
  };

  const pages = [];

  for (let i = 1; i <= pageNb; i++) {
    pages.push(
      <Page
        renderTextLayer={false}
        onLoadSuccess={onPageLoadSuccess}
        renderMode="svg"
        pageNumber={i}
        className={classes.page}
        width={pageWidth}
        loading={<CircularProgress className={classes.progress} />}
      />,
      i !== pageNb && <div className={classes.separator} />,
    );
  }

  return (
    <Dialog
      onClose={props.handleClose}
      open={props.open}
      loading={loading}
      classes={{
        dialogPaper: classes.dialogPaper,
      }}
      maxWidth={false}
    >
      {props.pdfdata === ''
        ? (
          <Document
            loading={<CircularProgress className={classes.progress} />}
            renderMode="svg"
            file={props.pdf} // {props.pdf} //
            onLoadSuccess={onDocumentLoadSuccess}
          >
            {pages}
          </Document>
        )
        : (
          <Document
            loading={<CircularProgress className={classes.progress} />}
            renderMode="svg"
            file={{ data: props.pdfdata }} // {props.pdf} //
            onLoadSuccess={onDocumentLoadSuccess}
          >
            {pages}
          </Document>
        )}
    </Dialog>
  );
};

PDFViewerDialog.propTypes = {
  handleClose: PropTypes.func,
  open: PropTypes.bool,
  pdf: PropTypes.string,
  pdfdata: PropTypes.string,
};

PDFViewerDialog.defaultProps = {
  handleClose: () => {},
  open: false,
  pdf: '',
  pdfdata: '',
};

export default enhancer(PDFViewerDialog);
