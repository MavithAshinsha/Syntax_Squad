import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Course {
    private static final String COURSES_FILE = "Courses.txt";
    private static final String COURSE_REGISTRATION_FILE = "CourseRegistration.txt";

    protected String courseId;
    protected String courseName;
    protected double courseFee;

    public Course() {
    }

    public Course(String courseId, String courseName, double courseFee) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.courseFee = courseFee;
    }

    public void manageCourses(Scanner scanner, String studentId) {
        int choice;
        do {
            System.out.println("\n==============================");
            System.out.println("         COURSE MENU");
            System.out.println("==============================");
            System.out.println("1. View Available Courses");
            System.out.println("2. Register for a Course");
            System.out.println("3. View My Registrations");
            System.out.println("4. Make Payment");
            System.out.println("0. Back to Dashboard");

            choice = readChoice(scanner, "\nEnter your choice: ");
            switch (choice) {
                case 1:
                    displayCourses();
                    break;
                case 2:
                    registerForCourse(scanner, studentId);
                    break;
                case 3:
                    displayStudentRegistrations(studentId);
                    break;
                case 4:
                    new Payment().makePayment(studentId, scanner);
                    break;
                case 0:
                    break;
                default:
                    System.out.println("\nInvalid course menu choice!");
            }
        } while (choice != 0);
    }

    private static void displayCourses() {
        File file = new File(COURSES_FILE);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            System.out.println("\n===== AVAILABLE COURSES =====");
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("CourseID")) {
                    System.out.println(line.replace(",", " | "));
                }
            }
        } catch (IOException e) {
            System.out.println("Could not read available courses from "
                    + COURSES_FILE + ": " + e.getMessage());
        }
    }

    private static void registerForCourse(Scanner scanner, String studentId) {
        displayCourses();
        System.out.print("\nEnter Course ID (or 0 to cancel): ");
        String courseId = scanner.nextLine().trim();
        if (courseId.equals("0")) {
            return;
        }

        try {
            if (!courseExists(courseId)) {
                System.out.println("Course ID not found!");
                return;
            }

            try (FileWriter writer = new FileWriter(COURSE_REGISTRATION_FILE, true)) {
                writer.write("Student ID: " + studentId + System.lineSeparator());
                writer.write("Course ID: " + courseId + System.lineSeparator());
                writer.write("-------------------" + System.lineSeparator());
            }
            System.out.println("Course registration successful!");
        } catch (IOException e) {
            System.out.println("Could not save course registration: " + e.getMessage());
        }
    }

    private static boolean courseExists(String courseId) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(COURSES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",", -1);
                if (fields.length == 3 && fields[0].trim().equalsIgnoreCase(courseId)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void displayStudentRegistrations(String studentId) {
        File file = new File(COURSE_REGISTRATION_FILE);
        if (!file.exists()) {
            System.out.println("You have no course registrations yet.");
            return;
        }

        boolean found = false;
        String currentStudentId = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            System.out.println("\n===== YOUR COURSE REGISTRATIONS =====");
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Student ID: ")) {
                    currentStudentId = line.substring("Student ID: ".length()).trim();
                }
                if (studentId.equals(currentStudentId)) {
                    System.out.println(line);
                    found = true;
                }
            }
            if (!found) {
                System.out.println("You have no course registrations yet.");
            }
        } catch (IOException e) {
            System.out.println("Could not read course registrations from "
                    + COURSE_REGISTRATION_FILE + ": " + e.getMessage());
        }
    }

    private static int readChoice(Scanner scanner, String prompt) {
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