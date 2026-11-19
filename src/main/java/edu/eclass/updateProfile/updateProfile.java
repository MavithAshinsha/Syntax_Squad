package edu.eclass.updateProfile;

// Importing classes for file handling
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

// Importing ArrayList and List to store file records temporarily
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Class declaration
public class updateProfile {

    // Encapsulation: private static final variable
    // Stores the name of the student data file
    private static final String STUDENT_FILE = "student.txt";

    // Method to view a student's profile
    // Takes studentId as a parameter
    public void viewProfile(String studentId) {

        try {
            // Calling findStudent() to search for the student
            String[] student = findStudent(studentId);

            // Checking whether the student exists
            if (student == null) {
                System.out.println("Student profile not found.");
                return;
            }

            // Displaying student profile details
            System.out.println("\n===== STUDENT PROFILE =====");
            System.out.println("Student ID: " + student[0]);
            System.out.println("Name: " + student[1]);
            System.out.println("Email: " + student[3]);
            System.out.println("Phone: " + student[4]);
            System.out.println("Full Name: " + student[5]);
            System.out.println("Birth Date: " + student[6]);
            System.out.println("Parent Name: " + student[7]);
            System.out.println("Parent Phone: " + student[8]);

        } catch (IOException e) {
            // Exception handling for file reading errors
            System.out.println("Could not read student profile: " + e.getMessage());
        }
    }

    // Method to update student profile details
    // Uses Scanner to get user input
    public void updateProfileInfo(String studentId, Scanner scanner) {

        try {
            // Finding the student record using student ID
            String[] student = findStudent(studentId);

            if (student == null) {
                System.out.println("Student profile not found.");
                return;
            }

            // Local variable to track whether changes were made
            boolean changed = false;

            // Loop continues until the user chooses 0
            while (true) {

                // Displaying update menu
                System.out.println("\n===== UPDATE PROFILE =====");
                System.out.println("1. Name");
                System.out.println("2. Email");
                System.out.println("3. Phone Number");
                System.out.println("4. Password");
                System.out.println("5. Full Name");
                System.out.println("6. Birth Date");
                System.out.println("7. Parent Name");
                System.out.println("8. Parent Phone Number");
                System.out.println("0. Save changes and return to dashboard");

                // Calling readChoice() to get a valid integer choice
                int choice = readChoice(scanner, "Choose a field to update: ");

                // If user enters 0, save changes and exit
                if (choice == 0) {

                    if (changed) {
                        saveStudent(studentId, student);
                        System.out.println("Profile changes saved.");
                    } else {
                        System.out.println("No profile changes to save.");
                    }

                    return;
                }

                // Variables to identify the selected field
                int fieldIndex;
                String fieldName;

                // Switch statement selects the field to update
                switch (choice) {

                    case 1:
                        fieldIndex = 1;
                        fieldName = "Name";
                        break;

                    case 2:
                        fieldIndex = 3;
                        fieldName = "Email";
                        break;

                    case 3:
                        fieldIndex = 4;
                        fieldName = "Phone Number";
                        break;

                    case 4:
                        fieldIndex = 2;
                        fieldName = "Password";
                        break;

                    case 5:
                        fieldIndex = 5;
                        fieldName = "Full Name";
                        break;

                    case 6:
                        fieldIndex = 6;
                        fieldName = "Birth Date";
                        break;

                    case 7:
                        fieldIndex = 7;
                        fieldName = "Parent Name";
                        break;

                    case 8:
                        fieldIndex = 8;
                        fieldName = "Parent Phone Number";
                        break;

                    default:
                        System.out.println("Invalid choice.");
                        continue;
                }

                // Getting the new value from the user
                System.out.print("Enter new " + fieldName + ": ");
                String newValue = scanner.nextLine().trim();

                // Input validation
                // Empty values and commas are not allowed
                if (newValue.isEmpty() || newValue.contains(",")) {
                    System.out.println("Value cannot be empty or contain commas.");
                    continue;
                }

                // Updating the selected array element
                student[fieldIndex] = newValue;

                // Updating the change status
                changed = true;

                System.out.println(fieldName +
                        " updated. Choose another field or enter 0 to save.");
            }

        } catch (IOException e) {
            // Handling file-related exceptions
            System.out.println("Could not update student profile: " + e.getMessage());
        }
    }

    // Private method to search for a student in the text file
    // Returns the student record as a String array
    private String[] findStudent(String studentId) throws IOException {

        // BufferedReader reads the file line by line
        // Try-with-resources automatically closes the reader
        try (BufferedReader reader = new BufferedReader(new FileReader(STUDENT_FILE))) {

            String line;

            while ((line = reader.readLine()) != null) {

                // Splitting the record using commas
                // -1 preserves empty fields
                String[] student = line.split(",", -1);

                // Checking student ID and number of fields
                if (student.length >= 9 && student[0].trim().equals(studentId)) {
                    return student;
                }
            }
        }

        // Returning null if the student is not found
        return null;
    }

    // Private method to save updated student details
    private void saveStudent(String studentId, String[] updatedStudent)
            throws IOException {

        File file = new File(STUDENT_FILE);

        // ArrayList stores all file records temporarily
        List<String> lines = new ArrayList<>();

        boolean updated = false;

        // Reading existing records
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] fields = line.split(",", -1);

                // Finding the correct student record
                if (!updated && fields.length >= 9 &&
                        fields[0].trim().equals(studentId)) {

                    // Replacing old record with updated details
                    lines.add(String.join(",", updatedStudent));
                    updated = true;

                } else {
                    // Keeping other student records unchanged
                    lines.add(line);
                }
            }
        }

        // Checking whether the student record was updated
        if (!updated) {
            throw new IOException("Student record was not found.");
        }

        // Writing all records back to the file
        // FileWriter updates the file content
        try (FileWriter writer = new FileWriter(file)) {

            for (String line : lines) {
                writer.write(line + System.lineSeparator());
            }
        }
    }

    // Private method to validate menu input
    private int readChoice(Scanner scanner, String prompt) {

        while (true) {

            System.out.print(prompt);

            String input = scanner.nextLine().trim();

            try {
                // Converting String input into an integer
                return Integer.parseInt(input);

            } catch (NumberFormatException e) {

                // Handling invalid number inputs
                System.out.println("Please enter a valid number.");
            }
        }
    }
}
