# Campus Resource & Study Space Booking System (CRSBS)

JavaFX desktop application for Part B of the ITS66704 Advanced Programming
group assignment (Group 3), built on top of the Part A analysis and design.

## Requirements
- JDK 17 or newer
- Maven 3.8+ (JavaFX is pulled in automatically via Maven Central — no local
  JavaFX SDK install needed)

## Running the app
```bash
mvn clean javafx:run
```

On first launch the app seeds `data/users.dat` and `data/resources.dat` with
sample accounts and resources (all files created under `data/` next to
wherever you run the app from):

| Role    | Username | Password   |
|---------|----------|------------|
| Student | student  | student123 |
| Staff   | staff    | staff123   |
| Admin   | admin    | admin123   |

## Building a runnable package
```bash
mvn clean package
```

## Project layout
```
src/main/java/com/booking/
  model/        User & Resource hierarchies, Booking, enums
  exception/    BookingException hierarchy (custom checked exceptions)
  service/      AuthService, BookingService, ResourceManager<T>, ResourceFactory
  fileio/       UserFileHandler, ResourceFileHandler, BookingFileHandler, LogManager
  util/         PasswordEncoder, SampleDataFactory
  controller/   JavaFX FXML controllers (Login, Dashboards, ResourceList, Booking)
  gui/          CRSBSApplication (entry point), SceneNavigator
src/main/resources/com/booking/view/   FXML views + styles.css
```

## What each role can do
- **Student** — browse/search resources, submit booking requests (go to
  PENDING), view and cancel their own bookings.
- **Staff** — everything a student can do, plus approve/reject pending
  bookings from any student. Staff/Admin bookings are auto-CONFIRMED.
- **Admin** — everything staff can do, plus full resource CRUD (add,
  delete, toggle maintenance), a read-only user directory, and a live view
  of `data/audit_log.txt`.

## Data persistence
All data lives under `./data/` next to the app:
- `users.dat`, `resources.dat`, `bookings.dat` — Java object serialization
  (`ObjectInputStream`/`ObjectOutputStream`), matching the syllabus'
  text/binary-file-only constraint (no SQL/network/cloud storage).
- `audit_log.txt` — plain-text, human-readable audit trail, appended to by
  the `LogManager` singleton on every login, logout, booking creation,
  cancellation, approval, and rejection.

Passwords are never stored in plain text — `PasswordEncoder` salts and
Base64-encodes them before they touch disk.
