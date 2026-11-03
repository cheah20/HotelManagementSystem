package hms;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

// price for one booking
public final class BookingDetail {
    public static final double BREAKFAST = 39.99;
    public static final double LUNCH = 59.99;
    public static final double DINNER = 88.88;

    private final Customer customer;
    private final Room room;
    private final Date checkIn;
    private final Date checkOut;
    private final Time checkInTime;
    private final List<String> meals = new ArrayList<>();
    private double mealsPerDay = 0;
    private String note = "";

    public BookingDetail(Customer customer, Room room, Date checkIn, Date checkOut, Time checkInTime) throws BookingException {
        if (customer == null) throw new BookingException("Please choose a customer.");
        if (room == null) throw new BookingException("No available room.");
        this.customer = customer;
        this.room = room;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.checkInTime = checkInTime;
        if (getNights() <= 0) throw new BookingException("Check-out must be after check-in.");
    }

    public void addMeal(String name, double price) {
        meals.add(name);
        mealsPerDay += price;
    }

    public void setNote(String note) { this.note = note == null ? "" : note.trim(); }

    public Customer getCustomer() { return customer; }
    public Room getRoom() { return room; }
    public Date getCheckIn() { return checkIn; }
    public Date getCheckOut() { return checkOut; }
    public Time getCheckInTime() { return checkInTime; }
    public String getNote() { return note; }

    public long getNights() {
        return ChronoUnit.DAYS.between(checkIn.toLocalDate(), checkOut.toLocalDate());
    }

    public String getMealPlan() {
        return meals.isEmpty() ? "No meals" : String.join(", ", meals);
    }

    public double getSubtotal() { return round((room.getPriceRate() + mealsPerDay) * getNights()); }
    public double getDiscount() { return round(getSubtotal() * customer.getDiscountRate()); }
    public double getTotal() { return round(getSubtotal() - getDiscount()); }

    private static double round(double amount) {
        return Math.round(amount * 100) / 100.0;
    }

    public String toString() {
        return String.format("Customer : %s (Level %d)%nRoom     : %s %s%nCheck-in : %s %s%nCheck-out: %s%n"
                + "Nights   : %d%nMeals    : %s%n%nSubtotal : RM %.2f%nDiscount : RM %.2f (%.0f%%)%nTotal    : RM %.2f",
                customer.getName(), customer.getLevel(), room.getID(), room.getName(), checkIn, checkInTime, checkOut,
                getNights(), getMealPlan(), getSubtotal(), getDiscount(), customer.getDiscountRate() * 100, getTotal());
    }
}
