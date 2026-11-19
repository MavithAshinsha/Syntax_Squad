package edu.eclass.Log_reg;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class logReg {
    private static final String STUDENT_FILE = "student.txt";

    private final Scanner scanner;
    private String stid;
    private String pw;
    private String initialsName;
    private String fullName;
    private String birthDate;
    private String email;
    private String pTel;
    private String parentName;
    private String parentTel;
    private int nextStudentNumber;

    public logReg() {
        this(new Scanner(System.in));
    }

    public logReg(Scanner scanner) {
        this.scanner = scanner;
        this.nextStudentNumber = getLastId();
    }

    public String getStid() {
        return stid;
    }

    public void setStid(String stid) {
        this.stid = stid;
    }

    public String getName() {
        return initialsName;
    }

    public void setName(String name) {
        this.initialsName = name;
    }

    public String getInitialsName() {
        return initialsName;
    }

    public void setInitialsName(String initialsName) {
        this.initialsName = initialsName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPtel() {
        return pTel;
    }

    public void setPtel(String pTel) {
        this.pTel = pTel;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getParentTel() {
        return parentTel;
    }

    public void setParentTel(String parentTel) {
        this.parentTel = parentTel;
    }

    public void reg() {
        System.out.print("\nEnter Your Name with Initials: ");
        setInitialsName(scanner.nextLine().trim());
        System.out.print("Enter Your Full Name: ");
        setFullName(scanner.nextLine().trim());
        System.out.print("Enter Your Birth Date (YYYY-MM-DD): ");
        setBirthDate(scanner.nextLine().trim());
        System.out.print("Enter the Password: ");
        pw = scanner.nextLine();
        System.out.print("Enter Your Email: ");
        setEmail(scanner.nextLine().trim());
        System.out.print("Enter Your Number: ");
        setPtel(scanner.nextLine().trim());
        System.out.print("Enter Your Parent Name: ");
        setParentName(scanner.nextLine().trim());
        System.out.print("Enter Your Parent Number: ");
        setParentTel(scanner.nextLine().trim());

        setStid(String.format("ST%03d", nextStudentNumber));
        if (saveData(getStid(), getInitialsName(), pw, getEmail(), getPtel(),
                getFullName(), getBirthDate(), getParentName(), getParentTel())) {
            nextStudentNumber++;
            System.out.println("\nRegistration successful!");
            System.out.println("Your Student ID: " + getStid());
        }
    }

    public String login() {
        System.out.print("\nEnter your Student ID: ");
        String studentId = scanner.nextLine().trim();
        System.out.print("Enter your Password: ");
        String password = scanner.nextLine();

        if (checkID(studentId, password)) {
            System.out.println("\nLogin successful!");
            return studentId;
        }

        System.out.println("\nLogin failed!");
        return null;
    }

    public boolean saveData(String sid, String initialsName, String password, String email,
            String pTel, String fullName, String birthDate, String parentName, String parentTel) {
        try (FileWriter writer = new FileWriter(STUDENT_FILE, true)) {
            writer.write(sid + "," + initialsName + "," + password + "," + email + ","
                    + pTel + "," + fullName + "," + birthDate + "," + parentName + ","
                    + parentTel + System.lineSeparator());
            return true;
        } catch (IOException e) {
            System.out.println("Could not save student details: " + e.getMessage());
            return false;
        }
    }

    public boolean saveData(String sid, String name, String password, String email,
            String phone, String parentPhone) {
        return saveData(sid, name, password, email, phone, "", "", "", parentPhone);
    }

    public boolean saveData(String sid, String name, String password, String email, String phone) {
        return saveData(sid, name, password, email, phone, phone);
    }

    public static boolean checkID(String id, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(STUDENT_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",", -1);
                if (fields.length >= 3
                        && fields[0].trim().equals(id)
                        && fields[2].equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("Could not read student details: " + e.getMessage());
        }
        return false;
    }

    public static boolean checkID(int id, String password) {
        return checkID(String.valueOf(id), password);
    }

    public int getLastId() {
        int lastId = 0;
        File studentFile = new File(STUDENT_FILE);
        if (!studentFile.exists()) {
            return 1;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(studentFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",", -1);
                if (fields.length == 0) {
                    continue;
                }
                String studentId = fields[0].trim();
                String number = studentId.matches("(?i)ST\\d+")
                        ? studentId.substring(2)
                        : studentId;
                if (number.matches("\\d+")) {
                    lastId = Math.max(lastId, Integer.parseInt(number));
                }
            }
        } catch (IOException e) {
            System.out.println("Could not read student details: " + e.getMessage());
        }

        return lastId + 1;
    }
}