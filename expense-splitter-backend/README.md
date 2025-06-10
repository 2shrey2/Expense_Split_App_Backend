# Expense Splitter Backend

This is a Spring Boot application for managing and splitting expenses among a group of people.

## Project Structure

- `src/main/java/com/expensesplitter/backend/`: Main application code
  - `ExpenseSplitterBackendApplication.java`: Main Spring Boot application class.
  - `controller/`: REST API controllers (ExpenseController, SettlementController, PersonController).
  - `service/`: Business logic services (ExpenseService, PersonService, SettlementService).
  - `model/`: Data models
    - `entity/`: JPA entities (Person, Expense, ExpenseParticipant, ShareType, BaseEntity).
    - `dto/`: Data Transfer Objects for requests and responses.
  - `repository/`: Spring Data JPA repositories.
  - `exception/`: Custom exceptions and GlobalExceptionHandler.
  - `config/`: Application configuration (though most is in `application.properties`).
- `src/main/resources/`:
  - `application.properties`: Main application configuration (database, server port, logging).
  - `static/`, `templates/`: For serving static content or templates (if any, currently not used for API).
- `pom.xml`: Maven project configuration, dependencies.

## Prerequisites

- Java 17 or higher
- Maven 3.6.x or higher
- PostgreSQL database running

## Setup Instructions

1.  **Clone the repository:**
    ```bash
    git clone <repository-url>
    cd expense-splitter-backend
    ```

2.  **Configure PostgreSQL:**
    - Create a PostgreSQL database (e.g., `expense_splitter_db`).
    - Update `src/main/resources/application.properties` with your PostgreSQL connection details:
      ```properties
      spring.datasource.url=jdbc:postgresql://localhost:5432/your_database_name
      spring.datasource.username=your_db_username
      spring.datasource.password=your_db_password
      ```

3.  **Build the project:**
    ```bash
    mvn clean install
    ```

4.  **Run the application:**
    ```bash
    mvn spring-boot:run
    ```
    The application will start on `http://localhost:8080` (or the port configured in `application.properties`).

## API Endpoints

Base URL: `http://localhost:8080/api`

### Persons

-   `GET /people`: Get all persons.
-   `GET /people/{id}`: Get a person by ID.
-   `GET /people/name/{name}`: Get a person by name.
    *Note: Persons are usually created automatically when an expense involves a new person.*

### Expenses

-   `POST /expenses`: Create a new expense.
    *Request Body Example (`CreateExpenseRequest`):*
    ```json
    {
      "amount": 100.00,
      "description": "Dinner with friends",
      "paidBy": "Alice",
      "category": "Food",
      "participants": [
        {
          "name": "Alice",
          "shareType": "EQUAL" 
        },
        {
          "name": "Bob",
          "shareType": "EQUAL"
        },
        {
          "name": "Charlie",
          "shareType": "EXACT",
          "value": 25.50
        }
      ]
    }
    ```
    *Supported `shareType` values: `EQUAL`, `EXACT`, `PERCENTAGE`.*
    *For `EXACT` and `PERCENTAGE`, the `value` field is required.*

-   `GET /expenses`: Get all expenses (sorted by creation date descending).
-   `GET /expenses/{id}`: Get an expense by ID.
-   `PUT /expenses/{id}`: Update an existing expense.
    *Request Body Example (`UpdateExpenseRequest` - only include fields to update):*
    ```json
    {
      "amount": 120.00,
      "description": "Updated dinner description"
    }
    ```
-   `DELETE /expenses/{id}`: Delete an expense by ID.

### Settlements

-   `GET /settlements`: Get a list of simplified transactions required to settle all debts.
    *Response Example:*
    ```json
    {
      "success": true,
      "message": "Successfully retrieved settlements",
      "data": [
        {
          "fromPerson": "Bob",
          "toPerson": "Alice",
          "amount": 37.25
        }
      ],
      "timestamp": "..."
    }
    ```
-   `GET /settlements/balances`: Get the current balance for each person (positive if owed, negative if owes).
    *Response Example:*
    ```json
    {
      "success": true,
      "message": "Successfully retrieved balances",
      "data": [
        {
          "personName": "Alice",
          "balance": 74.50
        },
        {
          "personName": "Bob",
          "balance": -37.25
        },
        {
          "personName": "Charlie",
          "balance": -37.25
        }
      ],
      "timestamp": "..."
    }
    ```

## Error Handling

The API returns standardized JSON error responses:

```json
{
  "success": false,
  "message": "Error message details...",
  "data": null, // Or specific error details for validation errors
  "timestamp": "..."
}
```

Common HTTP Status Codes Used:
-   `200 OK`: Successful GET or PUT/DELETE operation.
-   `201 Created`: Successful POST operation.
-   `400 Bad Request`: Invalid input, validation errors.
-   `404 Not Found`: Resource not found.
-   `500 Internal Server Error`: Unexpected server-side error.

## Settlement Algorithm

1.  **Calculate Balances**: For each person, sum up all amounts they've paid and subtract all their shares from expenses they participated in. This gives a net balance (positive if they are owed money, negative if they owe money).
2.  **Simplify Debts**: 
    a.  Separate people into two groups: those who owe money (debtors) and those who are owed money (creditors).
    b.  Iteratively match debtors with creditors. The smallest debt or credit is settled. For example, if DebtorA owes $10 and CreditorX is owed $50, DebtorA pays $10 to CreditorX. DebtorA is now settled. CreditorX is now owed $40.
    c.  Repeat until all debts are settled. This approach minimizes the number of transactions.

## Known Limitations / Future Enhancements

-   Currency is not explicitly handled (assumed to be consistent).
-   No user authentication/authorization.
-   Limited support for complex split scenarios (e.g., itemized splits within an expense).
-   Pagination for `GET /expenses` and `GET /people` is not yet implemented.
-   More sophisticated logging and monitoring.
-   Unit and integration tests need to be expanded.

## Building and Running Tests (Placeholder)

```bash
mvn test
```
