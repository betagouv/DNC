import {
  CardContent,
  Container,
  useMediaQuery,
} from '@material-ui/core';
import { Document, ExternalDocument } from 'pdfjs';
import { compose, pure } from 'recompose';
import { makeStyles, useTheme } from '@material-ui/core/styles';

import Button from 'react/components/buttons/button/Button';
import Card from 'react/components/card/Card';
import DemarcheManager from 'services/DemarcheManager';
import FixedFooter from 'react/components/FixedFooter';
import Logger from 'utils/Logger';
import PropTypes from 'prop-types';
import React from 'react';
import Tooltip from 'react/components/Tooltip';
import ameliLogo from 'assets/images/ameli-logo-square.png';
import attestationBacPdf from 'assets/pdf/attestation_bac.pdf';
import attestationDroitsPdf from 'assets/pdf/attestation_droits.pdf';
import attestationPoleEmploiPdf from 'assets/pdf/attestation_pole_emploi.pdf';
import attestationQuotientFamilialPdf from 'assets/pdf/attestation_quotient_familial.pdf';
import avisImpositionPdf from 'assets/pdf/avis_imposition.pdf';
import cafLogo from 'assets/images/caf_logo.png';
import carSvg from 'assets/images/ic-car.svg';
import colors from 'style/config.variables.scss';
import compteFormationLogo from 'assets/images/compte_formation_logo.png';
import compteFormationPdf from 'assets/pdf/compte_formation.pdf';
import { connect } from 'react-redux';
import dgfipLogo from 'assets/images/dgfip_logo_square.png';
import diplomeGouvLogo from 'assets/images/diplome_gouv_logo.png';
import fcLogo from 'assets/images/fc_logo_2.png';
import identiteFranceConnectPdf from 'assets/pdf/identite_franceconnect.pdf';
import infoBlueSvg from 'assets/images/ic-info-blue.svg';
import infoSvg from 'assets/images/ic-info.svg';
import justificatifDomicilePdf from 'assets/pdf/justificatif_domicile.pdf';
import ministereInterieurLogo from 'assets/images/ministere_interieur_2.png';
import poleEmploiLogo from 'assets/images/pole_emploi_logo.png';
import { saveAs } from 'file-saver';
import JustificatifsTable from './JustificatifsTable';
import styles from './Justificatifs.module.scss';

const enhancer = compose(pure);

const useStyles = makeStyles(theme => ({
  gridContainer: {
    // height: 'calc(100vh - 5rem)', // 5rem = header size
    padding: '1rem 4.6875rem',
    maxWidth: '100%',
    /* height: '100%', */
    display: 'flex',
    flexDirection: 'column',
  },
  card: {
    height: '100%',
  },
  icon: {
    height: 'auto',
    width: '3.75rem',
  },
  cardAction: {
    color: colors.darkishBlue,
    '&:hover': {
      backgroundColor: colors.darkishBlueAlpha,
    },
  },
  cardContent: {
    position: 'relative',
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    padding: '3.4375rem 3.4375rem 1rem',
    height: '100%',
  },
  button: {
    flex: '1 0 33%',
    maxWidth: '20rem',
    alignSelf: 'flex-end',

    [theme.breakpoints.down('xs')]: {
      marginTop: '1.5rem',
      marginBottom: '1.5rem',
      alignSelf: 'center',
      flex: '',
      maxWidth: '',
    },
  },
  headerCell: {
    fontSize: '0.75rem',
    fontWeight: 'bold',
    fontStretch: 'normal',
    fontStyle: 'normal',
    lineHeight: 'normal',
    letterSpacing: 'normal',
    color: colors.lightNavyBlue,
    height: '80px',
    marginTop: '16px',
  },
  checkBoxRoot: {
    color: colors.darkishBlue,
    '&:hover': {
      backgroundColor: colors.darkishBlueAlpha,
    },
    alignSelf: 'flex-end',
  },
  checkBoxChecked: {
    color: `${colors.darkishBlue} !important`,
    '&:hover': {
      backgroundColor: `${colors.darkishBlueAlpha} !important`,
    },
    alignSelf: 'flex-end',
  },
  info: {
    width: '1.5rem',
    height: '1.5rem',
    marginLeft: '5px',
    '&:hover': {
      backgroundImage: `${infoBlueSvg}`,
    },
  },
}));

/**
 * Créer un object contenant les données nécessaire pour afficher un justificatif.
 *
 * @param {string} label - Le nom du justificatif.
 * @param {string} downloadUri - Le lien du PDF du justificatif.
 * @param {*} icon - L'image du justificatif.
 * @param {boolean} disabled - Indique si le justificatif est désactivé.
 *
 * @returns {*} - Un object contenant les données nécessaire pour afficher un justificatif.
 */
function createJustificatifRow(label, downloadUri, icon, disabled) {
  return {
    label,
    downloadUri,
    icon,
    id: label.replace(/[.,\s]/g, '_').toLowerCase(),
    disabled,
  };
}

/**
 * Créer tous les justificatifs.
 *
 * @param {*} classes - Les styles.
 * @param {string} numeroAllocataire - Le numéro d'allocataire de l'usager.
 * @returns {any[]} - La liste des justificatifs.
 */
function createAllJustificatif(classes, numeroAllocataire) {
  return [
    createJustificatifRow(
      "Justificatif de domicile (Ministère de l'Intérieur)",
      justificatifDomicilePdf,
      <img
        alt="Logo ministère de l'intérieur"
        className={classes.icon}
        src={ministereInterieurLogo}
      />,
      true,
    ),
    createJustificatifRow(
      "avis d'imposition (dernière année connue)",
      avisImpositionPdf,
      <img alt="Logo DGFIP" className={classes.icon} src={dgfipLogo} />,
    ),
    createJustificatifRow(
      'Attestation de droits',
      attestationDroitsPdf,
      <img alt="Logo Ameli" className={classes.icon} src={ameliLogo} />,
    ),
    createJustificatifRow(
      'Attestation de quotient familial',
      attestationQuotientFamilialPdf,
      <img alt="Logo CAF" className={classes.icon} src={cafLogo} />,
      !numeroAllocataire,
    ),
    createJustificatifRow(
      "Attestation d'inscription à Pôle Emploi",
      attestationPoleEmploiPdf,
      <img alt="Logo Pôle Emploi" className={classes.icon} src={poleEmploiLogo} />,
    ),
    createJustificatifRow(
      'Justificatif de droit à conduire (carte grise)',
      '',
      <img alt="Logo carte grise" className={classes.icon} src={carSvg} />,
    ),
    createJustificatifRow(
      "Justificatif d'identité FranceConnect",
      identiteFranceConnectPdf,
      <img alt="Logo FranceConnect" className={classes.icon} src={fcLogo} />,
    ),
    createJustificatifRow(
      'Attestation de diplôme',
      attestationBacPdf,
      <img alt="Logo Diplome Gouv" className={classes.icon} src={diplomeGouvLogo} />,
    ),
    createJustificatifRow(
      'Solde du compte de formation',
      compteFormationPdf,
      <img alt="Logo Compte formation" className={classes.icon} src={compteFormationLogo} />,
    ),
  ];
}

const Justificatifs = (props) => {
  const classes = useStyles();
  const theme = useTheme();
  const xsDown = useMediaQuery(theme.breakpoints.down('xs'));

  const justificatifsList = createAllJustificatif(classes, props.numeroAllocataire);

  /**
   * Télécharge le PDF en fonction de son URI et créer un ExternalDocument à partir du Blob.
   *
   * @param {string} uri - L'URI du fichier PDF.
   * @returns {Promise<ExternalDocument>} - Une promesse contenant l'ExternalDocument.
   */
  const createExternalDocuments = async (uri) => {
    const file = await fetch(uri, {
      method: 'GET',
      mode: 'cors',
      cache: 'default',
    });

    const blob = await file.blob();

    return new Promise((resolve, reject) => {
      try {
        const fileReader = new FileReader();

        fileReader.onloadend = () => {
          try {
            resolve(new ExternalDocument(fileReader.result));
          } catch (error) {
            reject(error);
          }
        };

        fileReader.readAsArrayBuffer(blob);
      } catch (error) {
        reject(error);
      }
    });
  };

  /**
   * Télécharge tous les PDFs sélectionnés et les fusionne en 1 seul fichier.
   *
   * @param {*} justificatifsToDownload - Tableau de justificatifs à télécharger.
   */
  const multipleFilesDownload = async (justificatifsToDownload) => {
    const promises = [];

    try {
      if (justificatifsToDownload.length === 1) {
        saveAs(justificatifsToDownload[0].downloadUri, `${justificatifsToDownload[0].id}.pdf`);
        return;
      }

      const doc = new Document();

      justificatifsToDownload.forEach(({ downloadUri }) => {
        if (downloadUri) {
          promises.push(createExternalDocuments(downloadUri));
        }
      });

      const externalDocuments = await Promise.all(promises);

      externalDocuments.forEach((ext) => {
        doc.addPagesOf(ext);
      });

      const buffer = await doc.asBuffer();
      saveAs(new Blob([buffer]), 'mes_justificatifs.pdf');
    } catch (error) {
      Logger.error(error);
    }
  };

  const buttonOnClick = xsDown
    ? () => {
      props.history.push('/justificatifs/personnalises');
    }
    : () => {
      props.clearJustificatifInitial();
      DemarcheManager.fetchData('e3782539-260e-4f60-9661-f2acd15945b6');
      props.history.push('/justificatifs/personnalises');
    };

  const button = (
    <Button
      size="small"
      color={colors.darkishBlue}
      className={classes.button}
      onClick={buttonOnClick}
    >
      Créer mon justificatif
    </Button>
  );

  const table = (
    <>
      <JustificatifsTable
        justificatifs={justificatifsList}
        multipleFilesDownload={multipleFilesDownload}
      />
    </>
  );

  if (xsDown) {
    return (
      <>
        <div style={{ padding: '0.5rem' }}>
          <p className={styles.cardTitle}>
            Mes justificatifs{' '}
            <Tooltip title="Les justificatifs récupérés émanent directement des services concernés, ils ne sont pas conservés par le dossier numérique du citoyen. Le chargement peut prendre quelques secondes.">
              <img
                className={classes.info}
                alt="Info bulle"
                src={infoSvg}
                onMouseEnter={e => e.target.setAttribute('src', infoBlueSvg)}
                onMouseLeave={e => e.target.setAttribute('src', infoSvg)}
              />
            </Tooltip>
          </p>

          {table}
        </div>
        <FixedFooter>{button}</FixedFooter>
      </>
    );
  }

  return (
    <>
      <div
        style={{
          // width: '100%',
          display: 'flex',
          flexDirection: 'column',
        }}
      >
        <Container className={classes.gridContainer}>
          <Card className={classes.card}>
            <CardContent className={classes.cardContent}>
              <div
                style={{
                  display: 'flex',
                  width: '100%',
                }}
              >
                <div style={{ flex: '1 0 33%' }} />
                <p className={styles.cardTitle} style={{ flex: '1 0 33%' }}>
                    Mes justificatifs
                  <Tooltip title="Les justificatifs récupérés émanent directement des services concernés, ils ne sont pas conservés par le dossier numérique du citoyen. Le chargement peut prendre quelques secondes.">
                    <img
                      className={classes.info}
                      alt="Info bulle"
                      src={infoSvg}
                      onMouseEnter={e => e.target.setAttribute('src', infoBlueSvg)}
                      onMouseLeave={e => e.target.setAttribute('src', infoSvg)}
                    />
                  </Tooltip>
                </p>

                <div style={{ flex: '1 0 33%', display: 'flex', flexDirection: 'column' }}>
                  <Tooltip title="Créez un justificatif agrégeant toutes les informations nécessaires à votre démarche">
                    {button}
                  </Tooltip>
                </div>
              </div>

              {table}
            </CardContent>
          </Card>
        </Container>
      </div>
    </>
  );
};

Justificatifs.propTypes = {
  numeroAllocataire: PropTypes.string.isRequired,
  history: PropTypes.shape({ push: PropTypes.func.isRequired }).isRequired,
  clearJustificatifInitial: PropTypes.func.isRequired,
};

Justificatifs.defaultProps = {};

const mapStateToProps = state => ({
  numeroAllocataire: state.identity.numeroAllocataire,
});

const mapDispatchToProps = dispatch => ({
  clearJustificatifInitial: () => dispatch({
    type: 'SET_JUSTIFICATIF_PERSONNALISE_INITIAL',
    justificatifInitial: undefined,
  }),
});

export default connect(mapStateToProps, mapDispatchToProps)(enhancer(Justificatifs));
