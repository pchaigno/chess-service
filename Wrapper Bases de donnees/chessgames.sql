-- phpMyAdmin SQL Dump
-- version 3.5.1
-- http://www.phpmyadmin.net
--
-- Client: localhost
-- Généré le: Sam 23 Février 2013 à 16:48
-- Version du serveur: 5.5.24-log
-- Version de PHP: 5.3.13

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Base de données: `chessgames`
--

-- --------------------------------------------------------

--
-- Structure de la table `endings`
--

CREATE TABLE IF NOT EXISTS `endings` (
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

CREATE TABLE IF NOT EXISTS `openings` (
  `fen` text NOT NULL,
  `move` text NOT NULL,
  `probatowin` double NOT NULL,
  `probatonull` double NOT NULL,
  `nb` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Contenu de la table `openings`
--

INSERT INTO `openings` (`fen`, `move`, `probatowin`, `probatonull`, `nb`) VALUES
('fendebut', 'e4', 0.4, 0.2, 150453),
('fendebut', 'e5', 0.45, 0.2, 200432);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
