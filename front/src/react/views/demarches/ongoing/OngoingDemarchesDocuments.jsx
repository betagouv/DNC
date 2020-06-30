import { compose, pure } from 'recompose';

import PropTypes from 'prop-types';
import React from 'react';
// import editSvg from 'assets/images/ic-edit.svg';
// import { IconButton } from '@material-ui/core';

const enhancer = compose(pure);

const OngoingDemarchesDocuments = (props) => {
  if (props.documents.length === 0) {
    return <p>-</p>;
  }

  return (
    <>
      <div>
        {props.documents.map(document => (
          <p>{document}</p>
        ))}
      </div>
    </>
  );
};
/*
<IconButton style={{ marginLeft: '0.625rem', padding: 0 }}>
        <img src={editSvg} style={{ width: '1.875rem' }} alt="" />
      </IconButton>
*/

OngoingDemarchesDocuments.propTypes = {
  documents: PropTypes.arrayOf(PropTypes.string),
};

OngoingDemarchesDocuments.defaultProps = {
  documents: [],
};

export default enhancer(OngoingDemarchesDocuments);
