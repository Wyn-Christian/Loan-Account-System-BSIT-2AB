-- MySQL dump 10.13  Distrib 8.0.29, for Win64 (x86_64)
--
-- Host: localhost    Database: testdb
-- ------------------------------------------------------
-- Server version	8.0.29

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `borrow_tbl`
--

DROP TABLE IF EXISTS `borrow_tbl`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `borrow_tbl` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `user_ID` int NOT NULL,
  `amount` double NOT NULL,
  `term` enum('3','6','9','12') NOT NULL,
  `date_created` datetime NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `ID_UNIQUE` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `borrow_tbl`
--

LOCK TABLES `borrow_tbl` WRITE;
/*!40000 ALTER TABLE `borrow_tbl` DISABLE KEYS */;
INSERT INTO `borrow_tbl` VALUES (1,3,60000,'9','2022-06-23 07:55:43');
/*!40000 ALTER TABLE `borrow_tbl` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pay_loan_tbl`
--

DROP TABLE IF EXISTS `pay_loan_tbl`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pay_loan_tbl` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `user_ID` int NOT NULL,
  `borrow_ID` int NOT NULL,
  `amount` double NOT NULL,
  `date_created` datetime NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `ID_UNIQUE` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pay_loan_tbl`
--

LOCK TABLES `pay_loan_tbl` WRITE;
/*!40000 ALTER TABLE `pay_loan_tbl` DISABLE KEYS */;
INSERT INTO `pay_loan_tbl` VALUES (1,3,1,7000,'2022-06-23 07:56:02'),(2,3,1,7000,'2022-06-23 07:56:15'),(3,3,1,42000,'2022-06-23 07:56:30'),(4,3,1,5500,'2022-06-23 07:56:52');
/*!40000 ALTER TABLE `pay_loan_tbl` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transaction_tbl`
--

DROP TABLE IF EXISTS `transaction_tbl`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transaction_tbl` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `payment_ID` int NOT NULL,
  `payment_type` enum('borrow','pay loan') NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `ID_UNIQUE` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=2002 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transaction_tbl`
--

LOCK TABLES `transaction_tbl` WRITE;
/*!40000 ALTER TABLE `transaction_tbl` DISABLE KEYS */;
INSERT INTO `transaction_tbl` VALUES (2001,0,'borrow');
/*!40000 ALTER TABLE `transaction_tbl` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_status_tbl`
--

DROP TABLE IF EXISTS `user_status_tbl`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_status_tbl` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `user_ID` int NOT NULL,
  `current_borrow_ID` int DEFAULT NULL,
  `current_loan` double DEFAULT NULL,
  `remaining_term` int DEFAULT NULL,
  `total_balance` double DEFAULT NULL,
  `total_paid_amount` double DEFAULT NULL,
  `date_created` datetime NOT NULL,
  `date_updated` datetime NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `ID_UNIQUE` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_status_tbl`
--

LOCK TABLES `user_status_tbl` WRITE;
/*!40000 ALTER TABLE `user_status_tbl` DISABLE KEYS */;
INSERT INTO `user_status_tbl` VALUES (1,3,NULL,61500,0,NULL,61500,'2022-06-20 11:20:03','2022-06-23 07:56:52');
/*!40000 ALTER TABLE `user_status_tbl` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_tbl`
--

DROP TABLE IF EXISTS `user_tbl`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_tbl` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `firstname` text,
  `lastname` text,
  `gender` varchar(1) DEFAULT NULL,
  `birthday` date DEFAULT NULL,
  `username` varchar(45) NOT NULL,
  `password` varchar(45) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `ID_UNIQUE` (`ID`),
  UNIQUE KEY `username_UNIQUE` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_tbl`
--

LOCK TABLES `user_tbl` WRITE;
/*!40000 ALTER TABLE `user_tbl` DISABLE KEYS */;
INSERT INTO `user_tbl` VALUES (1,'john','Doe','m','2000-01-01','jhondoe','1234'),(2,'Jhon','Doe','m','2002-03-03','test','1234'),(3,'Wyn Christian','Rebanal','m','2002-03-03','wynter','Wyn1234!');
/*!40000 ALTER TABLE `user_tbl` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-06-23  7:59:11
