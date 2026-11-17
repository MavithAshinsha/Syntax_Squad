import java.util.Scanner;
import java.io.*;

public class Payment extends Course {
     public Payment() {
    }
   
    public Payment(String courseId, String courseName, double courseFee) {
        super(courseId, courseName, courseFee);
    }
    // Read course details from courses.txt
    public boolean findCourse(String id) {

        try {
            Scanner sc = new Scanner(new File("courses.txt"));

            while (sc.hasNextLine()) {

                String[] data = sc.nextLine().split(",");

                if (data.length == 3 && data[0].equals(id)) {

                    courseId = data[0];
                    courseName = data[1];
                    courseFee = Double.parseDouble(data[2]);

                    sc.close();
                    return true;
                }
            }

            sc.close();

        } catch (Exception e) {
            System.out.println("Course File Error!");
        }

        return false;
    }

    // Check student registration
    public boolean checkRegistration(String studentId, String courseId) {

        try {
            Scanner sc = new Scanner(new File("CourseRegistration.txt"));

            boolean foundStudent = false;

            while (sc.hasNextLine()) {

                String line = sc.nextLine();

                if (line.equals("Student ID: " + studentId)) {
                    foundStudent = true;
                }

                else if (foundStudent && line.equals("Course ID: " + courseId)) {
                    sc.close();
                    return true;
                }

                else if (line.equals("-------------------")) {
                    foundStudent = false;
                }
            }

            sc.close();

        } catch (Exception e) {
            System.out.println("Registration File Error!");
        }

        return false;
    }

    // Update payment status in CourseRegistration.txt
    public void updatePayment(String studentId, String courseId, String status) {

        try {
            File file = new File("CourseRegistration.txt");
            Scanner sc = new Scanner(file);

            String data = "";
            boolean foundStudent = false;

            while (sc.hasNextLine()) {

                String line = sc.nextLine();

                if (line.equals("Student ID: " + studentId)) {
                    foundStudent = true;
                }

                // Remove previous payment status
                if (foundStudent && line.startsWith("Payment Status:")) {
                    continue;
                }

                data += line + "\n";

                // Add new payment status
                if (foundStudent && line.equals("Course ID: " + courseId)) {
                    data += "Payment Status: " + status + "\n";
                }

                if (line.equals("-------------------")) {
                    foundStudent = false;
                }
            }

            sc.close();

            FileWriter fw = new FileWriter(file);
            fw.write(data);
            fw.close();

        } catch (Exception e) {
            System.out.println("File Error!");
        }
    }

    // Main payment method
    public void makePayment(String studentId, Scanner input) {
            System.out.println();
            System.out.println("       Welcome to the Payment Portal!");
            
            System.out.println();
            // Enter Course ID
            System.out.print("Enter your Course ID: ");
            String id = input.nextLine();

        // Read course details
        if (findCourse(id)) {
            System.out.println("Course Fee of " + courseName + "  is : " + courseFee);

           
            // Check registration
            if (checkRegistration(studentId, courseId)) {
                // Enter card details
                System.out.print("Enter Card Holder Name: ");
                String cardHolder = input.nextLine();

                System.out.print("Enter Card Number: ");
                String cardNumber = input.nextLine();

                System.out.print("Enter Expiry Date (MM/YY): ");
                String expiryDate = input.nextLine();

                System.out.print("Enter CVV: ");
                String cvv = input.nextLine();

                // Enter payment amount
                System.out.print("Enter Payment Amount: ");

                try {
                    double amount = Double.parseDouble(input.nextLine());

                    // Compare amount with course fee
                    if (amount == courseFee) {

                        System.out.println("Payment Successful!");

                        updatePayment(studentId, courseId, "Successful");

                    } else {

                        System.out.println("Payment Unsuccessful!");

                        updatePayment(studentId, courseId, "Unsuccessful");
                    }

                } catch (NumberFormatException e) {
                    System.out.println("Invalid Payment Amount!");
                }

            } else {
                System.out.println("Student is not registered for this course!");
            }

        } else {
            System.out.println("Course ID Not Found!");
        }
    }
}