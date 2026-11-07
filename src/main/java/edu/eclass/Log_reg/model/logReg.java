package main.java.edu.eclass.Log_reg.model;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class logReg {

    Scanner sc1 = new Scanner(System.in);
    Scanner sc2 = new Scanner(System.in);

    int stid;
    String pw;
    String name;
    private String email;
    private String pTel;
    int id = getLastId();

    //setter
    public logReg() {

    }

    public logReg(int stid, String pw, String name, String email, String pTel) {
        this.stid = stid;
        this.pw = pw;
        this.name = name;
        this.email = email;
        this.pTel = pTel;

    }

    //getter
    public int getStid() {
        return stid;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPtel() {
        return pTel;
    }
    
    public void reg() {
        System.out.print("\n\nEnter Your Name: ");
        String n = sc2.nextLine();
        System.out.print("Enter the Password: ");
        String pw = sc2.nextLine();
        System.out.print("Enter Your Email: ");
        String email = sc2.nextLine();
        System.out.print("Enter Your Number: ");
        String pn = sc2.nextLine();
        System.out.print("\n");
        
        int sid = id++;
        saveData(sid, n, pw, email, pn);
        System.out.println("\nRegistration Succesful!");
        System.out.println("Your Student ID: "+sid);
    }
    
    public void login() {
        System.out.print("\n\nEnter your Student ID: ");
        int sId = sc1.nextInt();
        System.out.print("Enter your Password: ");
        String pw=sc2.nextLine();
        if (checkID(sId,pw)) {
            System.out.println("\nLogin Succesful!");
        }else{
            System.out.println("\nLogin Faild!");
        }
    }

    public void saveData(int sid, String na, String pw, String email, String ptel) {
        try (FileWriter writer = new FileWriter("student.txt", true)) {
            writer.write(sid + "," + na + "," + pw + "," + email + "," + ptel + "\n");
        } catch (IOException e) {
            System.out.println("invalid: " + e.getMessage());
        }
    }
    
    public static boolean checkID(int id,String pw){
        try(BufferedReader r=new BufferedReader(new FileReader("student.txt"))){
            String line;
            while((line=r.readLine())!=null){
                String[] fields=line.split(",",-1);
                if(fields[0].equals(String.valueOf(id))){
                    if(fields[2].equals(String.valueOf(pw))){
                        return true;
                    }else{
                        return false;
                    }
                    
                }else{
                    return false;
                }            
            }
            return false;
        }
        catch (IOException e) {
            System.out.println("invalid: " + e.getMessage());
            return false;
        }
    }
    public int getLastId() {

    int lastId = 0;

    try {
        FileReader fr = new FileReader("student.txt");
        BufferedReader br = new BufferedReader(fr);

        String line;

        while ((line = br.readLine()) != null) {

            String data[] = line.split(",");

            lastId = Integer.parseInt(data[0]);
        }

        br.close();

    } catch (IOException e) {
        System.out.println(e.getMessage());
    }

    return lastId+1;
}
    
}