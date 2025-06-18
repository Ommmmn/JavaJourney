import java.util.Scanner;
public class practiceFunctions {

    // public static void  printHelloWorld(){
    //     System.out.println("Hello World");
    //     System.out.println("Hello World");
    // }

    // public static int calculateSum(int num1, int num2) { //parameter or formal paramameters
        
    //     int sum = num1 + num2;
    //     return sum; 

    // }
    // public static void main(String[] args) {
    //     Scanner sc = new Scanner(System.in);
        
    //     int a = sc.nextInt();
    //     int b = sc.nextInt();
    //     int sum = calculateSum(a, b); //arguements
    //     System.out.println("The sum is: " + sum);
    // } 


    // public static void swap(int a , int b) {
        
    //     int temp = a;
    //     a= b;
    //     b = temp;
    //     System.out.println("a =" + a);
    //     System.out.println("b =" + b);
    // }


    // public static void main(String[] args) {
        
    //     int a = 10;
    //     int b = 20;
    //     swap(a, b);

        
    // }
    // product of two numbers

    // public static int multiply (int a, int b) {

    //     int product = a * b;
    //     return product;
    // }

    // public static void main(String[] args) {
        
    //     int a = 5;
    //     int b = 10;
    //     int result =multiply(a,b);

    //     System.out.println("a * b: " + result);

    // }


    // function overloading using parameters


    // public static int sum(int a, int b) {
    //     return a + b;
    // }

    // public static int sum(int a, int b, int c) {
    //     return a + b + c;
    // }

    // public static void main(String[] args) {
    //     System.out.println(sum(3, 5));
    //     System.out.println(sum(3, 5, 7));


    // }

    // using data types

    // public static int sum(int a, int b) {
    //     return a + b;
    // }

    // public static float sum(double a, double b) {
    //     return   (float) (a + b);
    // }

    // public static void main(String[] args) {
    //     System.out.println(sum(3, 5));
    //     System.out.println(sum(3.2f,5.5f));
    // }

    // checking if a number is prime or not

    // public static boolean isPrime(int n) {
    //     for (int i =2; i<=n-1; i++) {
    //         if (n % i == 0){
    //             return false; // not prime
    //         }
    //     }
    //     return true; // is prime
    // }

    // public static boolean isPrime(int n) {
    //     if(n ==2){
    //         return true; 
    //     }
    //     for (int i = 2; i <= Math.sqrt(n); i++) {
    //         if (n%i == 0) {
    //             return false; // not prime
    //         }
    //     }
    //     return true;
    //  } // is prime
    // public static void main(String[] args) {
    //     System.out.println(isPrime(7)); 

    // }

    // prime in a range

    // public static void printPrimesInRange(int n) {
    //     for (int i = 2; i <= n; i++) {
    //         if (isPrime(i)) {
    //             System.out.print(i + " ");
    //         }
    //     }
    //     System.out.println();
    // }

    // private static boolean isPrime(int i) {
    //     // TODO Auto-generated method stub
    //     throw new UnsupportedOperationException("Unimplemented method 'isPrime'");
    // }

    // public static void main(String[] args) {
    //     printPrimesInRange(20);
    // }


    // binary to decimal


    public static void binaryToDecimal(int binary) {
        int MyNum = binary;
        int pow = 0;
        int decimal = 0;
        while (binary > 0) {
            int lastDigit = binary % 10;
            decimal = decimal + (lastDigit * (int)Math.pow(2, pow));
            pow++;
            binary = binary / 10;

        }
        System.out.println("Decimal value: " + MyNum + "=" + decimal);

    }

    public static void main(String[] args) {
        binaryToDecimal(101);
    }








        
        









    
}


