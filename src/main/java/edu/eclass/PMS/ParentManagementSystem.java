package edu.eclass.PMS;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Saves a demo attendance notification addressed to the student's parent or guardian. */
public class ParentManagementSystem {
    private static final String STUDENT_FILE = "student.txt";
    private static final String NOTIFICATION_FILE = "parent_notifications.txt";
    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public boolean notifyAttendance(String studentId, String className) {
        String[] student = findStudent(studentId);
        if (student == null) {
            System.out.println("Student ID " + studentId + " was not found in " + STUDENT_FILE + ".");
            return false;
        }

        String parentPhone;
        if (student.length >= 9) {
            parentPhone = student[8].trim();
        } else if (student.length >= 6) {
            parentPhone = student[5].trim();
        } else {
            parentPhone = student[4].trim();
        }
        if (parentPhone.isEmpty()) {
            System.out.println("No parent/guardian phone number is saved for " + studentId + ".");
            return false;
        }
        String dateTime = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String message = "To Parent Phone: " + parentPhone + System.lineSeparator()
                + "Attendance update: " + student[1].trim() + " (Student ID: " + studentId + ")"
                + " attended " + className + " on " + dateTime + "."
                + System.lineSeparator() + System.lineSeparator();

        try (FileWriter writer = new FileWriter(NOTIFICATION_FILE, true)) {
            writer.write(message);
            System.out.println("Parent notification saved to " + NOTIFICATION_FILE + ".");
            return true;
        } catch (IOException e) {
            System.out.println("Could not save parent notification: " + e.getMessage());
            return false;
        }
    }

    private String[] findStudent(String studentId) {
        try (BufferedReader reader = new BufferedReader(new FileReader(STUDENT_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",", -1);
                if (fields.length >= 5 && fields[0].trim().equals(studentId)) {
                    return fields;
                }
            }
        } catch (IOException e) {
            System.out.println("Could not read student details: " + e.getMessage());
        }
        return null;
    }
}
