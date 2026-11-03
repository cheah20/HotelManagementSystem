package hms;

public class Receipt {
    private final Integer bookingID;
    private final String customerID;
    private final double amount;
    private final Date date;

    public Receipt(Integer bookingID, String customerID, double amount, Date date) {
        this.bookingID = bookingID;
        this.customerID = customerID;
        this.amount = amount;
        this.date = date;
    }

    public Integer getBookingID() { return bookingID; }
    public String getCustomerID() { return customerID; }
    public double getAmount() { return amount; }
    public Date getDate() { return date; }
}
