package jsr269.user.impl;

import java.util.List;
import java.util.stream.Collectors;
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


        if(Math.random() >= 0.5) b =8;

        if(Math.random() > 0.5 )return;

        if(Math.random() >= 0.5){
            b =8;
        }

        if(b==1){
            System.out.println("b == 1");
        }else if(b==0){
            System.out.println("b == 0");
        }else if(b==-1){
            System.out.println("b == -1");
        }else{
            return;
        }

        List<Double> list = Stream.iterate(0, t -> t + 1).limit(10)
                .map(t -> Math.random()).collect(Collectors.toList());

        for (Double d : list) {
            if(d > 0.3)continue;

            if(d > 0.4){
                continue;
            }

            if(d> 0.99)break;

            if(d>0.9){
                break;
            }

            if(d> 0.99)return;

            if(d>0.9){
                return;
            }
            System.out.println(d);

            if(d>100)return;
            else return;
        }

        if(Math.random() > 0.5 )return;

    }

    public void test1(){

        if(Math.random() > 0.3)return;


        System.out.println("ddd");
    }
}
