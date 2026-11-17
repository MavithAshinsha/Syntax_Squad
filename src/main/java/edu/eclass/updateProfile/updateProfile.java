package main.java.edu.eclass.updateProfile;

import java.io.*;

public class updateProfile {
    private int studentID;
    private String studentName;
    private String email;
    private String pTel;
    private String pw;
    private String parentName;
    private String parentTel;

    public void loadFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(student.txt))) {
            String line;
            while ((line = reader.readlLine()) != null) {
                String[] parts = line.split(": ", 2);
                if (parts.length < 2)
                    continue;

                String key = parts[0];
                String value = parts[1];

                if (key.equals("Name"))
                    studentName = value;
                else if (key.equals("Email"))
                    email = value;
                else if (key.equals("Phone"))
                    pTel = value;
                else if (key.equals("Password"))
                    pw = value;
                else if (key.equals("Parent Name"))
                    parentName = value;
                else if (key.equals("Parent phone"))
                    parentTel = value;
            }
            System.out.println("Old Details Loaded!");
        } catch (IOException e) {
            System.out.println("File not Found!");
        }
    }

    public void saveFile() {
        try (FileWriter writer = new FileWriter(student.txt)) {
            writer.write(("Name: " + studentName + "\n"));
            writer.write("Email: " + email + "\n");
            writer.write("Phone: " + pTel + "\n");
            writer.write("password: " + pw + "\n");
            writer.write("Parent Name: " + parentName + "\n");
            writer.write("Parent Phone: " + parentTel + "\n");
            System.out.println("Save to file!");
        } catch (IOException e) {
            System.out.println("Error saving!");
        }
    }

    public void updtParentTel(String newparentTel) {
        this.parentTel = newparentTel;
        saveFile();
    }

    public void updtTel(String newptel) {
        this.pTel = newptel;
        saveFile();
    }

    public void updtName(String newName) {
        this.studentName = newName;
        saveFile();
    }

    public void updtEmail(String newEmail) {
        this.email = newEmail;
        saveFile();
    }

    public void updtPw(String newPw) {
        this.pw = newPw;
        saveFile();
    }

    public void updtParentName(String newparentName) {
        this.parentName = newparentName;
        saveFile();
    }

    public void viewProfile() {
        System.out.println("Student ID: " + studentID);
        System.out.println("Student name: " + studentName);
        System.out.println("E-mail: " + email);
        System.out.println("Phone Number: " + pTel);
        System.out.println("Parent's Name: " + parentName);
        System.out.println("Parent's phone number:" + parentTel);

    }

    public String getName() {
        return studentName;
    }

    public String getemail() {
        return email;
    }

    public String getpTel() {
        return pTel;
    }

    public String getpw() {
        return pw;
    }

    public String getparentName() {
        return parentName;
    }

    public String getparentTel() {
        return parentTel;
    }
}
