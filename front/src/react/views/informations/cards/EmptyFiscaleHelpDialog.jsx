import React from 'react';
import { compose, pure } from 'recompose';
import PropTypes from 'prop-types';
import Dialog from 'react/components/dialogs/Dialog';
import { makeStyles } from '@material-ui/core/styles';
import aideImg1 from 'assets/images/reference_avis_fiscal_1.jpg';
import aideImg2 from 'assets/images/reference_avis_fiscal_2.png';

const enhancer = compose(pure);

const useStyles = makeStyles({
  dialog: {
    '& .MuiPaper-root': {
      padding: '20px',
    },
  },
  content: {
    display: 'flex',
    flexDirection: 'column',
  },
  images: {
    display: 'flex',
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    marginBottom: '20px',
  },
});

const HelpDialog = (props) => {
  const classes = useStyles();

  return (
    <>
      <Dialog onClose={props.onClose} open={props.open} className={classes.dialog}>
        <div className={classes.content}>
          <div className={classes.images}>
            <img alt="Aide 1" src={aideImg1} style={{ width: '45%' }} />
            <img alt="Aide 2" src={aideImg2} style={{ width: '55%' }} />
          </div>
          <p>
            La référence de l’avis est un identifiant fiscal qui permet de retrouver l’avis d’impôt
            concerné par le document présenté par l’usager. Il est composé de 13 caractères
            alphanumériques. Il est situé en haut à gauche de l’avis dans le cadre&nbsp;
            <b>Vos références</b>.
          </p>
        </div>
      </Dialog>
    </>
  );
};

HelpDialog.propTypes = {
  onClose: PropTypes.func,
  open: PropTypes.bool,
};

HelpDialog.defaultProps = {
  onClose: () => {},
  open: false,
};

export default enhancer(HelpDialog);
