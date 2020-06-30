--CREATE EXTENSION IF NOT EXISTS "uuid-ossp"; -- Attention n'est plus maintenu
CREATE EXTENSION IF NOT EXISTS "pgcrypto";
CREATE EXTENSION IF NOT EXISTS "unaccent";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";

CREATE SEQUENCE IF NOT EXISTS partenaire_id_seq INCREMENT 50 START 1;
CREATE SEQUENCE IF NOT EXISTS credential_id_seq INCREMENT 50 START 1;

CREATE TABLE IF NOT EXISTS partenaire (
    id INTEGER NOT NULL DEFAULT nextval('partenaire_id_seq'),
    libelle_court VARCHAR NOT NULL,
    libelle_long VARCHAR NOT NULL,
    siret VARCHAR(14) NOT NULL UNIQUE,
    logo OID NOT NULL,
    date_creation TIMESTAMP,
    date_maj TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT part_siret_uq UNIQUE (siret)
);

CREATE TABLE IF NOT EXISTS credential (
    id INTEGER NOT NULL DEFAULT nextval('credential_id_seq'),
    partenaire_id integer REFERENCES partenaire(id),
    client_id VARCHAR NOT NULL,
    client_secret VARCHAR NOT NULL,
    date_creation TIMESTAMP,
    date_maj TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT credential_uq UNIQUE (partenaire_id,client_id)
);

ALTER SEQUENCE IF EXISTS partenaire_id_seq OWNED BY partenaire.id;
ALTER SEQUENCE IF EXISTS credential_id_seq OWNED BY credential.id;