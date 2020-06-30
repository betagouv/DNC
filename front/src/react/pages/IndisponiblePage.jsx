import React from 'react';
import { compose, pure } from 'recompose';
import Indisponible from 'react/views/indisponible/Indisponible';

const enhancer = compose(pure);

const IndisponiblePage = (props) => {
  React.useEffect(() => {
    document.title = 'Page en développement';
  }, []);

  return <Indisponible {...props} />;
};

IndisponiblePage.propTypes = {};

IndisponiblePage.defaultProps = {};

export default enhancer(IndisponiblePage);
