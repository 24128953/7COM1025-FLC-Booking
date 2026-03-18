# FLC Booking App

Console-based Java application for managing group exercise lesson bookings for Furzefield Leisure Centre.

## Features

- Member registration
- Lesson booking, changing, and cancellation
- Attendance tracking with lesson reviews
- Monthly lesson and champion reports

## Project Structure

- `src/flcbookingapp/` - application source code
- `test/flcbookingapp/` - JUnit tests for core logic
- `build.xml` - Ant build script
- `manifest.mf` - manifest for the runnable application jar

## Build and Run

```bash
ant jar
java -jar dist/FlcBookingApp.jar
```

Generated output such as `build/` and `dist/` is excluded from Git.
