ALTER TABLE tokens DROP PRIMARY KEY;
ALTER TABLE tokens ADD PRIMARY KEY (username, organization_id, token_type);