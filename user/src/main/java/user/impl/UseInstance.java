package user.impl;

import processor.MethodAnnotation;

/**
 * @author long.zhou1@dmall.com
 * @date 2022/1/4 11:21
 */
public class UseInstance {

    @MethodAnnotation
    public void test(){
        System.out.println("this is oragin print");
        if(Math.random()>=0.5){
            System.out.println("ge 0.5");
        }else{
            System.out.println("lt 0.5");
        }
    }
}
