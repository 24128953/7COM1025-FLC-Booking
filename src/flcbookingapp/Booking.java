package flcbookingapp;

public class Booking {
    private String bookingId;
    private Member member;
    private Lesson lesson;
    private String status = "booked";
    private Review review = null;

    public Booking() {
    }

    public Booking(String bookingId, Member member, Lesson lesson) {
        this.bookingId = bookingId;
        this.member = member;
        this.lesson = lesson;
    }

    public Booking(String bookingId, Member member, Lesson lesson, String status, Review review) {
        this.bookingId = bookingId;
        this.member = member;
        this.lesson = lesson;
        this.status = status;
        this.review = review;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public Lesson getLesson() {
        return lesson;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Review getReview() {
        return review;
    }

    public void setReview(Review review) {
        this.review = review;
    }
}
