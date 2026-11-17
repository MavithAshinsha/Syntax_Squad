package main.java.edu.eclass.Log_reg.model;
import java.util.Scanner;


public class EclassMain {
    
    public static void main(String[] args) {
        
        Scanner sc = new Scanner(System.in);
        logReg l=new logReg(sc);
        int choice;
        
        
        do{
            System.out.println("\n==============================");
            System.out.println("       E-CLASS SYSTEM");
            System.out.println("==============================");

            System.out.println("1. Student Registration");
            System.out.println("2. Student Login");
            System.out.println("3. Admin Login");
            System.out.println("0. Exit");

            System.out.print("Enter your choice: ");

            choice = sc.nextInt();
            
            sc.nextLine();

            switch(choice) {

                case 1:
                    l.reg();
                    break;

                case 2:
                    if (l.login() == 1) {
                        int dashboardChoice;
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

                            System.out.print("\nEnter your choice: ");
                            dashboardChoice = sc.nextInt();
                            sc.nextLine();

                            switch (dashboardChoice) {
                                case 1:
                                    System.out.println("\nView Profile Dashboard is opening...");
                                    break;
                                case 2:
                                    System.out.println("\nCourse Dashboard is opening...");
                                    break;
                                case 3:
                                    System.out.println("\nPayment Dashboard is opening...");
                                    break;
                                case 4:
                                    System.out.println("\nAttendance Dashboard is opening...");
                                    break;
                                case 5:
                                    System.out.println("\nUpdate Profile Dashboard is opening...");
                                    break;
                                case 6:
                                    System.out.println("\nLogging out...");
                                    break;
                                default:
                                    System.out.println("\nInvalid dashboard choice!");
                            }
                        } while (dashboardChoice != 6);

                        choice = 0;
                        System.out.println("\nThank you for using E-Class!\n");
                    }
                    break;
                case 3:
                    Scanner scanner = new Scanner(System.in);
                    new adminLogin(scanner).loginAndOpen();
                    break;
                case 0:
                    System.out.println("\nThank you for using E-Class!\n");
                    break;

                default:
                    System.out.println("\nInvalid choice!\n");
            }

        } while(choice != 0);

        sc.close();
    }
        
        
       
    }