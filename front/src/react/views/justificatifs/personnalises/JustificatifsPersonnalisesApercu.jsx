import { makeStyles } from '@material-ui/core/styles';
import marianneImg from 'assets/images/marianne.png';
import qrcodeImg from 'assets/images/qrcode.jpg';
import signatureImg from 'assets/images/signature.png';
import PropTypes from 'prop-types';
import React from 'react';
import { connect } from 'react-redux';
import { compose, pure } from 'recompose';
import JustificatifsPersonnalisesBox from './JustificatifsPersonnalisesBox';

const enhancer = compose(pure);

const useStyles = makeStyles({
  header: {
    display: 'flex',
    flexDirection: 'row',
    width: '100%',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  marianneImg: {
    height: '4.9375rem',
    width: '9.6875rem',
  },
  dncLabel: {
    textAlign: 'end',
    fontSize: '0.75rem',
  },
  dateLabel: {
    width: '100%',
    textAlign: 'end',
    fontSize: '0.75rem',
  },
  title: {
    marginTop: '2rem',
    fontSize: '0.75rem',
  },
  subtitle: {
    marginTop: '1rem',
    fontWeight: 500,
    textAlign: 'center',
    fontSize: '0.75rem',
  },
  dataList: {
    display: 'flex',
    flexDirection: 'column',
    marginBottom: '1.25rem',
  },
  dataContainer: {
    display: 'flex',
    flexDirection: 'row',
    marginTop: '0.75rem',
    alignItems: 'center',
  },
  dataImg: { height: '1.5rem', marginRight: '0.625rem' },
  dataLabel: { fontSize: '0.75rem' },
  footer: {
    display: 'flex',
    flexDirection: 'row',
    width: '100%',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginTop: 'auto',
  },
  qrcodeImg: {
    height: '5.9375rem',
    width: '5.9375rem',
  },
  signatureImg: {
    height: '2.4375rem',
    width: '3.9375rem',
  },
  mentionsLegales: { fontSize: '0.625rem', paddingBottom: '0.625rem' },
});

const JustificatifsPersonnalisesApercu = (props) => {
  const classes = useStyles();

  return (
    <>
      <JustificatifsPersonnalisesBox
        style={{
          width: '50%',
          maxWidth: '500px',
          alignItems: 'center',
          margin: '0 0.5rem',
        }}
      >
        <div className={classes.header}>
          <img src={marianneImg} className={classes.marianneImg} alt="Logo Marianne" />

          <p className={classes.dncLabel}>
            Dossier numérique <br /> du citoyen
          </p>
        </div>

        <p className={classes.dateLabel}>
          <b>Le :</b>&nbsp;{new Date().toLocaleDateString()}
        </p>

        <p className={classes.title}>
          <u>
            <b>JUSTIFICATIF</b>
          </u>
        </p>

        <p className={classes.subtitle}>
          <u>Informations nécessaires pour {props.label} :</u>
        </p>

        <div className={classes.dataList}>
          {props.data.map(data => (
            <div className={classes.dataContainer}>
              {data.img && (
                <img src={data.img} className={classes.dataImg} alt="Source de la donnée" />
              )}

              <p className={classes.dataLabel}>
                <b>{data.label.toUpperCase()} :</b> {data.value}
              </p>
            </div>
          ))}
        </div>

        {props.additionalData.length > 0 && (
          <>
            <p className={classes.subtitle}>
              <u>Informations supplémentaires :</u>
            </p>

            <div className={classes.dataList}>
              {props.additionalData.map(data => (
                <div className={classes.dataContainer}>
                  {data.img && (
                    <img src={data.img} className={classes.dataImg} alt="Source de la donnée" />
                  )}

                  <p className={classes.dataLabel}>
                    <b>{data.label.toUpperCase()} :</b> {data.value}
                  </p>
                </div>
              ))}
            </div>
          </>
        )}

        <div className={classes.footer}>
          <img src={qrcodeImg} className={classes.qrcodeImg} alt="QR Code" />

          <img src={signatureImg} className={classes.signatureImg} alt="Signature" />
        </div>

        <p className={classes.mentionsLegales}>
          <u>MENTIONS LEGALES :</u> <br />
          LE PRESENT DOCUMENT A ETE GENERE PAR LE DOSSIER NUMERIQUE DU CITOYEN, FAISANT SUITE A
          L’ARRETE N° 2020.12.18 XXX
        </p>
      </JustificatifsPersonnalisesBox>
    </>
  );
};

JustificatifsPersonnalisesApercu.propTypes = {
  label: PropTypes.string,
  data: PropTypes.arrayOf(PropTypes.string),
  additionalData: PropTypes.arrayOf(PropTypes.objectOf(PropTypes.string)),
};

JustificatifsPersonnalisesApercu.defaultProps = {
  label: '',
  data: [],
  additionalData: [],
};

const mapStateToProps = () => ({});

const mapDispatchToProps = () => ({});

export default connect(
  mapStateToProps,
  mapDispatchToProps,
)(enhancer(JustificatifsPersonnalisesApercu));
