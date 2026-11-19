import java.util.Scanner;
import java.io.*;

public class Payment extends Course {
     public Payment() {
    }
   
    public Payment(String courseId, String courseName, double courseFee) {
        super(courseId, courseName, courseFee);
    }
    // Read course details from Courses.txt
    public boolean findCourse(String id) {
        try (Scanner sc = new Scanner(new File("Courses.txt"))) {
            while (sc.hasNextLine()) {
                String[] data = sc.nextLine().split(",", -1);
                if (data.length == 3 && data[0].trim().equalsIgnoreCase(id.trim())) {
                    try {
                        courseFee = Double.parseDouble(data[2].trim());
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid course fee for " + data[0].trim() + ".");
                        return false;
                    }
                    courseId = data[0].trim();
                    courseName = data[1].trim();
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("Could not read course details from Courses.txt: " + e.getMessage());
        }

        return false;
    }

    // Check student registration
    public boolean checkRegistration(String studentId, String courseId) {
        try (Scanner sc = new Scanner(new File("CourseRegistration.txt"))) {
            String currentStudentId = null;
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.startsWith("Student ID: ")) {
                    currentStudentId = line.substring("Student ID: ".length()).trim();
                } else if (studentId.equals(currentStudentId)
                        && line.startsWith("Course ID: ")
                        && courseId.equalsIgnoreCase(
                                line.substring("Course ID: ".length()).trim())) {
                    return true;
                }
            }
        } catch (Exception e) {
            System.out.println("Registration File Error: " + e.getMessage());
        }

        return false;
    }

    // Check whether the student has successfully paid for this course.
    public boolean hasSuccessfulPayment(String studentId, String courseId) {
        try (Scanner sc = new Scanner(new File("CourseRegistration.txt"))) {
            String currentStudentId = null;
            String currentCourseId = null;
            String paymentStatus = null;

            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();

                if (line.startsWith("Student ID: ")) {
                    currentStudentId = line.substring("Student ID: ".length()).trim();
                    currentCourseId = null;
                    paymentStatus = null;
                } else if (line.startsWith("Course ID: ")) {
                    currentCourseId = line.substring("Course ID: ".length()).trim();
                } else if (line.startsWith("Payment Status: ")) {
                    paymentStatus = line.substring("Payment Status: ".length()).trim();
                } else if (line.equals("-------------------")) {
                    if (studentId.equals(currentStudentId)
                            && courseId.equalsIgnoreCase(currentCourseId)
                            && "Successful".equalsIgnoreCase(paymentStatus)) {
                        return true;
                    }
                    currentStudentId = null;
                    currentCourseId = null;
                    paymentStatus = null;
                }
            }

            return studentId.equals(currentStudentId)
                    && courseId.equalsIgnoreCase(currentCourseId)
                    && "Successful".equalsIgnoreCase(paymentStatus);
        } catch (Exception e) {
            System.out.println("Registration File Error: " + e.getMessage());
            return false;
        }
    }

    // Update payment status in CourseRegistration.txt
    public boolean updatePayment(String studentId, String courseId, String status) {
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
                if (foundStudent && line.startsWith("Course ID: ")
                        && line.substring("Course ID: ".length()).trim()
                                .equalsIgnoreCase(courseId)) {
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
            return true;
        } catch (IOException e) {
            System.out.println("Could not update payment status: " + e.getMessage());
            return false;
        }
    }

    // Main payment method
    public void makePayment(String studentId, Scanner input) {
            System.out.println();
            System.out.println("       Welcome to the Payment Portal!");
            
            System.out.println();
            // Enter Course ID
            System.out.print("Enter your Course ID: ");
            String id = input.nextLine().trim();

        // Read course details
        if (findCourse(id)) {
            System.out.println("Course: " + courseName + " (" + courseId + ")");
            System.out.printf("Course Fee: %.2f%n", courseFee);
           
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

                if (updatePayment(studentId, courseId, "Successful")) {
                    System.out.println("Payment Successful for " + courseName + ".");
                }

            } else {
                System.out.println("Student is not registered for this course!");
            }

        } else {
            System.out.println("Course ID Not Found!");
        }
    }
}