# Student Database Manager

A Java-based CRUD application for managing student records using JDBC and an in-memory Derby database. Features a command-line interface and demonstrates object-relational mapping patterns.

## Features

- CRUD operations for student and degree records
- In-memory Derby database with pre-populated test data
- Command-line interface for querying and exporting data
- CSV export functionality
- Performance-optimized caching layer

## Prerequisites

- Java 17
- Maven 3.9.0+

## Setup

Build the project:
```bash
mvn clean package
```

## Usage

Run the executable JAR:
```bash
# Fetch specific student
java -jar target/studentmanager.jar -select id42

# List all students
java -jar target/studentmanager.jar -all

# Export to CSV
java -jar target/studentmanager.jar -export students.csv
```

## Database Schema

**STUDENTS**: `id`, `first_name`, `name`, `degree` (10,000 records: id0-id9999)  
**DEGREES**: `id`, `name` (10 records: deg0-deg9)

## Architecture

The implementation uses `WeakHashMap` for caching to prevent memory leaks by allowing garbage collection of unreferenced objects. This provides memory safety while maintaining the required performance threshold of 500+ queries/second.

The database initialization uses batch inserts with prepared statements for efficient bulk data loading.

## Testing

Run the test suite:
```bash
mvn test
```
