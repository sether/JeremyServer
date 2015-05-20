CREATE DATABASE JeremyAPIDatabase;
USE JeremyAPIDatabase;

CREATE TABLE User(
email VARCHAR(50), 
firstName VARCHAR(20), 
lastName VARCHAR(20), 
password VARCHAR(20), 
creditCardNumber VARCHAR(20), 
publicApiKey VARCHAR(512), 
privateApiKey VARCHAR(512),
PRIMARY KEY (email));


CREATE TABLE APIEvents(
id INT NOT NULL AUTO_INCREMENT,
email VARCHAR(50),
publicApiKey VARCHAR(512),
logDate DATETIME DEFAULT CURRENT_TIMESTAMP,
fileName VARCHAR(50),
size VARCHAR(20),
methodType ENUM ('HTML', 'REST'),
description VARCHAR(500),
PRIMARY KEY (id),
FOREIGN KEY (email) REFERENCES User(email));