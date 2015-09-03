CREATE TABLE IF NOT EXISTS tokens (token_value varchar(128), username varchar(32),
organization_id varchar(32), role_id varchar(32), token_type TINYINT(10), PRIMARY KEY (organization_id, token_type));

CREATE TABLE IF NOT EXISTS users (name varchar(32), username varchar(32) primary key,
password varchar(256), salt varchar(256), api_client TINYINT(1), organization_id varchar(32), role_id varchar(32));

CREATE TABLE IF NOT EXISTS permissions (id INT AUTO_INCREMENT, name VARCHAR(32), http_verb VARCHAR(32), url VARCHAR(256), PRIMARY KEY (id));
ALTER TABLE permissions ADD UNIQUE INDEX(http_verb, url);

CREATE TABLE IF NOT EXISTS roles (name VARCHAR(32) PRIMARY KEY);
CREATE TABLE IF NOT EXISTS roles_permissions (role_name VARCHAR(32), permission_id INT, PRIMARY KEY (role_name, permission_id));
CREATE TABLE IF NOT EXISTS roles_users (username VARCHAR(32) PRIMARY KEY, role_name VARCHAR(32));