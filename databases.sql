BEGIN;

-- --------------------------------------------------------
-- Structure de la table "conjoint"
-- --------------------------------------------------------

DROP TABLE IF EXISTS "conjoint" CASCADE;
CREATE TABLE "conjoint" (
  "numPension" varchar(10) NOT NULL,
  "NomConjoint" varchar(50) NOT NULL,
  "PrenomConjoint" varchar(50) DEFAULT NULL,
  "montant" integer NOT NULL,
  CONSTRAINT "PK_conjoint" PRIMARY KEY ("numPension")
);

-- --------------------------------------------------------
-- Structure de la table "personne"
-- --------------------------------------------------------

DROP TABLE IF EXISTS "personne" CASCADE;
CREATE TABLE "personne" (
  "IM" varchar(10) NOT NULL,
  "Nom" varchar(50) NOT NULL,
  "Prenom" varchar(50) DEFAULT NULL,
  "datenais" date NOT NULL,
  "diplome" varchar(50) NOT NULL,
  "Contact" varchar(10) NOT NULL,
  "statut" boolean NOT NULL,
  "situation" varchar(20) NOT NULL,
  "NomConjoint" varchar(50) NOT NULL,
  "PrenomConjoint" varchar(50) DEFAULT NULL,
  CONSTRAINT "PK_personne" PRIMARY KEY ("IM")
);

-- --------------------------------------------------------
-- Structure de la table "tarif"
-- --------------------------------------------------------

DROP TABLE IF EXISTS "tarif" CASCADE;
CREATE TABLE "tarif" (
  "num_tarif" varchar(10) NOT NULL,
  "diplome" varchar(50) NOT NULL,
  "categorie" varchar(50) NOT NULL,
  "montant" integer NOT NULL,
  CONSTRAINT "PK_tarif" PRIMARY KEY ("num_tarif")
);

-- --------------------------------------------------------
-- Structure de la table "payer" (avec Clés Étrangères)
-- --------------------------------------------------------

DROP TABLE IF EXISTS "payer" CASCADE;
CREATE TABLE "payer" (
  "IM" varchar(10) NOT NULL,
  "num_tarif" varchar(10) NOT NULL,
  "datepayer" date DEFAULT NULL,
  CONSTRAINT "PK_payer" PRIMARY KEY ("IM", "num_tarif"),
  CONSTRAINT "FK_payer_personne" FOREIGN KEY ("IM") REFERENCES "personne" ("IM"),
  CONSTRAINT "FK_payer_tarif" FOREIGN KEY ("num_tarif") REFERENCES "tarif" ("num_tarif")
);

CREATE INDEX "IDX_payer_num_tarif" ON "payer" ("num_tarif");

COMMIT;