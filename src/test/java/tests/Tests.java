package tests;

public class Tests {

    public static void main(String[] args) {
        Object a = 10;

        if (a instanceof Integer) {
            System.out.println("Integer");
        } else {
            System.out.println("Not integer");
        }
    }

}
