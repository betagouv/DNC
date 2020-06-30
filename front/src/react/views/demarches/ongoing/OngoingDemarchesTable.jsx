import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow,
} from '@material-ui/core';

import PropTypes from 'prop-types';
import React from 'react';
import TableSortLabel from '@material-ui/core/TableSortLabel';
import colors from 'style/config.variables.scss';
import OngoingDemarchesTablesMobile from './OngoingDemarchesTablesMobile';
import OngoingDemarchesRow from './OngoingDemarchesRow';

const classes = {
  headerCell: {
    fontSize: '0.75rem',
    fontWeight: 'bold',
    fontStretch: 'normal',
    fontStyle: 'normal',
    lineHeight: 'normal',
    letterSpacing: 'normal',
    color: colors.lightNavyBlue,
    height: '80px',
  },
};

const headerRow = [
  'N° dossier',
  'Démarche',
  'Date',
  'Sources consultées',
  'état',
  'commentaire',
  '',
];

export default class OngoingDemarchesTable extends React.PureComponent {
  state = {
    sortDirection: 'asc',
    demarches: [],
    useMobileVersion: true,
  }

  /**
   * Constructor.
   *
   * @param {*} props - Les props.
   */
  constructor(props) {
    super();
    this.state.demarches = (props.demarches ? props.demarches : []);
    document.addEventListener('USE_MOBILE_VERSION', (event) => {
      [this.state.useMobileVersion] = event.detail;
      this.setState({ useMobileVersion: event.detail[0] });
    });
  }

  /**
   * Fonction de tri des démarches.
   *
   */
  order = () => {
    this.setState(previousState => ({ demarches: previousState.demarches.reverse() }));
    this.setState(previousState => ({ sortDirection: (previousState.sortDirection === 'asc' ? 'desc' : 'asc') }));
  }

  /**
   * Rend l'UI.
   *
   * @returns {*} L'UI.
   */
  render() {
    const content = (
      <OngoingDemarchesTablesMobile demarches={this.state.demarches} />
    );
    if (this.state.useMobileVersion) {
      return content;
    }
    return (
      <>
        <Table>
          <TableHead>
            <TableRow>
              {headerRow.map(cell => (
                <TableCell className={classes.headerCell}>
                  {cell.toUpperCase() === 'DATE'
                    ? (
                      <TableSortLabel
                        className={classes.headerCell}
                        style={{ color: 'rgb(47, 84, 150)' }}
                        active
                        direction={this.state.sortDirection}
                        onClick={this.order}
                      >
                        {cell.toUpperCase()}
                      </TableSortLabel>
                    )
                    : cell.toUpperCase()}
                </TableCell>
              ))}
            </TableRow>
          </TableHead>
          <TableBody>
            {this.state.demarches.map(row => (
              <OngoingDemarchesRow {...row} />
            ))}
          </TableBody>
        </Table>
      </>
    );
  }
}

OngoingDemarchesTable.propTypes = {
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

OngoingDemarchesTable.defaultProps = {
  demarches: [],
};
