package main.java.edu.eclass.updateProfile;

import java.util.Scanner;

public class test1 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        test1 profile1 = new test1();
        profile1.loadFromFile();
        System.out.println("-------------------");

        String newName = "";
        String newEmail = "";
        String newptel = "";
        String newpw = "";
        String newparentName = "";
        String newparentTel = "";

        System.out.println("---Enter your choise for update---");
        System.out.println("1.Your name\n" + "2.Your Email\n" + "3.Your Phone number\n" + "4.Your password\n" +
                "5.Parent's name\n" + "6.Parent's phone number");

        int choise = sc.nextInt();
        sc.nextLine();

        switch (choise) {
            case 1:
                System.out.print("Enter new name: ");
                newName = sc.nextLine();
                profile1.updtName(newName);
                break;

            case 2:
                System.out.print("Enter new Email: ");
                newEmail = sc.nextLine();
                profile1.updtEmail(newEmail);
                break;
            case 3:
                System.out.print("Enter new Phone Number: ");
                newptel = sc.nextLine();
                profile1.updtTel(newptel);
                break;

            case 4:
                System.out.print("Enter new password: ");
                newpw = sc.nextLine();
                profile1.updtPw(newpw);
                break;
            case 5:
                System.out.print("Enter Parent's Name:");
                newparentName = sc.nextLine();
                profile1.updtParentName(newparentName);
                break;

            case 6:
                System.out.print("Enter Parent's phone number:");
                newparentTel = sc.nextLine();
                profile1.updtParentTel(newparentTel);
                break;

            default:
                System.out.println("Invalid choise");
                break;

        }

        System.out.println("==================");
        profile1.viewProfile();
        sc.close();
    }
}
