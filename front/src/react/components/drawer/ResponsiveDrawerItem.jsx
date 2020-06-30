import React from 'react';
import PropTypes from 'prop-types';
import ListItem from '@material-ui/core/ListItem';
import { makeStyles } from '@material-ui/core/styles';
import { compose, pure } from 'recompose';
import { connect } from 'react-redux';
import colors from 'style/config.variables.scss';

const enhancer = compose(pure);

const useStyles = makeStyles(() => ({
  listItem: props => ({
    display: 'flex',
    flexDirection: 'column',
    justifyContent: 'center',
    alignItems: 'center',
    height: '15vh',
    color: props.selectedKey === props.index ? colors.darkishBlue : 'black',
    // eslint-disable-next-line no-shadow
    '&:hover': props => ({
      backgroundColor: props.selectedKey === props.index ? colors.darkishBlueAlpha : '',
    }),
    // eslint-disable-next-line no-shadow
    '&:after': props => (props.selectedKey === props.index
      ? {
        content: '""',
        height: '100%',
        background: colors.darkishBlue,
        width: '5px',
        position: 'absolute',
        right: 0,
        borderRadius: '2px',
      }
      : ''),
  }),
  paper: {
    borderRadius: '20px',
  },
  drawerItemLabel: {
    fontWeight: 'bold',
    lineHeight: 1,
    textAlign: 'center',
    margin: 0,
    fontSize: '1rem',
  },
  drawerItemImg: {
    height: '2.5rem',
    marginBottom: '8px',
    maxHeight: '30px',
  },
}));

const ResponsiveDrawerItem = (props) => {
  const classes = useStyles(props);

  let { svg } = props;

  if (props.selectedKey !== props.index) {
    // On change la couleur du SVG, Bleu vers Noir
    svg = props.svg.replace(/%23003189/g, '%23000000');
  }

  const onClick = (e, itemClickHandler) => {
    props.updateSelectedDrawerItem(props.index);

    if (itemClickHandler) {
      itemClickHandler();
    }
  };

  return (
    <ListItem button className={classes.listItem} onClick={e => onClick(e, props.onClick)}>
      <img src={svg} className={classes.drawerItemImg} alt="" />
      <p className={classes.drawerItemLabel}> {props.label}</p>
    </ListItem>
  );
};

ResponsiveDrawerItem.propTypes = {
  label: PropTypes.string,
  svg: PropTypes.string,
  onClick: PropTypes.func,
  selectedKey: PropTypes.number.isRequired,
  index: PropTypes.number.isRequired,
  updateSelectedDrawerItem: PropTypes.func.isRequired,
};

ResponsiveDrawerItem.defaultProps = {
  label: '',
  svg: '',
  onClick: null,
};

const mapStateToProps = state => ({
  selectedKey: state.navigation.selectedKey,
});

const mapDispatchToProps = dispatch => ({
  updateSelectedDrawerItem: selectedKey => dispatch({
    type: 'UPDATE_SELECTED_DRAWER_ITEM',
    selectedKey,
  }),
});

export default connect(mapStateToProps, mapDispatchToProps)(enhancer(ResponsiveDrawerItem));
