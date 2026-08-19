BEGIN;

-- 1. Insertion dans la table TARIF
INSERT INTO tarif (num_tarif, diplome, categorie, montant) VALUES
('TAR01', 'Baccalauréat', 'Catégorie B', 150000),
('TAR02', 'Licence', 'Catégorie A2', 250000),
('TAR03', 'Master / Ingénieur', 'Catégorie A1', 350000),
('TAR04', 'Doctorat', 'Catégorie Hors Echelle', 500000);

-- 2. Insertion dans la table PERSONNE
INSERT INTO personne (im, nom, prenom, datenais, diplome, contact, statut, situation, nomconjoint, prenomconjoint) VALUES
('101001', 'rakoto', 'jean claudel', '1965-03-15', 'Licence', '0341234567', true, 'Marié(e)', 'rasoa', 'marie'),
('101002', 'randria', 'paul', '1958-11-20', 'Master / Ingénieur', '0329876543', false, 'Marié(e)', 'razafy', 'jeanne'),
('101003', 'rasolo', 'henriette', '1970-07-08', 'Baccalauréat', '0331122334', true, 'Célibataire', '', ''),
('101004', 'ramano', 'michel', '1960-01-30', 'Doctorat', '0345566778', true, 'Veuf/Veuve', '', '');

-- 3. Insertion dans la table CONJOINT
-- (Données associées aux conjoints des retraités)
INSERT INTO conjoint (numpension, nomconjoint, prenomconjoint, montant) VALUES
('PEN001', 'rasoa', 'marie', 75000),
('PEN002', 'razafy', 'jeanne', 125000);

-- 4. Insertion dans la table PAYER (Chaque tarif est unique pour un IM)
INSERT INTO payer (im, num_tarif, datepayer) VALUES
('101001', 'TAR02', '2026-01-05'),
('101002', 'TAR03', '2026-01-05'),
('101003', 'TAR01', '2026-02-05'),
('101004', 'TAR04', '2026-02-05');
COMMIT;