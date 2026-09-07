# Police Management System — Simplified MVC Plan

## Goal

Refactor the existing project into a small MVC application that every member can understand completely. Reuse useful model, Swing, and database code, but remove unnecessary layers and placeholders.

Cases are the only managed records. The application supports:

1. Login and logout.
2. Police creating cases.
3. Users viewing cases available to their role.
4. Police updating case descriptions and statuses.

Victims and accused users have read-only access to cases linked to their usernames.

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
├── view/
│   ├── LoginFrame.java
│   ├── HomeFrame.java
│   ├── PolicePanel.java
│   ├── VictimPanel.java
│   ├── AccusedPanel.java
│   └── CasePanel.java
├── controller/
│   └── AppController.java
├── database/
│   ├── DatabaseConnection.java
│   └── DatabaseInitializer.java
└── test/
    └── AppTest.java

sql/
└── database_init.sql
```

The production packages are `model`, `view`, `controller`, and `database`. No other production packages or helper interfaces are required.

## Design Decisions

### Model side

- `Person` is the abstract base class for `Police`, `Victim`, and `Accused`.
- `UserRole` identifies the three roles.
- `CaseRecord` represents a case in memory. Its values cannot be changed after the object is created.
- `AppModel` handles login sessions, logout, input checks, permission checks, and case create/read/update operations.
- The `database` package contains the database support code used by the Model.

### View side

- Each Swing class keeps its component, listener, and layout initialization methods private.
- No common UI interface is used because private methods cannot form an interface contract.
- `LoginFrame` handles login input.
- `HomeFrame` hosts the current role panel and logout control.
- `PolicePanel` contains case create and update controls.
- `VictimPanel` and `AccusedPanel` contain read-only case access.
- Every role panel owns an instance of the reusable `CasePanel` class.
- Views never execute SQL, filter cases, or decide permissions.

### Controller side

- `AppController` connects view events to `AppModel` operations.
- It keeps the current user, active frame, role panel, and case panel.
- It runs database work away from the Swing UI thread, then updates Swing components on the UI thread.
- It owns logout and application shutdown behavior.
- Members 2 and 4 share this class. Member 2 owns the case create/update handlers. Member 4 owns the remaining controller structure and actions.

### Database side

- `DatabaseConnection` opens, provides, and closes one JDBC connection.
- `DatabaseInitializer` creates the selected database, runs the initialization script, and seeds demo users.
- `database_init.sql` defines only `USERS` and `CASES`.
- `AppModel` contains the small number of prepared case and authentication queries.
- No `DatabaseUtility` is retained. Try-with-resources handles statements and result sets more safely and clearly.

## Request Flow

```text
View event
  -> AppController
  -> AppModel
  -> DatabaseConnection
  -> MySQL
  -> AppController
  -> active View
```

## Permissions

| Action | Police | Victim | Accused |
|---|---:|---:|---:|
| Login/logout | Yes | Yes | Yes |
| View cases | All | Linked only | Linked only |
| Create cases | Yes | No | No |
| Update description/status | Yes | No | No |

`AppModel` requires the exact `Person` object returned by the latest successful login and checks that user again in the database. A user class or visible button alone never grants permission.

## Implementation Order

1. Standardize the person hierarchy and role enum.
2. Implement the case model whose values cannot change after creation.
3. Reduce the schema to two tables and make initialization configurable.
4. Implement authentication and case operations in `AppModel`.
5. Move the reusable Swing files from `ui` to `view` and simplify them.
6. Members 2 and 4 connect all view actions through `AppController`.
7. Add isolated model and database tests.
8. Run the complete acceptance workflow.
9. Remove old files only after the replacement compiles and passes its tests.
10. Update the README with setup, environment variables, and demo accounts.

## Six-Member Split

The original responsibility pattern is retained while fitting the reduced, case-only scope.

| Member | Responsibility | Owned files/classes | Shared functions contributed |
|---|---|---|---|
| 1 | User management and authentication | `Person`, `Police`, `Victim`, `Accused`, `UserRole` | `AppModel.authenticate`, user row mapping, user tests |
| 2 | Case registration | Creation section of `AppTest`; case-action part of `AppController` | `AppModel.createCase`, creation input checks, `handleCreateCase`, and `handleUpdateCase` |
| 3 | Case management | `CaseRecord`; shared structure of `AppModel` and `AppTest` | `AppModel.getVisibleCases`, `AppModel.updateCase`, case row mapping |
| 4 | Case access, input checks, and controller structure | Permission/input-check sections of `AppTest`; main structure of `AppController` | Stored-user checks, role checks, login/dashboard/refresh/logout/shutdown actions, and access tests |
| 5 | Database and JDBC | `DatabaseConnection`, `DatabaseInitializer`, `database_init.sql` | Test-database setup and review of all SQL in `AppModel` |
| 6 | Swing views and integration | All six view classes, `Main`, README | View components, layouts, public view methods, startup, and final UI integration |

`AppModel` and `AppTest` contain work from several members. Member 3 maintains their shared structure; Members 1, 2, and 4 add their assigned logic and tests in that order to reduce merge conflicts. Member 4 implements the shared linked-user role checks, while Member 2 calls them from case creation.

Member 4 maintains the shared `AppController` file. Member 2 adds its two case-action handlers after the main controller structure is ready. Member 6 supplies the public view methods and checks that the finished Controller connects correctly to the UI. Member 5 reviews all JDBC code.

Ownership is only for coordination. Every member must read all files and trace the complete login, case creation, case viewing, and case update flows.

The exact purpose, variables, constructors, function headers, and expected behavior for every class are defined in `specifications.md`. Each function header states its access level, return type, static or non-static form, parameters, and checked exceptions. The database schema and Member 5's database rules are defined in `database_schema.md`. Members must follow those contracts so independently written parts integrate without renaming or adapter work.

## Definition of Done

- Only the target structure remains.
- The application compiles from a clean checkout.
- Production and test database names are configurable and distinct.
- Initialization creates only `USERS` and `CASES` and prepares the demo users.
- Police can create, read, and update cases.
- Victims and accused can read only linked cases.
- Unauthorized operations are rejected by `AppModel`.
- Database work does not freeze the Swing interface.
- Invalid input and database failures produce readable messages.
- The JDBC connection closes exactly once when the application exits.
- Every member can explain all four production packages.
