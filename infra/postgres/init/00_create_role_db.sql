-- Create user if not exists
DO
$do$
BEGIN
   IF NOT EXISTS (
      SELECT FROM pg_catalog.pg_roles
      WHERE  rolname = 'ems') THEN

      CREATE ROLE ems LOGIN PASSWORD 'ems_pw';
   END IF;
END
$do$;

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE emsdb TO ems;
GRANT ALL ON SCHEMA public TO ems;