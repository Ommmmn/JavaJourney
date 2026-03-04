import java.util.*;

public class practiceflowofProgram {
    // public static void main(String[] args) {
    // System.out.println("hello world");
    // }

    // variables
    // sum of three numbers
    /**
     * @param args
     */
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        // int a = sc.nextInt();
        // int b = sc.nextInt();
        // int c = sc.nextInt();

        // System.out.println(a + b + c / 3);

        // }

        // public static void main (String[]args){
        // Scanner sc = new Scanner(System.in);
        // System.out.println("Enter the Value of Side 1");
        // int side1 = sc.nextInt();
        // System.out.println("Enter the value of side 2");
        // int side2 = sc.nextInt();

        // double area = (side1 * side2);
        // System.out.println("area of sqaure is " + area +"sqft");

        // }

        // public static void main(String[] args) {
        // Scanner sc = new Scanner(System.in);

        // System.out.println("ENTER THE VALUE OF PENCIL");
        // float pencil = sc.nextFloat();

        // System.out.println("Enter the value of pen ");
        // float pen = sc.nextFloat();

        // System.out.println("Enter the value of eraser");
        // float eraser = sc.nextFloat();

        // Float total = pencil + pen + eraser;
        // float gst = total * 0.18f;
        // float finalAmount = total + gst;

        // System.out.println("Your total amount of bill is " + finalAmount + " rs");
        // System.out.println("Thankyou for shopping with us");

        // }

        // ---------------------end of variables----------------------------

        // ---------------------start of operators----------------------------

        // int x = 200, y = 50, z = 100;
        // if (x > y && y > z) {
        // System.out.println("Hello");
        // }
        // if (z > y && z < x) {
        // System.out.println("Java");
        // }
        // if ((y + 200) < x && (y + 150) < z) {
        // System.out.println("Hello Java");
        // }

        // In a program, input two numbers A and B.
        // Print:

        // Sum

        // Difference

        // Product

        // Quotient

        // Hint: Use arithmetic operators.
        // int a = sc.nextInt();
        // int b = sc.nextInt();

        // System.out.println(a + b);
        // System.out.println(a - b);
        // System.out.println(a * b);
        // System.out.println(a / b);

        // // Input a number from the user.
        // Print whether the number is even or odd using operators.

        // Hint: % operator.

        // int num = sc.nextInt();

        // if (num % 2== 0){
        // System.out.println("Even");
        // }
        // else{
        // System.out.println("Odd");
        // }

        // Question 3 — Simple Interest

        // Input:

        // Principal (P)

        // Rate (R)

        // Time (T)

        // Calculate Simple Interest.

        // Formula:

        // SI = (P * R * T) / 100
        // System.out.println("Enter the value of Principle : ");
        // int p = sc.nextInt();
        // System.out.println("Enter the value of rate: ");
        // int r = sc.nextInt();
        // System.out.println("Enter the value of time : ");
        // int t = sc.nextInt();

        // int si = (p * r * t) / 100;
        // System.out.println(si);

        // int a = 10;
        // int b = ++a;
        // System.out.println(a + " " + b);

        // int a = 10;
        // int b = a++;
        // System.out.println(a + " " + b);

        // Input two numbers and print:

        // A > B

        // A < B

        // A == B

        // Use relational operators.

        // int a = sc.nextInt();
        // int b = sc.nextInt();

        // System.out.println(a>b);
        // System.out.println(a<b);
        // System.out.println(a==b);
        // System.out.println("Enter your age : ");
        // int age = sc.nextInt();

        // if (age >= 18 && age <= 60) {
        // System.out.println("Eligible");
        // } else {
        // System.out.println("Not Eligible");
        // }

        // System.out.println("Enter the attendance percentage");
        // float att = sc.nextFloat();

        // if (att >=75.00 && att<=99.99){
        // System.out.println("Eligible for exam");

        // }else{
        // System.out.println("not eligible for exam");
        // }

        // System.out.println("Enter the attendance");
        // float att = sc.nextFloat();

        // if (att >= 35 || att <= 100){
        // System.out.println("Eligible for exam");
        // }else{
        // System.out.println("not eligible for");
        // }

        // int x = 5;
        // x += 3;
        // x *= 2;
        // System.out.println(x);

        // Input two numbers and print the larger number using ternary operator.

        // int num1 = sc.nextInt();
        // int num2 = sc.nextInt();

        // System.out.println(num1> num2 ? num1 : num2);

        // Input age and print:

        // "Can Vote" or "Cannot Vote"

        // using ternary operator only.

        // int age = sc.nextInt();

        // System.out.println(age >= 18 ? "can vote ": "cant vote");

        // int a = 5;
        // double b = 2;
        // double result = a / b;
        // System.out.println(result);

        // ---------------------end of operator----------------------------

        // ---------------------start of conditional
        // statement----------------------------

        // positive negative zero
        // System.out.println("Enter the number");

        // int num = sc.nextInt();

        // if (num > 0)
        // System.out.println("positive");
        // else if(num < 0){
        // System.out.println("negative");
        // }
        // else {
        // System.out.println("Zero");

        // }

        // double temp = 103.5;

        // if ( temp > 98.4){
        // System.out.println("fever");

        // }else if ( temp <=98.4){
        // System.out.println("Normal");

        // }
        // System.out.println("Enter the number of week");
        // int numofweek =sc.nextInt();

        // switch (numofweek){
        // case 1:
        // System.out.println("Monday");
        // break;
        // case 2:
        // System.out.println("Tuesday");
        // break;
        // case 3:
        // System.out.println("wednesday");
        // break;
        // case 4:
        // System.out.println("Thursday");
        // break;
        // case 5:
        // System.out.println("friday");
        // break;
        // case 6:
        // System.out.println("saturday");
        // break;
        // case 7:
        // System.out.println("sunday");
        // break;
        // default:
        // System.out.println("Invalid");
        // break;

        // }

        // long year = sc.nextLong();

        // if ((year % 4==0 && year % 100 !=0 ) || (year % 400 == 0)){
        // System.out.println("Leap year");

        // }else{
        // System.out.println("Not a leap year");
        // }

        // int num = sc.nextInt();

        // if (num %2 ==0){
        // System.out.println("even");
        // }else{
        // System.out.println("odd");
        // }

        // System.out.println("Enter the first number");
        // long num1 = sc.nextLong();

        // System.out.println("Enter the second number");
        // long num2 = sc.nextLong();

        // if (num1 > num2){
        // System.out.println("greater number is");
        // System.out.println(num1);
        // }else{
        // System.out.println("greater number is");
        // System.out.println(num2);
        // }

        // System.out.println("Enter the number1");
        // int num1=sc.nextInt();

        // System.out.println("Enter the number2");
        // int num2=sc.nextInt();

        // switch (num1){
        // case 1 : System.out.println(num1+num2);
        // break;

        // case 2 : System.out.println(num1-num2);
        // break;

        // case 3 : System.out.println(num1*num2);
        // break;

        // case 4 : System.out.println(num1/num2);
        // break;
        // }

        // c:\Users\OM\JavaJourney\practiceflowofProgram.java

        // int N = sc.nextInt();

        // for (int i = 1; i <= N; i++) {
        // System.out.println(i + " ");
        // }

        // System.out.println("Enter the n");

        // int number = sc.nextInt();

        // int sum =0;

        // for ( int i =1 ; i <= number ; i++){
        // sum = sum + i;
        // }
        // System.out.println("sum of first " + number + " natural number is ");
        // System.out.println(sum);

        // int num = sc.nextInt();
        // for (int i = 1; i <= 10; i++) {
        //     System.out.println(num + " x " + i + " = " + (num * i));
        // }

        // System.out.println("Enter the number");
        // int num = sc.nextInt();

        // int reverse = 0;

        // while ( num >0){
        //     int digit = num %10;
        //     reverse  = reverse * 10 + digit;
        //     num = num / 10;
            
        // }

        // System.out.println("reverse of the number is " + reverse);


        // System.out.println("Enter the number");
        // int num = sc.nextInt();

        // int count = 0;

        // while (num >0){
        //     num = num/10;
        //     count++;
        // }
        // System.out.println(count);

        int num;

    //     do{
    //         System.out.println("Enter the number");
    //         num = sc.nextInt();
    //     }while ( num != 0);

    //     System.out.println("programm stopped");

    // for ( int i = 1; i<= 20; i++){
    //     if ( i == 20){
    //         break;
    //     }
    //     if (i % 3 == 0){
    //         continue;
    //     }
    //     System.out.println(i);
    // }


//    for(int i=0;i<5;i++)     
//    {System.out.println("Hello");
//    i+=2;}

System.out.println("Enter the numbers");
int num = sc.nextInt();

int evenSum=0;
int oddSum=0;

for (int i = 1; i <= num; i++) {
    if (i % 2 == 0){
        evenSum += i;
    }else{
        oddSum += i;
    }
}
System.out.println("sum of even numbers is " + evenSum);
System.out.println("sum of odd numbers is " + oddSum);




    }



}
