import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class attendence {
    private static final String COURSES_FILE = "Courses.txt";
    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public attendence() {
    }

    public boolean markAtt(String studentId, Scanner input) {
        System.out.print("Enter the class/course ID: ");
        String classId = input.nextLine().trim();
        if (classId.isEmpty()) {
            System.out.println("Class/course ID cannot be empty.");
            return false;
        }

        String className;
        try {
            className = findClassName(classId);
        } catch (IOException e) {
            System.out.println("Could not read class details from "
                    + COURSES_FILE + ": " + e.getMessage());
            return false;
        }
        if (className == null) {
            System.out.println("Class/course ID " + classId + " was not found.");
            return false;
        }

        Payment payment = new Payment();
        if (!payment.checkRegistration(studentId, classId)) {
            System.out.println("You are not registered for class/course " + classId + ".");
            return false;
        }
        if (!payment.hasSuccessfulPayment(studentId, classId)) {
            System.out.println("You cannot attend this class because payment has not been completed.");
            return false;
        }

        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        try (FileWriter fw = new FileWriter("attendence.txt", true)) {
            fw.write("Student ID: " + studentId + ", Class ID: " + classId
                    + ", Class Name: " + className + ", Date/Time: " + timestamp
                    + System.lineSeparator());
            System.out.println("Attendance marked for " + studentId + " in "
                    + className + " (" + classId + ").");
        } catch (IOException e) {
            System.out.println("Could not save attendance: " + e.getMessage());
            return false;
        }

        ParentManagementSystem pms = new ParentManagementSystem();
        if (!pms.notifyAttendance(studentId, className)) {
            return false;
        }
        System.out.println("Attendance and parent notification complete.");
        return true;
    }

    private String findClassName(String classId) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(COURSES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",", -1);
                if (fields.length == 3 && fields[0].trim().equalsIgnoreCase(classId)) {
                    return fields[1].trim();
                }
            }
        }
        return null;
    }
}