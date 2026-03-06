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
                    case "3":
                    case "4":
                    case "5":
                        System.out.println("Coming soon.");
                        break;
                    case "6":
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid option. Please select 1 to 6.");
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
        System.out.println("6. Exit");
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

    private static boolean isActiveStatus(String status) {
        return "booked".equalsIgnoreCase(status)
                || "attended".equalsIgnoreCase(status)
                || "changed".equalsIgnoreCase(status);
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
