-- This file is executed automatically when the PostgreSQL container starts for the first time
-- It's optional since Flyway will handle the schema and data initialization

-- Create extensions if needed
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- You can add any custom initialization here
-- For example, creating additional databases, users, or schemas

-- Note: The main database is already created by the POSTGRES_DB environment variable
