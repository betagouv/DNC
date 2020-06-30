import { CardContent, Grid, IconButton } from '@material-ui/core';

import Button from 'react/components/buttons/button/Button';
import Card from 'react/components/card/Card';
import DataList from 'react/components/list/DataList';
import PropTypes from 'prop-types';
import React from 'react';
import arrow from 'assets/images/Arrow-down.svg';
import colors from 'style/config.variables.scss';
import styles from './InformationCard.module.scss';

const style = {
  img: {
    height: '5rem',
    display: 'block',
    marginLeft: 'auto',
    marginRight: 'auto',
    marginBottom: '1rem',
  },
  card: {
    display: 'flex',
    flexDirection: 'column',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  button: {
    padding: '0.625rem',
    marginBottom: '1.5rem',
    marginLeft: '2rem',
    marginRight: '2rem',
    marginTop: '1rem',
    maxWidth: '21.25rem',
  },
  ICbutton: {
    // padding: '0.625rem',
    marginBottom: '1.5rem',
    marginLeft: '-0.5rem',
    marginRight: '2rem',
    marginTop: '1rem',
    maxWidth: '21.25rem',
    transform: 'rotate(180deg)',
  },
  buttonVP: {
    padding: '0.625rem',
    marginBottom: '1.5rem',
    marginLeft: '2rem',
    marginRight: '2rem',
    marginTop: '-2rem',
    maxWidth: '21.25rem',
  },
  content: {
    width: '100%',
    height: '100%',
  },
};

export default class InformationCard extends React.Component {
  state = {
    voirPlus: true,
  }

  /**
   * Rend l'UI.
   *
   * @returns {*} - UI.
   */
  render() {
    let actionButton = null;
    if (this.props.seeMore.length !== 0) {
      actionButton = (
        <>
          {this.state.voirPlus
            ? (
              <Button
                size="small"
                color={colors.darkSlateBlue}
                startIcon={<img src={arrow} className={styles.fcIcon} alt="" />}
                className={style.button}
                style={style.button}
                onClick={() => {
                  this.setState(state => ({ voirPlus: !state.voirPlus }));
                }}
              >
                Voir Plus
              </Button>
            )
            : (
              <>
                <div style={{ flex: 1, marginTop: '-2rem' }}>
                  <Grid container>
                    <Grid item style={{ flex: 4 }}>
                      <Button
                        size="small"
                        color={colors.darkSlateBlue}
                        className={style.button}
                        style={style.button}
                        onClick={() => {
                          window.location.href = this.props.actionButtonTarget;
                        }}
                      >
                        Accéder à mon espace personnel
                      </Button>
                    </Grid>
                    <Grid item style={{ flex: 1 }}>
                      <IconButton
                        size="small"
                        color={colors.darkSlateBlue}
                        className={style.ICbutton}
                        style={style.ICbutton}
                        onClick={() => {
                          this.setState(state => ({ voirPlus: !state.voirPlus }));
                        }}
                      >
                        <img src={arrow} className={styles.fcIcon} alt="" />
                      </IconButton>
                    </Grid>
                  </Grid>
                </div>
              </>
            )}

        </>
      );
    } else if (this.props.actionButtonTarget) {
      actionButton = (
        <div style={{ flex: 1, marginTop: '-2rem' }}>
          <Grid container>
            <Grid item style={{ flex: 1 }} />
            <Grid item style={{ flex: 15 }}>
              <Button
                size="small"
                color={colors.darkSlateBlue}
                className={style.button}
                style={style.button}
                onClick={() => {
                  window.location.href = this.props.actionButtonTarget;
                }}
              >
                Accéder à mon espace personnel
              </Button>
            </Grid>
            <Grid item style={{ flex: 1 }} />
          </Grid>
        </div>
      );
    }

    return (
      <>
        <Grid item xs style={{ minWidth: this.props.minWidth, padding: '1.5rem' }}>
          <Card className={style.card} style={style.card}>
            <CardContent className={styles.content} style={style.content}>
              {this.props.logoSrc
                ? (
                  <img
                    src={this.props.logoSrc}
                    alt={this.props.source}
                    className={style.img}
                    style={style.img}
                  />
                )
                : <></>}

              <p className={styles.title}>{this.props.title}</p>
              {this.props.source
                ? <p className={styles.source}>Source : {this.props.source}</p>
                : <></>}

              {this.props.children}
              {this.props.seeMore.length !== 0 && !this.state.voirPlus
                ? (
                  <DataList
                    labelWidth="40%"
                    valueWidth="60%"
                    data={this.props.seeMore}
                  />
                )
                : <></>}
            </CardContent>
            {actionButton}
          </Card>
        </Grid>
      </>
    );
  }
}

InformationCard.propTypes = {
  actionButtonTarget: PropTypes.string,
  logoSrc: PropTypes.string,
  source: PropTypes.string,
  title: PropTypes.string,
  children: PropTypes.node.isRequired,
  minWidth: PropTypes.number,
  seeMore: PropTypes.arrayOf(
    PropTypes.string,
    PropTypes.string,
  ),
};

InformationCard.defaultProps = {
  logoSrc: '',
  title: '',
  source: '',
  actionButtonTarget: '',
  minWidth: '25rem',
  seeMore: [],
};
