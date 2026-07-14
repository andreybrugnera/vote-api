ALTER TABLE member
    ALTER COLUMN created_at TYPE timestamp;

ALTER TABLE agenda
    ALTER COLUMN created_at TYPE timestamp;

ALTER TABLE voting_session
    ALTER COLUMN opened_at TYPE timestamp;

ALTER TABLE voting_session
    ALTER COLUMN closes_at TYPE timestamp;

ALTER TABLE voting_session
    ALTER COLUMN result_published_at TYPE timestamp;

ALTER TABLE voting_session
    ALTER COLUMN created_at TYPE timestamp;

ALTER TABLE vote
    ALTER COLUMN created_at TYPE timestamp;
