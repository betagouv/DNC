import React from 'react';
import { compose, pure } from 'recompose';
import strings from 'string/localized';
import previewSvg from 'assets/images/ic-view.svg';
import StartIconButton from './StartIconButton';

const enhancer = compose(pure);

const PreviewButton = props => (
  <StartIconButton
    icon={<img alt="Aperçu" src={previewSvg} style={{ width: '1.625rem' }} />}
    {...props}
  >
    {strings.PREVIEW}
  </StartIconButton>
);

PreviewButton.propTypes = {};

PreviewButton.defaultProps = {};

export default enhancer(PreviewButton);
