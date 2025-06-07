# tranqilo 🧘‍♂️

A health recovery web application for coaches and clients. Built with Java and Spring Boot.

## 🌟 Features (MVP)

- Role-based login (Client / Coach)
- Client check-ins (mood, energy, notes)
- 7-day dashboard metrics
- Messaging system between coaches and clients
- User profile with editable data
- Secure Spring Boot backend with PostgreSQL

## 🔮 Future Plans

- Integration with Garmin / Apple Health
- Activity tracking (manual & synced)
- Notifications, visual graphs, and more!

## 🛠 Tech Stack

- Java 17+
- Spring Boot 3
- Spring Data JPA
- PostgreSQL
- (Optional frontend: React / Vue)

## 🧪 Run Locally

Make sure you have Java + PostgreSQL installed.

```bash
git clone https://github.com/joshinwien/tranqilo-app.git
cd tranqilo
./mvnw spring-boot:run
```

Create a `.env` file or set environment variables for DB credentials (or directly in application.properties).

## 📄 License

[MIT](LICENSE)
