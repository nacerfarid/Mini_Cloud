-- phpMyAdmin SQL Dump
-- version 4.5.4.1deb2ubuntu2
-- http://www.phpmyadmin.net
--
-- Client :  localhost
-- Généré le :  Mer 03 Mai 2017 à 21:29
-- Version du serveur :  5.7.18-0ubuntu0.16.04.1
-- Version de PHP :  7.0.15-0ubuntu0.16.04.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données :  `cloud`
--
CREATE DATABASE IF NOT EXISTS `cloud` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;
USE `cloud`;

-- --------------------------------------------------------

--
-- Structure de la table `AvoirAccesFichier`
--

CREATE TABLE IF NOT EXISTS `AvoirAccesFichier` (
  `ID_Utilisateur` int(11) NOT NULL,
  `ID_Fichier` int(11) NOT NULL,
  `readOnly` tinyint(1) NOT NULL,
  PRIMARY KEY (`ID_Utilisateur`,`ID_Fichier`),
  KEY `FK_AvoirAccesFichier_ID_Fichier` (`ID_Fichier`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Structure de la table `Contient`
--

CREATE TABLE IF NOT EXISTS `Contient` (
  `ID_Conteneur` int(11) NOT NULL,
  `ID_Contenu` int(11) NOT NULL,
  PRIMARY KEY (`ID_Conteneur`,`ID_Contenu`),
  KEY `FK_Contient_ID_Contenu` (`ID_Contenu`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Contenu de la table `Contient`
--

INSERT INTO `Contient` (`ID_Conteneur`, `ID_Contenu`) VALUES
(1, 42),
(1, 43),
(43, 44),
(42, 45),
(1, 46);

-- --------------------------------------------------------

--
-- Structure de la table `EtreAbonne`
--

CREATE TABLE IF NOT EXISTS `EtreAbonne` (
  `ID_Utilisateur` int(11) NOT NULL,
  `ID_Fichier` int(11) NOT NULL,
  PRIMARY KEY (`ID_Utilisateur`,`ID_Fichier`),
  KEY `FK_EtreAbonne_ID_Fichier` (`ID_Fichier`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Structure de la table `Fichier`
--

CREATE TABLE IF NOT EXISTS `Fichier` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `dateCreation` datetime NOT NULL,
  `dateModification` datetime NOT NULL,
  `nomVirtuel` varchar(255) NOT NULL,
  `nomPhysique` varchar(255) DEFAULT NULL,
  `taille` double DEFAULT NULL,
  `ID_Type` int(11) NOT NULL,
  `ID_Proprietaire` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_Fichier_ID_Type` (`ID_Type`),
  KEY `FK_Fichier_ID_Utilisateur` (`ID_Proprietaire`)
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=latin1;

--
-- Contenu de la table `Fichier`
--

INSERT INTO `Fichier` (`ID`, `dateCreation`, `dateModification`, `nomVirtuel`, `nomPhysique`, `taille`, `ID_Type`, `ID_Proprietaire`) VALUES
(1, '2017-04-11 10:00:00', '2017-05-03 09:32:38', 'racine_eloualis', NULL, 1758966, 1, 1),
(2, '2017-04-11 10:00:00', '2017-04-11 10:01:00', 'corbeille_eloualis', NULL, 0, 8, 1),
(3, '2017-04-11 10:00:00', '2017-04-11 10:01:00', 'racine_faridn', NULL, 0, 1, 2),
(4, '2017-04-11 10:00:00', '2017-04-11 10:01:00', 'corbeille_faridn', NULL, 0, 8, 2),
(23, '2017-05-01 11:29:49', '2017-05-01 11:29:49', 'racine_test', NULL, NULL, 1, 6),
(24, '2017-05-01 11:29:49', '2017-05-01 11:29:49', 'corbeille_test', NULL, NULL, 8, 6),
(42, '2017-05-03 21:23:57', '2017-05-03 21:23:57', 'Documents', NULL, 586322, 1, 1),
(43, '2017-05-03 21:24:33', '2017-05-03 21:25:49', 'Web', NULL, 586322, 1, 1),
(44, '2017-05-03 21:24:48', '2017-05-03 21:25:49', 'fichier.html', '99cdea447ed137cfec0a82163bb656a950fc9fc3c6c1ec10f0d5c3d0b72ac9ce', 586322, 6, 1),
(45, '2017-05-03 21:26:28', '2017-05-03 21:26:28', 'fichier.txt', '1bafe7d2e87aa0b1727427f931b26187771234b41b42489215d351bbaca0e605', 586322, 2, 1),
(46, '2017-05-03 21:26:54', '2017-05-03 21:26:54', 'fichier', '4c6a4650c0ff9343b6ce6f1e1dc0c9a94342ca441bb9afdbea88a9a285eebfa3', 586322, 9, 1);

-- --------------------------------------------------------

--
-- Structure de la table `Type`
--

CREATE TABLE IF NOT EXISTS `Type` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `label` varchar(255) DEFAULT NULL,
  `image` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=latin1;

--
-- Contenu de la table `Type`
--

INSERT INTO `Type` (`ID`, `label`, `image`) VALUES
(1, 'Repertoire', 'repertoire.jpg'),
(2, 'Document', 'document.jpg'),
(3, 'Image', 'image.jpg'),
(4, 'Video', 'video.jpg'),
(5, 'Audio', 'audio.jpg'),
(6, 'Web', 'web.jpg'),
(7, 'Application', 'application.jpg'),
(8, 'Corbeille', 'corbeille.jpg'),
(9, 'Fichier', 'fichier.jpg');

-- --------------------------------------------------------

--
-- Structure de la table `Utilisateur`
--

CREATE TABLE IF NOT EXISTS `Utilisateur` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `login` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `nom` varchar(255) DEFAULT NULL,
  `prenom` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `freeze` tinyint(1) DEFAULT NULL,
  `quota` double DEFAULT NULL,
  `lastConnection` datetime NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `login` (`login`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=latin1;

--
-- Contenu de la table `Utilisateur`
--

INSERT INTO `Utilisateur` (`ID`, `login`, `password`, `nom`, `prenom`, `email`, `freeze`, `quota`, `lastConnection`) VALUES
(1, 'eloualis', '6ca13d52ca70c883e0f0bb101e425a89e8624de51db2d2392593af6a84118090', 'El Ouali', 'Sofiane', 'nouvel.email@email.com', 0, 10, '2017-05-03 21:23:51'),
(2, 'faridn', '6ca13d52ca70c883e0f0bb101e425a89e8624de51db2d2392593af6a84118090', 'Nacer', 'Farid', 'nacer.faridi@uga.com', 0, 10, '2017-02-01 00:32:52'),
(6, 'test', '9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08', 'test', 'test', 'test', NULL, 15.2, '2017-02-01 00:32:53');

--
-- Contraintes pour les tables exportées
--

--
-- Contraintes pour la table `AvoirAccesFichier`
--
ALTER TABLE `AvoirAccesFichier`
  ADD CONSTRAINT `FK_AvoirAccesFichier_ID_Fichier` FOREIGN KEY (`ID_Fichier`) REFERENCES `Fichier` (`ID`),
  ADD CONSTRAINT `FK_AvoirAccesFichier_ID_Utilisateur` FOREIGN KEY (`ID_Utilisateur`) REFERENCES `Utilisateur` (`ID`);

--
-- Contraintes pour la table `Contient`
--
ALTER TABLE `Contient`
  ADD CONSTRAINT `FK_Contient_ID_Conteneur` FOREIGN KEY (`ID_Conteneur`) REFERENCES `Fichier` (`ID`),
  ADD CONSTRAINT `FK_Contient_ID_Contenu` FOREIGN KEY (`ID_Contenu`) REFERENCES `Fichier` (`ID`);

--
-- Contraintes pour la table `EtreAbonne`
--
ALTER TABLE `EtreAbonne`
  ADD CONSTRAINT `FK_EtreAbonne_ID_Fichier` FOREIGN KEY (`ID_Fichier`) REFERENCES `Fichier` (`ID`),
  ADD CONSTRAINT `FK_EtreAbonne_ID_Utilisateur` FOREIGN KEY (`ID_Utilisateur`) REFERENCES `Utilisateur` (`ID`);

--
-- Contraintes pour la table `Fichier`
--
ALTER TABLE `Fichier`
  ADD CONSTRAINT `FK_Fichier_ID_Type` FOREIGN KEY (`ID_Type`) REFERENCES `Type` (`ID`),
  ADD CONSTRAINT `FK_Fichier_ID_Utilisateur` FOREIGN KEY (`ID_Proprietaire`) REFERENCES `Utilisateur` (`ID`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
