ALTER TABLE users MODIFY COLUMN name varchar(64), MODIFY COLUMN username varchar(64), MODIFY COLUMN organization_id varchar(64);
ALTER TABLE tokens MODIFY COLUMN username varchar(64), MODIFY COLUMN organization_id varchar(64);
ALTER TABLE roles_users MODIFY COLUMN username varchar(64);
ALTER TABLE permissions MODIFY COLUMN name varchar(128);