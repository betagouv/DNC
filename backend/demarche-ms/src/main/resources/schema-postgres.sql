--CREATE EXTENSION IF NOT EXISTS "uuid-ossp"; -- Attention n'est plus maintenu
CREATE EXTENSION IF NOT EXISTS "pgcrypto";
CREATE EXTENSION IF NOT EXISTS "unaccent";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";

CREATE SEQUENCE IF NOT EXISTS demarche_id_seq INCREMENT 50 START 1;

CREATE TABLE IF NOT EXISTS demarche (
    id INTEGER NOT NULL DEFAULT nextval('demarche_id_seq'),
    version INTEGER,
    libelle VARCHAR NOT NULL,
    statut VARCHAR NOT NULL,
    commentaires VARCHAR NOT NULL,
    code_demarche VARCHAR NOT NULL,
    id_usager VARCHAR NOT NULL,
    id_demarche VARCHAR NOT NULL,
    siret_partenaire VARCHAR(14) NOT NULL UNIQUE,
    logo OID NOT NULL,
    date_debut TIMESTAMP,
    date_fin TIMESTAMP,
    date_maj TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT demarche_demarche_id_uq UNIQUE (id_demarche,id_usager,siret_partenaire)
);

ALTER SEQUENCE IF EXISTS demarche_id_seq OWNED BY demarche.id;