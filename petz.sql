-- MySQL dump 10.13  Distrib 5.7.28, for osx10.13 (x86_64)
--
-- Host: localhost    Database: petz
-- ------------------------------------------------------
-- Server version	5.7.28-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `area`
--

DROP TABLE IF EXISTS `area`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `area` (
  `area` varchar(10) NOT NULL,
  `name` varchar(128) NOT NULL,
  PRIMARY KEY (`area`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `area`
--

LOCK TABLES `area` WRITE;
/*!40000 ALTER TABLE `area` DISABLE KEYS */;
/*!40000 ALTER TABLE `area` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bird`
--

DROP TABLE IF EXISTS `bird`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bird` (
  `birdId` char(36) NOT NULL,
  `name` varchar(128) NOT NULL,
  `age` int(11) DEFAULT NULL,
  `kind` enum('CAGED','FREERANGE') NOT NULL,
  `singTime` varchar(32) NOT NULL,
  `personNumber` int(11) NOT NULL,
  `created` datetime DEFAULT NULL,
  `modified` datetime DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  PRIMARY KEY (`birdId`),
  UNIQUE KEY `idx` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bird`
--

LOCK TABLES `bird` WRITE;
/*!40000 ALTER TABLE `bird` DISABLE KEYS */;
INSERT INTO `bird` VALUES ('7fbab277-7884-47c0-ae97-dc6fe21ee832','Bigger Bird',2,'FREERANGE','PT12M',1,'2019-12-29 15:50:05','2019-12-29 15:50:05',1),('9f4c2f9a-dea3-4d95-aa85-6eb3c2f55432','Big Bird',2,'FREERANGE','PT12M',1,'2019-12-29 15:31:53','2019-12-29 15:31:53',1),('b26ecf0b-fba9-4707-879c-16c06dfa5067','Bam bam',200,'FREERANGE','PT15M',1,'2019-12-29 14:22:37','2019-12-29 14:22:37',1),('ef358cd7-a6f4-4771-b38a-807cf87ad1aa','Mofo big bird yo',2,'FREERANGE','PT12M',1,'2019-12-29 15:54:36','2019-12-29 15:54:36',1);
/*!40000 ALTER TABLE `bird` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cat`
--

DROP TABLE IF EXISTS `cat`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cat` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL,
  `age` int(11) DEFAULT NULL,
  `personNumber` int(11) NOT NULL,
  `created` datetime DEFAULT NULL,
  `modified` datetime DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `type` enum('INDOOR','OUTDOOR') NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cat`
--

LOCK TABLES `cat` WRITE;
/*!40000 ALTER TABLE `cat` DISABLE KEYS */;
INSERT INTO `cat` VALUES (1,'Garfied',11,2,'2019-06-01 12:53:32','2019-06-01 12:53:32',1,'INDOOR'),(2,'Bob',3,2,'2019-06-01 12:53:44','2019-06-01 12:53:44',1,'INDOOR'),(3,'Top',4,3,'2019-06-01 12:53:57','2019-06-01 12:53:57',1,'INDOOR'),(5,'Cooler Cat',1,1,'2019-06-08 00:00:00','2019-06-08 00:00:00',1,'INDOOR'),(6,'Super Cat',1,1,'2019-06-18 00:00:00','2019-06-18 00:00:00',1,'INDOOR'),(7,'Super Cat',1,1,'2019-06-25 18:22:34','2019-06-25 18:22:34',1,'INDOOR'),(8,'Super Cat',1,1,NULL,NULL,1,'INDOOR'),(9,'Supreme Cat',1,1,NULL,NULL,1,'INDOOR'),(10,'Supreme Cat',1,1,'2020-01-14 21:01:38','2020-01-14 21:01:38',1,'INDOOR'),(11,'Supreme Cat',1,1,'2020-01-14 21:03:52','2020-01-14 21:03:52',1,'INDOOR');
/*!40000 ALTER TABLE `cat` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `company`
--

DROP TABLE IF EXISTS `company`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `company` (
  `companyNumber` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL,
  `created` datetime NOT NULL,
  `modified` datetime NOT NULL,
  `version` int(11) NOT NULL DEFAULT '1',
  PRIMARY KEY (`companyNumber`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `company`
--

LOCK TABLES `company` WRITE;
/*!40000 ALTER TABLE `company` DISABLE KEYS */;
INSERT INTO `company` VALUES (1,'Acme Corp','2019-05-11 14:24:11','2019-05-11 15:51:27',1),(2,'Foobar Inc','2019-05-11 16:28:02','2019-05-11 16:28:02',1),(3,'Stephan\'s Security Services','2019-05-12 00:00:00','2019-05-12 00:00:00',1);
/*!40000 ALTER TABLE `company` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dog`
--

DROP TABLE IF EXISTS `dog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dog` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL,
  `age` int(11) DEFAULT NULL,
  `personNumber` int(11) NOT NULL,
  `created` datetime DEFAULT NULL,
  `modified` datetime DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dog`
--

LOCK TABLES `dog` WRITE;
/*!40000 ALTER TABLE `dog` DISABLE KEYS */;
INSERT INTO `dog` VALUES (1,'Zeus',3,1,'2019-06-01 12:54:11','2019-06-01 12:54:11',1),(2,'Apollo',3,2,'2019-06-01 12:54:15','2019-06-01 12:54:15',1),(3,'Mad',5,3,'2019-06-01 12:54:23','2019-06-01 12:54:23',1);
/*!40000 ALTER TABLE `dog` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `person`
--

DROP TABLE IF EXISTS `person`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `person` (
  `personNumber` int(11) NOT NULL,
  `firstName` varchar(32) NOT NULL,
  `lastName` varchar(32) NOT NULL,
  `emailAddress` varchar(64) NOT NULL,
  `companyNumber` int(11) NOT NULL,
  `sex` enum('MALE','FEMALE','CONFUSED') DEFAULT NULL,
  `created` datetime NOT NULL,
  `modified` datetime NOT NULL,
  `version` int(11) NOT NULL DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `person`
--

LOCK TABLES `person` WRITE;
/*!40000 ALTER TABLE `person` DISABLE KEYS */;
INSERT INTO `person` VALUES (1,'Gideon','Le Grange','gideon@legrange.me',1,'MALE','2019-05-11 14:24:07','2019-05-11 15:51:31',1),(2,'John','Smith','john@handjobs.net',1,'CONFUSED','2019-05-11 14:24:07','2019-05-11 15:51:31',1),(3,'Bob','Hopeless','nohope@gmail.com',2,'MALE','2019-05-11 16:29:00','2019-05-11 16:29:00',1),(4,'Sally','Sanders','s.sanders@sallysanders.me',2,'FEMALE','2019-05-11 16:29:33','2019-05-11 16:29:33',1),(5,'Boris','Johnson','bj@commons.gov.uk',3,'MALE','2019-06-01 09:04:46','2019-06-01 09:04:46',1);
/*!40000 ALTER TABLE `person` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-01-18 12:06:27
