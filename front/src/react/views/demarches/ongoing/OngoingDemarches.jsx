import { CardContent, Grid } from '@material-ui/core';
import { compose, pure } from 'recompose';
import { makeStyles, useTheme } from '@material-ui/core/styles';

import Card from 'react/components/card/Card';
import PropTypes from 'prop-types';
import React from 'react';
import useMediaQuery from '@material-ui/core/useMediaQuery';
import styles from './OngoingDemarches.module.scss';
import OngoingDemarchesTable from './OngoingDemarchesTable';

const enhancer = compose(pure);
const useStyles = makeStyles(() => ({
  cardContent: {
    padding: '1rem',
    paddingTop: '3.4375rem',
  },
}));

const OngoingDemarches = (props) => {
  const theme = useTheme();
  const xsScreen = useMediaQuery(theme.breakpoints.down('xs'));
  const classes = useStyles();

  const demarches = props.demarches.map((demarche) => {
    const date = new Date(demarche.initiated_at);

    const options = { year: 'numeric', month: 'numeric', day: 'numeric' };

    let state = '';

    switch (demarche.state) {
      case 'refused':
        state = 'Refusé';
        break;
      case 'closed':
        state = 'Accepté';
        break;
      case 'without_continuation':
        state = 'Classé sans suite';
        break;
      default:
        state = 'En cours';
        break;
    }
    return {
      id: demarche.id,
      demarche: demarche.libelle_demarche,
      date: date.toLocaleDateString('fr-FR', options),
      sources: demarche.sources,
      state,
      documents: demarche.documents_en_attente,
      stateAlert: demarche.alerte_etat,
      documentsAlert: demarche.alerte_documents,
      link: `https://www.demarches-simplifiees.fr/procedures/${demarche.id}`,
    };
  });

  const content = (
    <>
      <p className={styles.cardTitle}>Mes {demarches.length} demarches en cours</p>

      <OngoingDemarchesTable demarches={demarches} />
    </>
  );

  if (xsScreen) {
    return content;
  }

  return (
    <>
      <Grid item xs={12} style={{ padding: '0.5rem' }}>
        <Card>
          <CardContent className={classes.cardContent}>{content}</CardContent>
        </Card>
      </Grid>
    </>
  );
};

OngoingDemarches.propTypes = {
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

OngoingDemarches.defaultProps = {
  demarches: [],
};

export default enhancer(OngoingDemarches);
