# Police Management System — MVC Refactoring Plan

## Goal

Refactor the current project into a small case-management application. Cases are the only managed records.

The application supports:

1. Login and logout.
2. Police creating cases.
3. Police viewing and updating cases.
4. Victims and accused users viewing cases linked to them.

## Target Structure

```text
src/
├── Main.java
├── model/
│   ├── Person.java
│   ├── Police.java
│   ├── Victim.java
│   ├── Accused.java
│   ├── UserRole.java
│   ├── CaseRecord.java
│   └── AppModel.java
├── controller/
│   ├── LoginController.java
│   ├── PoliceController.java
│   ├── VictimController.java
│   └── AccusedController.java
├── view/
│   ├── LoginFrame.java
│   ├── HomeFrame.java
│   ├── PolicePanel.java
│   ├── VictimPanel.java
│   ├── AccusedPanel.java
│   └── CasePanel.java
├── exceptions/
│   ├── AuthenticationException.java
│   ├── AuthorizationException.java
│   ├── DatabaseException.java
│   ├── EntityNotFoundException.java
│   └── ValidationException.java
├── database/
│   ├── DatabaseConnection.java
│   ├── DatabaseInitializer.java
│   └── DatabaseUtility.java
└── test/
    ├── UserTest.java
    ├── DBTest.java
    └── UITest.java

sql/
└── database_init.sql
```

The production packages are `model`, `controller`, `view`, `exceptions`, and `database`. The old `dao`, `service`, `ui`, `exception`, and `util` packages are removed after their required code has been moved.

## Package Responsibilities

### Model

- `Person`, `Police`, `Victim`, and `Accused` represent users.
- `UserRole` contains the three user roles.
- `CaseRecord` represents a case in memory.
- `AppModel` contains login, permission, validation, and case database operations.
- Database operations do not use separate DAO or service classes.

### Controller

- `LoginController` manages login, logout, and the current user.
- `PoliceController` connects police case actions to `AppModel`.
- `VictimController` loads cases linked to the logged-in victim.
- `AccusedController` loads cases linked to the logged-in accused.
- Controllers contain coordination code, not SQL or Swing layouts.
- Controllers return results or pass exceptions to the calling View. They do not display Swing messages.

### View

- Existing Swing components, layouts, comments, and useful variable names remain in their corresponding classes.
- `LoginFrame` collects credentials and displays login results.
- `HomeFrame` displays user information, contains `personPanel`, and provides logout.
- `PolicePanel` keeps only case create, view, update, and refresh controls.
- `VictimPanel` and `AccusedPanel` provide read-only linked-case views.
- `CasePanel` displays cases in a non-editable table.
- Views do not execute SQL or decide permissions.
- Views catch errors from Controller calls and display short, useful messages.

### Exception

- Existing exception classes provide clear authentication, authorization, validation, missing-record, and database failures.
- `AppModel` and the database setup convert JDBC failures into `DatabaseException`.
- Views display short exception messages. Technical database details remain in the exception cause and may be logged during startup or testing.

### Database

- `DatabaseConnection` owns the MySQL connection.
- `DatabaseConnection` first connects to the MySQL server without selecting a database.
- `DatabaseInitializer` validates the configured database name, creates the database when absent, selects it, and then runs `database_init.sql`.
- `DatabaseUtility` prepares statements, binds parameters, and closes JDBC resources.
- The database contains only `USERS` and `CASES`.
- Normal startup uses `DB_NAME`, or `PoliceDB` when it is not set. `DBTest` runs separately from the application and explicitly selects only `PoliceDBTest` before running setup.

## Request Flow

```text
Swing View
  -> matching Controller
  -> AppModel
  -> DatabaseUtility / DatabaseConnection
  -> MySQL
  -> Controller
  -> Swing View
```

## Permissions

| Action | Police | Victim | Accused |
|---|---:|---:|---:|
| Login and logout | Yes | Yes | Yes |
| View cases | All | Linked only | Linked only |
| Create cases | Yes | No | No |
| Update case description and status | Yes | No | No |

Permissions are checked in `AppModel`, even when an operation is called without the Swing interface.

## Implementation Order

1. Create the `view` package and move the six retained Swing classes from `ui`.
2. Update their package declarations and imports together.
3. Move the five error classes from `exception` to `exceptions` and update imports.
4. Remove excluded controls and files from the old `ui` package.
5. Prepare `CaseRecord` and the reduced person classes.
6. Replace the database schema with `USERS` and `CASES`.
7. Complete database-name validation, creation, selection, script execution, and test-database selection.
8. Move authentication and database access into `AppModel`, and convert JDBC failures at its public boundary.
9. Connect the four existing Controller classes to `AppModel` and their Views.
10. Make each View action catch Controller errors and show a short message.
11. Remove `dao`, `service`, and the duplicate utility package after no imports use them.
12. Update the three existing test classes.
13. Remove obsolete files only after the retained application compiles and its tests pass.
14. Update the README and run the complete workflow for all three roles.

## Six-Member Breakdown

| Member | Main responsibility | Owned classes/files | Shared work |
|---|---|---|---|
| 1 | Users and authentication Model | `Person`, `Police`, `Victim`, `Accused`, `UserRole` | Authentication functions in `AppModel`, `UserTest` |
| 2 | Police case actions | `PoliceController` | Case creation and update functions in `AppModel`, related tests |
| 3 | Case Model and reading | `CaseRecord`, shared `AppModel` structure | Visible-case queries and case mapping |
| 4 | Login and role access Controllers | `LoginController`, `VictimController`, `AccusedController` | Permission checks, logout, access tests |
| 5 | Database | `DatabaseConnection`, `DatabaseInitializer`, `DatabaseUtility`, `database_init.sql`, `DBTest` | Review all SQL and JDBC resource handling |
| 6 | Swing and startup | All six View classes, `Main`, `UITest`, README | Final UI connection and acceptance workflow |

Member 3 maintains the shared `AppModel` file. Members 1, 2, and 4 add only their assigned functions. Member 2 owns `PoliceController`; Member 4 owns the other three Controller classes. Member 5 reviews SQL written inside `AppModel`.

Every member must read all production classes and be able to trace login, case creation, case viewing, case updating, and logout.

Exact variables, function headers, and behavior are defined in `specifications.md`. Database table details are defined in `database_schema.md`.

## Definition of Done

- Only the target files and packages remain.
- Existing retained classes compile after their package moves.
- No production code imports `dao`, `service`, `ui`, `exception`, or `util`.
- Database setup creates only `USERS` and `CASES`.
- All three roles can log in and log out.
- Police can create, view, and update cases.
- Victims and accused can view only linked cases.
- Invalid and unauthorized operations produce clear messages.
- Database failures reach the Views as `DatabaseException`, not `SQLException`.
- `DBTest` cannot initialize or clean the production database.
- JDBC statements and result sets are closed safely.
- Every member can explain each package and complete request flow.
