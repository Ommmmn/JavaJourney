public class practicepatternsone {
    public static void main(String[] args) {

        // for (int line =1; line<=4; line++) {
        //     for (int star =1; star <= line; star++) {
        //         System.out.print("*");
        //     }
        //     System.out.println();

        // }

        // inverted star pattern

        // for (int i =1; i <= 5; i++){
        //     for (int star = 1; star <= 6 - i; star++) {
        //         System.out.print("*");
        //     } 
        //     System.err.println();
        // }

        // half pyramid with numbers

        // for (int line = 1; line <=4; line++) {
        //     for (int num =1; num <= line; num++) {
        //         System.out.print(num + " ");
        //     }
        //     System.out.println();
        // }

        // print character pattern

        
       char ch = 'A';

for (int num = 1; num <= 4; num++) {
    for (int chars = 1; chars <= num; chars++) {
        System.out.print(ch + " ");
        ch++;
    }
    System.out.println();
}











        
    
}
}
