# Police Management System

Java application for managing police records and database access.

## Requirements

- JDK 9 or newer
- MySQL Server
- MySQL Connector/J

## Project Structure

```text
src/       Java source files
sql/       SQL scripts
lib/       External JAR dependencies
```

## Database Setup

1. Start a local MySQL server.
2. Update the connection URL, username, and password in
	`src/database/DatabaseConnection.java`.
3. Call `new DatabaseInitializer().initializeDatabase()` from Java to create
	the database and tables from `sql/database_init.sql`.

The current connection settings use placeholders and connect to the MySQL
server at `localhost:3306`.

## MySQL Connector/J

Place the Connector/J JAR in `lib/`. If it is not already available, download
version `26.7.0` from [Maven Central](https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/26.7.0/mysql-connector-j-26.7.0.jar)
and save it as:

```text
lib/mysql-connector-j-26.7.0.jar
```

## Compile

From the project root, compile all Java sources into `out/`:

```bash
mkdir -p out
javac -cp "lib/mysql-connector-j-26.7.0.jar" -d out $(find src -name '*.java')
```

On Windows PowerShell, use:

```powershell
New-Item -ItemType Directory -Force out
javac -cp "lib/mysql-connector-j-26.7.0.jar" -d out (Get-ChildItem -Recurse src -Filter *.java).FullName
```

## Run

Run it with:

```bash
java -cp "out:lib/mysql-connector-j-26.7.0.jar" Main
```

On Windows, use `;` instead of `:` in the classpath:

```powershell
java -cp "out;lib/mysql-connector-j-26.7.0.jar" Main
```

## Dummy values

For tractibility, we have decided to initialize the database with dummy users and cases.  
Currently, you can add cases, but due to scale of the project, we have decided not to implement signup.  
The following users are present:
| Username | Password | Role |
|----------|----------|------|
| rajesh | password123 | Police |
| priya | password123 | Police |
| amit | password123 | Victim |
| neha | password123 | Victim |
| ravi | password123 | Accused |
| suresh | password123 | Accused |