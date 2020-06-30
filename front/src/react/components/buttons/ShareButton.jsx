import React from 'react';
import { compose, pure } from 'recompose';
import shareSvg from 'assets/images/ic-share.svg';
import StartIconButton from './StartIconButton';

const enhancer = compose(pure);

const ShareButton = props => (
  <StartIconButton
    icon={<img alt="Partager" src={shareSvg} style={{ width: '1.0625rem' }} />}
    {...props}
  >
    Partager
  </StartIconButton>
);

ShareButton.propTypes = {};

ShareButton.defaultProps = {};

export default enhancer(ShareButton);
