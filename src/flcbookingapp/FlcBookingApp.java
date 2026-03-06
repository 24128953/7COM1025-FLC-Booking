package flcbookingapp;

import java.util.ArrayList;
import java.util.List;

public class FlcBookingApp {
    private static final List<Member> members = new ArrayList<>();
    private static final List<Lesson> lessons = new ArrayList<>();
    private static final List<Booking> bookings = new ArrayList<>();

    public static void main(String[] args) {
        initializeData();
        System.out.println("System Initialized with " + lessons.size() + " lessons and " + members.size() + " members.");
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
