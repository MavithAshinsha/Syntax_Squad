package main.java.edu.eclass.Log_reg.model;
import java.util.Scanner;


public class EclassMain {
    
    public static void main(String[] args) {
        
        Scanner sc = new Scanner(System.in);
        logReg l=new logReg();
        int choice;
        
        
       
        do{
            System.out.println("\n==============================");
            System.out.println("       E-CLASS SYSTEM");
            System.out.println("==============================");

            System.out.println("1. Student Registration");
            System.out.println("2. Student Login");
            System.out.println("0. Exit");

            System.out.print("Enter your choice: ");

            choice = sc.nextInt();
            
            sc.nextLine();

            switch(choice) {

                case 1:
                    l.reg();
                    break;

                case 2:
                    l.login();
                    break;

                case 0:
                    System.out.println("Thank you for using E-Class!");
                    break;

                default:
                    System.out.println("Invalid choice!");
            }

        } while(choice != 0);

        sc.close();
    }
        
        
       
    }