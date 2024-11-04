package EmpManagement1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

class Employee {
    int id;
    String name;
    float salary;
    long contact_no;
    String email_id;

    public Employee(int id, String name, float salary, long contact_no, String email_id) {
        this.id = id;
        this.name = name;
        this.salary = salary;
        this.contact_no = contact_no;
        this.email_id = email_id;
    }

    @Override
    public String toString() {
        return """
                Employee Details :
                ID: """ + this.id + "\nName: " + this.name + "\nSalary: " +
                this.salary + "\nContact No: " + this.contact_no + "\nEmail-id: " + this.email_id;
    }
}

public class EmpManagement1 {
    private static Connection connection;

    // Hardcoded user credentials for demonstration purposes
    private final String USERNAME = "vikky";
    private final String PASSWORD = "saini123";

    static {
        try {
            String url = "jdbc:mysql://localhost:3306/vikky"; // Change as necessary
            String username = "root"; // Your database username
            String password = "123456789"; // Your database password
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            System.out.println("Database connection error: " + e.getMessage());
        }
    }

    static void display(ArrayList<Employee> al) {
        System.out.println("\n--------------Employee List---------------\n");
        System.out.println(String.format("%-10s%-15s%-10s%-20s%-10s", "ID", "Name", "Salary", "Contact No", "Email-Id"));
        for (Employee e : al) {
            System.out.println(String.format("%-5s%-20s%-10s%-15s%-10s", e.id, e.name, e.salary, e.contact_no, e.email_id));
        }
    }

    private void showLoginScreen() {
        JFrame loginFrame = new JFrame("Employee Management System - Login");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(300, 200);
        loginFrame.setLayout(new GridLayout(3, 2));

        JLabel userLabel = new JLabel("Username: ");
        JTextField userText = new JTextField();
        JLabel passwordLabel = new JLabel("Password: ");
        JPasswordField passwordText = new JPasswordField();
        JButton loginButton = new JButton("Login");

        loginFrame.add(userLabel);
        loginFrame.add(userText);
        loginFrame.add(passwordLabel);
        loginFrame.add(passwordText);
        loginFrame.add(loginButton);

        loginFrame.setVisible(true);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userText.getText();
                String password = new String(passwordText.getPassword());
                if (USERNAME.equals(username) && PASSWORD.equals(password)) {
                    loginFrame.dispose(); // Close login frame
                    showMainMenu();
                } else {
                    JOptionPane.showMessageDialog(loginFrame, "Invalid username or password.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void showMainMenu() {
        Scanner sc = new Scanner(System.in);
        ArrayList<Employee> al = new ArrayList<>();
        int choice;

        do {
            System.out.println("\n********* Welcome to the Employee Management System **********\n");
            System.out.println("""
                    1). Add Employee to the Database
                    2). Search for Employee
                    3). Edit Employee details
                    4). Delete Employee Details
                    5). Display all Employees working in this company
                    6). EXIT
                    """);
            System.out.print("Enter your choice: ");
            choice = sc.nextInt();

            switch (choice) {
                case 1 -> {
                    System.out.println("\nEnter the following details to ADD:\n");
                    System.out.print("Enter ID: ");
                    int id = sc.nextInt();
                    System.out.print("Enter Name: ");
                    String name = sc.next();
                    System.out.print("Enter Salary: ");
                    float salary = sc.nextFloat();
                    System.out.print("Enter Contact No: ");
                    long contact_no = sc.nextLong();
                    System.out.print("Enter Email-ID: ");
                    String email_id = sc.next();

                    try {
                        String query = "INSERT INTO employees (id, name, salary, contact_no, email_id) VALUES (?, ?, ?, ?, ?)";
                        PreparedStatement pst = connection.prepareStatement(query);
                        pst.setInt(1, id);
                        pst.setString(2, name);
                        pst.setFloat(3, salary);
                        pst.setLong(4, contact_no);
                        pst.setString(5, email_id);
                        pst.executeUpdate();
                        System.out.println("Employee added successfully!");
                    } catch (SQLException e) {
                        System.out.println("Error adding employee: " + e.getMessage());
                    }
                }

                case 2 -> {
                    System.out.print("Enter the Employee ID to search: ");
                    int id = sc.nextInt();
                    try {
                        String query = "SELECT * FROM employees WHERE id = ?";
                        PreparedStatement pst = connection.prepareStatement(query);
                        pst.setInt(1, id);
                        ResultSet rs = pst.executeQuery();
                        if (rs.next()) {
                            Employee emp = new Employee(rs.getInt("id"), rs.getString("name"),
                                    rs.getFloat("salary"), rs.getLong("contact_no"), rs.getString("email_id"));
                            System.out.println(emp);
                        } else {
                            System.out.println("Employee not found!");
                        }
                    } catch (SQLException e) {
                        System.out.println("Error searching employee: " + e.getMessage());
                    }
                }

                case 3 -> {
                    System.out.print("Enter the Employee ID to EDIT: ");
                    int id = sc.nextInt();
                    try {
                        String query = "UPDATE employees SET name = ?, salary = ?, contact_no = ?, email_id = ? WHERE id = ?";
                        PreparedStatement pst = connection.prepareStatement(query);
                        System.out.print("Enter new Name: ");
                        String name = sc.next();
                        System.out.print("Enter new Salary: ");
                        float salary = sc.nextFloat();
                        System.out.print("Enter new Contact No: ");
                        long contact_no = sc.nextLong();
                        System.out.print("Enter new Email-ID: ");
                        String email_id = sc.next();

                        pst.setString(1, name);
                        pst.setFloat(2, salary);
                        pst.setLong(3, contact_no);
                        pst.setString(4, email_id);
                        pst.setInt(5, id);

                        int rowsUpdated = pst.executeUpdate();
                        if (rowsUpdated > 0) {
                            System.out.println("Employee details updated successfully!");
                        } else {
                            System.out.println("Employee not found!");
                        }
                    } catch (SQLException e) {
                        System.out.println("Error updating employee: " + e.getMessage());
                    }
                }

                case 4 -> {
                    System.out.print("Enter Employee ID to DELETE: ");
                    int id = sc.nextInt();
                    try {
                        String query = "DELETE FROM employees WHERE id = ?";
                        PreparedStatement pst = connection.prepareStatement(query);
                        pst.setInt(1, id);
                        int rowsDeleted = pst.executeUpdate();
                        if (rowsDeleted > 0) {
                            System.out.println("Employee deleted successfully!");
                        } else {
                            System.out.println("Employee not found!");
                        }
                    } catch (SQLException e) {
                        System.out.println("Error deleting employee: " + e.getMessage());
                    }
                }

                case 5 -> {
                    try {
                        String query = "SELECT * FROM employees";
                        Statement stmt = connection.createStatement();
                        ResultSet rs = stmt.executeQuery(query);
                        while (rs.next()) {
                            Employee emp = new Employee(rs.getInt("id"), rs.getString("name"),
                                    rs.getFloat("salary"), rs.getLong("contact_no"), rs.getString("email_id"));
                            al.add(emp);
                        }
                        display(al);
                    } catch (SQLException e) {
                        System.out.println("Error displaying employees: " + e.getMessage());
                    }
                }

                case 6 -> {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        System.out.println("Error closing connection: " + e.getMessage());
                    }
                    System.out.println("Exiting the program.");
                    System.exit(0);
                }

                default -> System.out.println("Enter a correct choice from the List:");
            }
        } while (true);
    }

    public static void main(String[] args) {
        EmpManagement1 system = new EmpManagement1();
        system.showLoginScreen();
    }
}