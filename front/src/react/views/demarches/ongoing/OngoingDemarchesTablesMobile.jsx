import {
  Table,
  useMediaQuery,
} from '@material-ui/core';
import { compose, pure } from 'recompose';

import PropTypes from 'prop-types';
import React from 'react';
import { useTheme } from '@material-ui/core/styles';
import OngoingDemarchesRow from './OngoingDemarchesRow';

const enhancer = compose(pure);

const headerRow = [
  'N° dossier',
  'Démarche',
  'Date',
  'Sources consultées',
  'état',
  'commentaire',
  '',
];

const MobileDeviceRender = (props) => {
  const theme = useTheme();
  const xsDown = useMediaQuery(theme.breakpoints.down('xs'));

  if (xsDown) {
    const event = new CustomEvent(
      'USE_MOBILE_VERSION',
      {
        detail: [true],
      },
    );
    document.dispatchEvent(event);
    return (
      <>
        {props.demarches.map((demarche, index) => (
          <Table
            style={{
              marginBottom: '20px',
              borderTop: index > 0 ? '1px solid rgb(162, 162, 162)' : 'initial',
            }}
          >
            <OngoingDemarchesRow {...demarche} headers={headerRow} />
          </Table>
        ))}
      </>
    );
  }
  const event = new CustomEvent(
    'USE_MOBILE_VERSION',
    {
      detail: [false],
    },
  );
  document.dispatchEvent(event);
  return (null);
};
MobileDeviceRender.propTypes = {
  demarches: PropTypes.arrayOf(
    PropTypes.shape({
      id: PropTypes.number,
      updated_at: PropTypes.string,
      state: PropTypes.string,
      initiated_at: PropTypes.string,
      libelle_demarche: PropTypes.string,
      sources: PropTypes.string,
      documents_en_attente: PropTypes.arrayOf(PropTypes.string),
      alerte_etat: PropTypes.bool,
      alerte_documents: PropTypes.bool,
    }),
  ),
};

MobileDeviceRender.defaultProps = {
  demarches: [],
};

export default enhancer(MobileDeviceRender);
