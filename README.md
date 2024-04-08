# Transaction Manager API

This is a Reactive Spring Boot application to validate booking transactions
based on the details mentioned in [ASSIGNMENT.md](ASSIGNMENT.md) .

## Prerequisites

- Java 21
- Docker

## Getting Started

1. Clone the repository:

   ```sh
   git clone https://github.com/your/repository.git

2. Build the application:
   ```sh
   ./gradlew build

3. Run the application:
   ```sh

   docker build -t transaction-manager .
   docker run -p 8080:8080 transaction-manager

## Rest APIs

1. Validate Transactions:
   Validates booking transactions.

* URL: /api/v1/transactions/validate
* Method: POST
* Request Body:

```bash
     {
     "transactions": [
      "John,Doe,john@doe.com,190,TR0001",
"John,Doe1,john@doe1.com,200,TR0002",
"John,Doe2,john@doe2.com,210,TR0003",
"John,Doe,john@doe.com,9,TR0004",
"John,Doe,john@doe.com,2,TR0005"
],
     "creditLimit": 1000.0
     }
```

* Response:

```bash
{
    "Rejected Transactions": [
        {
            "Transaction Number": "TR0001",
            "First Name": "John",
            "Last Name": "Doe",
            "Email Id": "john@doe.com",
            "Transaction Amount": "190"
        },
        {
            "Transaction Number": "TR0001",
            "First Name": "John",
            "Last Name": "Doe1",
            "Email Id": "john@doe1.com",
            "Transaction Amount": "200"
        },
        {
            "Transaction Number": "TR0003",
            "First Name": "John",
            "Last Name": "Doe2",
            "Email Id": "john@doe2.com",
            "Transaction Amount": "210"
        }
    ]
}
```

```bash
curl -X POST http://localhost:8080/api/v1/transactions/validate -H "Content-Type: application/json" -d '{"transactions":["John,Doe,john@doe.com,190,TR0001",
"John,Doe1,john@doe1.com,200,TR0001"],"creditLimit":100.0}'
```

```bash
curl -X POST http://localhost:8080/api/v1/transactions/validate -H "Content-Type: application/json" -d '{
     "transactions": [
      "John,Doe,john@doe.com,190,TR0001",
"John,Doe1,john@doe1.com,200,TR0002",
"John,Doe2,john@doe2.com,210,TR0003",
"John,Doe,john@doe.com,9,TR0004",
"John,Doe,john@doe.com,2,TR0005"
],
     "creditLimit": 200.0
     }'
```