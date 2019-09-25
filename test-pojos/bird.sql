CREATE TABLE `bird` (
  `birdId` char(36) NOT NULL,
  `name` varchar(128) NOT NULL,
  `age` int(11) DEFAULT NULL,
  `personNumber` int(11) NOT NULL,
  `created` datetime DEFAULT NULL,
  `modified` datetime DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  PRIMARY KEY (`birdId`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
