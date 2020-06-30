import { ButtonBase } from '@material-ui/core';
import { makeStyles } from '@material-ui/core/styles';
import cozycloudLogo from 'assets/images/cozycloud_logo.jpg';
import digiposteLogo from 'assets/images/digiposte_logo.png';
import mailSvg from 'assets/images/ic-mail.svg';
import PropTypes from 'prop-types';
import React from 'react';
import Dialog from 'react/components/dialogs/Dialog';
import { compose, pure } from 'recompose';
import colors from 'style/config.variables.scss';
import colorUtils from 'utils/ColorUtils';

const enhancer = compose(pure);

const useStyles = makeStyles(theme => ({
  paper: {
    width: '37.5rem',
    borderRadius: '20px',
    boxShadow: '0 4px 20px 0 rgba(0, 0, 0, 0.15)',
    backgroundColor: '#ffffff',
    padding: '1rem 1.875rem 1.875rem 1.875rem',
    overflow: 'initial',

    '& p': {
      fontSize: '1.125rem',
      color: '#4f4f4f',
    },
  },

  buttonsContainer: {
    display: 'flex',
    justifyContent: 'center',
    flexDirection: 'row',
    alignItems: 'center',

    [theme.breakpoints.down('xs')]: {
      flexDirection: 'column',
    },
  },

  buttonBase: {
    width: '7.5rem',
    height: '7.5rem',
    borderRadius: '10px',
    border: `solid 1px ${colors.darkishBlue}`,
    margin: '1.25rem 0.625rem 0.5rem 0.625rem',

    '&:hover': {
      backgroundColor: colorUtils.addAlphaToColor(colors.darkishBlue),
    },
  },

  buttonRoot: {
    display: 'flex',
    justifyContent: 'center',
    flexDirection: 'column',
    alignItems: 'center',
  },

  buttonImg: {
    width: '3.125rem',
    height: '3.125rem',
    marginBottom: '0.625rem',
  },
}));

const ShareDocumentDialog = (props) => {
  const classes = useStyles();

  const shareButtons = [
    {
      label: 'Digiposte',
      img: digiposteLogo,
      onClick: () => {},
    },
    {
      label: 'Cozy Cloud',
      img: cozycloudLogo,
      onClick: () => {},
    },
    {
      label: 'Mail',
      img: mailSvg,
      onClick: () => {},
    },
  ];

  return (
    <Dialog onClose={props.onClose} open={props.open} classes={{ dialogPaper: classes.paper }}>
      <div
        style={{
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          textAlign: 'center',
        }}
      >
        <p>
          <b>Où souhaitez vous partager ce document ?</b>
        </p>

        <div className={classes.buttonsContainer}>
          {shareButtons.map(button => (
            <ButtonBase
              onClick={() => {
                button.onClick();
                props.onClose();
              }}
              className={classes.buttonBase}
            >
              <div className={classes.buttonRoot}>
                <img src={button.img} alt="Mail" className={classes.buttonImg} />
                <p>{button.label}</p>
              </div>
            </ButtonBase>
          ))}
        </div>
      </div>
    </Dialog>
  );
};

ShareDocumentDialog.propTypes = {
  onClose: PropTypes.func,
  open: PropTypes.bool,
};

ShareDocumentDialog.defaultProps = {
  onClose: () => {},
  open: false,
};

export default enhancer(ShareDocumentDialog);
