# vote-api

## Description

Vote-API is a voting system that allow members
to create voting sessions on an agenda (or topic)
and provides the voting result.

## Running the application

To start up the application from the command line, runs:
````
./mvnw spring-boot:run
````
It will starts a PostgreSQL container and apply the necessary database updates before starting up the application.

## Environment variables

To run the application, you should set the environment variables below:
- DB_USER
  - The database username
- DB_PASSWD 
  - The database password
