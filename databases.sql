BEGIN;

-- --------------------------------------------------------
-- Structure de la table "conjoint"
-- --------------------------------------------------------

DROP TABLE IF EXISTS conjoint CASCADE;
CREATE TABLE conjoint (
  numpension varchar(10) NOT NULL,
  nomconjoint varchar(50) NOT NULL,
  prenomconjoint varchar(50) DEFAULT NULL,
  montant integer NOT NULL,
  CONSTRAINT pk_conjoint PRIMARY KEY (numpension)
);

-- --------------------------------------------------------
-- Structure de la table "personne"
-- --------------------------------------------------------

DROP TABLE IF EXISTS personne CASCADE;
CREATE TABLE personne (
  im varchar(10) NOT NULL,
  nom varchar(50) NOT NULL,
  prenom varchar(50) DEFAULT NULL,
  datenais date NOT NULL,
  diplome varchar(50) NOT NULL,
  contact varchar(10) NOT NULL,
  statut boolean NOT NULL,
  situation varchar(20) NOT NULL,
  nomconjoint varchar(50) NOT NULL,
  prenomconjoint varchar(50) DEFAULT NULL,
  CONSTRAINT pk_personne PRIMARY KEY (im)
);

-- --------------------------------------------------------
-- Structure de la table "tarif"
-- --------------------------------------------------------

DROP TABLE IF EXISTS tarif CASCADE;
CREATE TABLE tarif (
  num_tarif varchar(10) NOT NULL,
  diplome varchar(50) NOT NULL,
  categorie varchar(50) NOT NULL,
  montant integer NOT NULL,
  CONSTRAINT pk_tarif PRIMARY KEY (num_tarif)
);

-- --------------------------------------------------------
-- Structure de la table "payer"
-- --------------------------------------------------------

DROP TABLE IF EXISTS payer CASCADE;
CREATE TABLE payer (
  im varchar(10) NOT NULL,
  num_tarif varchar(10) NOT NULL,
  datepayer date DEFAULT NULL,
  CONSTRAINT pk_payer PRIMARY KEY (im, num_tarif),
  CONSTRAINT fk_payer_personne FOREIGN KEY (im) REFERENCES personne (im),
  CONSTRAINT fk_payer_tarif FOREIGN KEY (num_tarif) REFERENCES tarif (num_tarif)
);

CREATE INDEX idx_payer_num_tarif ON payer (num_tarif);

COMMIT;