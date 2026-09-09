# Police Management System — Six-Member Plan

## Goal

Complete the backend around the current UI path:

```text
Main -> LoginFrame -> CaseFrame -> CasePanel
```

Police can view, create, and update cases. Victims and accused users can view only cases linked to them.

## Ownership

| Member | Responsibility | Primary files |
|---|---|---|
| 1 | Models | All `src/model` files and `UserTest` model checks |
| 2 | Police Controller/backend | `PoliceController` and police operation tests |
| 3 | Victim Controller/backend | `VictimController` and victim visibility tests |
| 4 | Login/session and accused Controller/backend | `LoginController`, `AccusedController`, exceptions, authentication/access tests |
| 5 | Database | `src/database`, SQL, `database_schema.md`, database README content, `DBTest` |
| 6 | UI/startup | `Main`, `LoginFrame`, `CaseFrame`, `CasePanel`, `UITest` |

Members 2–4 call Member 5’s `DatabaseUtility` and return Member 1’s objects to Member 6’s UI.

## Target structure

```text
src/
├── Main.java
├── model/
│   ├── Person.java
│   ├── Police.java
│   ├── Victim.java
│   ├── Accused.java
│   ├── UserRole.java
│   └── CaseRecord.java
├── controller/
│   ├── LoginController.java
│   ├── PoliceController.java
│   ├── VictimController.java
│   └── AccusedController.java
├── database/
│   ├── DatabaseConnection.java
│   ├── DatabaseInitializer.java
│   └── DatabaseUtility.java
├── exceptions/
│   ├── AuthenticationException.java
│   ├── AuthorizationException.java
│   ├── DatabaseException.java
│   ├── EntityNotFoundException.java
│   └── ValidationException.java
├── view/
│   ├── LoginFrame.java
│   ├── CaseFrame.java
│   └── CasePanel.java
└── test/
    ├── UserTest.java
    ├── DBTest.java
    └── UITest.java

sql/
└── database_init.sql
```

Legacy dashboard views remain only until active imports and tests no longer need them.

## Current state

Working UI structure:

- Login passes a `Person` to `CaseFrame`.
- `CaseFrame` selects a role Controller with `instanceof`.
- Returned cases become individual `CasePanel` objects.
- Police alone can add a creation panel.
- `newCasePanel` prevents duplicate unsaved forms.
- Victim and accused cards are read-only.

Incomplete backend:

- Login always returns a placeholder police object.
- All `getCases(...)` methods return empty lists.
- Police writes only print values.
- `CaseRecord` returns constant strings.
- Controllers are not initialized with `DatabaseUtility`.
- The SQL schema contains unused FIR/evidence functionality.
- `Main` does not initialize the database/backend.
- UI writes do not refresh case cards or display specific backend failures.
- `UserTest` and `UITest` are empty.
- `DBTest` currently targets and drops `PoliceDB`, so it is unsafe for valuable data.

## Phase 1 — Member 1 models

1. Make `Person.validateCredentials(...)` null-safe.
2. Implement all six stored fields and the constructor in `CaseRecord`.
3. Keep every getter name currently used by `CasePanel`.
4. Validate required text and `OPEN`/`CLOSED` status.
5. Test concrete user construction, roles, credentials, and case storage.

Exit condition: Controllers can create real model objects without placeholders.

## Phase 2 — Member 5 schema and safety

1. Stop running the current destructive `DBTest` against `PoliceDB`.
2. Replace the obsolete schema with `USERS` and `CASES` from `specifications.md`.
3. Include the common and subtype columns needed by current user constructors.
4. Add case username foreign keys and indexes.
5. Make initialization repeatable and non-destructive.
6. Read `DB_URL`, `DB_USER`, `DB_PASSWORD`, and `DB_NAME` from configuration.
7. Add the `DatabaseInitializer(String databaseName, Path scriptPath)` test constructor.
8. Require `PoliceDBTest` in `DBTest` and remove production-drop behavior.
9. Update `database_schema.md` and README to match the two-table design.

Exit condition: setup is safe to run twice, and tests cannot affect production.

## Phase 3 — Member 4 login and session

1. Add `LoginController.initialize(DatabaseUtility)`.
2. Validate credentials and query `USERS` by username.
3. Map the role row to the correct Member 1 subtype.
4. Replace the unconditional placeholder police login.
5. Store a user only after successful authentication.
6. Clear the session on failed login and logout.
7. Implement reusable session identity and role checks agreed with Members 2 and 3.
8. Test all roles, unknown users, wrong passwords, blank/null input, and logout.

Exit condition: login returns and stores the same correct concrete user.

## Phase 4 — Members 2 and 3 role reads

Member 2:

1. Add `PoliceController.initialize(DatabaseUtility)`.
2. Implement police `getCases(...)` returning every case ordered by ID.
3. Map all six columns to `CaseRecord`.

Member 3:

1. Add `VictimController.initialize(DatabaseUtility)`.
2. Implement victim `getCases(...)` filtered by `victim_username`.
3. Map all six columns to `CaseRecord`.

Member 4:

1. Add `AccusedController.initialize(DatabaseUtility)`.
2. Implement accused `getCases(...)` filtered by `accused_username`.
3. Map all six columns to `CaseRecord`.

Shared requirements:

- Verify the supplied object is the current session and has the correct role.
- Return an empty list, never null, when no rows match.
- Use prepared parameters and close results/statements.
- Wrap SQL failures in `DatabaseException`.

Exit condition: each role receives exactly its allowed cases.

## Phase 5 — Member 2 police writes

1. Implement case creation using the authenticated police username.
2. Reject blank values and duplicate case IDs.
3. Confirm victim and accused links exist with correct roles.
4. Force new status to `OPEN`.
5. Implement description/status updates.
6. Reject status outside `OPEN` and `CLOSED`.
7. Throw `EntityNotFoundException` when no case is updated.
8. Test successful and rejected writes without Swing.

Exit condition: police writes are validated, authorized, persisted, and testable.

## Phase 6 — Member 6 UI integration

1. Initialize the database and all Controllers in `Main`.
2. Start Swing with `SwingUtilities.invokeLater(...)`.
3. Construct `CaseFrame` before disposing Login.
4. Catch Controller failures in Login, case loading, and Save listeners.
5. Add `CaseFrame.refreshCases()` using the existing typed dispatch.
6. Refresh after successful create/update.
7. Clear `newCasePanel` after a successful creation.
8. Keep victim/accused fields locked and Save hidden.
9. Test all role paths and repeated Create clicks.
10. Remove legacy dashboard views after references disappear.

Exit condition: all allowed actions work through the current three-view workflow.

## Phase 7 — Integration verification

1. Compile all Java sources with Connector/J.
2. Run model tests without MySQL.
3. Run database tests only against `PoliceDBTest`.
4. Test login and case visibility for every role.
5. Test valid and invalid police writes.
6. Confirm views contain no SQL and Controllers contain no Swing code.
7. Confirm all JDBC resources close.
8. Confirm user-facing messages do not expose SQL details.

## Coordination rules

- Function names and parameters in `specifications.md` are integration contracts.
- A member changing a shared header must coordinate with its callers before merging.
- Member 1 completes `CaseRecord` before Members 2–4 return database rows.
- Member 5 completes safe test isolation before anyone runs database integration tests.
- Member 4 publishes the session-check behavior used by Members 2 and 3.
- Member 6 does not bypass Controllers to access the database.

## Definition of done

- `LoginFrame -> CaseFrame -> CasePanel` is the active UI path.
- Login returns the correct concrete user and session object.
- Police view all cases; victim and accused views are properly filtered.
- Case cards display real stored values.
- Police can create/update; other roles cannot write.
- Successful writes refresh the list without duplicate forms.
- The schema contains only workflow-required user/case data.
- Database setup is repeatable and tests cannot affect `PoliceDB`.
- Models contain no JDBC/Swing code, Controllers contain no Swing code, and Views contain no SQL.
- Application exceptions cross layer boundaries instead of raw SQL failures.
- All Java sources compile and all three test programs pass.
