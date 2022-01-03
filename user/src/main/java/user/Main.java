package user;

import processor.Test;


public class Main {

    public static void main(String[] args) throws Exception{
        System.out.println("success");
        test();
    }

    @Test
    public static void test(){
        System.out.println("this is oragin print");
        if(Math.random()>=0.5){
            System.out.println("ge 0.5");
        }else{
            System.out.println("lt 0.5");
        }
    }
}