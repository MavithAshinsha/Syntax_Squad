package eclass;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
public class attendence {
   
   public attendence(){
       
   }
   
   public void markAtt(int sId){
       LocalDateTime dt=LocalDateTime.now();
       try(FileWriter fw=new FileWriter("attendence.txt")){
           fw.write(sId+"   "+ dt);
       }
       catch(IOException e){
           System.out.println("An error has occured");
       }
   }
   
}