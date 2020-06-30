import { compose, pure } from 'recompose';

import GenericDemarcheCard from 'react/components/embedded/GenericDemarcheCard';
import PropTypes from 'prop-types';
import React from 'react';
import { TextField } from '@material-ui/core';
import { makeStyles } from '@material-ui/core/styles';
import styles from 'react/components/embedded/GenericDemarche.module.scss';

const enhancer = compose(pure);

const useStyles = makeStyles({
  input: {
    width: '120%',
    '& .MuiOutlinedInput-input': {
      fontSize: '1.125rem',
      padding: '0.625rem',
      maxLength: 5,
      fontStyle: 'italic',
      borderColor: '#bebebe',
    },
    '&.MuiOutlinedInput-root.Mui-focused .MuiOutlinedInput-notchedOutline': {
      borderColor: 'black',
    },
    '&.MuiOutlinedInput-root.Mui-error .MuiOutlinedInput-notchedOutline': {
      borderColor: 'red',
    },
  },
});

const GenericDemarcheProvideIdentity = (props) => {
  const classes = useStyles();
  const [textFields, setTextFields] = React.useState(props.textFields);

  const formId = 'provide-identity-form';

  const handleTextFieldChange = (event) => {
    setTextFields(
      textFields.map((field) => {
        if (field.id === event.target.id) {
          field.value = event.target.value;
        }

        return field;
      }),
    );
  };

  const onValidateClick = (event) => {
    event.preventDefault();

    const textFieldsWithErrors = textFields.map(field => ({
      ...field,
      error: !field.value || field.value === '',
    }));

    const error = textFieldsWithErrors.filter(field => field.error).length > 0;

    if (error) {
      setTextFields(textFieldsWithErrors);
      return;
    }

    props.saveDataCallback(
      textFields
        .map(field => ({
          [field.id]: field.value,
        }))
        .reduce(
          (accu, currentValue) => ({
            ...accu,
            ...currentValue,
          }),
          {},
        ),
    );
  };

  return (
    <>
      <GenericDemarcheCard formId={formId} buttonLabel="Valider et continuer" title={props.title}>
        <form
          className={styles.form}
          style={{
            marginRight: '10%',
          }}
          id={formId}
          onSubmit={onValidateClick}
        >
          {textFields.map(({
            label, id, error, value,
          }) => (
            <div style={{ display: 'flex', flexDirection: 'row' }}>
              <p className={styles.formLabel}> {label} </p>
              <TextField
                id={id}
                margin="normal"
                // placeholder={placeholder}
                InputLabelProps={{
                  className: classes.input,
                }}
                InputProps={{
                  className: classes.input,
                }}
                error={error}
                defaultValue={value}
                onChange={handleTextFieldChange}
                variant="outlined"
              />
            </div>
          ))}
        </form>
      </GenericDemarcheCard>
    </>
  );
};

GenericDemarcheProvideIdentity.propTypes = {
  title: PropTypes.string,
  history: PropTypes.shape({ push: PropTypes.func.isRequired }).isRequired,
  textFields: PropTypes.arrayOf(PropTypes.string),
  saveDataCallback: PropTypes.func,
};

GenericDemarcheProvideIdentity.defaultProps = {
  title: '',
  textFields: [],
  saveDataCallback: () => {},
};

export default enhancer(GenericDemarcheProvideIdentity);
