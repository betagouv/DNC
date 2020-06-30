import React from 'react';
import { compose, pure } from 'recompose';
import PropTypes from 'prop-types';
import { Slide, Dialog as MuiDialog, IconButton } from '@material-ui/core';
import { makeStyles } from '@material-ui/core/styles';
import closeSvg from 'assets/images/ic-close.svg';
import colors from 'style/config.variables.scss';

const Transition = React.forwardRef((props, ref) => <Slide direction="up" ref={ref} {...props} />);

const enhancer = compose(pure);

const useStyles = makeStyles(() => ({
  dialogPaper: {
    overflow: 'initial',
    borderRadius: '20px',
  },
  closeButton: props => ({
    display: props.loading ? 'none' : 'initial',
    position: 'absolute',
    right: 'calc(-2.5rem / 2)',
    top: 'calc(-2.5rem / 2)',
    padding: 0,
    zIndex: 1,
    background: colors.lightNavyBlue,
    '&:hover': { background: colors.lightNavyBlue },
  }),
  closeIcon: {
    width: '2.5rem',
    height: '2.5rem',
  },
}));

const Dialog = (props) => {
  const classes = useStyles(props);

  return (
    <MuiDialog
      scroll="body"
      TransitionComponent={Transition}
      {...props}
      classes={{
        paper: classes.dialogPaper,
      }}
    >
      <IconButton disableRipple onClick={props.onClose} className={classes.closeButton}>
        <img className={classes.closeIcon} alt="Fermer" src={closeSvg} />
      </IconButton>

      {props.children}
    </MuiDialog>
  );
};

Dialog.propTypes = {
  open: PropTypes.bool,
  loading: PropTypes.bool,
  onClose: PropTypes.func,
  children: PropTypes.node,
};

Dialog.defaultProps = {
  open: false,
  loading: false,
  onClose: () => {},
  children: null,
};

export default enhancer(Dialog);
