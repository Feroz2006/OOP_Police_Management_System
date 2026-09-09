# Police Management System — Six-Member Specification

## 1. Shared application contract

### Active workflow

The current UI workflow is authoritative:

```text
Main -> LoginFrame -> CaseFrame -> CasePanel
```

`HomeFrame`, `PolicePanel`, `VictimPanel`, and `AccusedPanel` are legacy views. New work must not depend on them, and they may be removed after the active workflow and tests compile without them.

Cases are the only managed records in this workflow:

| Action | Police | Victim | Accused |
|---|---:|---:|---:|
| Login | Yes | Yes | Yes |
| View cases | All | Linked only | Linked only |
| Create cases | Yes | No | No |
| Update description/status | Yes | No | No |

### Layer responsibilities

| Member | Responsibility |
|---|---|
| 1 | All data-model classes |
| 2 | Police Controller and police backend operations |
| 3 | Victim Controller and victim case-reading backend |
| 4 | Login/session Controller and accused case-reading backend |
| 5 | Database connection, utility, schema, initialization, and DB tests |
| 6 | `Main` and the active Swing workflow |

Members 2–4 use Member 5’s `DatabaseUtility` for prepared database operations and return Member 1’s model objects to Member 6’s views.

### Required call flow

```text
LoginFrame.login()
  -> LoginController.login(username, password)
  -> DatabaseUtility
  -> Police / Victim / Accused
  -> new CaseFrame(user)

CaseFrame.initializeLayout()
  -> matching role Controller.getCases(concreteUser)
  -> DatabaseUtility
  -> List<CaseRecord>
  -> one CasePanel per CaseRecord

CasePanel Save
  -> PoliceController.createCase(...)
     or PoliceController.updateCase(...)
  -> DatabaseUtility
```

Controllers contain no Swing code. Views contain no SQL. Model classes contain no Swing or JDBC code. SQL failures are converted to `DatabaseException` before reaching a view.

## 2. Member 1 — Model classes

Member 1 owns every file in `src/model` and the model checks in `UserTest`.

### Person

`Person` is the abstract user object passed from login through `CaseFrame` and `CasePanel`.

| Function header | Requirement |
|---|---|
| `public Person(String personID, String name, int age, String phone, String username, String password)` | Store common values |
| `public String getPersonID()` | Return the stable ID |
| `public String getName()` | Return the name |
| `public int getAge()` | Return age |
| `public String getPhone()` | Return phone |
| `public String getUsername()` | Return the login and case-link username |
| `public String getPassword()` | Return the classroom password |
| `public void setName(String newname)` | Replace the name |
| `public void setAge(int newage)` | Replace age |
| `public void setPhone(String newphone)` | Replace phone |
| `public boolean validateCredentials(String inputusername, String inputPassword)` | Return true only when both values match; return false for null input |
| `public abstract void viewCase(String caseID)` | Retain role-specific case behavior |
| `public abstract String getRole()` | Return the matching `UserRole` name |

### Police, Victim, and Accused

Login must construct one of these concrete types because `CaseFrame` selects a Controller with `instanceof`.

| Class | Required constructor | Role result |
|---|---|---|
| `Police` | `public Police(String personID, String name, int age, String phone, String username, String password, String rank, String department, String BadgeNumber)` | `UserRole.POLICE.name()` |
| `Victim` | `public Victim(String personID, String name, int age, String phone, String username, String password, String CrimeDetails, String EmergencyContact)` | `UserRole.VICTIM.name()` |
| `Accused` | `public Accused(String personID, String name, int age, String phone, String username, String password, String Charges, boolean BailStatus)` | `UserRole.ACCUSED.name()` |

Existing subtype getters and setters remain supported. `UserRole` contains `POLICE`, `VICTIM`, and `ACCUSED`.

### CaseRecord

`CaseRecord` is an immutable case returned by Controllers and displayed by `CasePanel`.

| Variable | Type |
|---|---|
| `caseId` | `String` |
| `description` | `String` |
| `status` | `String` |
| `officerUsername` | `String` |
| `victimUsername` | `String` |
| `accusedUsername` | `String` |

| Function header | Requirement |
|---|---|
| `public CaseRecord(String caseId, String description, String status, String officerUsername, String victimUsername, String accusedUsername)` | Validate and store one complete case |
| `public String getCaseId()` | Return the case ID |
| `public String getDescription()` | Return the description |
| `public String getStatus()` | Return `OPEN` or `CLOSED` |
| `public String getOfficerUsername()` | Return the officer username |
| `public String getVictimUsername()` | Return the linked victim username |
| `public String getAccusedUsername()` | Return the linked accused username |

The current constant-returning methods are placeholders. They must read stored fields before database case loading is enabled.

### Member 1 completion checks

- All concrete users preserve constructor values and return the correct role.
- Credential checks handle correct, incorrect, blank, and null input.
- `CaseRecord` preserves all six constructor values.
- Model classes import neither `database` nor `view`.

## 3. Member 2 — Police Controller and backend

Member 2 owns `PoliceController` and all police-only case operations.

### Public view-facing functions

| Function header | Requirement |
|---|---|
| `public static void initialize(DatabaseUtility utility)` | Store Member 5’s initialized database utility |
| `public static List<CaseRecord> getCases(Police police) throws AuthorizationException, DatabaseException` | Verify the current police session and return every case ordered by case ID |
| `public static void createCase(String caseId, String description, String victimUsername, String accusedUsername) throws AuthorizationException, ValidationException, DatabaseException` | Obtain the current police from `LoginController`, validate input, and insert an `OPEN` case |
| `public static void updateCase(Person currentUser, String caseId, String description, String status) throws AuthorizationException, ValidationException, EntityNotFoundException, DatabaseException` | Verify the current police session and update description/status |

The current empty list and console output are placeholders.

### Required private backend behavior

- Reject calls before `initialize(...)`.
- Require `LoginController.getCurrentUser()` to be the same authenticated police object supplied by the UI.
- Trim and reject blank case IDs, descriptions, and linked usernames.
- Accept only `OPEN` or `CLOSED` status.
- Check that a new case ID does not already exist.
- Confirm the victim username exists with role `VICTIM`.
- Confirm the accused username exists with role `ACCUSED`.
- Use the authenticated police username as `officer_username`; never trust a text field for it.
- Throw `EntityNotFoundException` if an update affects no row.
- Map every selected row to Member 1’s `CaseRecord` constructor.
- Use prepared parameters for all user input.
- Close every `ResultSet` and its `PreparedStatement`.
- Wrap `SQLException` in `DatabaseException` and preserve its cause.

### Member 2 completion checks

- Police receive all cases.
- Valid creation stores an `OPEN` case with the logged-in officer.
- Duplicate IDs, invalid links, and blank data are rejected.
- Only description and status can be updated.
- Non-police and stale session objects cannot write.

## 4. Member 3 — Victim Controller and backend

Member 3 owns `VictimController` and victim-visible case queries.

| Function header | Requirement |
|---|---|
| `public static void initialize(DatabaseUtility utility)` | Store Member 5’s initialized database utility |
| `public static List<CaseRecord> getCases(Victim victim) throws AuthorizationException, DatabaseException` | Verify the current victim session and return linked cases ordered by case ID |

Required query rule:

```sql
WHERE victim_username = ?
ORDER BY case_id
```

The parameter is `victim.getUsername()`. Member 3 maps all six selected columns to `CaseRecord`, closes JDBC resources, and wraps SQL failures in `DatabaseException`.

### Member 3 completion checks

- A victim receives only rows linked through `victim_username`.
- Null, police, accused, and stale user objects are rejected.
- An empty result returns an empty list, never null.
- Victim backend code exposes no create, update, or delete operation.

## 5. Member 4 — Login, session, and accused backend

Member 4 owns `LoginController`, `AccusedController`, session checks shared with Member 2/3, and the application exception classes.

### LoginController

| Function header | Requirement |
|---|---|
| `public static void initialize(DatabaseUtility utility)` | Store Member 5’s initialized database utility |
| `public static Person login(String username, String password) throws AuthenticationException, DatabaseException` | Authenticate, store, and return the same concrete user object |
| `public static void logout()` | Clear the stored user |
| `public static Person getCurrentUser()` | Return the stored user or null |

`login(...)` must:

1. Reject null or blank credentials with `AuthenticationException`.
2. Query the user by username with a prepared parameter.
3. Return the same generic invalid-credentials message for an unknown username or wrong password.
4. Map `role` to `Police`, `Victim`, or `Accused` using Member 1’s constructors.
5. Store the returned object in `loggedinUser` only after successful authentication.
6. Leave `loggedinUser` null after failure.

The current unconditional placeholder `Police` must be removed.

### AccusedController

| Function header | Requirement |
|---|---|
| `public static void initialize(DatabaseUtility utility)` | Store Member 5’s initialized database utility |
| `public static List<CaseRecord> getCases(Accused accused) throws AuthorizationException, DatabaseException` | Verify the current accused session and return linked cases ordered by case ID |

Required query rule:

```sql
WHERE accused_username = ?
ORDER BY case_id
```

The parameter is `accused.getUsername()`. An empty result returns an empty list, never null.

### Exception contracts

| Exception | Type | Use |
|---|---|---|
| `AuthenticationException` | Checked | Invalid credentials; retains attempted username |
| `AuthorizationException` | Checked | No session, wrong role, or stale user object |
| `ValidationException` | Runtime | Blank, duplicate, malformed, or invalid input |
| `EntityNotFoundException` | Runtime | Unknown case ID |
| `DatabaseException` | Runtime | Connection, SQL, configuration, or resource failure |

### Member 4 completion checks

- Login maps and stores all three concrete roles.
- Failed login never leaves a previous user authenticated.
- Logout clears the session.
- Accused users receive only rows linked through `accused_username`.
- Session checks are reusable and agreed with Members 2 and 3.

## 6. Member 5 — Database

Member 5 owns `DatabaseConnection`, `DatabaseInitializer`, `DatabaseUtility`, SQL scripts, `database_schema.md`, README database instructions, and `DBTest`.

### DatabaseConnection

| Function header | Requirement |
|---|---|
| `private DatabaseConnection()` | Read configuration and connect to the MySQL server |
| `public static DatabaseConnection getInstance()` | Return the singleton connection owner |
| `public Connection getConnection()` | Return the active connection |
| `public void closeConnection()` | Close the connection and wrap SQL failure |
| `public void close()` | Delegate to `closeConnection()` |
| `public boolean isConnected()` | Report connection state and wrap SQL failure |

Configuration comes from `DB_URL`, `DB_USER`, `DB_PASSWORD`, and `DB_NAME`; only `DB_NAME` defaults to `PoliceDB`. Completed code must not contain placeholder credentials.

### DatabaseInitializer

| Function header | Requirement |
|---|---|
| `public DatabaseInitializer()` | Use the configured production/default database name |
| `public DatabaseInitializer(String databaseName, Path scriptPath)` | Allow `DBTest` to select `PoliceDBTest` explicitly |
| `public void initializeDatabase()` | Validate, create, select, and initialize the database safely |
| `public void createTables()` | Run the same repeatable table setup |

Normal initialization must never drop tables or erase case data. Production startup must not call `dropTables()`.

### DatabaseUtility

| Function header | Requirement |
|---|---|
| `public DatabaseUtility()` | Use the shared connection |
| `public ResultSet executeQuery(String sql, Object... parameters) throws SQLException` | Prepare, bind, and run a read query |
| `public int executeUpdate(String sql, Object... parameters) throws SQLException` | Prepare, bind, run, and close a write statement |
| `public void close(AutoCloseable resource) throws SQLException` | Close nullable JDBC resources |

### Target schema

The active UI requires only two tables.

`USERS` contains:

- `person_id`, `name`, `age`, `phone`, `username`, `password`, and `role`.
- Nullable police fields: `rank`, `department`, and `badge_number`.
- Nullable victim fields: `crime_details` and `emergency_contact`.
- Nullable accused fields: `charges` and `bail_status`.

`CASES` contains:

- `case_id`, `description`, and `status` (`OPEN` or `CLOSED`).
- `officer_username`, `victim_username`, and `accused_username`, each referencing `USERS.username`.

Both tables use InnoDB and `utf8mb4`. The current FIR, evidence, role-table, and join-table SQL is obsolete for this UI and must be replaced.

### DBTest safety

- Tests use exactly `PoliceDBTest`.
- Tests abort before setup or cleanup for any other database name.
- Tests never create, drop, or clean `PoliceDB`.
- Test rows use a unique prefix and cleanup removes only those rows, cases first.
- Repeated initialization must preserve existing data.

The current `DBTest` targets and drops `PoliceDB`; it must not be run against valuable production data until corrected.

### Member 5 completion checks

- Setup creates only `USERS` and `CASES` and works repeatedly.
- Foreign keys and role-compatible demo data are valid.
- Prepared statements and result sets close safely.
- Connection and SQL failures become `DatabaseException` at Controller boundaries.
- `database_schema.md`, SQL, README, and tests describe the same schema.

## 7. Member 6 — UI and startup

Member 6 owns `Main`, `LoginFrame`, `CaseFrame`, `CasePanel`, UI navigation, and `UITest`.

### LoginFrame

| Function header | Requirement |
|---|---|
| `public LoginFrame()` | Build the login window |
| `private void initializeComponents()` | Create credential inputs and Login button |
| `private void initializeListeners()` | Connect Login to `login()` |
| `private void initializeLayout()` | Build the padded grid |
| `private void login()` | Call `LoginController.login(...)`, construct `CaseFrame(user)`, then dispose Login and show CaseFrame |
| `private void clearFields()` | Clear inputs and re-enable Login |

Invalid credentials show the existing warning. Other application errors show a short message. Do not dispose Login until `CaseFrame` is successfully constructed.

### CaseFrame

| Variable | Type | Requirement |
|---|---|---|
| `currentUser` | `Person` | Store the authenticated concrete user |
| `caseList` | `JPanel` | Vertically contain case cards |
| `scrollPane` | `JScrollPane` | Scroll the list |
| `createCaseButton` | `JButton` | Be visible and active only for police |
| `newCasePanel` | `CasePanel` | Track one unsaved form |

| Function header | Requirement |
|---|---|
| `CaseFrame(Person currentUser)` | Reject null/unknown user types and build the frame |
| `private void initializeComponents()` | Create list, scroll pane, and Create button |
| `private void initializeListeners()` | Add no more than one police creation form |
| `private void initializeLayout()` | Call exactly one matching role Controller and add one `CasePanel` per returned case |
| `private void refreshCases()` | Reload cases, rebuild the list, and call `revalidate()`/`repaint()` |

Required dispatch inside case loading:

```java
PoliceController.getCases((Police) currentUser)
VictimController.getCases((Victim) currentUser)
AccusedController.getCases((Accused) currentUser)
```

Only the matching call executes. Controller exceptions are caught and displayed by the view.

### CasePanel

| Function header | Requirement |
|---|---|
| `CasePanel(Person currentUser, CaseRecord caseRecord)` | Display an existing case; police can update it |
| `CasePanel(Police currentUser)` | Display a blank creation form with status `OPEN` |
| `private void initializeComponents()` | Read all six `CaseRecord` getters or create blank inputs |
| `private void initializeListeners()` | Call police create/update or enforce read-only access |
| `private void initializeLayout()` | Arrange the seven label/value rows |

Creation calls:

```java
PoliceController.createCase(
    id.getText(),
    description.getText(),
    victimName.getText(),
    accusedName.getText());
```

Update calls:

```java
PoliceController.updateCase(
    currentUser,
    caseRecord.getCaseId(),
    description.getText(),
    status.getText());
```

Victim and accused panels hide Save and lock status and description. Case ID and linked usernames are never editable after creation. Successful writes refresh `CaseFrame`; successful creation clears `newCasePanel` so a later case can be created.

### Main

`public static void main(String[] args)` must:

1. Initialize the database.
2. Construct one `DatabaseUtility`.
3. Initialize all four Controllers with that same utility.
4. Display `LoginFrame` through `SwingUtilities.invokeLater(...)`.
5. Show one short startup error if initialization fails.

### Member 6 completion checks

- All three roles follow `LoginFrame -> CaseFrame -> CasePanel`.
- Police alone see Create and Save controls.
- Repeated Create clicks do not create multiple unsaved forms.
- A case list refreshes after successful writes.
- Views catch Controller failures without exposing SQL details.
- `UITest` covers dispatch, visibility, editing, and duplicate-form behavior.

## 8. Integration requirements

1. Member 1 completes real model storage before Members 2–4 enable database results.
2. Member 5 finalizes the schema and safe test database before Controller database tests run.
3. Members 2–4 agree on session identity checks and `DatabaseUtility` initialization.
4. Member 6 keeps the current Controller method names and parameters; checked exception declarations may be added.
5. Shared public headers in this file change only with agreement from their caller and owner.
6. Each member tests their own layer before full integration.

## 9. Project acceptance criteria

- The active application uses only `LoginFrame -> CaseFrame -> CasePanel`.
- Login returns the correct concrete user and stores the same session object.
- Police see every case; victims and accused see only linked cases.
- Each returned case appears once with all six correct values.
- Police can create and update cases; other roles cannot mutate them.
- Repeated Create clicks produce only one unsaved form.
- Successful writes refresh the list.
- Models contain no JDBC/Swing code, Controllers contain no Swing code, and Views contain no SQL.
- SQL uses prepared parameters and JDBC resources close safely.
- Backend failures use the defined application exceptions.
- `DBTest` cannot affect `PoliceDB`.
- All Java sources compile and `UserTest`, `DBTest`, and `UITest` pass.
