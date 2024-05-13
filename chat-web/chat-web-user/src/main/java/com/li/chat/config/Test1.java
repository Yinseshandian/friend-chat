package com.li.chat.config;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;

/**
 * @author malaka
 */
public class Test1 {

    public static void main(String[] args)  {
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass = pool.makeClass("com.li.chat.common.utils.ResultData1");
        try {
            ctClass.toClass();
        } catch (CannotCompileException e) {
            System.out.println(114514);
            e.printStackTrace();
        }
    }

}
