ALTER TABLE voting_session
    ADD COLUMN status varchar(20) NOT NULL DEFAULT 'OPEN'
        CHECK (status IN ('OPEN', 'CLOSED'));
