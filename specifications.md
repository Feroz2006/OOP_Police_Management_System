# Police Management System — MVC Specification

## 1. Shared Rules

Cases are the only managed records. Police can create, view, and update cases. Victims and accused users can view only cases linked to their usernames.

The active Swing workflow is `Main -> LoginFrame -> CaseFrame -> CasePanel`. `HomeFrame`, `PolicePanel`, `VictimPanel`, and `AccusedPanel` are not part of this workflow. Views pass the logged-in `Person` object explicitly: `LoginFrame` passes it to `CaseFrame`, and `CaseFrame` passes it to each `CasePanel` and to the matching role Controller.

The production packages are `model`, `controller`, `view`, `exceptions`, and `database`. Tests remain in `test`.

Every table below gives a complete Java function header. A header without `static` is non-static. Constructors do not have a return type. Members must agree before changing a public header used by another package.

JDBC helper functions may throw `SQLException`. Controllers will convert database failures to `DatabaseException` when database operations replace the current placeholders. Controllers pass application exceptions to Views. Views catch them and show short messages; Controllers do not use `JOptionPane` or any other Swing component.

## 2. Member 1 — User Models and Authentication

### Person

`Person` is the abstract base type carried through the complete view workflow. `CaseFrame` uses its runtime subtype to select a Controller, and `CasePanel` uses the object for permissions and display values.

| Function header | Required use in the workflow |
|---|---|
| `public Person(String personID, String name, int age, String phone, String username, String password)` | Initialize values shared by every user subtype |
| `public String getPersonID()` | Return the stable user ID |
| `public String getName()` | Return the displayed name |
| `public int getAge()` | Return age |
| `public String getPhone()` | Return phone number |
| `public String getUsername()` | Supply the officer name displayed by a new `CasePanel` and the username used to filter cases |
| `public String getPassword()` | Return the stored classroom password |
| `public boolean validateCredentials(String inputusername, String inputPassword)` | Check both login values |
| `public abstract void viewCase(String caseID)` | Perform subtype-specific case viewing behavior |
| `public abstract String getRole()` | Return the subtype role name |

### User subtypes

`LoginController.login(...)` must return one of these concrete types. This is required because `CaseFrame` dispatches with `instanceof`.

| Class | Required constructor | `getRole()` result |
|---|---|---|
| `Police` | `public Police(String personID, String name, int age, String phone, String username, String password, String rank, String department, String BadgeNumber)` | `UserRole.POLICE.name()` |
| `Victim` | `public Victim(String personID, String name, int age, String phone, String username, String password, String CrimeDetails, String EmergencyContact)` | `UserRole.VICTIM.name()` |
| `Accused` | `public Accused(String personID, String name, int age, String phone, String username, String password, String Charges, boolean BailStatus)` | `UserRole.ACCUSED.name()` |

### UserRole

The enum contains `POLICE`, `VICTIM`, and `ACCUSED`.

Member 1 maintains construction, role, credential, and login checks in `UserTest`.

## 3. Members 2, 3, and 4 — Case Model

### CaseRecord

Purpose: represent one case returned by a role Controller. Each existing record is passed to `new CasePanel(currentUser, caseRecord)`.

| Function header | Purpose |
|---|---|
| `public String getCaseId()` | Return the case ID |
| `public String getDescription()` | Return the description |
| `public String getStatus()` | Return the status displayed and passed to `PoliceController.updateCase(...)` |
| `public String getOfficerUsername()` | Return the police username |
| `public String getVictimUsername()` | Return the victim username |
| `public String getAccusedUsername()` | Return the accused username |

The current methods may return placeholder strings until database mapping is implemented. When implemented, the object must store all six values used by `CasePanel`.

## 4. Members 2 and 4 — Controllers

Controllers return data to Swing classes and do not contain component layout code. Each role-specific `getCases(...)` method receives the same concrete user object held by `CaseFrame`.

### LoginController — Member 4

`loggedinUser` is the static Controller session user.

| Function header | Purpose |
|---|---|
| `public static Person login(String username, String password) throws AuthenticationException` | Authenticate, store, and return a concrete `Police`, `Victim`, or `Accused` object |
| `public static void logout()` | Clear `loggedinUser` |
| `public static Person getCurrentUser()` | Return the current user or null |

### PoliceController — Member 2

| Function header | Purpose |
|---|---|
| `public static List<CaseRecord> getCases(Police police)` | Return all cases visible to the supplied police user; currently returns an empty placeholder list |
| `public static void createCase(String caseId, String description, String victimUsername, String accusedUsername)` | Create an open case from the new-case `CasePanel` fields |
| `public static void updateCase(Person currentUser, String caseId, String description, String status)` | Update the existing case represented by an editable police `CasePanel` |

### VictimController — Member 4

| Function header | Purpose |
|---|---|
| `public static List<CaseRecord> getCases(Victim victim)` | Return cases linked to the supplied victim; currently returns an empty placeholder list |

### AccusedController — Member 4

| Function header | Purpose |
|---|---|
| `public static List<CaseRecord> getCases(Accused accused)` | Return cases linked to the supplied accused user; currently returns an empty placeholder list |

When persistence is implemented, each `getCases(...)` method must filter with the supplied user's identifying value, normally `getUsername()`. Member 2 tests police Controller actions. Member 4 tests login, logout, and both read-only Controllers.

## 5. Member 5 — Database

### DatabaseConnection

Purpose: retain one shared MySQL connection.

Variables:

| Variable | Type | Purpose |
|---|---|---|
| `instance` | `DatabaseConnection`, static | Single connection owner |
| `connection` | JDBC `Connection` | Active connection |
| `DATABASE_URL` | `String`, static constant | MySQL server URL |
| `DATABASE_USERNAME` | `String`, static constant | MySQL username |
| `DATABASE_PASSWORD` | `String`, static constant | MySQL password |

Functions:

| Function header | Purpose |
|---|---|
| `private DatabaseConnection()` | Read configuration and open a server connection without a database name; wrap connection failures in `DatabaseException` |
| `public static DatabaseConnection getInstance()` | Return the single connection owner |
| `public Connection getConnection()` | Return the current connection |
| `public void closeConnection()` | Close the connection when open |
| `public void close()` | Call `closeConnection` |
| `public boolean isConnected()` | Return whether the connection is open |

### DatabaseInitializer

The default constructor reads `DB_NAME`, using `PoliceDB` when it is absent. The second constructor lets `DBTest` explicitly select `PoliceDBTest` and the script path. Both constructors use the same initialization steps.

Variables:

| Variable | Type | Purpose |
|---|---|---|
| `dbConnectionClass` | `DatabaseConnection` | Connection owner |
| `dbConnection` | `Connection` | Connection used during setup |
| `DATABASE_NAME` | `String`, final | Database name that this initializer validates and selects |
| `scriptPath` | `Path`, final | SQL script read by this initializer |

Functions:

| Function header | Purpose |
|---|---|
| `public DatabaseInitializer()` | Obtain the server connection, read `DB_NAME`, and use `sql/database_init.sql` |
| `public DatabaseInitializer(String databaseName, Path scriptPath)` | Obtain the server connection and use the given database and script; mainly used by `DBTest` |
| `public void initializeDatabase()` | Validate, create, and select the database, then run the initialization script; wrap JDBC failures in `DatabaseException` |
| `public void createTables()` | Perform the same validation, creation, selection, and safe table setup as `initializeDatabase`; wrap JDBC failures in `DatabaseException` |
| `private String validateDatabaseName(String databaseName)` | Return a name that begins with a letter and contains only letters, digits, or underscores; otherwise throw `ValidationException` |
| `private void createDatabase(String databaseName) throws SQLException` | Create the validated database when it does not exist |
| `private void selectDatabase(String databaseName) throws SQLException` | Select the validated database on the shared connection |
| `private void executeSqlScript()` | Run the configured script path and wrap SQL or file-reading failures in `DatabaseException` |
| `private void executeSqlScript(Path scriptPath) throws SQLException, IOException` | Read UTF-8 SQL and execute each simple statement |

### DatabaseUtility

Variables:

| Variable | Type | Purpose |
|---|---|---|
| `dbConnection` | `Connection` | Connection used by prepared statements |

Functions:

| Function header | Purpose |
|---|---|
| `public DatabaseUtility()` | Obtain the current database connection |
| `public ResultSet executeQuery(String sql, Object... parameters) throws SQLException` | Run a prepared read query; caller closes the result and its statement |
| `public int executeUpdate(String sql, Object... parameters) throws SQLException` | Run a prepared change and return the affected-row count |
| `public void close(AutoCloseable resource) throws SQLException` | Safely close a nullable resource |
| `private void bindParameters(PreparedStatement statement, Object... parameters) throws SQLException` | Bind parameters in their given order |

Member 5 ensures change statements close their prepared statements and read callers close both the result and its prepared statement.

Low-level JDBC helpers, private mapping functions, and direct database-test helpers may declare `SQLException`. `DatabaseInitializer` and the Controller-facing database operations catch JDBC failures and wrap them in `DatabaseException`. The original `SQLException` is kept as the cause for logs and tests.

### Database schema

`database_init.sql` creates only `USERS` and `CASES`. Exact columns, constraints, demo accounts, and test safety rules are in `database_schema.md`.

### DBTest

`DBTest` contains `private static final String TEST_DATABASE_NAME = "PoliceDBTest"`. It checks this value before setup and uses it for initialization and cleanup. It must stop immediately if the value is different.

| Function header | Purpose |
|---|---|
| `public static void main(String[] args)` | Check that the requested name is exactly `PoliceDBTest`, construct `DatabaseInitializer("PoliceDBTest", scriptPath)`, run checks, clean test rows, and close the connection |
| `private static void verifyDatabase(Connection connection) throws SQLException` | Confirm that `USERS` and `CASES` exist |

Tests never drop the production database.
`DBTest` runs in its own process while the application is stopped, so the singleton connection cannot already be using the production database.

## 6. Member 6 — Views and Startup

The active views are `LoginFrame`, `CaseFrame`, and `CasePanel`, all in `view`. The user object is passed explicitly between them.

### Required workflow

1. `Main` constructs and displays `LoginFrame`.
2. `LoginFrame.login()` calls `LoginController.login(username, password)`.
3. A successful login returns a concrete `Police`, `Victim`, or `Accused` as `Person`.
4. `LoginFrame` constructs `CaseFrame(user)` and displays it.
5. `CaseFrame.initializeLayout()` calls exactly one role Controller based on the runtime subtype:
   - `PoliceController.getCases((Police) currentUser)`
   - `VictimController.getCases((Victim) currentUser)`
   - `AccusedController.getCases((Accused) currentUser)`
6. For every returned `CaseRecord`, `CaseFrame` constructs `CasePanel(currentUser, caseRecord)`.
7. Police can also press Create Case, causing `CaseFrame` to construct one `CasePanel((Police) currentUser)` creation form. Repeated clicks must not create multiple unsaved forms.
8. A police `CasePanel` calls `PoliceController.createCase(...)` or `PoliceController.updateCase(...)` when Save is pressed. Victim and accused panels are read-only.

### LoginFrame

Variables:

| Variable | Type | Purpose |
|---|---|---|
| `userField` | `JTextField` | Username input |
| `userLabel` | `JLabel` | Username label |
| `passField` | `JPasswordField` | Password input |
| `passLabel` | `JLabel` | Password label |
| `loginButton` | `JButton` | Starts login |

Functions:

| Function header | Purpose |
|---|---|
| `public LoginFrame()` | Configure the existing login window |
| `private void initializeComponents()` | Create its current fields |
| `private void initializeListeners()` | Connect `loginButton` to `login` |
| `private void initializeLayout()` | Build the current padded grid layout |
| `private void login()` | Call `LoginController.login`, dispose the login window after success, construct `CaseFrame(user)`, and display it; show authentication or other Controller failures in a dialog |
| `private void clearFields()` | Clear inputs and enable login |

### CaseFrame

| Variable | Type | Purpose |
|---|---|---|
| `currentUser` | `Person` | Concrete logged-in user passed by `LoginFrame` |
| `caseList` | `JPanel` | Vertical container for case cards and an optional creation form |
| `scrollPane` | `JScrollPane` | Makes the case list scrollable |
| `createCaseButton` | `JButton` | Adds a creation form for police only |
| `newCasePanel` | `CasePanel` | Tracks the single unsaved creation form and prevents duplicates |

| Function header | Purpose |
|---|---|
| `CaseFrame(Person currentUser)` | Store the user and initialize the frame |
| `private void initializeComponents()` | Create `caseList`, `scrollPane`, and the police-only creation button |
| `private void initializeListeners()` | Add at most one new-case `CasePanel` when police press Create Case |
| `private void initializeLayout()` | Call the matching role Controller, add one `CasePanel` per returned `CaseRecord`, and arrange the frame |

### CasePanel

`CasePanel` is one case form/card. Its constructor determines whether it represents an existing case or a new police-created case.

| Variable | Type | Purpose |
|---|---|---|
| `currentUser` | `Person` | User viewing or editing the card |
| `readOnly` | `boolean` | True for victims and accused users |
| `creatingCase` | `boolean` | True for the police creation form |
| `caseRecord` | `CaseRecord` | Existing record, or null during creation |
| `id`, `status`, `victimName`, `accusedName` | `JTextField` | Display or collect case values |
| `officerName` | `JLabel` | Display the officer username |
| `description` | `JTextArea` | Display or edit the description |
| `saveButton` | `JButton` | Create or update a case; hidden for read-only users |

| Function header | Purpose |
|---|---|
| `CasePanel(Person currentUser, CaseRecord caseRecord)` | Build an existing case card; editable for police and read-only otherwise |
| `CasePanel(Police currentUser)` | Build a new-case form with status `OPEN` and officer name from `currentUser.getUsername()` |
| `private void initializeComponents()` | Read existing values through all six `CaseRecord` getters, or create blank inputs for a new case |
| `private void initializeListeners()` | Hide Save for read-only users; otherwise call the matching police create or update method |
| `private void initializeLayout()` | Arrange the seven label/value rows |

The creation form calls:

```java
PoliceController.createCase(
    id.getText(),
    description.getText(),
    victimName.getText(),
    accusedName.getText());
```

An editable existing case calls:

```java
PoliceController.updateCase(
    currentUser,
    caseRecord.getCaseId(),
    description.getText(),
    status.getText());
```

### Main

| Function header | Purpose |
|---|---|
| `public static void main(String[] args)` | Construct and display `LoginFrame` |

### UITest

| Function header | Purpose |
|---|---|
| `public static void main(String[] args)` | Check the `LoginFrame -> CaseFrame -> CasePanel` workflow and its main controls |

## 7. Exception Classes

The existing exception files move together from `exception` to `exceptions`. Member 4 maintains these shared error types and Member 5 reviews `DatabaseException` use.

`AuthenticationException` and `AuthorizationException` are checked exceptions. `DatabaseException`, `EntityNotFoundException`, and `ValidationException` extend `RuntimeException`; their public headers still list them so each caller's possible failures are clear.

| Class | Function header | Purpose |
|---|---|---|
| `AuthenticationException` | `public AuthenticationException(String message, String attemptedUsername)` | Report failed login and store the attempted username |
| `AuthenticationException` | `public String getAttemptedUsername()` | Return the attempted username |
| `AuthorizationException` | `public AuthorizationException(String message)` | Report a denied action |
| `DatabaseException` | `public DatabaseException(String message)` | Report a database failure |
| `DatabaseException` | `public DatabaseException(String message, Throwable cause)` | Report a database failure with its cause |
| `EntityNotFoundException` | `public EntityNotFoundException(String entityName, String id)` | Report a missing record |
| `ValidationException` | `public ValidationException(String message)` | Report invalid input |

## 8. Integration Order

1. Complete `CaseRecord` storage while keeping all six getter signatures used by `CasePanel`.
2. Replace the placeholder in `LoginController.login(...)` with authentication that returns the correct concrete user subtype.
3. Replace the empty lists in all three `getCases(...)` methods with database-backed, role-filtered results.
4. Implement `PoliceController.createCase(...)` and `PoliceController.updateCase(...)` without changing the view-facing headers.
5. Make `CasePanel` show short dialogs for Controller validation, authorization, missing-record, and database failures.
6. Refresh the `CaseFrame` list after a successful create or update and clear `newCasePanel` after creation succeeds.
7. Update and run `UserTest`, `DBTest`, and `UITest` for the active workflow.

## 9. Acceptance Criteria

- Only the five production packages remain.
- Existing Swing layouts and useful case controls remain recognizable.
- No production class imports `dao`, `service`, `ui`, `exception`, or `util`.
- `LoginFrame` uses `LoginController`; `CaseFrame` uses all three role Controllers; `CasePanel` uses `PoliceController` for writes.
- Members 2 and 4 own the Controller work assigned above.
- Database setup creates only `USERS` and `CASES`.
- Police can create, view, and update cases.
- Victims and accused can view only linked cases.
- `LoginController.login(...)` returns a concrete `Police`, `Victim`, or `Accused` object, never a plain or null user on success.
- `CaseFrame.initializeLayout()` calls exactly one typed `getCases(...)` method and creates one `CasePanel` per returned `CaseRecord`.
- Repeated Create Case clicks do not add multiple unsaved forms.
- Victim and accused `CasePanel` instances hide Save and prevent editing.
- Invalid login, input, permissions, and missing cases use the correct exception class.
- Views display errors; Controllers contain no Swing message code.
- JDBC errors leave Controller-facing operations and database setup as `DatabaseException`, not `SQLException`.
- `CasePanel` extends `JPanel`.
- `DBTest` rejects every database name except `PoliceDBTest`.
- JDBC resources close safely.
- Each member can explain every class and request flow.
