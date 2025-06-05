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

        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();

        int sum= 0;
        int i = 1;

        while(i <= n) {
            
            sum += i; 
            i++;
        }
        System.out.println("Sum of number " + sum );









        











        
    }
    
}
