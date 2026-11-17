package main.java.edu.eclass.Log_reg.model;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class logReg {

    private final Scanner scanner;

    private String stid;
    String pw;
    private String initialsName;
    private String fullName;
    private String birthDate;
    private String email;
    private String pTel;
    private String parentName;
    private String parentTel;
    private int nextStudentNumber = getLastId();

    //setter
    public logReg() {
        this(new Scanner(System.in));

    }

    public logReg(Scanner scanner) {
        this.scanner = scanner;
    }

    public logReg(String stid, String pw, String name, String email, String pTel) {
        this();
        this.stid = stid;
        this.pw = pw;
        this.initialsName = name;
        this.email = email;
        this.pTel = pTel;

    }

    public logReg(int stid, String pw, String name, String email, String pTel) {
        this(String.valueOf(stid), pw, name, email, pTel);
    }

    //getter
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
        System.out.print("\n\nEnter Your Name with Initials: ");
        setInitialsName(scanner.nextLine());
        System.out.print("Enter Your Full Name: ");
        setFullName(scanner.nextLine());
        System.out.print("Enter Your Birth Date (YYYY-MM-DD): ");
        setBirthDate(scanner.nextLine());
        System.out.print("Enter the Password: ");
        String pw = scanner.nextLine();
        System.out.print("Enter Your Email: ");
        setEmail(scanner.nextLine());
        System.out.print("Enter Your Number: ");
        setPtel(scanner.nextLine());

        System.out.print("Enter Your Parent Name: ");
        setParentName(scanner.nextLine());
        System.out.print("Enter Your Parent Number: ");
        setParentTel(scanner.nextLine());



        System.out.print("\n");
        
        setStid(String.format("ST%03d", nextStudentNumber++));
        saveData(getStid(), getInitialsName(), pw, getEmail(), getPtel(), getFullName(), getBirthDate(),
                getParentName(), getParentTel());
        System.out.println("\nRegistration Succesful!");
        System.out.println("Your Student ID: "+getStid());
    }
    
    public int login() {
        System.out.print("\n\nEnter your Student ID: ");
        String sId = scanner.nextLine().trim();
        System.out.print("Enter your Password: ");
        String pw = scanner.nextLine();
        if (checkID(sId,pw)) {
            System.out.println("\nLogin Succesful!");
            return 1;
        }else{
            System.out.println("\nLogin Faild!");
            return 0;
        }
    }

    public void saveData(String sid, String na, String pw, String email, String ptel) {
        try (FileWriter writer = new FileWriter("student.txt", true)) {
            writer.write(sid + "," + na + "," + pw + "," + email + "," + ptel + "\n");
        } catch (IOException e) {
            System.out.println("invalid: " + e.getMessage());
        }
    }

    public void saveData(String sid, String initialsName, String pw, String email, String ptel,
                         String fullName, String birthDate, String parentName, String parentTel) {
        try (FileWriter writer = new FileWriter("student.txt", true)) {
            writer.write(sid + "," + initialsName + "," + pw + "," + email + "," + ptel + ","
                    + fullName + "," + birthDate + "," + parentName + "," + parentTel + "\n");
        } catch (IOException e) {
            System.out.println("invalid: " + e.getMessage());
        }
    }

    public void saveData(int sid, String na, String pw, String email, String ptel) {
        saveData(String.valueOf(sid), na, pw, email, ptel);
    }
    
    public static boolean checkID(String id,String pw){
        try(BufferedReader r=new BufferedReader(new FileReader("student.txt"))){
            String line;
            while((line=r.readLine())!=null){
                String[] fields=line.split(",",-1);
                if(fields.length >= 3
                        && fields[0].equals(id)
                        && fields[2].equals(pw)){
                    return true;
                }            
            }

            return false;
        }
        catch (IOException e) {
            System.out.println("invalid: " + e.getMessage());
            return false;
        }
    }

    public static boolean checkID(int id, String pw) {
        return checkID(String.valueOf(id), pw);
    }

    public int getLastId() {

    int lastId = 0;

    try (BufferedReader br = new BufferedReader(new FileReader("student.txt"))) {
        String line;

        while ((line = br.readLine()) != null) {
            String[] data = line.split(",", -1);
            String studentId = data[0].trim();
            String number = studentId.matches("(?i)ST\\d+")
                    ? studentId.substring(2)
                    : studentId;
            if (number.matches("\\d+")) {
                lastId = Math.max(lastId, Integer.parseInt(number));
            }
        }
    } catch (IOException e) {
        System.out.println(e.getMessage());
    } catch (NumberFormatException e) {
        System.out.println("Invalid student ID in student.txt: " + e.getMessage());
    }

    return lastId+1;
}
    
}