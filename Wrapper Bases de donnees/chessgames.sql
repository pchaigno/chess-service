-- phpMyAdmin SQL Dump
-- version 3.5.1
-- http://www.phpmyadmin.net
--
-- Client: localhost
-- Généré le: Sam 27 Avril 2013 à 15:05
-- Version du serveur: 5.5.25
-- Version de PHP: 5.4.4

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

--
-- Base de données: `chessgames`
--

-- --------------------------------------------------------

--
-- Structure de la table `endings`
--

CREATE TABLE `endings` (
  `fen` varchar(90) NOT NULL,
  `move` varchar(10) NOT NULL,
  `probatowin` double NOT NULL,
  `probatonull` double NOT NULL,
  `nb` int(11) NOT NULL,
  PRIMARY KEY (`fen`,`move`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Structure de la table `openings`
--

CREATE TABLE `openings` (
  `fen` varchar(90) NOT NULL,
  `move` varchar(10) NOT NULL,
  `probatowin` double NOT NULL,
  `probatonull` double NOT NULL,
  `nb` int(11) NOT NULL,
  PRIMARY KEY (`fen`,`move`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Contenu de la table `openings`
--