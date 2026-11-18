
package com.mycompany.eclassmain;

public class logReg extends EclassMain {
    private int stid;
    private String pw;
    private String name;
    private String email;
    private String pTel;
    int y=3;
    int[] id=new int[y];
    int a=1;
    
    
    
    //setter
    public logReg(){
        
    }
    public logReg(int stid,String pw,String name,String email,String pTel){
        this.stid=stid;
        this.pw=pw;
        this.name=name;
        this.email=email;
        this.pTel=pTel;
        
    }
    //getter
    public int getStid(){
        return stid;
    }
    public String getName(){
        return name;
    }
    public String getEmail(){
        return email;
    }
    public String getPtel(){
        return pTel;
    }
    public void login(){
        for(int x=0;x<y;x++){
            id[x]=a;
            a++;
            y++;
            System.out.println(id[x]);
        }
    }
    
    
    
    
    
}
