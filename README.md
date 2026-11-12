
# Comments Platform

A distributed comment management and moderation
platform built with Java 21 and Spring Boot.

## Architecture

The platform consists of two independent microservices:

### Comment Service

Responsible for:
- Creating comments.
- Requesting comment moderation.
- Storing approved comments.
- Retrieving approved comments.
- Listing comments with pagination.

### Moderation Service

Responsible for:
- Validating comment content.
- Identifying prohibited words.
- Returning moderation decisions.

## Technology Stack

- Java 21
- Spring Boot 4.1.2
- Maven
- Spring Data JPA
- H2 Database
- Spring RestClient
- JUnit 5
- Mockito

## Repository Structure

```text
comments-platform/
├── comment-service/
├── moderation-service/
├── .gitignore
└── README.md
```

## Project Status

In development.
