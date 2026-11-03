package hms;

public class Invoice {
    private final Integer bookingID;
    private final String customerID;
    private final String items;
    private final double amount;
    private final Date date;

    public Invoice(Integer bookingID, String customerID, String items, double amount, Date date) {
        this.bookingID = bookingID;
        this.customerID = customerID;
        this.items = items;
        this.amount = amount;
        this.date = date;
    }

    public Integer getBookingID() { return bookingID; }
    public String getCustomerID() { return customerID; }
    public String getItems() { return items; }
    public double getAmount() { return amount; }
    public Date getDate() { return date; }
}
