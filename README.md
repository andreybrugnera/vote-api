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

## Environment variables

To run the application, you should set the environment variables below:

- DB_USER: Database username
- DB_PASSWD: Database password

## Running the application

First start a PostgreSQL container, then run the application from the command line:
````
$ docker compose up -d postgres
$ ./mvnw spring-boot:run
````
Flyway applies the necessary database updates during start up.

## Docker

### Running the full stack with Docker Compose

The `compose.yaml` file defines both the PostgreSQL database and the application. With the
`DB_USER` / `DB_PASSWD` environment variables set, start everything with a single command:
````
docker compose up
````
Compose builds the application image, waits for Postgres to become healthy, then starts the
app on http://localhost:8080. Add `-d` to run in the background and `--build` to force an
image rebuild after code changes.

### Creating the Docker image

The image uses a multi-stage build: the jar is compiled with `azul/zulu-openjdk:25-latest`
and runs on the lighter `azul/zulu-openjdk:25-jre-latest`, so no local build is required.
To build the image on its own, run:
````
$ docker build -t vote-api .
````

### Running the container with external database

To run just the container with a external PostgreSQL instance you manage, provide the datasource
URL and credentials as environment variables (`SPRING_DATASOURCE_URL` overrides the default
that points to `localhost`):
````
$ docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://<db-host>:5432/vote-db \
  -e DB_USER=$DB_USER -e DB_PASSWD=$DB_PASSWD \
  vote-api
````

## API Documentation

The API documentation is available at http://localhost:8080/swagger-ui/index.html
