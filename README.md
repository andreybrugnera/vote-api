# vote-api

## Description

Vote-API is a voting system that allow members
to create voting sessions on an agenda (or topic)
and provides the voting result.

A scheduler computes voting session results every 10 minutes.
The result is stored into Agenda status, (ACCEPTED or REJECTED).

## Entities

### Member

A user member that can create Agendas, Voting Sessions and Vote for specific Agenda in a Voting Session.
Only registered members with a valid document can submit a vote.

### Agenda

An Agenda (or Topic) is a list of items that must be approved in a Voting Session.

### Voting Session

A Voting Session for an Agenda must be created so that members can submit votes to approve or reject it.
Members can submit votes only while it is open (openAt > now < closeAt).

### Vote

A vote is a personal opinion of a member regarding the voting session's agenda.
It can be YES (approve) or NO (reject).

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

## API Documentation

The API documentation is available at http://localhost:8080/swagger-ui/index.html
