package hms;

import javax.swing.*;
import java.awt.*;

// opening screen
public class HotelManagementSystem extends JFrame {

    HotelManagementSystem() {
        super("Hotel Management System");
        setSize(1200, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        JLabel image = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("icon/main.jpg")));
        image.setBounds(0, 0, 1200, 560);
        add(image);

        JLabel text = new JLabel("HOTEL MANAGEMENT SYSTEM", SwingConstants.CENTER);
        text.setBounds(0, 180, 1200, 70);
        text.setForeground(new Color(255, 215, 0));
        text.setFont(new Font("Serif", Font.BOLD, 60));
        image.add(text);

        JButton next = new JButton("Next");
        next.setForeground(Color.BLACK);
        next.setBounds(1000, 450, 150, 50);
        image.add(next);

        Timer blink = new Timer(500, e -> text.setVisible(!text.isVisible()));
        blink.start();

        next.addActionListener(e -> {
            blink.stop();
            dispose();
            new Login();
        });
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(HotelManagementSystem::new);
    }
}
