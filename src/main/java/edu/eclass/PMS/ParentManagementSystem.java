package edu.eclass.PMS;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Finds the student's parent phone and appends a demo attendance notice to a text file. */
public class ParentManagementSystem {


    private final String studentFile = "student.txt";
    private final String notificationFile = "parent_notifications.txt";

    
    public boolean notifyAttendance(int studentId, String className) {
        String[] student = findStudent(studentId);

        if (student == null) {
            System.out.println("Student ID " + studentId + " was not found in student.txt.");
            return false;
        }

        String studentName = student[1];
        String parentPhone = student[5];
        String dateTime = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        String message = "To Parent Phone: " + parentPhone + "\n"
                + "Attendance update: " + studentName + " (Student ID: " + studentId + ")"
                + " attended " + className + " on " + dateTime + ".\n\n";

        
        try (FileWriter writer = new FileWriter(notificationFile, true)) {
            writer.write(message);
            System.out.println("The notification has been successfully send  " + notificationFile);
            return true;
        } catch (IOException e) {
            System.out.println("Could not save parent notification: " + e.getMessage());
            return false;
        }
    }

    /** Returns the matching comma-separated student record, or null if not found. */
    private String[] findStudent(int studentId) {
        try (BufferedReader reader = new BufferedReader(new FileReader(studentFile))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",", -1);

                // Field 0 is the ID. Field 5 is the parent's phone number.
                if (fields.length >= 6 && fields[0].trim().equals(String.valueOf(studentId))) {
                    return fields;
                }
            }
        } catch (IOException e) {
            System.out.println("Could not read student.txt: " + e.getMessage());
        }

        return null;
    }
}
