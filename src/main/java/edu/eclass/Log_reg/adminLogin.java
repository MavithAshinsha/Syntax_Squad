package main.java.edu.eclass.Log_reg.model;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class adminLogin {
    private static final String ADMIN_FILE = "admin.txt";
    private static final String STUDENT_FILE = "student.txt";
    private static final String COURSES_FILE = "Courses.txt";
    private static final String REGISTRATION_FILE = "CourseRegistration.txt";
    private static final String ATTENDANCE_FILE = "attendence.txt";
    private static final String NOTIFICATION_FILE = "parent_notifications.txt";
    private static final int MAX_ATTEMPTS = 3;

    private final Scanner scanner;

    public adminLogin() {
        this(new Scanner(System.in));
    }

    public adminLogin(Scanner scanner) {
        this.scanner = scanner;
        createDefaultAdminIfMissing();
    }


    public String login() {
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            System.out.print("\nEnter Admin ID: ");
            String adminId = scanner.nextLine().trim();
            System.out.print("Enter Admin Password: ");
            String password = scanner.nextLine();

            if (checkAdmin(adminId, password)) {
                System.out.println("\nAdmin login successful!");
                return adminId;
            }
            System.out.println("\nInvalid admin ID or password. (" + attempt + "/" + MAX_ATTEMPTS + ")");
        }
        System.out.println("Too many failed attempts. Returning to main menu.");
        return null;
    }

    public static boolean checkAdmin(String id, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(ADMIN_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] f = line.split(",", -1);
                if (f.length >= 3 && f[0].trim().equals(id) && f[2].equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("Could not read admin details: " + e.getMessage());
        }
        return false;
    }

    private void createDefaultAdminIfMissing() {
        File file = new File(ADMIN_FILE);
        if (file.exists()) {
            return;
        }
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("ADM001,System Admin,admin123" + System.lineSeparator());
        } catch (IOException e) {
            System.out.println("Could not create default admin: " + e.getMessage());
        }
    }


    public void openDashboard(String adminId) {
        int choice;
        do {
            System.out.println("\n==============================");
            System.out.println("        ADMIN DASHBOARD");
            System.out.println("==============================");
            System.out.println("1. View All Students");
            System.out.println("2. Search Student by ID");
            System.out.println("3. View Courses");
            System.out.println("4. Add New Course");
            System.out.println("5. View Course Registrations & Payments");
            System.out.println("6. View Attendance Records");
            System.out.println("7. View Parent Notifications");
            System.out.println("8. Change Admin Password");
            System.out.println("0. Logout");

            choice = readChoice("\nEnter your choice: ");
            switch (choice) {
                case 1:
                    viewAllStudents();
                    break;
                case 2:
                    searchStudent();
                    break;
                case 3:
                    printFile(COURSES_FILE, "AVAILABLE COURSES", true);
                    break;
                case 4:
                    addCourse();
                    break;
                case 5:
                    printFile(REGISTRATION_FILE, "COURSE REGISTRATIONS & PAYMENTS", false);
                    break;
                case 6:
                    printFile(ATTENDANCE_FILE, "ATTENDANCE RECORDS", false);
                    break;
                case 7:
                    printFile(NOTIFICATION_FILE, "PARENT NOTIFICATIONS", false);
                    break;
                case 8:
                    changePassword(adminId);
                    break;
                case 0:
                    System.out.println("\nAdmin logged out.");
                    break;
                default:
                    System.out.println("\nInvalid admin menu choice!");
            }
        } while (choice != 0);
    }

    public void loginAndOpen() {
        String adminId = login();
        if (adminId != null) {
            openDashboard(adminId);
        }
    }


    private void viewAllStudents() {
        File file = new File(STUDENT_FILE);
        if (!file.exists()) {
            System.out.println("\nNo students registered yet.");
            return;
        }
        System.out.println("\n===== ALL STUDENTS =====");
        System.out.printf("%-8s %-28s %-14s %-14s%n", "ID", "Name", "Phone", "Parent Phone");
        System.out.println("--------------------------------------------------------------");
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] f = line.split(",", -1);
                if (f.length >= 9) {
                    System.out.printf("%-8s %-28s %-14s %-14s%n",
                            f[0].trim(), f[1].trim(), f[4].trim(), f[8].trim());
                    count++;
                }
            }
        } catch (IOException e) {
            System.out.println("Could not read students: " + e.getMessage());
            return;
        }
        System.out.println("Total students: " + count);
    }

    private void searchStudent() {
        System.out.print("\nEnter Student ID: ");
        String id = scanner.nextLine().trim();
        try (BufferedReader reader = new BufferedReader(new FileReader(STUDENT_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] f = line.split(",", -1);
                if (f.length >= 9 && f[0].trim().equalsIgnoreCase(id)) {
                    System.out.println("\n===== STUDENT DETAILS =====");
                    System.out.println("Student ID: " + f[0]);
                    System.out.println("Name: " + f[1]);
                    System.out.println("Full Name: " + f[5]);
                    System.out.println("Birth Date: " + f[6]);
                    System.out.println("Email: " + f[3]);
                    System.out.println("Phone: " + f[4]);
                    System.out.println("Parent Name: " + f[7]);
                    System.out.println("Parent Phone: " + f[8]);
                    return;
                }
            }
            System.out.println("Student not found.");
        } catch (IOException e) {
            System.out.println("Could not read students: " + e.getMessage());
        }
    }

    private void addCourse() {
        System.out.print("\nEnter new Course ID (e.g. C006): ");
        String id = scanner.nextLine().trim();
        System.out.print("Enter Course Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter Course Fee: ");
        String feeText = scanner.nextLine().trim();

        if (id.isEmpty() || name.isEmpty() || id.contains(",") || name.contains(",")) {
            System.out.println("ID and name cannot be empty or contain commas.");
            return;
        }
        double fee;
        try {
            fee = Double.parseDouble(feeText);
            if (fee <= 0) {
                System.out.println("Fee must be greater than 0.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid fee amount.");
            return;
        }
        if (courseExists(id)) {
            System.out.println("A course with that ID already exists.");
            return;
        }


        String feeOut = (fee == Math.floor(fee)) ? String.valueOf((long) fee) : String.valueOf(fee);
        try (FileWriter writer = new FileWriter(COURSES_FILE, true)) {
            if (!endsWithNewline(new File(COURSES_FILE))) {
                writer.write(System.lineSeparator());
            }
            writer.write(id + "," + name + "," + feeOut + System.lineSeparator());
            System.out.println("Course added successfully!");
        } catch (IOException e) {
            System.out.println("Could not save course: " + e.getMessage());
        }
    }

    private boolean courseExists(String id) {
        try (BufferedReader reader = new BufferedReader(new FileReader(COURSES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] f = line.split(",", -1);
                if (f.length == 3 && f[0].trim().equalsIgnoreCase(id)) {
                    return true;
                }
            }
        } catch (IOException e) {
            // file missing -> no courses yet
        }
        return false;
    }

    private boolean endsWithNewline(File file) throws IOException {
        if (!file.exists() || file.length() == 0) {
            return true;
        }
        try (java.io.RandomAccessFile raf = new java.io.RandomAccessFile(file, "r")) {
            raf.seek(file.length() - 1);
            int last = raf.read();
            return last == '\n' || last == '\r';
        }
    }

    private void changePassword(String adminId) {
        System.out.print("\nEnter current password: ");
        String current = scanner.nextLine();
        if (!checkAdmin(adminId, current)) {
            System.out.println("Current password is incorrect.");
            return;
        }
        System.out.print("Enter new password: ");
        String newPw = scanner.nextLine();
        if (newPw.length() < 6 || newPw.contains(",")) {
            System.out.println("Password must be at least 6 characters and cannot contain commas.");
            return;
        }

        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(ADMIN_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] f = line.split(",", -1);
                if (f.length >= 3 && f[0].trim().equals(adminId)) {
                    f[2] = newPw;
                    lines.add(String.join(",", f));
                } else {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            System.out.println("Could not read admin file: " + e.getMessage());
            return;
        }
        try (FileWriter writer = new FileWriter(ADMIN_FILE)) {
            for (String l : lines) {
                writer.write(l + System.lineSeparator());
            }
            System.out.println("Password changed successfully.");
        } catch (IOException e) {
            System.out.println("Could not save new password: " + e.getMessage());
        }
    }



    private void printFile(String fileName, String title, boolean skipHeader) {
        File file = new File(fileName);
        if (!file.exists()) {
            System.out.println("\nNo data found (" + fileName + " does not exist yet).");
            return;
        }
        System.out.println("\n===== " + title + " =====");
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (skipHeader && line.startsWith("CourseID")) {
                    continue;
                }
                System.out.println(skipHeader ? line.replace(",", " | ") : line);
            }
        } catch (IOException e) {
            System.out.println("Could not read " + fileName + ": " + e.getMessage());
        }
    }

    private int readChoice(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}
