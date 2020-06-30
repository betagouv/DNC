import React from 'react';
import { compose, pure } from 'recompose';
import { connect } from 'react-redux';
import {
  TextField, Checkbox, TableCell, TableRow, useMediaQuery,
} from '@material-ui/core';
import Button from 'react/components/buttons/button/Button';
import { makeStyles, useTheme } from '@material-ui/core/styles';
import colors from 'style/config.variables.scss';
import PropTypes from 'prop-types';

const enhancer = compose(pure);

const useStyles = makeStyles({
  checkBoxRoot: {
    color: colors.darkishBlue,
    '&:hover': {
      backgroundColor: colors.darkishBlueAlpha,
    },
  },
  checkBoxChecked: {
    color: `${colors.darkishBlue} !important`,
    '&:hover': {
      backgroundColor: `${colors.darkishBlueAlpha} !important`,
    },
  },
  button: {
    padding: '10px',
    fontSize: '1rem',
    width: '6.25rem',
  },
  dataCell: {
    fontSize: '1rem',
    fontWeight: 'normal',
    fontStretch: 'normal',
    fontStyle: 'normal',
    lineHeight: 'normal',
    letterSpacing: 'normal',
    color: '#4f4f4f',
  },
  input: {
    color: colors.darkSlateBlue,
    /* '& .MuiOutlinedInput-input': {
      padding: 0,
    }, */
  },
  label: {
    color: colors.darkSlateBlue,
  },
});

const EmptyFamilleJustificatifRow = (props) => {
  const classes = useStyles();
  const theme = useTheme();
  const xsDown = useMediaQuery(theme.breakpoints.down('xs'));

  const [textFieldValue, setTextFieldValue] = React.useState('');
  const [textFieldError, setTextFieldError] = React.useState(false);

  const handleTextFieldChange = (e) => {
    setTextFieldValue(e.target.value);
  };

  const setNumeroAllocataire = async (event) => {
    const numeroAllocataire = textFieldValue;
    event.preventDefault();

    if (!numeroAllocataire || numeroAllocataire === '') {
      setTextFieldError(true);
      return;
    }

    props.updateNumeroAllocataire(numeroAllocataire);
  };

  const formContent = (
    <>
      <TextField
        variant="outlined"
        placeholder="Numéro d'allocataire"
        size="small"
        style={{
          // width: '10.625rem',
          color: colors.darkSlateBlue,
          marginRight: '10px',
        }}
        InputLabelProps={{
          className: classes.input,
        }}
        InputProps={{
          className: classes.input,
        }}
        type="number"
        onChange={e => handleTextFieldChange(e)}
        error={textFieldError}
        onClick={() => setTextFieldError(false)}
      />

      <Button
        type="submit"
        size="small"
        color={colors.darkSlateBlue}
        className={classes.button}
        disabled={!textFieldValue || textFieldValue === ''}
      >
        Valider
      </Button>
    </>
  );

  return (
    <>
      <TableRow className={classes.dataRow}>
        <TableCell
          className={classes.dataCell}
          style={{
            textAlign: 'center',
          }}
        >
          {props.icon}
        </TableCell>
        <TableCell className={classes.dataCell}>
          <div
            style={{
              display: 'flex',
              flexDirection: 'row',
              alignItems: 'center',
            }}
          >
            <Checkbox
              checked={false}
              classes={{ root: classes.checkBoxRoot, checked: classes.checkBoxChecked }}
              style={{ alignSelf: 'flex-end' }}
              disabled
            />
            {props.label}
          </div>

          {xsDown && (
            <form
              style={{
                display: 'flex',
                flexDirection: 'row',
                alignItems: 'center',
                justifyContent: 'center',
              }}
              onSubmit={setNumeroAllocataire}
            >
              {formContent}
            </form>
          )}
        </TableCell>

        {!xsDown && (
          <TableCell className={classes.dataCell}>
            <form
              style={{
                display: 'flex',
                flexDirection: 'row',
                alignItems: 'center',
                justifyContent: 'flex-end',
              }}
              onSubmit={setNumeroAllocataire}
            >
              {formContent}
            </form>
          </TableCell>
        )}
      </TableRow>
    </>
  );
};

EmptyFamilleJustificatifRow.displayName = 'EmptyFamilleJustificatifRow';

EmptyFamilleJustificatifRow.propTypes = {
  icon: PropTypes.element,
  label: PropTypes.string,
  updateNumeroAllocataire: PropTypes.func.isRequired,
};

EmptyFamilleJustificatifRow.defaultProps = {
  icon: null,
  label: '-',
};

const mapStateToProps = () => {};

const mapDispatchToProps = dispatch => ({
  updateNumeroAllocataire: numeroAllocataire => dispatch({
    type: 'UPDATE_NUMERO_ALLOCATAIRE',
    numeroAllocataire,
  }),
});

export default connect(mapStateToProps, mapDispatchToProps)(enhancer(EmptyFamilleJustificatifRow));
