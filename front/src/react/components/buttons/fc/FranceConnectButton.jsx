/* eslint-disable jsx-a11y/no-noninteractive-element-interactions */
/* eslint-disable jsx-a11y/click-events-have-key-events */
import React from 'react';
import { compose, pure } from 'recompose';
import PropTypes from 'prop-types';
import fcSvg from 'assets/images/fc_button.svg';
import styles from './FranceConnectButton.module.scss';

const enhancer = compose(pure);

const FranceConnectButton = props => (
  <img
    src={fcSvg}
    {...props}
    className={`${props.className} ${styles.button}`}
    onClick={props.onClick}
    alt="Bouton FranceConnect"
  />
);

FranceConnectButton.propTypes = {
  className: PropTypes.string,
  onClick: PropTypes.func,
};

FranceConnectButton.defaultProps = {
  className: '',
  onClick: () => {},
};

export default enhancer(FranceConnectButton);
