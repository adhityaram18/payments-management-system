# Payment Management System

A modular Java-based console application that manages organizational payments, tracks transaction statuses, generates detailed reports, and maintains audit logs. Designed for future extension into a Spring Boot web application.

---

## Features

- **Role-based Access**
  - Admin: Add/register users
  - Finance Manager: Add payments, update statuses
  - Viewer: View reports only

- **Payment Management**
  - Record internal (e.g., salaries) and external (e.g., vendor) transactions
  - Categorized into `SALARY`, `VENDOR_PAYMENT`, and `CLIENT_INVOICE`

- **Reporting Module**
  - Monthly, quarterly reports
  - Aggregation by direction, category, employee, and counterparty
  - Export as **CSV**, (HTML/PDF coming soon)

- **Audit Logging**
  - Tracks changes like status updates for all payments

- **Extensible Architecture**
  - Built using plain Java + JDBC + PostgreSQL
  - HikariCP connection pooling
  - Ready for Spring Boot migration

---

## Tech Stack

| Layer          | Technology              |
|----------------|--------------------------|
| Language       | Java 17                 |
| Database       | PostgreSQL              |
| ORM/Database   | JDBC                    |
| Build Tool     | Maven                   |
| UI             | Console-based (CUI)     |
| Future Ready   | Spring Boot compatible  |

---

## Project Structure

project-root/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── adhitya/
│   │   │           └── paymgmt/
│   │   │               ├── presentation/        # UI/Menu classes (e.g., FinanceManagerMenu.java)
│   │   │               ├── service/             # Service classes (ReportService.java, PaymentService.java, etc.)
│   │   │               ├── repository/          # Repositories (EmployeeRepository.java, CounterpartyRepository.java)
│   │   │               ├── util/                # Utilities & Calculator (ReportCalculator.java, ReportPrinter.java)
│   │   │               ├── dto/                 # DTOs (ReportDataDTO.java)
│   │   │               └── model/               # Entities (User.java, Payment.java, Employee.java, etc.)
│   │   └── resources/                            # Resources, config files
│   │
│   └── test/
│       └── java/
│           └── <test packages>
│
├── sql/
│   ├── schema.sql                               # Schema creation SQL scripts (table definitions)
│   ├── sample_data.sql                          # Sample data insertions covering diverse use cases
│
├── docs/
│   ├── CodeLogic.pdf                            # Document: overall project logic walkthrough + workflow documentation
│   ├── UMLDiagrams.pdf                          # Document: ER, Class, Sequence diagrams + API contracts if any
│   └── README.md                                # Project overview, setup instructions, GitHub repo link, run instructions
│
├── pom.xml                                     # Maven configuration (dependencies, build)
├── .gitignore                                  # Git ignore file
└── LICENSE                                    # License file if applicable


---

## How to Run

1. Make sure PostgreSQL is running.
2. Insert a default admin into the database:
   ```sql
   INSERT INTO users (username, password, role, email) 
   VALUES ('admin', 'admin123', 'ADMIN', 'admin@example.com');
