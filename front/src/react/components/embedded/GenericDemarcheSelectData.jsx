import React from 'react';
import { compose, pure } from 'recompose';
import { MenuItem, Select } from '@material-ui/core';
import PropTypes from 'prop-types';
import GenericDemarcheCard from 'react/components/embedded/GenericDemarcheCard';
import styles from 'react/components/embedded/GenericDemarche.module.scss';
import { makeStyles } from '@material-ui/core/styles';

const enhancer = compose(pure);

const useStyles = makeStyles(theme => ({
  select: {
    width: '50vw',
    fontSize: '1.125rem',
    padding: '0.625rem',
    fontStyle: 'italic',
    borderColor: '#bebebe',
    lineHeight: 'normal',
    maxWidth: '19.6875rem',
    '& .MuiSelect-root': {
      padding: '0',
    },
  },
  popover: {
    [theme.breakpoints.down('xs')]: {
      backgroundColor: 'rgba(0, 0, 0, 0.35)',
    },
  },
}));

const GenericDemarcheSelectData = (props) => {
  const classes = useStyles();

  const [selects, setSelects] = React.useState([]);

  const formId = 'select-data-form';

  React.useEffect(() => {
    setSelects(
      props.selects.map((select) => {
        const options = [
          ...select.options,
          {
            id: 'autre',
            value: 'Autre',
          },
        ];

        if (select.options && select.options.length > 0 && select.options[0].id) {
          select.value = select.options[0].id;
        }

        return {
          ...select,
          options,
        };
      }),
    );
  }, [props.selects]);

  const handleSelectChange = (event) => {
    setSelects(
      selects.map((select) => {
        if (select.id === event.target.name) {
          select.value = event.target.value;
        }

        return select;
      }),
    );
  };

  const onValidateClick = (event) => {
    event.preventDefault();

    const selectsWithErrors = selects.map(select => ({
      ...select,
      error: !select.value || select.value === '',
    }));

    const error = selectsWithErrors.filter(select => select.error).length > 0;

    if (error) {
      setSelects(selectsWithErrors);
      return;
    }

    props.saveDataCallback(
      selects
        .map(select => ({
          [select.id]: select.value,
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
        <form className={styles.form} id={formId} onSubmit={onValidateClick}>
          {selects.map(({
            label, id, error, value, options,
          }, index) => (
            <div
              style={{
                display: 'flex',
                flexDirection: 'row',
                marginTop: index !== 0 ? '30px' : 'initial',
              }}
            >
              <p className={styles.formLabel}> {label} </p>
              <Select
                name={id}
                className={classes.select}
                variant="outlined"
                value={value || ' '}
                onChange={handleSelectChange}
                error={error}
                MenuProps={{
                  PopoverClasses: {
                    root: classes.popover,
                  },
                }}
              >
                {options.map(option => (
                  <MenuItem value={option.id}>{option.value}</MenuItem>
                ))}
              </Select>
            </div>
          ))}
        </form>
      </GenericDemarcheCard>
    </>
  );
};

GenericDemarcheSelectData.propTypes = {
  saveDataCallback: PropTypes.func,
  history: PropTypes.shape({ push: PropTypes.func.isRequired }).isRequired,
  selects: PropTypes.arrayOf(PropTypes.objectOf(PropTypes.string)),
  title: PropTypes.string,
};

GenericDemarcheSelectData.defaultProps = {
  saveDataCallback: () => {},
  selects: [],
  title: '',
};

export default enhancer(GenericDemarcheSelectData);
