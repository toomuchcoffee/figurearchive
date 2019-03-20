# DB setup for local development
- Start postgres server
- Open psql console
    - CREATE DATABASE figurearchive;
    - CREATE USER figures WITH PASSWORD 's3cr3t';
    - GRANT ALL PRIVILEGES ON DATABASE "figurearchive" TO figures;
  
