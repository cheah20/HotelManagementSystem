package hms;

public class DiningTable implements Bookable {
    private final String tableID;
    private final String status;

    public DiningTable(String tableID, String status) {
        this.tableID = tableID;
        this.status = status;
    }

    public String getID() { return tableID; }
    public String getStatus() { return status; }
}
