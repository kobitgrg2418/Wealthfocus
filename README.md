# WealthFocus — Java/JSP Edition

A Java port of the WealthFocus personal finance dashboard, using **JSP + Servlets + JSTL + JDBC + Maven** (no Spring).

- 🎨 Same dark UI as the React version (built with vanilla CSS)
- 💱 **Nepali Rupee (रू)** currency throughout
- 📊 Charts powered by [Chart.js](https://www.chartjs.org/) (CDN)
- 🗄️ Reuses the existing **MySQL `wealthfocus` database** (same schema as the Node.js version)

## Stack

| Layer       | Tech                                              |
| ----------- | ------------------------------------------------- |
| Build       | Maven (pom.xml)                                   |
| Runtime     | Tomcat 7+ (via `tomcat7-maven-plugin`)            |
| Server-side | Java 11, Servlets 4.0, JSP 2.3, JSTL 1.2          |
| Persistence | JDBC + MySQL Connector/J 8.0                      |
| JSON        | Gson                                              |
| Frontend    | HTML / CSS / vanilla JS / Chart.js (CDN)          |

## Prerequisites

- **Java 11+** (`java -version`)
- **Maven 3.6+** (`mvn -v`)
- **MySQL 5.7+ / 8.0** (XAMPP works) with database `wealthfocus` already created and migrated by the Node.js version, OR run the migrations in `apps/api/src/db/migrations/*.sql` once.

## Setup

### 1. Confirm MySQL is reachable

The default config (`src/main/resources/db.properties`) assumes:
```
host=localhost  port=3306  user=root  password=(empty)  database=wealthfocus
```
Edit `src/main/resources/db.properties` if your MySQL setup is different.

### 2. Make sure tables exist

If you've already run the Node.js migrations once, you're done. Otherwise:
```bash
mysql -u root wealthfocus < ../apps/api/src/db/migrations/001_create_users.sql
mysql -u root wealthfocus < ../apps/api/src/db/migrations/002_create_categories.sql
mysql -u root wealthfocus < ../apps/api/src/db/migrations/003_create_incomes.sql
mysql -u root wealthfocus < ../apps/api/src/db/migrations/004_create_expenses.sql
```
The Java app will auto-insert a default user (`Jhon`) on first request.

### 3. Run

From the `java-version/` directory:

```bash
mvn tomcat7:run
```

Then open **http://localhost:8080/**

To build a deployable WAR instead:
```bash
mvn clean package
# WAR is in target/wealthfocus.war — drop into Tomcat's webapps/
```

## Project Layout

```
java-version/
├── pom.xml
├── README.md
└── src/main/
    ├── java/com/wealthfocus/
    │   ├── model/        # POJOs: Income, Expense, Category, Recommendation
    │   ├── dao/          # JDBC: IncomeDAO, ExpenseDAO, CategoryDAO
    │   ├── service/      # Business logic: net savings, monthly breakdown, score
    │   ├── servlet/      # DashboardServlet (/), IncomeServlet, ExpenseServlet, InvestmentServlet, InitServlet
    │   └── util/         # DBConnection, Money (NPR formatter), TimeRangeUtil
    ├── resources/
    │   └── db.properties
    └── webapp/
        ├── index.jsp                      # redirects to /dashboard
        ├── WEB-INF/
        │   ├── web.xml
        │   └── views/dashboard.jsp        # main view (server-rendered)
        └── static/
            ├── css/main.css
            └── js/app.js                  # Chart.js setup + AJAX advice
```

## Routes

| Method | URL                  | Handler             | Purpose                                     |
| ------ | -------------------- | ------------------- | ------------------------------------------- |
| GET    | `/` or `/dashboard`  | DashboardServlet    | Server-renders the dashboard JSP            |
| POST   | `/income/add`        | IncomeServlet       | Add a new income entry                      |
| POST   | `/income/delete`     | IncomeServlet       | Delete an income entry                      |
| POST   | `/expense/add`       | ExpenseServlet      | Add a new expense entry                     |
| POST   | `/expense/delete`    | ExpenseServlet      | Delete an expense entry                     |
| GET    | `/advice?period=...` | InvestmentServlet   | JSON of mocked NPR investment recommendations |

## Differences from the Node.js version

- **Single user only** — a hardcoded default user (`Jhon`) is auto-created on startup. Login is out of scope; add session-based auth if you want multi-user support.
- **AI recommendations are mocked** — the OpenAI/LLM call is replaced with a hand-written set of three Nepal-specific suggestions (FD / mutual fund / NEPSE). Plug in a real LLM client in `FinanceService.mockRecommendations()` if needed.
- **No edit endpoint** — only add and delete. Add an `/income/update` servlet if you need it.
- **Server-rendered** — full page reloads on add/delete/period change. The advice section is the only AJAX call.

## Troubleshooting

**Port 8080 already in use**
```bash
mvn tomcat7:run -Dmaven.tomcat.port=8090
```

**`Access denied` for MySQL** — edit `src/main/resources/db.properties` with the real password.

**`Communications link failure`** — ensure MySQL is running (XAMPP Control Panel → start MySQL).
