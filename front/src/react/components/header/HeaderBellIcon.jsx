import React from 'react';
import { compose, pure } from 'recompose';
import PropTypes from 'prop-types';
import bellSvg from 'assets/images/ic-cloche-blue.svg';
import { useTheme } from '@material-ui/core/styles';
import useMediaQuery from '@material-ui/core/useMediaQuery';
import styles from './Header.module.scss';

const enhancer = compose(pure);

const HeaderBellIcon = (props) => {
  const theme = useTheme();
  const smScreen = useMediaQuery(theme.breakpoints.down('sm'));

  return (
    <div style={{ position: 'relative' }}>
      <div
        style={{
          position: 'absolute',
          right: '-8px',
          top: '-8px',
        }}
      >
        {props.alertNumber > 0 && (
          <div className={styles.oval} style={{ width: smScreen ? '2rem' : '' }}>
            {props.alertNumber}
          </div>
        )}
      </div>

      <img src={bellSvg} className={props.className} alt="Icone notifications" />
    </div>
  );
};

HeaderBellIcon.propTypes = {
  alertNumber: PropTypes.number,
  className: PropTypes.string,
};

HeaderBellIcon.defaultProps = {
  alertNumber: 0,
  className: '',
};

export default enhancer(HeaderBellIcon);
