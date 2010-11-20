DROP DATABASE IF EXISTS `freedb`;
CREATE DATABASE `freedb` /*!40100 DEFAULT CHARACTER SET latin1 */;

DROP TABLE IF EXISTS `freedb`.`discs`;
CREATE TABLE  `freedb`.`discs` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `artist` varchar(16383) DEFAULT NULL,
  `title` varchar(16383) DEFAULT NULL,
  `year` date DEFAULT NULL,
  `genre` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2945591 DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `freedb`.`cddb_ids`;
CREATE TABLE  `freedb`.`cddb_ids` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `disc_id` int(10) unsigned NOT NULL,
  `cddb_id` char(8) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `disc_id` (`disc_id`),
  KEY `cddb_id` (`cddb_id`) USING BTREE,
  CONSTRAINT `cddb_ids_ibfk_1` FOREIGN KEY (`disc_id`) REFERENCES `discs` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2948824 DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `freedb`.`tracks`;
CREATE TABLE  `freedb`.`tracks` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `disc_id` int(10) unsigned NOT NULL,
  `num` tinyint(3) unsigned NOT NULL,
  `title` varchar(16383) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `disc_id` (`disc_id`),
  CONSTRAINT `tracks_ibfk_1` FOREIGN KEY (`disc_id`) REFERENCES `discs` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=40394432 DEFAULT CHARSET=latin1;
