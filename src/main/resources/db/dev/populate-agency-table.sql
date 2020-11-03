--POPULATES AGENCY TABLE
INSERT INTO AGENCY VALUES (1, 'SSHRC', 'CRSN', 'Social Sciences and Humanities Research Council', 'Conseil de Recherches en Sciences Humaines');
INSERT INTO AGENCY VALUES (2, 'NSERC', 'CRSNG', 'Natural Sciences and Engineering Research Council', 'Conseil de Recherches en Sciences Naturelles et en Génie');
INSERT INTO AGENCY VALUES (3, 'CIHR', 'IRSC', 'Canadian Institutes of Health Research', 'Instituts de Recherche en Santé');

--POPULATES AGENCY AUDITING TABLE AND INSERTS THE CORRESPONDING METADATA INTO REVISION_INFO TABLE
INSERT INTO REVISION_INFO VALUES (2, CONVERT(VARCHAR(19), SYSDATETIME(), 120), 'app-admin');
INSERT INTO AGENCY_HISTORY VALUES (1, 2, 0, 'SSHRC', 'CRSN', 'Social Sciences and Humanities Research Council', 'Conseil de Recherches en Sciences Humaines');
INSERT INTO AGENCY_HISTORY VALUES (2, 2, 0, 'NSERC', 'CRSNG', 'Natural Sciences and Engineering Research Council', 'Conseil de Recherches en Sciences Naturelles et en Génie');
INSERT INTO AGENCY_HISTORY VALUES (3, 2, 0, 'CIHR', 'IRSC', 'Canadian Institutes of Health Research', 'Instituts de Recherche en Santé');
