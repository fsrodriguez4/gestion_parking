import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

class ParkingApp {
    private Connection connection;
    private JFrame frame;

    public ParkingApp() {
        connectToDatabase();
        initializeGUI();
    }

    private void connectToDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/parking", "root", "password");
            System.out.println("Database connected successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database connection failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initializeGUI() {
        frame = new JFrame("Parking Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        
        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel);

        frame.setVisible(true);
    }

    private void placeComponents(JPanel panel) {
        panel.setLayout(null);

        JLabel titleLabel = new JLabel("Parking System", SwingConstants.CENTER);
        titleLabel.setBounds(100, 10, 200, 30);
        panel.add(titleLabel);

        JButton registerButton = new JButton("Register Vehicle");
        registerButton.setBounds(100, 60, 200, 30);
        registerButton.addActionListener(e -> showRegisterVehicleForm());
        panel.add(registerButton);

        JButton queryButton = new JButton("Query Vehicle");
        queryButton.setBounds(100, 110, 200, 30);
        queryButton.addActionListener(e -> showQueryVehicleForm());
        panel.add(queryButton);

        JButton updateButton = new JButton("Update Vehicle Info");
        updateButton.setBounds(100, 160, 200, 30);
        updateButton.addActionListener(e -> showUpdateVehicleForm());
        panel.add(updateButton);
    }

    private void showRegisterVehicleForm() {
        JFrame registerFrame = new JFrame("Register Vehicle");
        registerFrame.setSize(400, 300);
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2));
        
        panel.add(new JLabel("License Plate:"));
        JTextField licenseField = new JTextField();
        panel.add(licenseField);

        panel.add(new JLabel("Owner Name:"));
        JTextField ownerField = new JTextField();
        panel.add(ownerField);

        panel.add(new JLabel("Vehicle Model:"));
        JTextField modelField = new JTextField();
        panel.add(modelField);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            try {
                String sql = "INSERT INTO vehicles (license_plate, owner_name, vehicle_model) VALUES (?, ?, ?)";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, licenseField.getText());
                statement.setString(2, ownerField.getText());
                statement.setString(3, modelField.getText());
                statement.executeUpdate();
                JOptionPane.showMessageDialog(registerFrame, "Vehicle registered successfully.");
                registerFrame.dispose();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(registerFrame, "Error registering vehicle.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(saveButton);

        registerFrame.add(panel);
        registerFrame.setVisible(true);
    }

    private void showQueryVehicleForm() {
        JFrame queryFrame = new JFrame("Query Vehicle");
        queryFrame.setSize(400, 300);
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));

        panel.add(new JLabel("License Plate:"));
        JTextField licenseField = new JTextField();
        panel.add(licenseField);

        JButton queryButton = new JButton("Query");
        queryButton.addActionListener(e -> {
            try {
                String sql = "SELECT * FROM vehicles WHERE license_plate = ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, licenseField.getText());
                ResultSet rs = statement.executeQuery();

                if (rs.next()) {
                    String owner = rs.getString("owner_name");
                    String model = rs.getString("vehicle_model");
                    JOptionPane.showMessageDialog(queryFrame, "Owner: " + owner + "\nModel: " + model);
                } else {
                    JOptionPane.showMessageDialog(queryFrame, "No vehicle found.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(queryFrame, "Error querying vehicle.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(queryButton);

        queryFrame.add(panel);
        queryFrame.setVisible(true);
    }

    private void showUpdateVehicleForm() {
        JFrame updateFrame = new JFrame("Update Vehicle Info");
        updateFrame.setSize(400, 300);
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2));

        panel.add(new JLabel("License Plate:"));
        JTextField licenseField = new JTextField();
        panel.add(licenseField);

        panel.add(new JLabel("New Owner Name:"));
        JTextField ownerField = new JTextField();
        panel.add(ownerField);

        panel.add(new JLabel("New Vehicle Model:"));
        JTextField modelField = new JTextField();
        panel.add(modelField);

        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(e -> {
            try {
                String sql = "UPDATE vehicles SET owner_name = ?, vehicle_model = ? WHERE license_plate = ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, ownerField.getText());
                statement.setString(2, modelField.getText());
                statement.setString(3, licenseField.getText());

                int rowsUpdated = statement.executeUpdate();
                if (rowsUpdated > 0) {
                    JOptionPane.showMessageDialog(updateFrame, "Vehicle information updated successfully.");
                } else {
                    JOptionPane.showMessageDialog(updateFrame, "No vehicle found with the provided license plate.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(updateFrame, "Error updating vehicle information.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(updateButton);

        updateFrame.add(panel);
        updateFrame.setVisible(true);
    }
}

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new ParkingApp();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
