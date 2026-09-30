CREATE TABLE applications (
    id BIGSERIAL PRIMARY KEY ,
    company VARCHAR(255) NOT NULL ,
    role VARCHAR(255) NOT NULL ,
    status VARCHAR(32) NOT NULL ,
    deadline TIMESTAMPTZ ,
    created_at TIMESTAMPTZ ,
    updated_at TIMESTAMPTZ
);
CREATE TABLE email_logs (
    id BIGSERIAL PRIMARY KEY ,
    application_id BIGINT REFERENCES applications(id) ON DELETE SET NULL ,
    message_id VARCHAR(512) NOT NULL UNIQUE ,
    subject VARCHAR(500) ,
    sender VARCHAR(255) ,
    recieved_at TIMESTAMPTZ ,
    classification VARCHAR(32) NOT NULL ,
    raw_snippet TEXT
);

CREATE INDEX idx_email_logs_application_id ON email_logs(application_id);