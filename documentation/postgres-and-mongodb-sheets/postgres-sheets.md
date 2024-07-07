# PostgreSQL & MongoDB Commands Cheat Sheet

## PostgreSQL Commands

### Database Management

#### Create Database
- `createdb mydatabase`: Create a new PostgreSQL database named `mydatabase`.

#### Drop Database
- `dropdb mydatabase`: Delete an existing PostgreSQL database named `mydatabase`.

### User Management

#### Create User
- `createuser --interactive`: Interactively create a new PostgreSQL user.

#### Drop User
- `dropuser myuser`: Delete an existing PostgreSQL user named `myuser`.

### Schema Management

#### List Schemas
- `\dn`: List all schemas in the current database.

#### Create Schema
- `CREATE SCHEMA myschema;`: Create a new schema named `myschema`.

#### Drop Schema
- `DROP SCHEMA myschema;`: Delete an existing schema named `myschema`.

### Table Management

#### List Tables
- `\dt`: List all tables in the current schema.

#### Create Table
- Example:
  ```sql
  CREATE TABLE users (
      id SERIAL PRIMARY KEY,
      username VARCHAR(50) UNIQUE NOT NULL,
      password VARCHAR(100) NOT NULL
  );
  ```

# MongoDB Commands

## Database Management

### Connect to MongoDB Shell
- `mongo`: Start the MongoDB shell and connect to the default MongoDB instance.

### Show Databases
- `show dbs`: List all databases in the MongoDB instance.

### Use Database
- `use mydatabase`: Switch to the `mydatabase` database.

### Drop Database
- `db.dropDatabase()`: Delete the current database.

## Collection Management

### Show Collections
- `show collections`: List all collections in the current database.

### Create Collection
- `db.createCollection("mycollection")`: Create a new collection named `mycollection`.

### Drop Collection
- `db.mycollection.drop()`: Delete an existing collection named `mycollection`.

## Document Manipulation

### Insert Document
- Example:
  ```javascript
  db.users.insertOne({ username: "john_doe", password: "password123" });
````