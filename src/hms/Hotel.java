package hms;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// database work behind the dashboard
public class Hotel {

    static List<Customer> loadCustomers() throws SQLException {
        List<Customer> list = new ArrayList<>();
        try (ResultSet rs = Database.query("SELECT * FROM customer ORDER BY customerID")) {
            while (rs.next()) {
                list.add(new Customer(rs.getString("customerID"), rs.getString("name"), rs.getString("ic"),
                        rs.getString("phone"), rs.getBoolean("gender"), rs.getInt("level")));
            }
        }
        return list;
    }

    static List<Room> loadAvailableRooms() throws SQLException {
        List<Room> list = new ArrayList<>();
        try (ResultSet rs = Database.query("SELECT * FROM room WHERE status = 'Available' ORDER BY roomID")) {
            while (rs.next()) list.add(Room.of(rs.getString("roomType"), rs.getString("roomID"), rs.getString("status")));
        }
        return list;
    }

    // booking, invoice and room together
    static int saveBooking(BookingDetail b) throws SQLException {
        int[] id = new int[1];
        inTransaction(() -> {
            id[0] = Database.insert("INSERT INTO booking (customerID, roomID, checkInDate, checkInTime, checkOutDate, "
                            + "mealPlan, note, totalPrice) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                    b.getCustomer().getID(), b.getRoom().getID(), b.getCheckIn(), b.getCheckInTime(), b.getCheckOut(),
                    b.getMealPlan(), b.getNote(), b.getTotal());
            String items = String.format("%s %s, %d night(s), %s", b.getRoom().getID(), b.getRoom().getName(), b.getNights(), b.getMealPlan());
            saveInvoice(new Invoice(id[0], b.getCustomer().getID(), items, b.getTotal(), Date.today()));
            Database.update("UPDATE room SET status = 'Occupied' WHERE roomID = ?", b.getRoom().getID());
        });
        return id[0];
    }

    // check out or cancel
    static void updateBooking(int bookingID, String roomID, String status) throws SQLException, BookingException {
        boolean cancel = status.equals("Cancelled");
        if (cancel) {
            try (ResultSet rs = Database.query("SELECT COUNT(*) FROM receipt WHERE bookingID = ?", bookingID)) {
                rs.next();
                if (rs.getInt(1) > 0) throw new BookingException("Booking " + bookingID + " is already paid and cannot be cancelled.");
            }
        }
        inTransaction(() -> {
            Database.update("UPDATE booking SET status = ? WHERE bookingID = ?", status, bookingID);
            if (cancel) Database.update("DELETE FROM invoice WHERE bookingID = ?", bookingID);
            Database.update("UPDATE room SET status = ? WHERE roomID = ?", cancel ? "Available" : "Preparing", roomID);
        });
    }

    static void saveInvoice(Invoice i) throws SQLException {
        Database.update("INSERT INTO invoice (bookingID, customerID, items, amount, date) VALUES (?, ?, ?, ?, ?)",
                i.getBookingID(), i.getCustomerID(), i.getItems(), i.getAmount(), i.getDate());
    }

    // counts as spent only once paid
    static void recordPayment(Receipt r) throws SQLException {
        inTransaction(() -> saveReceipt(r));
    }

    static void settleInvoice(int invoiceID, Receipt r) throws SQLException {
        inTransaction(() -> {
            saveReceipt(r);
            Database.update("DELETE FROM invoice WHERE invoiceID = ?", invoiceID);
        });
    }

    private static void saveReceipt(Receipt r) throws SQLException {
        Database.update("INSERT INTO receipt (bookingID, customerID, amount, date) VALUES (?, ?, ?, ?)",
                r.getBookingID(), r.getCustomerID(), r.getAmount(), r.getDate());
        Database.update("UPDATE customer SET totalExpenses = totalExpenses + ? WHERE customerID = ?", r.getAmount(), r.getCustomerID());
    }

    interface Work {
        void run() throws SQLException;
    }

    private static void inTransaction(Work work) throws SQLException {
        Connection conn = Database.get();
        conn.setAutoCommit(false);
        try {
            work.run();
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }
}
