# Police Management System — Detailed MVC Specification

## 1. Scope and How to Read This Document

Cases are the only managed records. Police can create, read, and update cases. Victims and accused users can only read cases linked to them. Login and logout exist only to identify the current role.

The project uses the `model`, `view`, `controller`, and `database` production packages. The method and variable names below are shared rules for the team. Members should not rename them without agreement from everyone.

All fields are private unless another visibility is stated. Public methods form the API used by other packages. Every function table gives the complete Java header. A header without `static` is non-static, and a header without an access word is package-private. Constructors have no return type. Private helper methods may be adjusted internally only when their documented header and behavior remain unchanged.

## 2. Member 1 — User Management and Authentication

### Person class

Purpose: provide the common state and behavior shared by every authenticated user.

Variables:

| Variable | Type | Purpose |
|---|---|---|
| `personId` | String | Stable unique identifier from `USERS` |
| `name` | String | Name displayed in `HomeFrame` |
| `username` | String | Unique login name used to link a user to a case |
| `password` | String | Classroom login secret; never displayed or included in text output |

Functions:

| Function header | Purpose and behavior |
|---|---|
| `protected Person(String personId, String name, String username, String password)` | Validate and store all four common values so only subclasses can create a person |
| `public String getPersonId()` | Return the stable person ID |
| `public String getName()` | Return the display name |
| `public String getUsername()` | Return the login username |
| `public boolean passwordMatches(String suppliedPassword)` | Return whether the supplied password matches; return false for null input |
| `public abstract UserRole getRole()` | Require each subclass to return its role |
| `public String toString()` | Return person ID, name, username, and role without the password |

`Person` values cannot be changed after construction, so the class has no setters.

### Police class

Purpose: represent a logged-in police user as a type of `Person`.

It adds no variables.

| Function header | Purpose and behavior |
|---|---|
| `public Police(String personId, String name, String username, String password)` | Pass the four common values to `Person` |
| `public UserRole getRole()` | Return the police role |

### Victim class

Purpose: represent a victim who can read linked cases.

It adds no variables.

| Function header | Purpose and behavior |
|---|---|
| `public Victim(String personId, String name, String username, String password)` | Pass the four common values to `Person` |
| `public UserRole getRole()` | Return the victim role |

### Accused class

Purpose: represent an accused user who can read linked cases.

It adds no variables.

| Function header | Purpose and behavior |
|---|---|
| `public Accused(String personId, String name, String username, String password)` | Pass the four common values to `Person` |
| `public UserRole getRole()` | Return the accused role |

### UserRole enum

Purpose: provide fixed role values used by models, queries, permissions, and views. This avoids spelling mistakes in role names.

It contains exactly `POLICE`, `VICTIM`, and `ACCUSED`.

### AppModel authentication contribution

Member 1 implements these functions inside the shared `AppModel` class:

| Function header | Purpose and behavior |
|---|---|
| `public Person authenticate(String username, String password) throws SQLException` | Clear any previous Model session, validate credentials, map a matching row to the correct subclass, store that exact object as the authenticated user, and return it; return null when credentials are incorrect |
| `public void logout()` | Clear the Model's authenticated-user reference without closing the database connection |
| `private Person mapPerson(ResultSet result) throws SQLException` | Read a `USERS` result row and construct `Police`, `Victim`, or `Accused`; reject an unknown stored role |

### Member 1 tests

Member 1 adds tests for constructors, getters, all three role results, null and correct password matching, password-safe text output, successful login, invalid login, logout, and rejection of a newly constructed lookalike user.

## 3. Member 2 — Case Registration

Purpose: implement the complete police case-creation operation without introducing another record type.

### AppModel creation contribution

| Function header | Purpose and behavior |
|---|---|
| `public void createCase(Person currentPerson, String caseId, String description, String victimUsername, String accusedUsername) throws SQLException` | Check the supplied values, verify permissions and linked-user roles, insert one open case using the police username, and reject duplicates |
| `private boolean caseIdExists(String caseId) throws SQLException` | Return whether a case ID already exists and provide an early readable error; the database primary key still prevents duplicates if two inserts happen at nearly the same time |

`createCase` must call Member 4's shared input and permission helpers instead of repeating those checks. It does not accept an officer username or initial status from the View. If insertion still reports a duplicate-key error, it changes that failure into the same readable duplicate-ID message.

### Member 2 tests

Member 2 adds tests for successful creation, automatic open status, stored officer username, duplicate IDs, blank creation fields, missing linked users, and linked users with incorrect roles.

### AppController case-action contribution

Member 2 implements `handleCreateCase` and `handleUpdateCase` in the shared `AppController`. These functions read police-panel input, start the matching Model operation, clear successful input, and refresh the case table. Member 2 also connects the police panel's create and update actions when helping Member 4 build the dashboard.

## 4. Member 3 — Case Management and Shared Model Structure

### CaseRecord class

Purpose: represent one case in memory without allowing its values to change afterward.

Variables:

| Variable | Type | Purpose |
|---|---|---|
| `caseId` | String | Unique stable case identifier |
| `description` | String | Human-readable case description |
| `status` | `CaseRecord.Status` | Current open or closed state |
| `officerUsername` | String | Police user who created the case |
| `victimUsername` | String | Victim linked to the case |
| `accusedUsername` | String | Accused user linked to the case |

The nested `Status` enum contains exactly `OPEN` and `CLOSED`.

Functions:

| Function header | Purpose and behavior |
|---|---|
| `public CaseRecord(String caseId, String description, String officerUsername, String victimUsername, String accusedUsername)` | Validate the five textual values and create an open case |
| `CaseRecord(String caseId, String description, Status status, String officerUsername, String victimUsername, String accusedUsername)` | Check all values and create an object from a database row using its saved status; package-private and used only by `AppModel` |
| `public String getCaseId()` | Return the unique case ID |
| `public String getDescription()` | Return the case description |
| `public Status getStatus()` | Return the open or closed status |
| `public String getOfficerUsername()` | Return the linked police username |
| `public String getVictimUsername()` | Return the linked victim username |
| `public String getAccusedUsername()` | Return the linked accused username |
| `public boolean involves(String username)` | Return whether the username matches the officer, victim, or accused username; return false for null; never use this general helper to decide which cases a role may see |
| `public String toString()` | Return a readable one-line case summary |

There are no setters. Database changes occur only through `AppModel.updateCase`, followed by a fresh query.

### AppModel class

Purpose: act as the main MVC Model class containing application rules and database operations.

Variables:

| Variable | Type | Purpose |
|---|---|---|
| `databaseConnection` | `DatabaseConnection` | Supplies the active JDBC connection and handles opening/closing it |
| `authenticatedUser` | `Person` | Exact object returned by the latest successful authentication, or null outside a session |
| `closed` | boolean | Prevents work after shutdown and makes repeated closure harmless |

Member 3 owns the class structure, constructor, and closing behavior:

| Function header | Purpose and behavior |
|---|---|
| `public AppModel(DatabaseConnection databaseConnection)` | Require a connected `DatabaseConnection` and store it |
| `private Connection getConnection() throws SQLException` | Return the active JDBC connection or reject operations after closure |
| `public void close() throws SQLException` | Close the database connection once and mark the model closed |

### AppModel case-management contribution

| Function header | Purpose and behavior |
|---|---|
| `public List<CaseRecord> getVisibleCases(Person currentPerson) throws SQLException` | Verify the current stored user, query all cases for police or the correct linked column for other roles, and return rows ordered by case ID |
| `public void updateCase(Person currentPerson, String caseId, String description, CaseRecord.Status status) throws SQLException` | Verify stored police permission, validate case ID, description, and status, update description/status, and reject an unknown case ID |
| `private CaseRecord mapCase(ResultSet result) throws SQLException` | Convert one `CASES` result row into a stored `CaseRecord` whose values cannot change |

Login, creation, case reading, updating, and closing may report `SQLException`. Callers must handle these database errors. Invalid input and denied actions use `IllegalArgumentException` with a message the user can understand.

### AppTest shared class

Purpose: provide one executable test entry point containing clearly separated model and integration checks.

Variables:

| Variable | Type | Purpose |
|---|---|---|
| `TEST_DATABASE_NAME` | String constant | Fixed safe name `PoliceDBTest` |
| `testPrefix` | String | Unique prefix for rows created by the current run |
| `passedCount` | integer | Number of completed checks reported at the end |

Functions maintained by Member 3:

| Function header | Purpose and behavior |
|---|---|
| `public static void main(String[] args) throws Exception` | Validate test configuration, run member test groups in order, clean test rows, and print the result |
| `private static void check(boolean condition, String message)` | Throw an assertion error with the supplied message when the condition is false and increment the count otherwise |
| `private static void expectFailure(Runnable operation, String expectedMessage)` | Check that an operation throws `IllegalArgumentException` containing the expected message |
| `private static void cleanupTestRows(Connection connection, String prefix) throws SQLException` | Delete cases using the prefix first and then users using the prefix so database links are not broken |
| `private static void insertTestUser(Connection connection, String personId, String name, String username, String password, UserRole role) throws SQLException` | Insert one test user using a prepared statement |
| `private static Person[] createTestUsers(Connection connection, String prefix) throws SQLException` | Create and return the police, victim, and accused users needed by database tests |

Member 3 adds case construction, participation, reading, update, ordering, and unknown-case tests. Member 3 maintains the shared test runner; Member 5 implements and reviews its database test-data and cleanup helpers.

## 5. Member 4 — Case Access, Validation, and Cross-Role Tests

Purpose: keep shared checks in one place so the Model enforces permissions and the Controller/View do not repeat them.

### AppModel validation contribution

| Function header | Purpose and behavior |
|---|---|
| `private String requireNonBlank(String value, String fieldName)` | Return trimmed text or throw a readable invalid-argument error naming the missing field |
| `private UserRole findStoredRole(String username) throws SQLException` | Query a username and return its stored role, or null when the user is absent |
| `private void requireStoredUser(Person currentPerson) throws SQLException` | Require the supplied object to be the currently logged-in object, then confirm it still exists with the same ID, username, and role; reject manually created or old user objects |
| `private void requirePolice(Person currentPerson) throws SQLException` | Call `requireStoredUser` and require the police role before changing data |
| `private void requireLinkedUserRole(String username, UserRole expectedRole) throws SQLException` | Require a linked username to exist with the expected victim or accused role |
| `private void requireStatus(CaseRecord.Status status)` | Reject a missing case status |

These helpers are used by Members 1–3. Member 4 reviews `getVisibleCases` to ensure victim and accused queries use the correct linked-user column instead of searching unrelated text.

### Member 4 tests

Member 4 adds tests for manually created user objects, old logged-out users, incorrect linked-user roles, case visibility for each role, blocked creation, blocked updates, blank inputs, and missing status.

### AppController structure contribution

Member 4 owns the shared controller file and all controller fields. Member 4 implements login, dashboard creation, case refresh, busy-state control, logout, error display, and safe shutdown. Member 2 adds only the two case-action handlers described above.

## 6. Member 5 — Database and JDBC

`database_schema.md` is the detailed source for table relationships, demo accounts, initialization behavior, query rules, test safety, and Member 5's migration checklist. The class and function headers remain defined below.

### DatabaseConnection class

Purpose: own one JDBC connection without using one shared global instance.

Variables:

| Variable | Type | Purpose |
|---|---|---|
| `connection` | JDBC `Connection` | Active server connection |

Functions:

| Function header | Purpose and behavior |
|---|---|
| `public DatabaseConnection(String serverUrl, String username, String password) throws SQLException` | Validate the server URL and credentials, open the connection, and report SQL failure to the caller |
| `public Connection getConnection() throws SQLException` | Return the open connection; reject access after closure |
| `public boolean isConnected()` | Return true only when the connection exists and is not closed; return false if its state cannot be read |
| `public void useDatabase(String databaseName) throws SQLException` | Select the database after its name has been validated by `DatabaseInitializer` |
| `public void close() throws SQLException` | Close an open connection; do nothing when already closed |

### DatabaseInitializer class

Purpose: create and prepare the selected database before the UI starts.

Variables:

| Variable | Type | Purpose |
|---|---|---|
| `databaseConnection` | `DatabaseConnection` | Connection used for setup |
| `databaseName` | String | Validated production or test database name |
| `scriptPath` | filesystem path | Location of `database_init.sql` |

Functions:

| Function header | Purpose and behavior |
|---|---|
| `public DatabaseInitializer(DatabaseConnection databaseConnection, String databaseName, Path scriptPath)` | Require the connection, database name, and script path |
| `public void initialize() throws SQLException, IOException` | Validate the name, create and select the database, read the script, and execute its statements in order |
| `private String validateDatabaseName(String databaseName)` | Return the checked name; allow only letters, digits, and underscores, with a letter first |
| `private void createDatabase(String validatedName) throws SQLException` | Create the checked database name when it does not exist |
| `private void executeScript() throws SQLException, IOException` | Read the SQL file as UTF-8 text, split its simple statements, skip blank statements, and run each one |

Initialization reports SQL and file-reading failures to `Main`. It runs before Swing starts.

### database_init.sql

Purpose: define the complete small database structure and demo data that has the same result every time setup runs.

`USERS` columns:

| Column | SQL type and rules | Purpose |
|---|---|---|
| `person_id` | `VARCHAR(20)`, primary key | Primary user identifier |
| `name` | `VARCHAR(100)`, not null | Display name |
| `username` | `VARCHAR(50)`, not null, unique | Login and case-link value |
| `password` | `VARCHAR(100)`, not null | Classroom demo password |
| `role` | enum restricted to the three `UserRole` names, not null | Stored permission role |

`CASES` columns:

| Column | SQL type and rules | Purpose |
|---|---|---|
| `case_id` | `VARCHAR(20)`, primary key | Primary case identifier |
| `description` | `VARCHAR(500)`, not null | Required case description |
| `status` | enum restricted to `OPEN` and `CLOSED`, not null | Current case state |
| `officer_username` | `VARCHAR(50)`, not null, foreign key to `USERS.username` | Police user who created the case |
| `victim_username` | `VARCHAR(50)`, not null, foreign key to `USERS.username` | Linked victim user |
| `accused_username` | `VARCHAR(50)`, not null, foreign key to `USERS.username` | Linked accused user |

Both tables use InnoDB, the `utf8mb4` character set, and the `utf8mb4_unicode_ci` text-comparison setting. This makes username comparisons consistent. Related rows are not deleted automatically because deletion is outside the application.

The script creates both tables when absent. For each fixed demo username, it inserts the account when missing or updates the existing account to the documented ID, name, password, and role. Running setup again therefore gives the same demo accounts without duplicates.

### Database configuration

Member 5 defines and documents these environment variables:

| Variable | Purpose | Default |
|---|---|---|
| `DB_URL` | MySQL server URL without a database name | `jdbc:mysql://localhost:3306/` |
| `DB_USER` | MySQL username | None; required |
| `DB_PASSWORD` | MySQL password | None; required |
| `DB_NAME` | Selected database | `PoliceDB` |

Tests force the name `PoliceDBTest` and refuse destructive cleanup for any other name.

### DatabaseUtility decision

Neither existing `DatabaseUtility` is retained. Member 5 verifies that every statement and result set in `AppModel` uses try-with-resources and that no caller depends on either utility before removal.

### Member 5 tests

Member 5 adds tests for repeated setup, opening/closing connections, keeping production and test data separate, restoring demo users, and closing SQL resources.

## 7. Member 6 — Swing Views

All view classes keep `initializeComponents`, `initializeListeners`, and `initializeLayout` private. They are normal private helper methods, not interface methods. Each stored action starts as an empty action until the Controller replaces it, including the action for closing a window.

### LoginFrame class

Purpose: collect credentials, display login messages, and forward window-closing requests to the Controller. Startup failures occur before this frame exists and are reported by `Main`.

Variables:

| Variable | Type | Purpose |
|---|---|---|
| `usernameField` | text field | Username input |
| `passwordField` | password field | Masked password input |
| `loginButton` | button | Starts authentication |
| `loginAction` | `Runnable` action | Controller action run by the login button |
| `shutdownAction` | `Runnable` action | Controller action run when the window is closed |

Functions:

| Function header | Purpose and behavior |
|---|---|
| `public LoginFrame()` | Configure the frame and call the three private initialization helpers |
| `public void setLoginAction(Runnable loginAction)` | Store the Controller's login action |
| `public void setShutdownAction(Runnable shutdownAction)` | Store the Controller's shared application-close action |
| `public String getUsernameInput()` | Return trimmed username text |
| `public String getPasswordInput()` | Return the entered password for immediate authentication |
| `public void clearInputs()` | Clear both fields after logout or failed login |
| `public void setBusy(boolean busy)` | Disable login when busy and enable it otherwise |
| `public void showMessage(String message)` | Display a readable dialog message |
| `private void initializeComponents()` | Create the frame's components |
| `private void initializeListeners()` | Make button and window events run the currently stored actions |
| `private void initializeLayout()` | Arrange the components |

The frame does not close itself. Its window listener runs `shutdownAction`, allowing the Controller to close the application safely.

### HomeFrame class

Purpose: host session information, the correct role panel, and logout/shutdown controls.

Variables:

| Variable | Type | Purpose |
|---|---|---|
| `currentUserLabel` | label | Displays current name and role |
| `rolePanel` | panel | Active police, victim, or accused panel |
| `logoutButton` | button | Ends the current session |
| `logoutAction` | `Runnable` action | Controller action for logout |
| `shutdownAction` | `Runnable` action | Controller action for closing the application |

Functions:

| Function header | Purpose and behavior |
|---|---|
| `public HomeFrame(Person currentPerson, JPanel rolePanel)` | Accept the current person and role panel, configure the frame, and initialize its UI |
| `public void setLogoutAction(Runnable logoutAction)` | Store the Controller's logout action |
| `public void setShutdownAction(Runnable shutdownAction)` | Store the shared application-close action |
| `public void setBusy(boolean busy)` | Disable logout when busy and enable it otherwise |
| `public void showMessage(String message)` | Display a readable message relative to the home window |
| `private void initializeComponents()` | Create the frame's components |
| `private void initializeListeners()` | Make button and window events run the currently stored actions |
| `private void initializeLayout()` | Arrange the components |

This frame also waits for `shutdownAction` to handle closing instead of closing itself.

### CasePanel class

Purpose: provide one reusable, read-only table component for case display and row selection.

Variables:

| Variable | Type | Purpose |
|---|---|---|
| `caseTable` | table | Displays case fields and selection |
| `tableModel` | non-editable table model | Holds displayed rows while preventing direct editing |

Functions:

| Function header | Purpose and behavior |
|---|---|
| `public CasePanel()` | Create the table and layout |
| `public void showCases(List<CaseRecord> cases)` | Replace existing rows with the supplied case list |
| `public String getSelectedCaseId()` | Return the selected case ID or null when no row is selected |
| `private void initializeComponents()` | Create the table and its non-editable model |
| `private void initializeListeners()` | Prepare table listeners; the function may remain empty |
| `private void initializeLayout()` | Arrange the table inside the panel |

### PolicePanel class

Purpose: combine the shared case table with police-only create and update controls.

Variables:

| Variable group | Purpose |
|---|---|
| `casePanel` | Displays all cases and supplies the selected ID |
| `caseIdField`, `createDescriptionArea`, `victimUsernameField`, `accusedUsernameField` | Case-creation input |
| `updateDescriptionArea`, `statusBox` | Selected-case update input |
| `createButton`, `updateButton`, `refreshButton` | Starts each supported action |
| `createAction`, `updateAction`, `refreshAction` | Controller actions, initially empty |

Functions:

| Function header | Purpose and behavior |
|---|---|
| `public PolicePanel()` | Create its own `CasePanel`, controls, actions, and layout |
| `public CasePanel getCasePanel()` | Return the contained case table component |
| `public String getCaseIdInput()` | Return trimmed case ID creation input |
| `public String getCreateDescriptionInput()` | Return trimmed creation description |
| `public String getVictimUsernameInput()` | Return trimmed victim username input |
| `public String getAccusedUsernameInput()` | Return trimmed accused username input |
| `public String getSelectedCaseId()` | Ask `CasePanel` for the selected case ID and return it |
| `public String getUpdateDescriptionInput()` | Return trimmed replacement description |
| `public CaseRecord.Status getUpdateStatus()` | Return the selected open or closed status |
| `public void setCreateAction(Runnable createAction)` | Register the Controller's create action |
| `public void setUpdateAction(Runnable updateAction)` | Register the Controller's update action |
| `public void setRefreshAction(Runnable refreshAction)` | Register the Controller's refresh action |
| `public void clearCreateInputs()` | Clear creation fields after successful insertion |
| `public void clearUpdateInputs()` | Clear update fields after a successful update |
| `public void setBusy(boolean busy)` | Disable the create, update, and refresh controls when busy and enable them otherwise |
| `private void initializeComponents()` | Create the case table and police controls |
| `private void initializeListeners()` | Make button events run the currently stored actions |
| `private void initializeLayout()` | Arrange the controls and case table |

### VictimPanel and AccusedPanel classes

Purpose: provide clearly named, read-only dashboards for their corresponding roles.

Each class has a `casePanel`, `refreshButton`, and `refreshAction`, which is empty until the Controller sets it.

Each class provides these functions:

| Function header | Purpose and behavior |
|---|---|
| `public VictimPanel()` | Create the victim panel's `CasePanel`, refresh button, action, and layout |
| `public AccusedPanel()` | Create the accused panel's `CasePanel`, refresh button, action, and layout |
| `public CasePanel getCasePanel()` | Return the contained case table component |
| `public void setRefreshAction(Runnable refreshAction)` | Register the Controller's refresh action |
| `public void setBusy(boolean busy)` | Disable refresh when busy and enable it otherwise |
| `private void initializeComponents()` | Create the case table and refresh control |
| `private void initializeListeners()` | Make the refresh button run the currently stored action |
| `private void initializeLayout()` | Arrange the small read-only panel |

The shared non-constructor headers above appear once in each class.

## 8. Members 2 and 4 — AppController

### AppController class

Purpose: coordinate views and Model operations without containing SQL or application rules.

Member 4 maintains the class, fields, and general flow. Member 2 owns `handleCreateCase` and `handleUpdateCase`. Both members review the final file together before Member 6 connects it to the views.

Variables:

| Variable | Type | Purpose |
|---|---|---|
| `model` | `AppModel` | Application operations and rules |
| `loginFrame` | `LoginFrame` | Reused login window |
| `homeFrame` | `HomeFrame` | Active session window, or null before login |
| `currentUser` | `Person` | Authenticated session identity |
| `activeRolePanel` | panel | Current role-specific panel |
| `activeCasePanel` | `CasePanel` | Current target for case results |
| `policePanel` | `PolicePanel` | Police inputs when the active user is police |
| `victimPanel` | `VictimPanel` | Victim refresh controls when the active user is a victim |
| `accusedPanel` | `AccusedPanel` | Accused refresh controls when the active user is accused |
| `activeWorker` | `SwingWorker` | Database task currently running away from the UI thread, or null when idle |
| `shuttingDown` | boolean | Prevents duplicate shutdown work |

Functions:

| Function header | Owner | Purpose and behavior |
|---|---|---|
| `public AppController(AppModel model, LoginFrame loginFrame)` | Member 4 | Store Model/login references and register login and shutdown actions |
| `public void start()` | Member 4 | Display the login frame |
| `private void handleLogin()` | Member 4 | Read credentials, authenticate in the background, then open the correct dashboard or show an error |
| `private void openDashboard(Person person)` | Member 4 | Store the authenticated person, create the correct role panel, keep its case panel, register actions, create `HomeFrame`, and load cases |
| `private void handleCreateCase()` | Member 2 | Read police creation input, call `AppModel.createCase` in the background, clear input, and refresh |
| `private void handleUpdateCase()` | Member 2 | Require a selected case, read update input, call `AppModel.updateCase` in the background, clear input, and refresh |
| `private void refreshCases()` | Member 4 | Load visible cases in the background and pass them to `activeCasePanel` |
| `private void setActiveViewBusy(boolean busy)` | Member 4 | Disable or enable `HomeFrame` and the correct police, victim, or accused panel |
| `private void handleLogout()` | Member 4 | Call `AppModel.logout`, dispose the home frame, clear session/panel references and inputs, then show login without closing the database |
| `private void showFailure(Throwable failure)` | Member 4 | Log technical details and display a short user-facing message |
| `private void shutdown()` | Member 4 | Close `AppModel` exactly once and terminate the application |

Every database handler stores one `activeWorker`. It runs Model calls away from the Swing UI thread. When finished, it updates Swing components on the UI thread, clears `activeWorker`, and enables the controls again. Users cannot start another operation or log out until the current task finishes.

If either window is closed while `activeWorker` is still running, `shutdown` shows a short “operation in progress” message and leaves the window and connection open. Closing works after the task finishes. This prevents the connection from closing while JDBC is using it.

## 9. Member 6 — Startup and Final Integration

### Main class

Purpose: assemble the application and contain no case or UI logic.

Local variables hold database configuration, `DatabaseConnection`, `DatabaseInitializer`, `AppModel`, `LoginFrame`, and `AppController` only as long as startup requires them.

Functions:

| Function header | Purpose and behavior |
|---|---|
| `public static void main(String[] args)` | Read settings, connect and set up the database outside the Swing UI thread, then start the View and Controller on the UI thread |
| `private static String requiredEnvironmentValue(String variableName)` | Read a required environment variable and fail with a clear startup message when absent |
| `private static void closeAfterStartupFailure(DatabaseConnection databaseConnection, Throwable startupFailure)` | Close a partially opened connection without hiding the original startup error |

### Member 6 integration work

Member 6 updates the README, verifies the MySQL connector classpath, ensures `database_init.sql` is found from the documented working directory, and runs the final acceptance workflow for all roles.

## 10. How the Members' Parts Connect

### Login

`LoginFrame` action → `AppController.handleLogin` → `AppModel.authenticate` → Member 1 row mapping → dashboard creation.

### Case creation

`PolicePanel` action → `AppController.handleCreateCase` → Member 2 creation function → Member 4 permission and linked-user checks → Member 5-reviewed prepared SQL insert → table refresh.

### Case reading

Role-panel refresh action → `AppController.refreshCases` → Member 3 visible-case query → Member 4 access review → `CasePanel.showCases`.

### Case update

`PolicePanel` action → `AppController.handleUpdateCase` → Member 3 update function → Member 4 police check → Member 5-reviewed prepared update → table refresh.

## 11. Work and Integration Order

- Members work against the names and responsibilities in this document.
- Add shared `AppModel` work in this order: login, creation, reading/updating, then permission review.
- Member 4 creates the `AppController` structure first; Member 2 then adds the case create/update handlers; Member 6 performs the final UI connection check.
- Move retained Swing classes from `ui` to `view` and update their packages/imports together.
- Replace person constructors and all call sites in one coordinated change.
- Replace the old schema before enabling new Model queries.
- Remove both database utilities only after all callers use try-with-resources.
- Remove obsolete files only after the new structure compiles and tests pass.
- No Java or SQL implementation change is made without team approval.

## 12. Acceptance Criteria

- A clean checkout compiles and starts with documented configuration.
- Initialization creates only `USERS` and `CASES` and restores the demo accounts.
- All three roles authenticate successfully.
- Police create, read, and update cases.
- Victims and accused read only their linked cases.
- Non-police changes fail in `AppModel` even when called without the UI.
- Duplicate and unknown case IDs produce readable errors.
- Database operations do not freeze Swing.
- Logout creates a clean session without closing the connection.
- Closing either frame releases the connection once.
- Tests use only `PoliceDBTest` and clean only their prefixed rows.
- Every member can explain every class and each complete request flow.
