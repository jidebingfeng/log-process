package jsr269.user.impl;

import java.util.stream.Stream;

/**
 * @author long.zhou1@dmall.com
 * @date 2022/1/4 11:21
 */
public class User {
    private String s = "sssss";

    public void test(){
        System.out.println("this is oragin print");
        Integer b = Math.random() >= 0.5?1:0;
        if(b==1){
            System.out.println("ge 0.5");
        }else if(b==0){
            System.out.println("lt 0.5");
        }else{
            System.out.println("UNKOWN");
        }

        Stream.iterate(0,t->t+1).limit(10).map(t->Math.random())
                .filter(t->t>=0.5).forEach(t->{
                    System.out.println(t);
                });


    }
}
