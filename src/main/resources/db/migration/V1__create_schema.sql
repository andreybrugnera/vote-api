CREATE TABLE member (
    id         uuid         NOT NULL PRIMARY KEY,
    name       varchar(255) NOT NULL,
    document   varchar(11)  NOT NULL UNIQUE,
    type       varchar(20)  NOT NULL CHECK (type IN ('ASSOCIATE', 'ADMIN')),
    created_at timestamp with time zone NOT NULL
);

CREATE TABLE agenda (
    id          uuid NOT NULL PRIMARY KEY,
    description text NOT NULL,
    created_at  timestamp with time zone NOT NULL
);

CREATE TABLE voting_session (
    id                  uuid NOT NULL PRIMARY KEY,
    agenda_id           uuid NOT NULL UNIQUE REFERENCES agenda (id),
    opened_at           timestamp with time zone NOT NULL,
    closes_at           timestamp with time zone NOT NULL,
    result_published_at timestamp with time zone,
    created_at          timestamp with time zone NOT NULL
);

CREATE TABLE vote (
    id                uuid        NOT NULL PRIMARY KEY,
    voting_session_id uuid        NOT NULL REFERENCES voting_session (id),
    member_id         uuid        NOT NULL REFERENCES member (id),
    choice            varchar(3)  NOT NULL CHECK (choice IN ('YES', 'NO')),
    created_at        timestamp with time zone NOT NULL,
    CONSTRAINT uk_vote_session_member UNIQUE (voting_session_id, member_id)
);
