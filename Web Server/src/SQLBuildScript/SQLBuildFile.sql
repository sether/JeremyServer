CREATE DATABASE JeremyAPIDatabase;
USE JeremyAPIDatabase

CREATE TABLE User(
email VARCHAR(30), 
firstName VARCHAR(20), 
lastName VARCHAR(20), 
password VARCHAR(20), 
creditCardNumber VARCHAR(20), 
apiKey VARCHAR(256), 
PRIMARY KEY (email));


CREATE TABLE APIMethod(
methodType ENUM ('html', 'rest'), 
price ('0.10', '0.20',)
PRIMARY KEY (methodType));


CREATE TABLE APIEvents(
id INT NOT NULL AUTO_INCREMENT,
email VARCHAR(30),
apiKey VARCHAR(256),
logDate DATETIME DEFAULT CURRENT_TIMESTAMP,
fileName VARCHAR(50),
size VARCHAR(20),
converionType ENUM ('html', 'rest'),
methodType ENUM ('html', 'rest'),
description VARCHAR(500),
PRIMARY KEY (id),
FOREIGN KEY (email) REFERENCES User(email),
FOREIGN KEY (methodType) REFERENCES APIMethod(methodType));
;