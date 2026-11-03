package hms;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Dashboard extends JFrame {
    private static final String[] STATUS = { "Available", "Occupied", "Preparing" };

    private final String staffID;

    Dashboard(String staffID) {
        super("Hotel Management System");
        this.staffID = staffID;
        setSize(1280, 760);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JLabel background = Ui.image("3.jpg", 1280, 760);
        background.setBorder(null);
        background.setLayout(null);
        setContentPane(background);

        JLabel welcome = new JLabel("UTeM GROUP WELCOMES YOU");
        welcome.setFont(new Font("Serif", Font.PLAIN, 35));
        welcome.setForeground(Color.WHITE);
        welcome.setBounds(250, 85, 1000, 50);
        background.add(welcome);

        JMenuBar bar = new JMenuBar();
        bar.add(menu("Hotel Management",
                item("New Customer", this::newCustomer), item("New Room", this::newRoom),
                item("New Booking", this::newBooking), item("New Table", this::newTable)));
        bar.add(menu("Admin",
                item("New Staff", this::newStaff), item("New Invoice", this::newInvoice),
                item("New Receipt", this::newReceipt)));
        bar.add(menu("View",
                item("View Staff", this::viewStaff), item("View Customers", this::viewCustomers),
                item("View Rooms", this::viewRooms), item("View Tables", this::viewTables),
                item("View Bookings", this::viewBookings), item("View Invoices", this::viewInvoices),
                item("View Receipts", this::viewReceipts)));
        setJMenuBar(bar);
        setVisible(true);
    }

    private static JMenu menu(String name, JMenuItem... items) {
        JMenu m = new JMenu(name);
        for (JMenuItem i : items) m.add(i);
        return m;
    }

    private static JMenuItem item(String name, Runnable action) {
        JMenuItem i = new JMenuItem(name);
        i.addActionListener(e -> action.run());
        return i;
    }

    // ids like S0001
    private static String id(JTextField field) {
        return field.getText().trim().toUpperCase();
    }

    private static boolean tooLong(Component parent, String label, String value, int max) {
        if (value.length() <= max) return false;
        Ui.error(parent, label + " can have at most " + max + " characters.");
        return true;
    }

    private static String dbError(SQLException e) {
        if (e instanceof SQLIntegrityConstraintViolationException) {
            String m = String.valueOf(e.getMessage());
            if (m.contains("Duplicate")) return "This ID already exists.";
            if (m.contains("foreign key")) return "This record is still used by bookings, invoices or receipts.";
        }
        return "Database error: " + e.getMessage();
    }

    // new records
    private void newStaff() {
        JFrame f = Ui.window("New Staff", 950, 520);
        JTextField sid = Ui.field(), name = Ui.field(), ic = Ui.field(), phone = Ui.field(), salary = Ui.field();
        JPasswordField pass = new JPasswordField(18);
        pass.setFont(Ui.INPUT);
        Ui.GenderInput gender = new Ui.GenderInput();

        Ui.Form form = new Ui.Form().add("Staff ID:", sid).add("Name:", name).add("IC Number:", ic)
                .add("Phone:", phone).add("Gender:", gender).add("Salary (RM):", salary).add("Password:", pass);

        JButton save = Ui.button("Submit"), cancel = Ui.button("Cancel");
        save.addActionListener(e -> {
            String id = id(sid), password = new String(pass.getPassword());
            if (id.isEmpty() || name.getText().trim().isEmpty() || ic.getText().trim().isEmpty() || password.isEmpty()) {
                Ui.error(f, "Staff ID, name, IC and password are required.");
                return;
            }
            if (tooLong(f, "Staff ID", id, 5) || tooLong(f, "Name", name.getText().trim(), 32)
                    || tooLong(f, "IC", ic.getText().trim(), 14) || tooLong(f, "Phone", phone.getText().trim(), 13)) return;
            if (gender.isMale() == null) {
                Ui.error(f, "Please select gender.");
                return;
            }
            double pay;
            try {
                pay = Double.parseDouble(salary.getText().trim());
                if (pay < 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                Ui.error(f, "Salary must be a positive number.");
                return;
            }

            Staff s = new Staff(id, name.getText().trim(), ic.getText().trim(), phone.getText().trim(), gender.isMale(), pay);
            try {
                Database.update("INSERT INTO staff (staffID, name, ic, phone, gender, salary, password) VALUES (?, ?, ?, ?, ?, ?, ?)",
                        s.getID(), s.getName(), s.getIc(), s.getPhone(), s.isMale(), s.getSalary(), password);
                Ui.info(f, "Staff " + s.getID() + " saved.");
                f.dispose();
            } catch (SQLException ex) {
                Ui.error(f, dbError(ex));
            }
        });
        cancel.addActionListener(e -> f.dispose());

        f.add(form, BorderLayout.CENTER);
        f.add(Ui.image("staff.png", 300, 300), BorderLayout.EAST);
        f.add(Ui.buttons(save, cancel), BorderLayout.SOUTH);
        f.setVisible(true);
    }

    private void newCustomer() {
        JFrame f = Ui.window("New Customer", 950, 480);
        JTextField cid = Ui.field(), name = Ui.field(), ic = Ui.field(), phone = Ui.field();
        Ui.GenderInput gender = new Ui.GenderInput();
        JComboBox<Integer> level = new JComboBox<>(new Integer[] { 1, 2, 3, 4, 5 });
        level.setFont(Ui.INPUT);

        Ui.Form form = new Ui.Form().add("Customer ID:", cid).add("Name:", name).add("IC Number:", ic)
                .add("Phone:", phone).add("Gender:", gender).add("Member Level:", level);

        JButton save = Ui.button("Submit"), cancel = Ui.button("Cancel");
        save.addActionListener(e -> {
            String id = id(cid);
            if (id.isEmpty() || name.getText().trim().isEmpty() || ic.getText().trim().isEmpty()) {
                Ui.error(f, "Customer ID, name and IC are required.");
                return;
            }
            if (tooLong(f, "Customer ID", id, 5) || tooLong(f, "Name", name.getText().trim(), 32)
                    || tooLong(f, "IC", ic.getText().trim(), 14) || tooLong(f, "Phone", phone.getText().trim(), 13)) return;
            if (gender.isMale() == null) {
                Ui.error(f, "Please select gender.");
                return;
            }

            Customer c = new Customer(id, name.getText().trim(), ic.getText().trim(), phone.getText().trim(),
                    gender.isMale(), (Integer) level.getSelectedItem());
            try {
                Database.update("INSERT INTO customer (customerID, name, ic, phone, gender, level) VALUES (?, ?, ?, ?, ?, ?)",
                        c.getID(), c.getName(), c.getIc(), c.getPhone(), c.isMale(), c.getLevel());
                Ui.info(f, "Customer " + c.getID() + " saved.");
                f.dispose();
            } catch (SQLException ex) {
                Ui.error(f, dbError(ex));
            }
        });
        cancel.addActionListener(e -> f.dispose());

        f.add(form, BorderLayout.CENTER);
        f.add(Ui.image("customer.jpg", 320, 190), BorderLayout.EAST);
        f.add(Ui.buttons(save, cancel), BorderLayout.SOUTH);
        f.setVisible(true);
    }

    private void newRoom() {
        JFrame f = Ui.window("New Room", 820, 400);
        JTextField rid = Ui.field();
        String[] types = { "DeluxeSingle", "DeluxeDouble", "PremiumSuite" };
        JComboBox<String> type = new JComboBox<>();
        for (String t : types) {
            Room sample = Room.of(t, "", "");
            type.addItem(String.format("%s (RM %.2f / night)", sample.getName(), sample.getPriceRate()));
        }
        type.setFont(Ui.INPUT);

        Ui.Form form = new Ui.Form().add("Room ID:", rid).add("Room Type:", type);
        JButton save = Ui.button("Submit"), cancel = Ui.button("Cancel");
        save.addActionListener(e -> {
            String id = id(rid);
            if (id.isEmpty()) {
                Ui.error(f, "Please enter a room ID.");
                return;
            }
            if (tooLong(f, "Room ID", id, 5)) return;
            try {
                Database.update("INSERT INTO room (roomID, roomType) VALUES (?, ?)", id, types[type.getSelectedIndex()]);
                Ui.info(f, "Room " + id + " saved.");
                f.dispose();
            } catch (SQLException ex) {
                Ui.error(f, dbError(ex));
            }
        });
        cancel.addActionListener(e -> f.dispose());

        f.add(form, BorderLayout.CENTER);
        f.add(Ui.image("single.jpg", 300, 200), BorderLayout.EAST);
        f.add(Ui.buttons(save, cancel), BorderLayout.SOUTH);
        f.setVisible(true);
    }

    private void newTable() {
        JFrame f = Ui.window("Create Dining Table", 450, 250);
        JTextField tid = new JTextField(15);
        tid.setFont(Ui.INPUT);
        JButton save = Ui.button("Submit"), cancel = Ui.button("Cancel");
        save.addActionListener(e -> {
            String id = id(tid);
            if (id.isEmpty()) {
                Ui.error(f, "Please enter a table ID.");
                return;
            }
            if (tooLong(f, "Table ID", id, 5)) return;
            DiningTable t = new DiningTable(id, "Available");
            try {
                Database.update("INSERT INTO diningtable (tableID, status) VALUES (?, ?)", t.getID(), t.getStatus());
                Ui.info(f, "Table " + t.getID() + " saved.");
                f.dispose();
            } catch (SQLException ex) {
                Ui.error(f, dbError(ex));
            }
        });
        cancel.addActionListener(e -> f.dispose());

        JLabel label = new JLabel("Table ID:");
        label.setFont(Ui.LABEL);
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        form.add(label, gbc);
        form.add(tid, gbc);
        f.add(form, BorderLayout.CENTER);
        f.add(Ui.buttons(save, cancel), BorderLayout.SOUTH);
        f.setVisible(true);
    }

    private JComboBox<String> customerBox(List<Customer> customers, boolean showLevel) {
        JComboBox<String> box = new JComboBox<>();
        for (Customer c : customers) box.addItem(c.getID() + " - " + c.getName() + (showLevel ? " (Level " + c.getLevel() + ")" : ""));
        box.setFont(Ui.INPUT);
        return box;
    }

    private void newBooking() {
        List<Customer> customers;
        List<Room> rooms;
        try {
            customers = Hotel.loadCustomers();
            rooms = Hotel.loadAvailableRooms();
        } catch (SQLException ex) {
            Ui.error(this, dbError(ex));
            return;
        }

        JFrame f = Ui.window("New Booking", 1150, 640);
        JComboBox<String> customer = customerBox(customers, true);
        JComboBox<String> room = new JComboBox<>();
        for (Room r : rooms) room.addItem(String.format("%s - %s (RM %.2f / night)", r.getID(), r.getName(), r.getPriceRate()));
        room.setFont(Ui.INPUT);

        Ui.DateInput checkIn = new Ui.DateInput(), checkOut = new Ui.DateInput();
        checkIn.set(Date.today());
        JTextField time = Ui.field();
        time.setText("14:00");

        JCheckBox breakfast = new JCheckBox(String.format("Breakfast (RM %.2f)", BookingDetail.BREAKFAST));
        JCheckBox lunch = new JCheckBox(String.format("Lunch (RM %.2f)", BookingDetail.LUNCH));
        JCheckBox dinner = new JCheckBox(String.format("Dinner (RM %.2f)", BookingDetail.DINNER));
        JPanel meals = new JPanel(new GridLayout(3, 1, 0, 4));
        meals.setBackground(Color.WHITE);
        for (JCheckBox c : new JCheckBox[] { breakfast, lunch, dinner }) {
            c.setFont(Ui.INPUT);
            c.setBackground(Color.WHITE);
            meals.add(c);
        }
        JTextArea note = new JTextArea(3, 18);
        note.setFont(Ui.INPUT);
        note.setLineWrap(true);

        Ui.Form form = new Ui.Form().add("Customer:", customer).add("Room:", room)
                .add("Check-in (YYYY/MM/DD):", checkIn).add("Check-in Time (HH:MM):", time)
                .add("Check-out (YYYY/MM/DD):", checkOut).add("Meals (per day):", meals)
                .add("Note:", new JScrollPane(note));

        JButton save = Ui.button("Confirm Booking"), cancel = Ui.button("Cancel");
        save.addActionListener(e -> {
            try {
                Customer c = customer.getSelectedIndex() < 0 ? null : customers.get(customer.getSelectedIndex());
                Room r = room.getSelectedIndex() < 0 ? null : rooms.get(room.getSelectedIndex());
                BookingDetail b = new BookingDetail(c, r, checkIn.get(), checkOut.get(), Time.parse(time.getText()));
                if (breakfast.isSelected()) b.addMeal("Breakfast", BookingDetail.BREAKFAST);
                if (lunch.isSelected()) b.addMeal("Lunch", BookingDetail.LUNCH);
                if (dinner.isSelected()) b.addMeal("Dinner", BookingDetail.DINNER);
                b.setNote(note.getText());
                if (tooLong(f, "Note", b.getNote(), 255)) return;

                if (JOptionPane.showConfirmDialog(f, b.toString(), "Confirm Booking", JOptionPane.OK_CANCEL_OPTION)
                        != JOptionPane.OK_OPTION) return;
                Hotel.saveBooking(b);
                Ui.info(f, String.format("Booking saved. Invoice of RM %.2f created.", b.getTotal()));
                f.dispose();
            } catch (BookingException | DateException | TimeException ex) {
                Ui.error(f, ex.getMessage());
            } catch (SQLException ex) {
                Ui.error(f, dbError(ex));
            }
        });
        cancel.addActionListener(e -> f.dispose());

        f.add(form, BorderLayout.CENTER);
        f.add(Ui.image("double.jpg", 400, 230), BorderLayout.EAST);
        f.add(Ui.buttons(save, cancel), BorderLayout.SOUTH);
        f.setVisible(true);
    }

    private Double amount(Component parent, JTextField field) {
        try {
            double a = Double.parseDouble(field.getText().trim());
            if (a > 0) return Math.round(a * 100) / 100.0;
        } catch (NumberFormatException ignored) {
        }
        Ui.error(parent, "Amount must be a number above 0.");
        return null;
    }

    private void newInvoice() {
        List<Customer> customers;
        try {
            customers = Hotel.loadCustomers();
        } catch (SQLException ex) {
            Ui.error(this, dbError(ex));
            return;
        }
        JFrame f = Ui.window("Create New Invoice", 600, 450);
        JComboBox<String> customer = customerBox(customers, false);
        JTextField item = Ui.field(), amount = Ui.field();
        Ui.DateInput date = new Ui.DateInput();

        Ui.Form form = new Ui.Form().add("Customer:", customer).add("Item (e.g. Extra Bed):", item)
                .add("Amount (RM):", amount).add("Date (YYYY/MM/DD):", date);
        JButton save = Ui.button("Save Invoice"), cancel = Ui.button("Cancel");
        save.addActionListener(e -> {
            if (customer.getSelectedIndex() < 0 || item.getText().trim().isEmpty()) {
                Ui.error(f, "Please choose a customer and enter the item.");
                return;
            }
            if (tooLong(f, "Item", item.getText().trim(), 255)) return;
            Double a = amount(f, amount);
            if (a == null) return;
            try {
                Hotel.saveInvoice(new Invoice(null, customers.get(customer.getSelectedIndex()).getID(), item.getText().trim(), a, date.get()));
                Ui.info(f, "Invoice saved.");
                f.dispose();
            } catch (DateException ex) {
                Ui.error(f, ex.getMessage());
            } catch (SQLException ex) {
                Ui.error(f, dbError(ex));
            }
        });
        cancel.addActionListener(e -> f.dispose());

        f.add(form, BorderLayout.CENTER);
        f.add(Ui.buttons(save, cancel), BorderLayout.SOUTH);
        f.setVisible(true);
    }

    private void newReceipt() {
        List<Customer> customers;
        try {
            customers = Hotel.loadCustomers();
        } catch (SQLException ex) {
            Ui.error(this, dbError(ex));
            return;
        }
        JFrame f = Ui.window("Create New Receipt (Payment)", 550, 400);
        JComboBox<String> customer = customerBox(customers, false);
        JTextField amount = Ui.field();
        Ui.DateInput date = new Ui.DateInput();

        Ui.Form form = new Ui.Form().add("Payer (Customer):", customer).add("Amount Paid (RM):", amount).add("Payment Date:", date);
        JButton save = Ui.button("Generate Receipt"), cancel = Ui.button("Cancel");
        save.addActionListener(e -> {
            if (customer.getSelectedIndex() < 0) {
                Ui.error(f, "Please choose a customer.");
                return;
            }
            Double a = amount(f, amount);
            if (a == null) return;
            try {
                Hotel.recordPayment(new Receipt(null, customers.get(customer.getSelectedIndex()).getID(), a, date.get()));
                Ui.info(f, "Payment recorded.");
                f.dispose();
            } catch (DateException ex) {
                Ui.error(f, ex.getMessage());
            } catch (SQLException ex) {
                Ui.error(f, dbError(ex));
            }
        });
        cancel.addActionListener(e -> f.dispose());

        f.add(form, BorderLayout.CENTER);
        f.add(Ui.buttons(save, cancel), BorderLayout.SOUTH);
        f.setVisible(true);
    }

    // lists
    private DefaultTableModel fill(DefaultTableModel model, String sql) {
        model.setRowCount(0);
        try (ResultSet rs = Database.query(sql)) {
            int n = model.getColumnCount();
            while (rs.next()) {
                Object[] row = new Object[n];
                for (int i = 0; i < n; i++) row[i] = rs.getObject(i + 1);
                model.addRow(row);
            }
        } catch (SQLException ex) {
            Ui.error(this, dbError(ex));
        }
        return model;
    }

    private static Object selected(JTable table, int column) {
        int row = table.getSelectedRow();
        return row < 0 ? null : table.getValueAt(row, column);
    }

    private void showList(String title, int width, JTable table, JComponent bottom) {
        JFrame f = Ui.window(title, width, 520);
        f.add(new JScrollPane(table), BorderLayout.CENTER);
        if (bottom != null) f.add(bottom, BorderLayout.SOUTH);
        f.setVisible(true);
    }

    private static final String STAFF_SQL = "SELECT staffID, name, ic, phone, IF(gender, 'Male', 'Female'), "
            + "CONCAT('RM ', CAST(salary AS DECIMAL(10, 2))) FROM staff ORDER BY staffID";

    private void viewStaff() {
        DefaultTableModel model = fill(Ui.tableModel("Staff ID", "Name", "IC", "Phone", "Gender", "Salary"), STAFF_SQL);
        JTable table = new JTable(model);
        JButton remove = new JButton("Remove Selected Staff (Resigned)");
        remove.setBackground(Color.RED);
        remove.setForeground(Color.WHITE);
        remove.setFont(new Font("Tahoma", Font.BOLD, 14));
        remove.addActionListener(e -> {
            Object id = selected(table, 0);
            if (id == null) return;
            if (id.equals(staffID)) {
                Ui.error(table, "You cannot remove your own account while logged in.");
                return;
            }
            if (JOptionPane.showConfirmDialog(table, "Remove staff " + id + "?", "Remove", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
            try {
                Database.update("DELETE FROM staff WHERE staffID = ?", id);
                fill(model, STAFF_SQL);
            } catch (SQLException ex) {
                Ui.error(table, dbError(ex));
            }
        });
        JFrame f = Ui.window("Manage Staff - View & Remove", 900, 550);
        f.add(new JScrollPane(table), BorderLayout.CENTER);
        f.add(remove, BorderLayout.SOUTH);
        f.setVisible(true);
    }

    private static final String CUSTOMER_SQL = "SELECT customerID, name, ic, phone, IF(gender, 'Male', 'Female'), level, "
            + "totalExpenses FROM customer ORDER BY customerID";

    private void viewCustomers() {
        DefaultTableModel model = fill(Ui.tableModel("Customer ID", "Name", "IC", "Phone", "Gender", "Level", "Total Paid (RM)"), CUSTOMER_SQL);
        JTable table = Ui.table(model);
        JButton upgrade = Ui.button("Upgrade Level (+1)"), remove = Ui.button("Delete Customer");
        upgrade.addActionListener(e -> {
            Object id = selected(table, 0);
            if (id == null) return;
            try {
                if (Database.update("UPDATE customer SET level = level + 1 WHERE customerID = ? AND level < ?", id, Customer.MAX_LEVEL) == 0) {
                    Ui.info(table, "Customer " + id + " is already at the top level (" + Customer.MAX_LEVEL + ").");
                }
                fill(model, CUSTOMER_SQL);
            } catch (SQLException ex) {
                Ui.error(table, dbError(ex));
            }
        });
        remove.addActionListener(e -> {
            Object id = selected(table, 0);
            if (id == null) return;
            if (JOptionPane.showConfirmDialog(table, "Delete customer " + id + "?", "Delete", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
            try {
                Database.update("DELETE FROM customer WHERE customerID = ?", id);
                fill(model, CUSTOMER_SQL);
            } catch (SQLException ex) {
                Ui.error(table, dbError(ex));
            }
        });
        showList("Customers", 1050, table, Ui.buttons(upgrade, remove));
    }

    // click a row to change status
    private void statusList(String title, String sql, String update, DefaultTableModel model, int statusColumn) {
        JTable table = Ui.table(fill(model, sql));
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                Object id = selected(table, 0);
                if (id == null) return;
                int choice = JOptionPane.showOptionDialog(table, "Update " + id + " status:", title,
                        JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, STATUS, selected(table, statusColumn));
                if (choice < 0) return;
                try {
                    Database.update(update, STATUS[choice], id);
                    fill(model, sql);
                } catch (SQLException ex) {
                    Ui.error(table, dbError(ex));
                }
            }
        });
        showList(title + " - click a row to change status", 720, table, null);
    }

    private void viewRooms() {
        statusList("Rooms", "SELECT roomID, CASE roomType WHEN 'DeluxeSingle' THEN 'Deluxe Single' "
                        + "WHEN 'DeluxeDouble' THEN 'Deluxe Double' ELSE 'Premium Suite' END, status FROM room ORDER BY roomID",
                "UPDATE room SET status = ? WHERE roomID = ?", Ui.tableModel("Room ID", "Room Type", "Status"), 2);
    }

    private void viewTables() {
        statusList("Dining Tables", "SELECT tableID, status FROM diningtable ORDER BY tableID",
                "UPDATE diningtable SET status = ? WHERE tableID = ?", Ui.tableModel("Table ID", "Status"), 1);
    }

    private static final String BOOKING_SQL = "SELECT bookingID, customerID, roomID, DATE_FORMAT(checkInDate, '%d/%m/%Y'), "
            + "TIME_FORMAT(checkInTime, '%H:%i'), DATE_FORMAT(checkOutDate, '%d/%m/%Y'), mealPlan, totalPrice, status "
            + "FROM booking ORDER BY bookingID";

    private void viewBookings() {
        DefaultTableModel model = fill(Ui.tableModel("Booking ID", "Customer", "Room", "Check-in", "Time", "Check-out",
                "Meals", "Total (RM)", "Status"), BOOKING_SQL);
        JTable table = Ui.table(model);
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                Object id = selected(table, 0);
                if (id == null) return;
                if (!"Confirmed".equals(selected(table, 8))) {
                    Ui.info(table, "Booking " + id + " is already " + selected(table, 8) + ".");
                    return;
                }
                String[] options = { "Checked Out", "Cancelled" };
                int choice = JOptionPane.showOptionDialog(table, "Update booking " + id + ":", "Booking",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                if (choice < 0) return;
                try {
                    Hotel.updateBooking((Integer) id, (String) selected(table, 2), options[choice]);
                    fill(model, BOOKING_SQL);
                } catch (BookingException ex) {
                    Ui.error(table, ex.getMessage());
                } catch (SQLException ex) {
                    Ui.error(table, dbError(ex));
                }
            }
        });
        table.getColumnModel().getColumn(6).setPreferredWidth(220);
        showList("Bookings - click a confirmed booking to check out or cancel", 1250, table, null);
    }

    private static final String INVOICE_SQL = "SELECT invoiceID, bookingID, customerID, items, amount, DATE_FORMAT(date, '%d/%m/%Y') "
            + "FROM invoice ORDER BY invoiceID";

    private void viewInvoices() {
        DefaultTableModel model = fill(Ui.tableModel("Invoice ID", "Booking ID", "Customer", "Description", "Amount (RM)", "Date"), INVOICE_SQL);
        JTable table = Ui.table(model);
        JButton pay = Ui.button("Settle Payment");
        pay.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) return;
            int invoiceID = (Integer) model.getValueAt(row, 0);
            Integer bookingID = (Integer) model.getValueAt(row, 1);
            String customerID = (String) model.getValueAt(row, 2);
            double amount = ((Number) model.getValueAt(row, 4)).doubleValue();
            if (JOptionPane.showConfirmDialog(table, String.format("Receive RM %.2f from %s?", amount, customerID),
                    "Settle Payment", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
            try {
                Hotel.settleInvoice(invoiceID, new Receipt(bookingID, customerID, amount, Date.today()));
                fill(model, INVOICE_SQL);
            } catch (SQLException ex) {
                Ui.error(table, dbError(ex));
            }
        });
        table.getColumnModel().getColumn(3).setPreferredWidth(320);
        showList("Unpaid Invoices", 1000, table, Ui.buttons(pay));
    }

    private void viewReceipts() {
        DefaultTableModel model = fill(Ui.tableModel("Receipt ID", "Booking ID", "Customer", "Amount Paid (RM)", "Date"),
                "SELECT receiptID, bookingID, customerID, amount, DATE_FORMAT(date, '%d/%m/%Y') FROM receipt ORDER BY receiptID");
        showList("Receipts", 850, Ui.table(model), null);
    }
}
