# Police Management System — MVC Specification

## 1. Shared Rules

Cases are the only managed records. Police can create, view, and update cases. Victims and accused users can view only cases linked to their usernames.

The production packages are `model`, `controller`, `view`, `exceptions`, and `database`. Tests remain in `test`.

Every table below gives a complete Java function header. A header without `static` is non-static. Constructors do not have a return type. Members must agree before changing a public header used by another package.

JDBC helper functions may throw `SQLException`. Public `AppModel` functions catch it and throw `DatabaseException` with the original exception as the cause. Controllers pass application exceptions to Views. Views catch them and show short messages; Controllers do not use `JOptionPane` or any other Swing component.

## 2. Member 1 — User Models and Authentication

### Person

Purpose: hold the values shared by all users.

Variables:

| Variable | Type | Purpose |
|---|---|---|
| `personId` | `String` | Stable user ID |
| `name` | `String` | User's displayed name |
| `username` | `String` | Login and case-link name |
| `password` | `String` | Classroom login password |

Functions:

| Function header | Purpose |
|---|---|
| `protected Person(String personId, String name, String username, String password)` | Store the common values |
| `public String getPersonId()` | Return the user ID |
| `public String getName()` | Return the displayed name |
| `public String getUsername()` | Return the username |
| `public boolean validateCredentials(String inputUsername, String inputPassword)` | Return whether both login values match; return false for null input |
| `public abstract UserRole getRole()` | Return the subclass role |
| `public String toString()` | Return a summary without the password |

The stored values do not have setters.

### Police

Purpose: represent a police user.

| Function header | Purpose |
|---|---|
| `public Police(String personId, String name, String username, String password)` | Pass common values to `Person` |
| `public UserRole getRole()` | Return `POLICE` |

### Victim

Purpose: represent a victim with read-only case access.

| Function header | Purpose |
|---|---|
| `public Victim(String personId, String name, String username, String password)` | Pass common values to `Person` |
| `public UserRole getRole()` | Return `VICTIM` |

### Accused

Purpose: represent an accused user with read-only case access.

| Function header | Purpose |
|---|---|
| `public Accused(String personId, String name, String username, String password)` | Pass common values to `Person` |
| `public UserRole getRole()` | Return `ACCUSED` |

### UserRole

The enum contains `POLICE`, `VICTIM`, and `ACCUSED`.

### AppModel authentication functions

| Function header | Purpose |
|---|---|
| `public Person authenticate(String username, String password) throws AuthenticationException, DatabaseException` | Find and store the matching user, report invalid credentials, or convert a JDBC failure to `DatabaseException` |
| `public void logout()` | Clear the stored logged-in user without closing the connection |
| `private Person mapPerson(ResultSet result) throws SQLException` | Create the correct `Person` subclass from a user row |

Member 1 maintains the user checks in `UserTest`.

| Function header | Purpose |
|---|---|
| `public static void main(String[] args)` | Run user construction, role, credential, and authentication checks |
| `private static void check(boolean condition, String message)` | Report a failed check or count a passed check |

## 3. Members 2, 3, and 4 — Case Model

### CaseRecord

Purpose: represent one case returned by the database.

Variables:

| Variable | Type | Purpose |
|---|---|---|
| `caseId` | `String` | Unique case ID |
| `description` | `String` | Case description |
| `status` | `CaseRecord.Status` | `OPEN` or `CLOSED` |
| `officerUsername` | `String` | Police user who created it |
| `victimUsername` | `String` | Linked victim |
| `accusedUsername` | `String` | Linked accused user |

Functions:

| Function header | Purpose |
|---|---|
| `public CaseRecord(String caseId, String description, Status status, String officerUsername, String victimUsername, String accusedUsername)` | Check and store one complete case |
| `public String getCaseId()` | Return the case ID |
| `public String getDescription()` | Return the description |
| `public Status getStatus()` | Return the status |
| `public String getOfficerUsername()` | Return the police username |
| `public String getVictimUsername()` | Return the victim username |
| `public String getAccusedUsername()` | Return the accused username |
| `public String toString()` | Return a one-line summary |

The nested `Status` enum contains `OPEN` and `CLOSED`. Case values do not have setters.

### AppModel

Purpose: contain application rules and database operations formerly spread across DAO and service classes.

Variables:

| Variable | Type | Purpose |
|---|---|---|
| `databaseConnection` | `DatabaseConnection` | Owns the active connection |
| `databaseUtility` | `DatabaseUtility` | Runs prepared database statements |
| `authenticatedUser` | `Person` | Current logged-in user or null |
| `closed` | `boolean` | Prevents work after shutdown |

Shared structure owned by Member 3:

| Function header | Purpose |
|---|---|
| `public AppModel(DatabaseConnection databaseConnection)` | Store a connected database object and create its utility |
| `public void close()` | Close the connection once; convert a JDBC close failure to `DatabaseException` |
| `private CaseRecord mapCase(ResultSet result) throws SQLException` | Convert one result row to `CaseRecord` |

Case creation and update owned by Member 2:

| Function header | Purpose |
|---|---|
| `public void createCase(Person currentUser, String caseId, String description, String victimUsername, String accusedUsername) throws AuthorizationException, ValidationException, DatabaseException` | Insert an open case for the logged-in police user; convert JDBC failures to `DatabaseException` |
| `public void updateCase(Person currentUser, String caseId, String description, CaseRecord.Status status) throws AuthorizationException, ValidationException, EntityNotFoundException, DatabaseException` | Update one case's description and status; convert JDBC failures to `DatabaseException` |
| `private boolean caseIdExists(String caseId) throws SQLException` | Return whether the ID is already stored |

Case reading owned by Member 3:

| Function header | Purpose |
|---|---|
| `public List<CaseRecord> getVisibleCases(Person currentUser) throws AuthorizationException, DatabaseException` | Return all cases for police or linked cases for other roles, ordered by case ID; convert JDBC failures to `DatabaseException` |

Permission and input checks owned by Member 4:

| Function header | Purpose |
|---|---|
| `private String requireNonBlank(String value, String fieldName)` | Return trimmed text or throw `ValidationException` |
| `private void requireLoggedInUser(Person currentUser) throws AuthorizationException` | Require the current authenticated object |
| `private void requirePolice(Person currentUser) throws AuthorizationException` | Require a logged-in police user |
| `private void requireLinkedRole(String username, UserRole expectedRole) throws SQLException, ValidationException` | Confirm that a linked username has the required role |

## 4. Members 2 and 4 — Controllers

Controllers call `AppModel` and return results to the existing Swing classes. They do not contain SQL or component layout code.

### LoginController — Member 4

Variables:

| Variable | Type | Purpose |
|---|---|---|
| `loggedinUser` | `Person`, static | Current Controller session user |
| `appModel` | `AppModel`, static | Shared Model used for authentication |

Functions:

| Function header | Purpose |
|---|---|
| `public static void initialize(AppModel model)` | Store the Model during startup |
| `public static Person login(String username, String password) throws AuthenticationException, DatabaseException` | Authenticate, store, and return the current user |
| `public static void logout()` | Clear the Controller and Model sessions |
| `public static Person getCurrentUser()` | Return the current user or null |

### PoliceController — Member 2

Variables:

| Variable | Type | Purpose |
|---|---|---|
| `appModel` | `AppModel`, static | Shared case Model |

Functions:

| Function header | Purpose |
|---|---|
| `public static void initialize(AppModel model)` | Store the Model during startup |
| `public static void createCase(String caseId, String description, String victimUsername, String accusedUsername) throws AuthorizationException, ValidationException, DatabaseException` | Create a case for `LoginController.getCurrentUser()` |
| `public static void updateCase(String caseId, String description, CaseRecord.Status status) throws AuthorizationException, ValidationException, EntityNotFoundException, DatabaseException` | Update the selected case |
| `public static List<CaseRecord> viewCases() throws AuthorizationException, DatabaseException` | Return all cases visible to police |

### VictimController — Member 4

It contains the static `AppModel appModel` variable used for victim case access.

| Function header | Purpose |
|---|---|
| `public static void initialize(AppModel model)` | Store the shared Model |
| `public static List<CaseRecord> viewCases() throws AuthorizationException, DatabaseException` | Return cases linked to the current victim |

### AccusedController — Member 4

It contains the static `AppModel appModel` variable used for accused case access.

| Function header | Purpose |
|---|---|
| `public static void initialize(AppModel model)` | Store the shared Model |
| `public static List<CaseRecord> viewCases() throws AuthorizationException, DatabaseException` | Return cases linked to the current accused user |

Member 2 tests police Controller actions. Member 4 tests login, logout, and both read-only Controllers.

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

Low-level JDBC helpers, private mapping functions, and direct database-test helpers may declare `SQLException`. `DatabaseInitializer` and public `AppModel` functions catch JDBC failures and wrap them in `DatabaseException`. The original `SQLException` is kept as the cause for logs and tests.

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

The six Swing classes move from `ui` to `view`. Their component fields and three initialization methods remain in their original classes. The common UI interface is not required because its methods are private.

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
| `private void login()` | Read inputs and call `LoginController.login`; catch `AuthenticationException` or `DatabaseException`, show a short message, and otherwise open `HomeFrame` |
| `private void clearFields()` | Clear inputs and enable login |

### HomeFrame

Variables retain their current names:

| Variable | Type | Purpose |
|---|---|---|
| `currentUser` | `Person` | Logged-in user |
| `userInfo` | `JPanel` | Contains the user labels |
| `personPanel` | `JPanel` | Police, victim, or accused content |
| `logoutButton` | `JButton` | Logs out |
| `username` | `JLabel` | Displays the username |
| `role` | `JLabel` | Displays the role |
| `id` | `JLabel` | Displays the user ID |

Functions:

| Function header | Purpose |
|---|---|
| `public HomeFrame(Person currentUser)` | Store the user and build the window; public access allows `UITest` to open it from the `test` package |
| `private void initializeComponents()` | Select the correct role panel and create the existing labels and button using `currentUser` values |
| `private void initializeListeners()` | Connect logout |
| `private void initializeLayout()` | Keep the current border layout |
| `private void logout()` | Clear the Controller session, close this window, and show login |

### PolicePanel

`PolicePanel` keeps the existing case-section names `title`, `casePanel`, `caseTitle`, `createCase`, `updateCase`, and `viewCase`. Controls unrelated to cases are removed. New input fields are added only where required for case creation and update.

| Variable | Type | Purpose |
|---|---|---|
| `title` | `JLabel` | Police dashboard title |
| `casePanel` | `CasePanel` | Displays cases and selection |
| `caseTitle` | `JLabel` | Case-section title |
| `createCase` | `JButton` | Starts case creation |
| `updateCase` | `JButton` | Updates the selected case |
| `viewCase` | `JButton` | Refreshes visible cases |
| `caseIDField` | `JTextField` | New case ID |
| `descriptionArea` | `JTextArea` | New case description |
| `victimUsernameField` | `JTextField` | Linked victim username |
| `accusedUsernameField` | `JTextField` | Linked accused username |
| `updateDescriptionArea` | `JTextArea` | Replacement description |
| `statusBox` | `JComboBox<CaseRecord.Status>` | Replacement status |

| Function header | Purpose |
|---|---|
| `public PolicePanel(Police police)` | Build the police case panel; public access allows `HomeFrame` and `UITest` to open it |
| `private void initializeComponents()` | Create the case table, case inputs, and case buttons |
| `private void initializeListeners()` | Connect buttons to case functions |
| `private void initializeLayout()` | Arrange only the case controls |
| `private void createCase()` | Read inputs and call `PoliceController.createCase`; catch and display authorization, validation, or database errors |
| `private void updateCase()` | Read the selected case and call `PoliceController.updateCase`; catch and display authorization, validation, missing-case, or database errors |
| `private void viewCase()` | Load cases through `PoliceController`; catch and display authorization or database errors |

### CasePanel

`CasePanel` extends `JPanel` so it can be placed directly inside each role panel.

| Variable | Type | Purpose |
|---|---|---|
| `caseTable` | `JTable` | Displays cases and row selection |
| `tableModel` | `DefaultTableModel` | Stores non-editable displayed rows |

| Function header | Purpose |
|---|---|
| `public CasePanel()` | Create the case table |
| `public void showCases(List<CaseRecord> cases)` | Replace displayed rows |
| `public String getSelectedCaseId()` | Return the selected ID or null |
| `private void initializeComponents()` | Create the table and non-editable model |
| `private void initializeListeners()` | Set single-row selection |
| `private void initializeLayout()` | Place the table in a scroll pane |

### VictimPanel

| Variable | Type | Purpose |
|---|---|---|
| `casePanel` | `CasePanel` | Displays linked cases |
| `viewCase` | `JButton` | Refreshes linked cases |

| Function header | Purpose |
|---|---|
| `public VictimPanel()` | Build a read-only case panel and refresh button |
| `private void initializeComponents()` | Create the retained case controls |
| `private void initializeListeners()` | Connect refresh to `viewCases` |
| `private void initializeLayout()` | Arrange the button and case table |
| `private void viewCases()` | Load linked cases through `VictimController`; catch and display authorization or database errors |

### AccusedPanel

| Variable | Type | Purpose |
|---|---|---|
| `casePanel` | `CasePanel` | Displays linked cases |
| `viewCase` | `JButton` | Refreshes linked cases |

| Function header | Purpose |
|---|---|
| `public AccusedPanel()` | Build a read-only case panel and refresh button |
| `private void initializeComponents()` | Create the retained case controls |
| `private void initializeListeners()` | Connect refresh to `viewCases` |
| `private void initializeLayout()` | Arrange the button and case table |
| `private void viewCases()` | Load linked cases through `AccusedController`; catch and display authorization or database errors |

### Main

| Function header | Purpose |
|---|---|
| `public static void main(String[] args)` | Prepare the database and Model, initialize all four Controllers, and display `LoginFrame`; catch startup `DatabaseException`, log its cause, and show one short startup message |

### UITest

| Function header | Purpose |
|---|---|
| `public static void main(String[] args)` | Open each retained Swing class and check its main controls |

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

1. Move all six retained Swing files to `view` and update package declarations and imports.
2. Move the five error classes from `exception` to `exceptions` and update imports.
3. Remove excluded controls and old UI-only files.
4. Implement `CaseRecord` and update user constructors and their call sites together.
5. Update the database classes and replace the schema.
6. Confirm that normal startup selects `DB_NAME` and `DBTest` explicitly selects only `PoliceDBTest`.
7. Implement the shared `AppModel` structure and Member 1 authentication functions.
8. Add Member 4 permission functions before Member 2 and Member 3 case functions call them.
9. Implement `LoginController`, followed by the three role Controllers.
10. Make every View action catch the Controller exceptions listed in its description.
11. Remove `dao`, `service`, and `util` only after no imports reference them.
12. Update and run `UserTest`, `DBTest`, and `UITest`.
13. Remove remaining obsolete files only after compilation and tests succeed.

## 9. Acceptance Criteria

- Only the five production packages remain.
- Existing Swing layouts and useful case controls remain recognizable.
- No production class imports `dao`, `service`, `ui`, `exception`, or `util`.
- All four Controller classes are used.
- Members 2 and 4 own the Controller work assigned above.
- Database setup creates only `USERS` and `CASES`.
- Police can create, view, and update cases.
- Victims and accused can view only linked cases.
- Invalid login, input, permissions, and missing cases use the correct exception class.
- Views display errors; Controllers contain no Swing message code.
- JDBC errors leave `AppModel` and database setup as `DatabaseException`, not `SQLException`.
- `HomeFrame` and `PolicePanel` can be opened by `UITest` from the `test` package.
- `CasePanel` extends `JPanel`.
- `DBTest` rejects every database name except `PoliceDBTest`.
- JDBC resources close safely.
- Each member can explain every class and request flow.
