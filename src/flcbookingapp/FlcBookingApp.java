package flcbookingapp;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FlcBookingApp {
    private static final List<Member> members = new ArrayList<>();
    private static final List<Lesson> lessons = new ArrayList<>();
    private static final List<Booking> bookings = new ArrayList<>();

    public static void main(String[] args) {
        initializeData();
        System.out.println("System Initialized with " + lessons.size() + " lessons and " + members.size() + " members.");
        printMemberList();

        try (Scanner scanner = new Scanner(System.in)) {
            boolean running = true;
            while (running) {
                printMainMenu();
                String choice = scanner.nextLine().trim();

                switch (choice) {
                    case "1":
                        bookLesson(scanner);
                        break;
                    case "2":
                        changeOrCancelBooking(scanner);
                        break;
                    case "3":
                        attendLesson(scanner);
                        break;
                    case "4":
                        printLessonReport();
                        break;
                    case "5":
                        printChampionReport();
                        break;
                    case "6":
                        registerMember(scanner);
                        break;
                    case "7":
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid option. Please select 1 to 7.");
                        break;
                }
            }
        }
    }

    private static void printMainMenu() {
        System.out.println();
        System.out.println("=== FLC Booking Menu ===");
        System.out.println("1. Book a group exercise lesson");
        System.out.println("2. Change/Cancel a booking");
        System.out.println("3. Attend a lesson");
        System.out.println("4. Monthly lesson report");
        System.out.println("5. Monthly champion lesson type report");
        System.out.println("6. Register a new member");
        System.out.println("7. Exit");
        System.out.print("Enter your choice: ");
    }

    private static void bookLesson(Scanner scanner) {
        System.out.print("Enter Member ID: ");
        String memberId = scanner.nextLine().trim();
        Member selectedMember = findMemberById(memberId);
        if (selectedMember == null) {
            System.out.println("Member not found.");
            return;
        }

        System.out.println("View timetable by:");
        System.out.println("1. Day");
        System.out.println("2. Exercise");
        System.out.print("Enter option: ");
        String viewOption = scanner.nextLine().trim();

        boolean hasMatches = false;
        if ("1".equals(viewOption)) {
            System.out.print("Enter day (Saturday or Sunday): ");
            String day = scanner.nextLine().trim();
            for (Lesson lesson : lessons) {
                if (lesson.getDay().equalsIgnoreCase(day)) {
                    printLesson(lesson);
                    hasMatches = true;
                }
            }
        } else if ("2".equals(viewOption)) {
            System.out.print("Enter exercise name: ");
            String exerciseName = scanner.nextLine().trim();
            for (Lesson lesson : lessons) {
                if (lesson.getExerciseType().equalsIgnoreCase(exerciseName)) {
                    printLesson(lesson);
                    hasMatches = true;
                }
            }
        } else {
            System.out.println("Invalid timetable view option.");
            return;
        }

        if (!hasMatches) {
            System.out.println("No lessons matched your search.");
        }

        System.out.print("Enter lessonId to book: ");
        String lessonId = scanner.nextLine().trim();
        Lesson selectedLesson = findLessonById(lessonId);
        if (selectedLesson == null) {
            System.out.println("Lesson not found.");
            return;
        }

        for (Booking booking : bookings) {
            boolean sameMember = booking.getMember() != null
                    && booking.getMember().getMemberId().equalsIgnoreCase(selectedMember.getMemberId());
            boolean sameLesson = booking.getLesson() != null
                    && booking.getLesson().getLessonId().equalsIgnoreCase(selectedLesson.getLessonId());
            boolean notCancelled = booking.getStatus() != null
                    && !booking.getStatus().equalsIgnoreCase("cancelled");

            if (sameMember && sameLesson && notCancelled) {
                System.out.println("Booking rejected: member already has an active booking for this lesson.");
                return;
            }
        }

        int activeBookingsForLesson = 0;
        for (Booking booking : bookings) {
            boolean sameLesson = booking.getLesson() != null
                    && booking.getLesson().getLessonId().equalsIgnoreCase(selectedLesson.getLessonId());
            boolean activeStatus = isActiveStatus(booking.getStatus());
            if (sameLesson && activeStatus) {
                activeBookingsForLesson++;
            }
        }

        if (activeBookingsForLesson >= 4) {
            System.out.println("Booking rejected: lesson is over capacity.");
            return;
        }

        String newBookingId = String.valueOf(bookings.size() + 1);
        Booking newBooking = new Booking(newBookingId, selectedMember, selectedLesson);
        newBooking.setStatus("booked");
        bookings.add(newBooking);

        System.out.println("Booking successful. Booking ID: " + newBookingId);
    }

    private static void changeOrCancelBooking(Scanner scanner) {
        System.out.print("Enter Booking ID: ");
        String bookingId = scanner.nextLine().trim();
        Booking booking = findBookingById(bookingId);
        if (booking == null) {
            System.out.println("Booking not found.");
            return;
        }

        String status = booking.getStatus();
        if (status == null || status.equalsIgnoreCase("cancelled") || status.equalsIgnoreCase("attended")) {
            System.out.println("Cannot change or cancel this booking.");
            return;
        }

        System.out.println("Select action:");
        System.out.println("1. Change");
        System.out.println("2. Cancel");
        System.out.print("Enter option: ");
        String action = scanner.nextLine().trim();

        if ("2".equals(action)) {
            booking.setStatus("cancelled");
            System.out.println("Booking cancelled successfully.");
            return;
        }

        if (!"1".equals(action)) {
            System.out.println("Invalid action.");
            return;
        }

        System.out.print("Enter new lessonId: ");
        String newLessonId = scanner.nextLine().trim();
        Lesson newLesson = findLessonById(newLessonId);
        if (newLesson == null) {
            System.out.println("Lesson not found.");
            return;
        }

        int activeBookingsForLesson = 0;
        for (Booking existingBooking : bookings) {
            boolean sameLesson = existingBooking.getLesson() != null
                    && existingBooking.getLesson().getLessonId().equalsIgnoreCase(newLesson.getLessonId());
            boolean activeStatus = isActiveStatus(existingBooking.getStatus());
            if (sameLesson && activeStatus) {
                activeBookingsForLesson++;
            }
        }

        if (activeBookingsForLesson >= 4) {
            System.out.println("Change rejected: lesson is over capacity.");
            return;
        }

        booking.setLesson(newLesson);
        booking.setStatus("changed");
        System.out.println("Booking changed successfully. Old place released and new booking confirmed.");
    }

    private static void attendLesson(Scanner scanner) {
        System.out.print("Enter Booking ID: ");
        String bookingId = scanner.nextLine().trim();
        Booking booking = findBookingById(bookingId);
        if (booking == null) {
            System.out.println("Booking not found.");
            return;
        }

        String status = booking.getStatus();
        if (status == null || status.equalsIgnoreCase("cancelled") || status.equalsIgnoreCase("attended")) {
            System.out.println("Cannot attend this booking.");
            return;
        }

        booking.setStatus("attended");

        int rating = 0;
        while (rating < 1 || rating > 5) {
            System.out.print("Enter rating (1-5): ");
            String ratingInput = scanner.nextLine().trim();
            try {
                rating = Integer.parseInt(ratingInput);
            } catch (NumberFormatException e) {
                rating = 0;
            }
            if (rating < 1 || rating > 5) {
                System.out.println("Invalid rating. Please enter a number between 1 and 5.");
            }
        }

        System.out.print("Enter review comment: ");
        String comment = scanner.nextLine().trim();
        Review review = new Review(rating, comment);
        booking.setReview(review);

        System.out.println("Attendance recorded and review submitted.");
    }

    private static void printLessonReport() {
        System.out.println();
        System.out.println("=== Monthly Lesson Report ===");
        for (Lesson lesson : lessons) {
            int attendance = 0;
            int ratingSum = 0;
            int ratingCount = 0;

            for (Booking booking : bookings) {
                boolean sameLesson = booking.getLesson() != null
                        && booking.getLesson().getLessonId().equalsIgnoreCase(lesson.getLessonId());
                boolean attended = booking.getStatus() != null
                        && booking.getStatus().equalsIgnoreCase("attended");
                if (sameLesson && attended) {
                    attendance++;
                    Review review = booking.getReview();
                    if (review != null) {
                        ratingSum += review.getRating();
                        ratingCount++;
                    }
                }
            }

            double averageRating = ratingCount > 0 ? (double) ratingSum / ratingCount : 0.0;
            System.out.printf(
                    "%s | %s | %s | %s | Attended: %d | Avg Rating: %.2f%n",
                    lesson.getLessonId(),
                    lesson.getExerciseType(),
                    lesson.getDay(),
                    lesson.getTimeSlot(),
                    attendance,
                    averageRating
            );
        }
    }

    private static void printChampionReport() {
        double yogaIncome = 0.0;
        double zumbaIncome = 0.0;
        double aquaciseIncome = 0.0;
        double boxFitIncome = 0.0;

        for (Booking booking : bookings) {
            boolean attended = booking.getStatus() != null
                    && booking.getStatus().equalsIgnoreCase("attended");
            Lesson lesson = booking.getLesson();
            if (!attended || lesson == null) {
                continue;
            }

            String type = lesson.getExerciseType();
            double price = lesson.getPrice();
            if (type == null) {
                continue;
            }

            if (type.equalsIgnoreCase("Yoga")) {
                yogaIncome += price;
            } else if (type.equalsIgnoreCase("Zumba")) {
                zumbaIncome += price;
            } else if (type.equalsIgnoreCase("Aquacise")) {
                aquaciseIncome += price;
            } else if (type.equalsIgnoreCase("Box Fit")) {
                boxFitIncome += price;
            }
        }

        String champion = "Yoga";
        double maxIncome = yogaIncome;

        if (zumbaIncome > maxIncome) {
            champion = "Zumba";
            maxIncome = zumbaIncome;
        }
        if (aquaciseIncome > maxIncome) {
            champion = "Aquacise";
            maxIncome = aquaciseIncome;
        }
        if (boxFitIncome > maxIncome) {
            champion = "Box Fit";
            maxIncome = boxFitIncome;
        }

        System.out.println();
        System.out.println("=== Monthly Champion Lesson Type Report ===");
        System.out.printf("Yoga: £%.2f%n", yogaIncome);
        System.out.printf("Zumba: £%.2f%n", zumbaIncome);
        System.out.printf("Aquacise: £%.2f%n", aquaciseIncome);
        System.out.printf("Box Fit: £%.2f%n", boxFitIncome);
        System.out.printf("Champion: %s with £%.2f%n", champion, maxIncome);
    }

    private static void registerMember(Scanner scanner) {
        System.out.print("Enter member name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Name cannot be empty.");
            return;
        }

        String memberId = generateMemberId();
        Member newMember = new Member(memberId, name);
        members.add(newMember);

        System.out.println("Member registered. ID: " + memberId);
    }

    private static Member findMemberById(String memberId) {
        for (Member member : members) {
            if (member.getMemberId().equalsIgnoreCase(memberId)) {
                return member;
            }
        }
        return null;
    }

    private static Lesson findLessonById(String lessonId) {
        for (Lesson lesson : lessons) {
            if (lesson.getLessonId().equalsIgnoreCase(lessonId)) {
                return lesson;
            }
        }
        return null;
    }

    private static Booking findBookingById(String bookingId) {
        for (Booking booking : bookings) {
            if (booking.getBookingId().equalsIgnoreCase(bookingId)) {
                return booking;
            }
        }
        return null;
    }

    private static boolean isActiveStatus(String status) {
        return "booked".equalsIgnoreCase(status)
                || "attended".equalsIgnoreCase(status)
                || "changed".equalsIgnoreCase(status);
    }

    private static String generateMemberId() {
        int max = 0;
        for (Member member : members) {
            String id = member.getMemberId();
            if (id == null || !id.startsWith("M")) {
                continue;
            }
            String numericPart = id.substring(1);
            try {
                int value = Integer.parseInt(numericPart);
                if (value > max) {
                    max = value;
                }
            } catch (NumberFormatException ignored) {
                // Ignore non-numeric ids
            }
        }
        return String.format("M%02d", max + 1);
    }

    private static void printMemberList() {
        System.out.println("Available Members:");
        for (Member member : members) {
            System.out.println(member.getMemberId() + " - " + member.getName());
        }
    }

    public static boolean isDuplicate(Member member, Lesson lesson, List<Booking> bookings) {
        if (member == null || lesson == null || bookings == null) {
            return false;
        }
        for (Booking booking : bookings) {
            if (booking == null) {
                continue;
            }
            Member bookingMember = booking.getMember();
            Lesson bookingLesson = booking.getLesson();
            String status = booking.getStatus();
            boolean sameMember = bookingMember != null
                    && bookingMember.getMemberId().equalsIgnoreCase(member.getMemberId());
            boolean sameLesson = bookingLesson != null
                    && bookingLesson.getLessonId().equalsIgnoreCase(lesson.getLessonId());
            boolean notCancelled = status == null || !status.equalsIgnoreCase("cancelled");

            if (sameMember && sameLesson && notCancelled) {
                return true;
            }
        }
        return false;
    }

    public static double calculateTotalIncome(int attendanceCount, double price) {
        return attendanceCount * price;
    }

    private static void printLesson(Lesson lesson) {
        System.out.println(
                lesson.getLessonId() + " | "
                        + lesson.getExerciseType() + " | "
                        + lesson.getDay() + " | "
                        + lesson.getTimeSlot() + " | £"
                        + lesson.getPrice()
        );
    }

    private static void initializeData() {
        members.clear();
        lessons.clear();
        bookings.clear();

        for (int i = 1; i <= 10; i++) {
            String memberId = String.format("M%02d", i);
            String memberName = "Member " + i;
            members.add(new Member(memberId, memberName));
        }

        String[] exerciseTypes = {"Yoga", "Zumba", "Aquacise", "Box Fit"};
        double[] prices = {12.0, 11.5, 10.0, 13.0};
        String[] days = {"Saturday", "Sunday"};
        String[] timeSlots = {"Morning", "Afternoon", "Evening"};

        int lessonCounter = 0;
        for (int week = 1; week <= 8; week++) {
            for (String day : days) {
                for (String timeSlot : timeSlots) {
                    int typeIndex = lessonCounter % exerciseTypes.length;
                    String dayCode = day.equals("Saturday") ? "Sat" : "Sun";
                    String slotCode = timeSlot.equals("Morning") ? "Morn"
                            : timeSlot.equals("Afternoon") ? "Aft" : "Eve";
                    String lessonId = "W" + week + "-" + dayCode + "-" + slotCode;

                    Lesson lesson = new Lesson(
                            lessonId,
                            exerciseTypes[typeIndex],
                            day,
                            timeSlot,
                            prices[typeIndex]
                    );
                    lesson.setMaxCapacity(4);
                    lessons.add(lesson);
                    lessonCounter++;
                }
            }
        }
    }
}
