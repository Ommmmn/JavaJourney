import java.util.Scanner;

public class practiceloops {
    public static void main(String[] args) {
        // int counter = 0;
        // while (counter < 100) {
        //     System.out.println("Hello World");
        //     counter++;

            
        // }
        // System.out.println("Printed Hello world 100 times");

        // printing num 1 to 10

        // int counter = 1;
        // while (counter <= 10) {
        //     System.out.println(counter);
        //     counter ++;
        // }

        // printing even numbers from 1 to n

        // Scanner sc = new Scanner(System.in);
        // int n = sc.nextInt();

        // int counter = 1;

        // while (counter <= n) {
        //     System.out.println(counter + " ");
        //     counter++;
        // }


        //  sum of first n natural numbers

        // Scanner sc = new Scanner(System.in);
        // int n = sc.nextInt();

        // int sum= 0;
        // int i = 1;

        // while(i <= n) {
            
        //     sum += i; 
        //     i++;
        // }
        // System.out.println("Sum of number " + sum );


        // for loop

        // 
        
        // print square pattern



        // for (int line = 1;line <= 4; line++) {
        //     System.err.println("****");
        // } 

        //  print reverse number from n to 1
        
        // Scanner sc = new Scanner(System.in);

        // int n = sc.nextInt();
        // for (int i = n; i >= 1; i--) {
        //     System.out.println(i);
        // }

        //  print reverse number
        // int n = 10899;

        // while(n >0) {
        //     int lastdigit = n % 10;
        //     System.out.println(lastdigit + " ");
        //     n = n / 10;

        // }
        // System.out.println("Reverse number printed successfully");


        //  dp while loop

        // int counter = 1;
        // do{
        //     System.out.println("Hello World");
        //     counter++;
        // }while (counter <= 10);

        // break statement 

        // for (int i = 1; i <= 10; i++) {
        //     if (i == 5) {
        //         break;

        //     }
        //     System.out.println(i);
        
        // }

        // beak example with while loop

        // Scanner sc = new Scanner(System.in);

        // do{
        //     int n = sc.nextInt();
        //     if (n% 10 ==0){
        //         break;
        //     }
        //     System.out.println(n);
        // }while (true);
        // System.out.println("Exited the loop because last digit was 0");

        // continue statement

        // for (int i=1; i<=5; i++) {
        //     if (i == 3) {
        //         continue; // skips the rest of the loop when i is 3
        //     }
        //     System.out.println(i); // prints 1, 2, 4, 5
        // } 

        // continue  ( dont print multiples of 10) example with while loop
        // Scanner sc = new Scanner(System.in);
        
        // do{
        //     int n = sc.nextInt();
        //     if (n % 10 == 0) {
        //         continue; // skips the rest of the loop when n is a multiple of 10
        //     }
        //     System.out.println("number was :" + n);
        // }while (true);

        // check wheter prime or not

        Scanner sc = new Scanner(System.in);
        System.out.println("Enter a number to check if it is prime or not:");

        int n = sc.nextInt();
        boolean isPrime = true;
        for (int i = 2; i <= Math.sqrt(n); i++) {
            if (n % i == 0) {
                isPrime = false; // n is divisible by i, so it's not prime
                break; // no need to check further
            }
        }



















    


















        











        
    }
    
}
