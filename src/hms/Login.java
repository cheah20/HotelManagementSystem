package hms;

import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Login extends JFrame {
    private final JTextField staffID = Ui.field();
    private final JPasswordField password = new JPasswordField(18);

    Login() {
        super("Staff Login");
        setSize(620, 320);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        password.setFont(Ui.INPUT);
        Ui.Form form = new Ui.Form().add("Staff ID", staffID).add("Password", password);

        JButton login = Ui.button("Login");
        JButton cancel = Ui.button("Cancel");
        login.addActionListener(e -> login());
        cancel.addActionListener(e -> System.exit(0));
        getRootPane().setDefaultButton(login);

        add(form, BorderLayout.CENTER);
        add(Ui.image("admin.png", 170, 170), BorderLayout.EAST);
        add(Ui.buttons(login, cancel), BorderLayout.SOUTH);
        setVisible(true);
    }

    private void login() {
        String id = staffID.getText().trim().toUpperCase();
        String pass = new String(password.getPassword());
        if (id.isEmpty() || pass.isEmpty()) {
            Ui.error(this, "Please enter your staff ID and password.");
            return;
        }

        try (ResultSet rs = Database.query("SELECT staffID FROM staff WHERE staffID = ? AND password = ?", id, pass)) {
            if (rs.next()) {
                dispose();
                new Dashboard(id);
            } else {
                Ui.error(this, "Invalid staff ID or password.");
                password.setText("");
            }
        } catch (SQLException e) {
            Ui.error(this, "Cannot connect to the database. Please start MySQL in XAMPP.\n" + e.getMessage());
        }
    }
}
