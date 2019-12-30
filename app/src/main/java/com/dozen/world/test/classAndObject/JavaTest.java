package com.dozen.world.test.classAndObject;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Hugo on 19-9-26.
 * Describe:
 */
public class JavaTest {
    public static void main(String[] args) {


//        hh();
//        ((JavaTest)null).testMethod();

//        hh();
//        testMod();

//        hh();
//        testE();

//        hh();
//        System.out.println(new B().getValue());



//        hh();
////        System.out.println(S.abc);
//        hh();
////        S s=new S();
//        System.out.println(S.bcd);

//        hh();
//        test2();

//        hh();
//        test();

//        hh();
//        numQuest();

//        hh();
//        threeControl();

//        hh();
//        mathVar();
//
//        hh();
//        testExtends();

//        hh();
//        testOut();


//        testFor();

        testByte();

    }

    private static void testByte() {

        Byte a=127;
        Byte b=-128;

        byte c=127;

        Byte d=127;

        hh("a:"+a+"     b:"+b+"     c:"+c);

        ++a;
        --b;

        hh("a:"+a+"     b:"+b+"     c:"+c);


        addByte(a);
        addByte(b);
        addByte(c);
        addByte(d);

        hh("a:"+a+"     b:"+b+"     c:"+c+"     d:"+d);

    }

    private static void addByte(Byte c) {

        Byte e=1;

        c=e;

        ++c;
//        c++;
    }

    static void testFor(){
        for (int i = 0; i < 5153; i++) {
            for (int j = 0; j < 99994; j++) {
                System.out.print("1");
            }
            System.out.print("2");
        }
    }

    static void testOut(){
        System.out.println(false);
        System.out.println("123".equals("123"));
        System.out.println(456==123);
        System.out.println(true);
    }

    static void testExtends(){
        Test mv = new Test();
        Thread t1 = new ThreadExample(mv);
        Thread t2 = new ThreadExample(mv);
        Thread t3 = new ThreadExample(mv);
        t1.start();
        t2.start();
        t3.start();
    }

    static void testFinalAByte(){
        byte b1=1,b2=2,b3=1,b6;
        final byte b4=4,b5=6,b7;
        b6=b4+b5;
//        b3=(b1+b2);
//        b7=b1+b2;
        b7=10;
//        b4=5;
        System.out.println(b3+b6);
    }


    private static void testMethod(){
        System.out.println("null class private static use");
    }


    static void testMod(){


        int a=15;
        int b=6;
        int c=-8;
        int d=-3;

        System.out.println(a+"%"+b+"="+(a%b));
        System.out.println(a+"%"+c+"="+(a%c));
        System.out.println(c+"%"+d+"="+(c%d));
        System.out.println(c+"%"+b+"="+(c%b));

        System.out.println(Math.floorMod(c,b));


    }

    static void testE(){
        try
        {
            int i=100/0;
            System.out.println("error1");
        }
        catch (Exception e)
        {
            System.out.println("error2");
//            int a=100/0;
        }finally {
            System.out.println("error4");
        }
        System.out.println("error3");
    }

    final void abc() {
    }

    final void abc(int a) {
    }

    static void hh() {
        System.out.println();
    }

    static void hh(String s) {
        System.out.println(s);
    }

    static void test() {
        long x, y;
        x = -15 >> 2;

        //源码1000 1111 ,反码 1111 0000 ,补码 1111 0001
        //右移2,补码 1111 1100 ,反码 1111 1011 ，源码 1000 0100 得-4

        y = x >>> 2;

        System.out.println(x);
        System.out.println(y);

        byte b1 = 1, b2 = 2, b3, b6, b8;
        final byte b4 = 4, b5 = 6, b7;
//        b3= (b1+b2);  /*语句1*/
//        b6=b4+b5;    /*语句2*/
//        b8= (b1+b4);  /*语句3*/
//        b7= (b2+b5);  /*语句4*/
//        System.out.println(b3+b6);

        int sdlfk2;
//        int 2lskdfj;
        int lsdfj_$_sdkf;
        int $_skldjf;
        int _lsdjkf;

    }

    static void test2(){
        int a = 10;
        double f = 1.15641;
        System.out.println(a + f);
        System.out.println(-5 + 1 / 4 + 2 * -3 + 5.0);
        String str =
                "";
        System.out.println(str.split(",").length);
    }

    static void mathVar() {
        System.out.println(Math.floor(10.42));//向下取整
        System.out.println(Math.ceil(10.42));//向上取整

//        System.out.println(Integer.parseInt("10.42"));
        System.out.println("10.42".split("\\.")[0]);
    }


    static void threeControl() {

        //三元操作符如果遇到可以转换为数字的类型，会做自动类型提升。

        Object o1 = true ? new Integer(1) : new Double(2.0);
        Object o2;
        if (true) {
            o2 = new Integer(1);
        } else {
            o2 = new Double(2.0);
        }
        System.out.print(o1);
        System.out.print(" ");
        System.out.print(o2);
    }


    public static void numQuest() {

        Integer[] abc = {0, 0, 4, 2, 5, 0, 3, 0};
        List<Integer> nums = new ArrayList<>(Arrays.asList(abc));

        int k = 0;
        Integer zero = new Integer(0);
        while (k < nums.size()) {
            if (nums.get(k).equals(zero))
                nums.remove(k);
            k++;
        }
        System.out.println(nums.toString());
    }

    static class A {
        protected int value;
        public A (int v) {
            setValue(v);
        }
        public void setValue(int value) {
            this.value= value;
        }
        public int getValue() {
            try {
                value ++;
                return value;
            } finally {
                this.setValue(value);
                System.out.println(value);
            }
        }
    }
    static class B extends A {
        public B () {
            super(5);
            setValue(getValue()- 3);
        }
        public void setValue(int value) {
            super.setValue(2 * value);
        }
    }
}

class P {
    public static int abc = 123;
    static{
        System.out.println("P is init");
    }
}
class S extends P {

    static  int bcd=456;

    static{
        System.out.println("S is init");
    }
}

class Test {
    private int data;
    int result = 0;

    public void m() {
        result += 2;
        data += 2;
        System.out.print(result + "  " + data);
    }
}

class ThreadExample extends Thread {
    private Test mv;

    public ThreadExample(Test mv) {
        this.mv = mv;
    }

    public void run() {
        synchronized (mv) {
            mv.m();
        }
    }
}


class Car extends Vehicle {
    public static void main(String[] args) {
        new Car().run();
    }

    private final void run() {
        System.out.println("Car");
    }
}

class Vehicle {
    private final void run() {
        System.out.println("Vehicle");
    }
}


