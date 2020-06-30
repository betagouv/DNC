import React from 'react';
import { compose, pure } from 'recompose';
import strings from 'string/localized';
import downloadSvg from 'assets/images/ic-download.svg';
import StartIconButton from './StartIconButton';

const enhancer = compose(pure);

const DownloadButton = props => (
  <StartIconButton
    icon={<img alt="Télécharger" src={downloadSvg} style={{ width: '1.0625rem' }} />}
    {...props}
  >
    {strings.DOWNLOAD}
  </StartIconButton>
);

DownloadButton.propTypes = {};

DownloadButton.defaultProps = {};

export default enhancer(DownloadButton);
