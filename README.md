# instantpayment

This a demo app for instant payment.

# Getting started

To start the application first start the dependencies which are needed for the application. Navigate to a docker folder in a commandline and run the following command:

```
docker compose up -d
```

To build a docker image use the following command:

```
mvn spring-boot:build-image
```

To run the application without docker:

```
mvn spring-boot:run
```

Swagger API description:

```
http://localhost:8080/swagger-ui/index.html
```

# Local test

To test the application insert two account into the account table:

```
insert into account(account_number, balance, version) values ('12345678-12345678', 100, 1);
insert into account(account_number, balance, version) values ('12345678-12345679', 100, 1);
```

Invoke the pay endpoint with the following body:

```
{
"transactionId": "123456789",
"sourceAccountNumber": "12345678-12345678",
"targetAccountNumber": "12345678-12345679",
"transactionDate": "2025-05-15T09:05:42.067Z",
"transactionAmount": 50
}
```

Verify the transaction and account tables, also check the notification topic in kafka.

You can access the topic in kafka ui:

```
http://localhost:8085/ui/clusters/local/all-topics/notifications
```

# MVP implementations

The application has some basic functionalities with some dummy data.

- It connects to an external PostgreSQL database and can send a message to a kafka topic.
- It's covered by unit tests. 
- The database is versioned by liquibase.
- The payment has optimistic locking for account balances to detect if something changed concurrently during the process.


# Limitations

The current application is an MVP project which could fit in a time limit.

Because of time limit:

- The application doesn't detect if a transaction was sent earlier in a concurrent way.
- It doesn't store failed transaction with all cases currently.
- The notification service does not check about the notification status if it was successfully sent.
- The application lacks integration tests.
- The application lacks proper error handling in the REST side. 
