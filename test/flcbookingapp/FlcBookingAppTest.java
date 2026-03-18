package flcbookingapp;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FlcBookingAppTest {
    @Test
    void testLessonHasAvailableSpace() {
        Lesson lesson = new Lesson("L1", "Yoga", "Saturday", "Morning", 12.0);
        assertTrue(lesson.hasAvailableSpace(3));
        assertFalse(lesson.hasAvailableSpace(4));
    }

    @Test
    void testReviewValidRating() {
        assertTrue(Review.isValidRating(5));
        assertFalse(Review.isValidRating(6));
        assertFalse(Review.isValidRating(0));
    }

    @Test
    void testBookingCanBeAttended() {
        Member member = new Member("M01", "Member 1");
        Lesson lesson = new Lesson("L1", "Yoga", "Saturday", "Morning", 12.0);
        Booking booking = new Booking("B1", member, lesson);
        booking.setStatus("booked");
        assertTrue(booking.canBeAttended());
        booking.setStatus("cancelled");
        assertFalse(booking.canBeAttended());
    }

    @Test
    void testIsDuplicateBooking() {
        Member member = new Member("M01", "Member 1");
        Lesson lesson = new Lesson("L1", "Yoga", "Saturday", "Morning", 12.0);
        Booking booking = new Booking("B1", member, lesson);
        booking.setStatus("booked");
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);

        assertTrue(FlcBookingApp.isDuplicate(member, lesson, bookings));
    }

    @Test
    void testCalculateTotalIncome() {
        assertEquals(62.0, FlcBookingApp.calculateTotalIncome(4, 15.50), 0.0001);
    }
}
