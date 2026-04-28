# Assignment Backend

Spring Boot backend for the assignment portal.

## Requirements

- Java 17
- Maven 3.9+
- MySQL running locally

## Database

Create a database named `assignment_portal`.

```sql
create database assignment_portal;
```

## Configuration

Edit `src/main/resources/application.properties` values through environment variables or by changing the file directly.

Recommended environment variables:

```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/assignment_portal"
$env:DB_USERNAME="root"
$env:DB_PASSWORD="root"
$env:MAIL_USERNAME="your-gmail@gmail.com"
$env:MAIL_PASSWORD="your-app-password"
$env:APP_JWT_SECRET="replace-with-a-long-secret-key-for-jwt-signing"
```

For Gmail OTP delivery, use your full Gmail address in `MAIL_USERNAME` and a Google App Password in `MAIL_PASSWORD`.
Regular Gmail account passwords will not work for SMTP.

## Run

```powershell
cd backend
mvn spring-boot:run
```

The API starts at `http://localhost:8080/api`.
