![Language](https://img.shields.io/badge/language-Java%20-blue.svg)
![Technologies](https://img.shields.io/badge/technologies-Spring_boot%20-green.svg)
![Technologies](https://img.shields.io/badge/technologies-Spring_MVC%20-green.svg)
![Technologies](https://img.shields.io/badge/technologies-Spring_Security%20-green.svg)
![Technologies](https://img.shields.io/badge/technologies-Spring_Data_jpa%20-green.svg)
![Technologies](https://img.shields.io/badge/technologies-Thymeleaf_&_Bootstrap%20-purple.svg)

# Expenses-Tracker-WebApp
## Overview
The Expenses Tracker App is a robust financial management solution developed using cutting-edge technologies such as Spring Boot, Spring Security, and MySQL. With user authentication and authorization features, users can securely sign up, sign in, and perform CRUD operations on their expenses. The app's intuitive interface, powered by Thymeleaf and Bootstrap, ensures a seamless user experience. The filtering functionality allows users to efficiently organize and analyze their financial data. Explore the power of streamlined expense tracking and financial control with this feature-rich application.<br> (Screenshots below for more illustration)

## Technologies Used
- Java
- Spring boot
- Spring MVC
- Spring Security
- Spring Data (JPA)
- MySQL
- Thymeleaf
- Bootstrap

## Features
- **User Authentication and Authorization:** Securely sign up, sign in, and access the app with built-in authentication and authorization.
- **CRUD Operations:** Perform essential financial tracking actions such as adding, reading, updating, and deleting expenses.
- **Filtering:** Utilize the filtering feature to efficiently sort and view expenses based on various criteria.
- **Report Export (CSV/JSON/PDF):** Download filtered expense data from `/reports/expenses.csv`, `/reports/expenses.json`, or `/reports/expenses.pdf`.
- **Production Health Endpoints:** Liveness/readiness and health checks via Spring Boot Actuator.
- **Analytics Insights:** Total, average, max, top category, and this-month spend on list and filtered views.
- **Analytics APIs:** Monthly and category summaries for dashboard/chart integrations.
- **Recurring Expenses:** Auto-generate expenses from schedules using `@Scheduled`.
- **Budget Management:** Monthly budget per category with exceeded alerts and progress.
- **In-App Notifications:** Unread counts + APIs to list and mark read.
- **Expense Templates and Quick Add:** Save reusable templates and add expenses quickly.
- **Receipt Upload:** Attach receipt image/PDF and preview/download later.
- **Calendar View:** Group expenses by date in dedicated UI/API.
- **Shared Expenses (Split):** Create groups, add members, split shared costs, and view balances.
- **Savings Goals:** Define monthly savings goals and monitor progress.
- **Rule-Based Insights:** Detect unusual increase and high spending patterns.

## Configuration Profiles
- `dev` (default): local development with SQL logging enabled.
- `prod`: environment-variable driven production profile.

## Environment Variables
- `APP_PROFILE` (default: `dev`)
- `SERVER_PORT` (default: `8080`)
- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`

## Run Locally (Without Docker)
1. Create MySQL schema: `expenses_tracker`.
2. Optional: run `sql_script.sql` if you want full schema bootstrap from script.
3. Run:
`./mvnw clean spring-boot:run`
4. Open:
`http://localhost:8080`

Note: The app now auto-seeds missing master data (`ROLE_STANDARD` and expense categories) on startup.

## Run with Docker Compose
1. Build and start:
`docker compose up --build`
2. App URL:
`http://localhost:8080`
3. Health check:
`http://localhost:8080/actuator/health`

## Build a Deployable Jar
`./mvnw clean package -DskipTests`

Run:
`java -jar target/ExpensesTracker-0.0.1-SNAPSHOT.jar`

## Report Export API
Authenticated endpoint:
`GET /reports/expenses.csv`
`GET /reports/expenses.json`
`GET /reports/expenses.pdf`

Optional query params:
- `category` (for example `groceries`, `all`)
- `from`
- `to`
- `month` (`01`-`12`, `all`)
- `year` (for example `2024`, `all`)
- `keyword` (description search)
- `sortBy` (`dateDesc`, `dateAsc`, `amountDesc`, `amountAsc`)

Additional analytics endpoints:
- `GET /reports/monthly-summary`
- `GET /reports/category-summary`

## Advanced Feature APIs
- Recurring: `POST/GET /api/recurring`, `POST /api/recurring/{id}/toggle`
- Recurring control: `POST /api/recurring/run-now`
- Budgets: `POST /api/budgets`, `GET /api/budgets/status`
- Notifications: `GET /api/notifications`, `POST /api/notifications/{id}/read`, `POST /api/notifications/mark-all-read`, `GET /api/notifications/unread-count`
- Dashboard: `GET /api/dashboard/monthly-trend`, `GET /api/dashboard/category-distribution`, `GET /api/dashboard/month-comparison`, `GET /api/dashboard/insights`, `GET /api/dashboard/recent-expenses`
- Templates: `POST/GET /api/templates`
- Calendar: `GET /api/calendar/expenses-by-date`
- Savings goals: `POST/GET /api/goals`
- Split: `POST/GET /api/split/groups`, `POST /api/split/groups/{groupId}/members`, `POST /api/split/expenses`, `GET /api/split/groups/{groupId}/balances`

## New UI Routes
- `GET /finance-hub`
- `GET /calendar-view`

## ScreenShots
![Example Image](screenshots/1.png) <br>
![Example Image](screenshots/2-2.png) <br>
![Example Image](screenshots/3-3.png) <br>
![Example Image](screenshots/4-4.png) <br>
![Example Image](screenshots/5-5.png) <br>
![Example Image](screenshots/6-6.png) <br>
![Example Image](screenshots/7.png) <br>
![Example Image](screenshots/8.png) <br>

## Contributions
Contributions are welcome! If you find a bug or have suggestions for improvement, feel free to open an issue or create a pull request.

## License
This project is licensed under the MIT License.
