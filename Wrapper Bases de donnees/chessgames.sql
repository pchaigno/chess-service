-- phpMyAdmin SQL Dump
-- version 3.5.1
-- http://www.phpmyadmin.net
--
-- Client: localhost
-- Généré le: Jeu 21 Février 2013 à 17:15
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
  `fen` text NOT NULL,
  `move` text NOT NULL,
  `probatowin` double NOT NULL,
  `probatonull` double NOT NULL,
  `nb` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Structure de la table `openings`
--

CREATE TABLE `openings` (
  `fen` text NOT NULL,
  `move` text NOT NULL,
  `probatowin` double NOT NULL,
  `probatonull` double NOT NULL,
  `nb` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
