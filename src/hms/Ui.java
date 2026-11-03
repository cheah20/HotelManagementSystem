package hms;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.net.URL;

// shared look
public class Ui {
    static final Font LABEL = new Font("Tahoma", Font.BOLD, 14);
    static final Font INPUT = new Font("Tahoma", Font.PLAIN, 14);

    static JFrame window(String title, int width, int height) {
        JFrame f = new JFrame(title);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setSize(width, height);
        f.setLocationRelativeTo(null);
        f.setLayout(new BorderLayout());
        f.getContentPane().setBackground(Color.WHITE);
        return f;
    }

    static JButton button(String text) {
        JButton b = new JButton(text);
        b.setBackground(Color.BLACK);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Tahoma", Font.BOLD, 14));
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(Math.max(130, text.length() * 10 + 30), 38));
        return b;
    }

    static JPanel buttons(JButton... list) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 12));
        p.setBackground(Color.WHITE);
        for (JButton b : list) p.add(b);
        return p;
    }

    static JLabel image(String name, int width, int height) {
        URL url = Ui.class.getClassLoader().getResource("icon/" + name);
        if (url == null) return new JLabel();
        Image img = new ImageIcon(url).getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        JLabel label = new JLabel(new ImageIcon(img));
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
        return label;
    }

    static JTextField field() {
        JTextField t = new JTextField(18);
        t.setFont(INPUT);
        return t;
    }

    static DefaultTableModel tableModel(String... columns) {
        return new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
    }

    static JTable table(DefaultTableModel model) {
        JTable t = new JTable(model);
        t.setFont(INPUT);
        t.setRowHeight(24);
        t.getTableHeader().setFont(LABEL);
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        t.setFillsViewportHeight(true);
        // money and status colour
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            protected void setValue(Object value) {
                setText(value instanceof Double ? String.format("%.2f", value) : value == null ? "-" : value.toString());
                setForeground(statusColor(getText()));
            }
        });
        return t;
    }

    static Color statusColor(String status) {
        switch (status) {
            case "Available": return new Color(0, 140, 60);
            case "Occupied": return new Color(200, 30, 30);
            case "Preparing": return new Color(210, 120, 0);
            case "Confirmed": return new Color(30, 80, 200);
            case "Cancelled": return Color.GRAY;
            default: return Color.BLACK;
        }
    }

    static void error(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    static void info(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message);
    }

    // label on the left, input on the right
    static class Form extends JPanel {
        private final GridBagConstraints gbc = new GridBagConstraints();
        private int row = 0;

        Form() {
            super(new GridBagLayout());
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
            gbc.insets = new Insets(8, 8, 8, 8);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.WEST;
        }

        Form add(String label, JComponent input) {
            JLabel l = new JLabel(label);
            l.setFont(LABEL);
            gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
            super.add(l, gbc);
            gbc.gridx = 1; gbc.weightx = 1;
            super.add(input, gbc);
            row++;
            return this;
        }
    }

    // year / month / day boxes
    static class DateInput extends JPanel {
        private final JTextField year = box(4), month = box(2), day = box(2);

        DateInput() {
            super(new FlowLayout(FlowLayout.LEFT, 4, 0));
            setBackground(Color.WHITE);
            add(year); add(new JLabel("/")); add(month); add(new JLabel("/")); add(day);
        }

        void set(Date d) {
            String[] parts = d.toString().split("/");
            day.setText(parts[0]); month.setText(parts[1]); year.setText(parts[2]);
        }

        Date get() throws DateException {
            try {
                return new Date(Integer.parseInt(year.getText().trim()),
                        Integer.parseInt(month.getText().trim()), Integer.parseInt(day.getText().trim()));
            } catch (NumberFormatException e) {
                throw new DateException("Please enter the date as numbers (YYYY / MM / DD).");
            }
        }

        private static JTextField box(int columns) {
            JTextField t = new JTextField(columns);
            t.setFont(INPUT);
            return t;
        }
    }

    // gender radio buttons
    static class GenderInput extends JPanel {
        private final JRadioButton male = radio("Male"), female = radio("Female");

        GenderInput() {
            super(new FlowLayout(FlowLayout.LEFT, 0, 0));
            setBackground(Color.WHITE);
            ButtonGroup g = new ButtonGroup();
            g.add(male); g.add(female);
            add(male); add(female);
        }

        // null when nothing is picked
        Boolean isMale() {
            return male.isSelected() ? Boolean.TRUE : female.isSelected() ? Boolean.FALSE : null;
        }

        private static JRadioButton radio(String text) {
            JRadioButton r = new JRadioButton(text);
            r.setFont(INPUT);
            r.setBackground(Color.WHITE);
            return r;
        }
    }
}
