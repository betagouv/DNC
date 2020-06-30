import { makeStyles, useTheme } from '@material-ui/core/styles';
import { useMediaQuery } from '@material-ui/core';
import FixedFooter from 'react/components/FixedFooter';
import infoBlueSvg from 'assets/images/ic-info-blue.svg';
import infoSvg from 'assets/images/ic-info.svg';
import React from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { withRouter } from 'react-router-dom';
import Button from 'react/components/buttons/button/Button';
import Tooltip from 'react/components/Tooltip';
import { compose, pure } from 'recompose';
import colors from 'style/config.variables.scss';
import PropTypes from 'prop-types';
import JustificatifsPersonnalisesApercu from '../JustificatifsPersonnalisesApercu';
import JustificatifsPersonnalisesBox from '../JustificatifsPersonnalisesBox';
import JustificatifsPersonnalisesAdditionalDataButton from './JustificatifsPersonnalisesAdditionalDataButton';
import JustificatifsPersonnalisesAdditionalDataSelect from './JustificatifsPersonnalisesAdditionalDataSelect';
import JustificatifsPersonnalisesMandatoryDataSelect from './JustificatifsPersonnalisesMandatoryDataSelect';

const enhancer = compose(pure);

const useStyles = makeStyles(theme => ({
  button: {
    alignSelf: 'center',
    minWidth: '15.625rem',
    marginTop: '2rem',
    marginBottom: '2rem',

    [theme.breakpoints.down('xs')]: {
      marginTop: '0.5rem',
      marginBottom: '0.5rem',
    },
  },
  info: {
    width: '1.5rem',
    height: '1.5rem',
    '&:hover': {
      backgroundImage: `${infoBlueSvg}`,
    },
  },
}));

const JustificatifsPersonnalisesModification = (props) => {
  const classes = useStyles();
  const theme = useTheme();
  const xsDown = useMediaQuery(theme.breakpoints.down('xs'));

  const [selectedAdditionalData, setSelectedAdditionalData] = React.useState(new Map());
  // const [address, setAddress] = React.useState(null);

  const [justificatif, setJustificatif] = React.useState(props.justificatifInitial);

  /* if (address) {
    justificatif.data = justificatif.data.map(data => ({
      ...data,
      value: data.id === 'adresse' ? address.value : data.value,
      img: data.id === 'adresse' && address.sourceImg ? address.sourceImg : data.img,
    }));
  }

  const addresses = [
    {
      id: 'adresse_1',
      value: 'BAT 3 APT 93, RUE DU CENTRE NOISY LE GRAND',
      source: 'FranceConnect',
      sourceImg: fcImg,
    },
    {
      id: 'adresse_2',
      value: '3 Rue Jean Jaurès 25660 Montrond-le-Château',
      source: 'DGFiP',
      sourceImg: dgfipImg,
    },
    {
      id: 'adresse_3',
      value: '182 Avenue de France - Paris',
      source: 'CNAF',
      sourceImg: cafImg,
    },
  ]; */

  const onMandatoryDataChange = (selectedData, id) => {
    const justifData = justificatif.data.filter(dataElement => dataElement.id === id)[0];

    if (!justifData) {
      return;
    }

    justifData.value = selectedData.value;
    justifData.img = selectedData.sourceImg || justifData.img;

    setJustificatif({ ...justificatif });
  };

  const onAdditionalDataButtonClick = (datas) => {
    if (datas) {
      const map = new Map(selectedAdditionalData.entries());

      datas.forEach((data) => {
        if (!selectedAdditionalData.has(data.id) && !data.data) {
          return;
        }

        if (
          selectedAdditionalData.has(data.id)
          && selectedAdditionalData.get(data.id) === data.data
        ) {
          return;
        }

        if (selectedAdditionalData.has(data.id) && !data.data) {
          map.delete(data.id);
        } else {
          map.set(data.id, data.data);
        }
      });

      setSelectedAdditionalData(map);
    }
  };

  const onAdditionalDataSelectChange = (id, data) => {
    if (!selectedAdditionalData.has(id) && !data) {
      return;
    }

    if (selectedAdditionalData.has(id) && selectedAdditionalData.get(id) === data) {
      return;
    }

    const map = new Map(selectedAdditionalData.entries());

    if (selectedAdditionalData.has(id) && !data) {
      map.delete(id);
    } else {
      map.set(id, data);
    }

    setSelectedAdditionalData(map);
  };

  if (!props.justificatifInitial) {
    return <Redirect to="selection" />;
  }

  let previewComponent = (
    <JustificatifsPersonnalisesApercu
      label={justificatif.label}
      data={justificatif.data}
      additionalData={[...selectedAdditionalData.values()]}
    />
  );

  let validationButton = (
    <Button
      disabled={Object.keys(justificatif).length === 0}
      size="small"
      color={colors.darkishBlue}
      className={classes.button}
      onClick={() => {
        props.history.push('finalisation');
      }}
    >
      Continuer
    </Button>
  );

  if (xsDown) {
    previewComponent = null;

    validationButton = <FixedFooter>{validationButton}</FixedFooter>;
  }

  return (
    <>
      <div
        style={{
          display: 'flex',
          flexDirection: 'row',
          height: !xsDown ? '100%' : '100%',
          justifyContent: 'space-evenly',
          alignItems: 'center',
        }}
      >
        <div
          style={{
            display: 'flex',
            flexDirection: 'column',
            height: '100%',
            width: !xsDown ? '50%' : '100%',
            maxWidth: '500px',
          }}
        >
          <JustificatifsPersonnalisesBox
            style={{
              height: 'initial',
              justifyContent: 'flex-start',
              margin: '0 0.625rem',
            }}
          >
            <p
              style={{
                color: '#4f4f4f',
                fontSize: '1rem',
                fontWeight: 'bold',
                textAlign: 'center',
                marginBottom: '1rem',
              }}
            >
              Sélectionner les informations
            </p>

            {props.justificatifInitial.data
              .filter(dataElement => dataElement.options && dataElement.options.length > 0)
              .map(dataElement => (
                <JustificatifsPersonnalisesMandatoryDataSelect
                  label={dataElement.label}
                  source={dataElement.source}
                  options={dataElement.options}
                  defaultValue={dataElement.options[0].id}
                  onChange={(selectedData) => {
                    onMandatoryDataChange(selectedData, dataElement.id);
                  }}
                />
              ))}
            {/* <JustificatifsPersonnalisesMandatoryDataSelect
              options={addresses}
              defaultValue={addresses[0].id}
              onChange={setAddress}
            /> */}
          </JustificatifsPersonnalisesBox>

          {props.justificatifInitial.additionalData.length > 0 && (
            <JustificatifsPersonnalisesBox
              style={{ margin: '1.25rem 0.625rem 0.625rem', justifyContent: 'flex-start' }}
            >
              <div
                style={{
                  display: 'flex',
                }}
              >
                <p
                  style={{
                    color: '#4f4f4f',
                    fontSize: '1rem',
                    fontWeight: 'bold',
                    textAlign: 'center',
                    flexGrow: '1',
                    marginBottom: '1rem',
                  }}
                >
                  Sélectionner des données supplémentaires
                </p>

                <Tooltip title="Certaines démarches nécessitent des informations supplémentaires">
                  <img
                    className={classes.info}
                    alt="Info bulle"
                    src={infoSvg}
                    onMouseEnter={e => e.target.setAttribute('src', infoBlueSvg)}
                    onMouseLeave={e => e.target.setAttribute('src', infoSvg)}
                  />
                </Tooltip>
              </div>

              {props.justificatifInitial.additionalData.map((data) => {
                if (!data.options) {
                  return (
                    <JustificatifsPersonnalisesAdditionalDataButton
                      id={data.id}
                      label={data.label}
                      values={data.values}
                      source={data.source}
                      img={data.img}
                      onClick={onAdditionalDataButtonClick}
                    />
                  );
                }
                return (
                  <JustificatifsPersonnalisesAdditionalDataSelect
                    id={data.id}
                    options={data.options}
                    label={data.label}
                    source={data.source}
                    img={data.img}
                    onChange={onAdditionalDataSelectChange}
                  />
                );
              })}
            </JustificatifsPersonnalisesBox>
          )}
        </div>
        {previewComponent}
      </div>

      {validationButton}
    </>
  );
};

JustificatifsPersonnalisesModification.propTypes = {
  justificatifInitial: PropTypes.shape({
    label: PropTypes.string,
    enabled: PropTypes.bool,
    data: PropTypes.arrayOf(PropTypes.objectOf(PropTypes.string)),
    additionalData: PropTypes.arrayOf(PropTypes.objectOf(PropTypes.string)),
  }),
  history: PropTypes.shape({ push: PropTypes.func.isRequired }).isRequired,
};

JustificatifsPersonnalisesModification.defaultProps = {
  justificatifInitial: null,
};

const mapStateToProps = state => ({
  justificatifInitial: state.justificatifs.justificatifInitial,
});

const mapDispatchToProps = () => ({});

export default withRouter(
  connect(mapStateToProps, mapDispatchToProps)(enhancer(JustificatifsPersonnalisesModification)),
);
