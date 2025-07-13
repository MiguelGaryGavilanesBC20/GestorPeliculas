-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: localhost    Database: gestor_peliculas
-- ------------------------------------------------------
-- Server version	8.0.42

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
-- Table structure for table `directores`
--

DROP TABLE IF EXISTS `directores`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `directores` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `foto_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `directores`
--

LOCK TABLES `directores` WRITE;
/*!40000 ALTER TABLE `directores` DISABLE KEYS */;
INSERT INTO `directores` VALUES (5,'James Cameron','C:\\Users\\Legen\\OneDrive\\Escritorio\\APPMOVIE\\Director\\james-cameron-directing-his-first-non-avatar-movie-since-199_5v1u.1200.webp'),(6,'James Gunn','C:\\Users\\Legen\\OneDrive\\Escritorio\\APPMOVIE\\Director\\James Gunn.jpg'),(7,'Christopher Nolan','C:\\Users\\Legen\\OneDrive\\Escritorio\\APPMOVIE\\Director\\cristhpher nolan.jpg');
/*!40000 ALTER TABLE `directores` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `peliculas`
--

DROP TABLE IF EXISTS `peliculas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `peliculas` (
  `id` int NOT NULL AUTO_INCREMENT,
  `titulo` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `descripcion` text COLLATE utf8mb4_unicode_ci,
  `portada_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ranking` int DEFAULT NULL,
  `director_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `director_id` (`director_id`),
  CONSTRAINT `peliculas_ibfk_1` FOREIGN KEY (`director_id`) REFERENCES `directores` (`id`) ON DELETE SET NULL,
  CONSTRAINT `peliculas_chk_1` CHECK ((`ranking` between 0 and 100))
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `peliculas`
--

LOCK TABLES `peliculas` WRITE;
/*!40000 ALTER TABLE `peliculas` DISABLE KEYS */;
INSERT INTO `peliculas` VALUES (5,'Avatar','Entramos en el mundo Avatar de la mano de Jake Sully, un ex-Marine en silla de ruedas, que ha sido reclutado para viajar a Pandora, donde existe un mineral raro y muy preciado que puede solucionar la crisis energética existente en la Tierra.','C:\\Users\\Legen\\OneDrive\\Escritorio\\APPMOVIE\\Peliculas\\Avatar 1.jpg',79,5),(6,'Superman(2025)','Superman es una película de superhéroes estadounidense basada en DC Comics y protagonizada por el personaje del mismo nombre. Producida por DC Studios y distribuida por Warner Bros. Pictures, es la primera película del Universo DC y un reinicio de la serie de películas Superman.','C:\\Users\\Legen\\OneDrive\\Escritorio\\APPMOVIE\\Peliculas\\superman2025.jpg',82,6);
/*!40000 ALTER TABLE `peliculas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `resenas`
--

DROP TABLE IF EXISTS `resenas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `resenas` (
  `id` int NOT NULL AUTO_INCREMENT,
  `pelicula_id` int DEFAULT NULL,
  `usuario_id` int DEFAULT NULL,
  `texto_resena` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `fecha_resena` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `pelicula_id` (`pelicula_id`),
  KEY `usuario_id` (`usuario_id`),
  CONSTRAINT `resenas_ibfk_1` FOREIGN KEY (`pelicula_id`) REFERENCES `peliculas` (`id`) ON DELETE CASCADE,
  CONSTRAINT `resenas_ibfk_2` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `resenas`
--

LOCK TABLES `resenas` WRITE;
/*!40000 ALTER TABLE `resenas` DISABLE KEYS */;
/*!40000 ALTER TABLE `resenas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuarios`
--

DROP TABLE IF EXISTS `usuarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuarios` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fecha_registro` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuarios`
--

LOCK TABLES `usuarios` WRITE;
/*!40000 ALTER TABLE `usuarios` DISABLE KEYS */;
INSERT INTO `usuarios` VALUES (1,'LoganDiaz','Grande12@',NULL,'2025-07-12 17:56:05'),(2,'MiguelonTT','Grande12@',NULL,'2025-07-12 18:48:25'),(3,'StefaniaLimongi','Pepito12345',NULL,'2025-07-13 03:13:02'),(4,'Juanito Hernandez','12345',NULL,'2025-07-13 16:26:48'),(5,'MiguelLoyola','Grande12\"',NULL,'2025-07-13 16:41:51');
/*!40000 ALTER TABLE `usuarios` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-07-13 12:02:42
