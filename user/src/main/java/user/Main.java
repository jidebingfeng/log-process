package user;

import user.impl.UseInstance;


public class Main {

    public static void main(String[] args) throws Exception{
        System.out.println("success");
        UseInstance useInstance = new UseInstance();
        useInstance.test();
    }
}
