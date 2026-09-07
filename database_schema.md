# Police Management System — Database Guide

## 1. Purpose

This file is Member 5's main guide for the new database. The database stores users for login and cases for case work. Old record tables and separate role tables are not part of the new design.

The target database contains exactly two tables:

- `USERS` stores police, victim, and accused accounts.
- `CASES` stores cases and links each case to one police user, one victim, and one accused user.

## 2. Relationship

```text
USERS.username
   ├── CASES.officer_username
   ├── CASES.victim_username
   └── CASES.accused_username
```

One user may be linked to many cases. Every case must have all three links.

## 3. USERS Table

| Column | Type and rules | Purpose |
|---|---|---|
| `person_id` | `VARCHAR(20)`, primary key | Stable user ID |
| `name` | `VARCHAR(100)`, not null | Name shown in the application |
| `username` | `VARCHAR(50)`, not null, unique | Login name and case-link value |
| `password` | `VARCHAR(100)`, not null | Password used by this classroom project |
| `role` | `ENUM('POLICE', 'VICTIM', 'ACCUSED')`, not null | User permission role |

Rules:

- `person_id` and `username` must each identify only one user.
- Role text must exactly match the Java `UserRole` values.
- Usernames are compared using the database's case-insensitive text setting.
- Blank values are rejected by `AppModel` before insertion or login.
- Text must not be longer than its column limit.

## 4. CASES Table

| Column | Type and rules | Purpose |
|---|---|---|
| `case_id` | `VARCHAR(20)`, primary key | Stable case ID |
| `description` | `VARCHAR(500)`, not null | Current case description |
| `status` | `ENUM('OPEN', 'CLOSED')`, not null | Current case state |
| `officer_username` | `VARCHAR(50)`, not null, foreign key | Police user who created the case |
| `victim_username` | `VARCHAR(50)`, not null, foreign key | Victim who may view the case |
| `accused_username` | `VARCHAR(50)`, not null, foreign key | Accused user who may view the case |

Foreign-key targets:

| CASES column | Target |
|---|---|
| `officer_username` | `USERS.username` |
| `victim_username` | `USERS.username` |
| `accused_username` | `USERS.username` |

Rules:

- New cases always start with `OPEN`; `AppModel.createCase` supplies this value.
- Only the description and status may be updated.
- An officer link must refer to a `POLICE` user, a victim link to a `VICTIM` user, and an accused link to an `ACCUSED` user. Foreign keys confirm that users exist; `AppModel` confirms their roles.
- Deleting users or cases is outside the application. Foreign keys use restricted delete and update behavior, with no cascading changes.
- Foreign-key columns must be indexed. MySQL may create these indexes while creating the constraints; Member 5 must verify them after setup.

## 5. Table Settings

Both tables use:

| Setting | Required value |
|---|---|
| Storage engine | `InnoDB` |
| Character set | `utf8mb4` |
| Text comparison | `utf8mb4_unicode_ci` |

These settings must be written into both table definitions so results do not depend on server defaults.

## 6. Demo Accounts

The initialization script prepares these classroom accounts:

| Person ID | Name | Username | Password | Role |
|---|---|---|---|---|
| `P001` | Demo Police | `police` | `demo123` | `POLICE` |
| `V001` | Demo Victim | `victim` | `demo123` | `VICTIM` |
| `A001` | Demo Accused | `accused` | `demo123` | `ACCUSED` |

Running initialization again must not create duplicates. Each demo username is inserted when absent and restored to the values above when already present.

These are simple classroom passwords. They must not be presented as a secure design for a real police system.

## 7. Initialization Rules

`sql/database_init.sql` is the only required SQL script in the target project. `DatabaseInitializer` performs this sequence:

1. Validate the selected database name.
2. Create the database when it does not exist.
3. Select that database.
4. Read `sql/database_init.sql` as UTF-8 text.
5. Create `USERS` before `CASES`.
6. Prepare the three demo users.

The script must be safe to run repeatedly. It must not drop tables, erase cases, or remove non-demo users.

The simple script reader splits statements at semicolons. The script must therefore avoid stored procedures, triggers, or other statements containing internal semicolons.

## 8. Configuration

| Environment variable | Meaning | Default |
|---|---|---|
| `DB_URL` | MySQL server URL without a database name | `jdbc:mysql://localhost:3306/` |
| `DB_USER` | MySQL username | Required; no default |
| `DB_PASSWORD` | MySQL password | Required; no default |
| `DB_NAME` | Database selected after connection | `PoliceDB` |

The application connects to the server first because the selected database may not exist yet. The database name may contain only letters, digits, and underscores and must begin with a letter.

## 9. Database Operations Used by AppModel

Member 5 reviews the prepared SQL used for these operations:

| Operation | Required database behavior |
|---|---|
| Authenticate | Find one user by username and compare the supplied password |
| Confirm session user | Find the user by ID and confirm the stored username and role |
| Find linked-user role | Return the role for one username |
| Detect duplicate case | Check whether a case ID exists |
| Create case | Insert the six `CASES` values, including `OPEN` status |
| Police case list | Return every case ordered by case ID |
| Victim case list | Return cases whose `victim_username` matches the logged-in username |
| Accused case list | Return cases whose `accused_username` matches the logged-in username |
| Update case | Change description and status for one case ID and check that one row changed |

All user-supplied values use prepared-statement parameters. A database or table name cannot use a prepared-statement parameter, so it must pass the strict name check before being included in a database command.

`DatabaseConnection` owns the connection, and `AppModel.close` closes it once during shutdown. The developed `database.DatabaseUtility` is retained for prepared statements and resource closing. The empty duplicate in `util` is removed.

## 10. Test Database Safety

Database tests use only `PoliceDBTest`.

- Test startup must reject any other database name.
- Each run creates a unique row prefix.
- Cleanup deletes only cases and users carrying that prefix.
- Cases are removed before their linked users.
- Cleanup runs even when a test fails.
- Tests never drop a database and never clean `PoliceDB`.
- Repeated-initialization tests confirm that tables and demo users are not duplicated.

## 11. Member 5 Files and Work

Member 5 owns:

- `src/database/DatabaseConnection.java`
- `src/database/DatabaseInitializer.java`
- `sql/database_init.sql`
- Database setup and verification in `src/test/DBTest.java`
- Database setup instructions in `README.md`

Member 5 also reviews every JDBC operation in `AppModel`, even though the members responsible for those Model functions write the operations.

The exact Java function headers are defined in `specifications.md`. This file defines the database behavior those functions must follow.

## 12. Migration Checklist

- Replace the old multi-table initialization script with the two-table schema.
- Remove dependencies on old record, role-specific, and join tables from the new code.
- Move all required queries into `AppModel`.
- Confirm that no code imports `util.DatabaseUtility` and that database callers use `database.DatabaseUtility` safely.
- Confirm that every SQL resource closes automatically.
- Confirm that setup works twice without losing data.
- Confirm that production and test database names stay separate.
- Remove old database helpers and `database_drop.sql` only after the new application compiles and tests pass.
- Do not change or remove existing Java or SQL files until the team approves implementation work.

## 13. Completion Checks

Member 5's work is complete when:

- Setup creates only `USERS` and `CASES`.
- All constraints, table settings, and demo accounts match this document.
- All three roles can log in.
- Valid case creation, reading, and updating work.
- Invalid links, duplicate IDs, and unknown IDs produce clear failures.
- Closing the application closes its one JDBC connection.
- Tests prove that production data cannot be cleaned accidentally.
