package hms;

public class Staff extends Human {
    private final String staffID;
    private final double salary;

    public Staff(String staffID, String name, String ic, String phone, boolean male, double salary) {
        super(name, ic, phone, male);
        this.staffID = staffID;
        this.salary = salary;
    }

    public String getID() { return staffID; }
    public double getSalary() { return salary; }
}
