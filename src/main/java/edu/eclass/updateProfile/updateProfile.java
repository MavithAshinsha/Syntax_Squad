package main.java.edu.eclass.updateProfile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class updateProfile {
    private static final String STUDENT_FILE = "student.txt";

    public void viewProfile(String studentId) {
        try {
            String[] student = findStudent(studentId);
            if (student == null) {
                System.out.println("Student profile not found.");
                return;
            }

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
            System.out.println("Could not read student profile: " + e.getMessage());
        }
    }

    public void updateProfile(String studentId, Scanner scanner) {
        try {
            String[] student = findStudent(studentId);
            if (student == null) {
                System.out.println("Student profile not found.");
                return;
            }

            boolean changed = false;
            while (true) {
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

                int choice = readChoice(scanner, "Choose a field to update: ");
                if (choice == 0) {
                    if (changed) {
                        saveStudent(studentId, student);
                        System.out.println("Profile changes saved.");
                    } else {
                        System.out.println("No profile changes to save.");
                    }
                    return;
                }

                int fieldIndex;
                String fieldName;
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

                System.out.print("Enter new " + fieldName + ": ");
                String newValue = scanner.nextLine().trim();
                if (newValue.isEmpty() || newValue.contains(",")) {
                    System.out.println("Value cannot be empty or contain commas.");
                    continue;
                }

                student[fieldIndex] = newValue;
                changed = true;
                System.out.println(fieldName + " updated. Choose another field or enter 0 to save.");
            }
        } catch (IOException e) {
            System.out.println("Could not update student profile: " + e.getMessage());
        }
    }

    private String[] findStudent(String studentId) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(STUDENT_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] student = line.split(",", -1);
                if (student.length >= 9 && student[0].trim().equals(studentId)) {
                    return student;
                }
            }
        }
        return null;
    }

    private void saveStudent(String studentId, String[] updatedStudent) throws IOException {
        File file = new File(STUDENT_FILE);
        List<String> lines = new ArrayList<>();
        boolean updated = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",", -1);
                if (!updated && fields.length >= 9 && fields[0].trim().equals(studentId)) {
                    lines.add(String.join(",", updatedStudent));
                    updated = true;
                } else {
                    lines.add(line);
                }
            }
        }

        if (!updated) {
            throw new IOException("Student record was not found.");
        }

        try (FileWriter writer = new FileWriter(file)) {
            for (String line : lines) {
                writer.write(line + System.lineSeparator());
            }
        }
    }

    private int readChoice(Scanner scanner, String prompt) {
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
