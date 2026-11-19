package edu.eclass.Log_reg;

import edu.eclass.Payment.Payment;
import edu.eclass.attendence.attendence;
import edu.eclass.course.Course;
import edu.eclass.updateProfile.updateProfile;
import java.util.Scanner;

public class EclassConsoleMain{
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        logReg account = new logReg(scanner);
        int choice;

        do {
            System.out.println("\n==============================");
            System.out.println("       E-CLASS PORTAL");
            System.out.println("==============================");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Admin Login");
            System.out.println("0. Exit");
            choice = readChoice(scanner, "Enter your choice: ");

            switch (choice) {
                case 1:
                    account.reg();
                    break;
                case 2:
                    String studentId = account.login();
                    if (studentId != null) {
                        openDashboard(scanner, studentId);
                    }
                    break;
                case 3:
                    new adminLogin(scanner).loginAndOpen();
                    break;
                case 0:
                    System.out.println("Thank you for using E-Class!");
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        } while (choice != 0);
    }

    private static void openDashboard(Scanner scanner, String studentId) {
        int choice;
        do {
            System.out.println("\n==============================");
            System.out.println("       STUDENT DASHBOARD");
            System.out.println("==============================");
            System.out.println("01. View Profile");
            System.out.println("02. View Courses");
            System.out.println("03. Make Payment");
            System.out.println("04. View Attendance");
            System.out.println("05. Update Profile");
            System.out.println("06. Logout");

            choice = readChoice(scanner, "\nEnter your choice: ");
            switch (choice) {
                case 1:
                    new updateProfile().viewProfile(studentId);
                    break;
                case 2:
                    new Course().manageCourses(scanner, studentId);
                    break;
                case 3:
                    new Payment().makePayment(studentId, scanner);
                    break;
                case 4:
                    new attendence().markAtt(studentId, scanner);
                    break;
                case 5:
                    new updateProfile().updateProfileInfo(studentId, scanner);
                    break;
                case 6:
                    System.out.println("\nLogging out...");
                    break;
                default:
                    System.out.println("\nInvalid dashboard choice!");
            }
        } while (choice != 6);
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
