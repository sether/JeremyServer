CREATE DATABASE JeremyAPIDatabase;
USE JeremyAPIDatabase;

CREATE TABLE User(
email VARCHAR(70), 
firstName VARCHAR(30), 
lastName VARCHAR(30), 
password VARCHAR(30), 
salt VARCHAR(30),
creditCardNumber VARCHAR(20), 
publicApiKey VARCHAR(1024), 
privateApiKey VARCHAR(1024),
PRIMARY KEY (email));

CREATE TABLE APIEvents(
id INT NOT NULL AUTO_INCREMENT,
email VARCHAR(70),
publicApiKey VARCHAR(1024),
logDate DATETIME DEFAULT CURRENT_TIMESTAMP,
fileName VARCHAR(50),
size VARCHAR(30),
methodType ENUM ('HTML', 'REST'),
description VARCHAR(1000),
PRIMARY KEY (id),
FOREIGN KEY (email) REFERENCES User(email));