package tka;
import java.util.*;


public class logicalprograms {

    public static void sumEvenodd(){
        int evenSum = 0, oddSum =0;

        for (int i =1 ; i <= 10; i++){
            if( i % 2==0 )
                evenSum += i;
            else
                oddSum += i;
        }
         System.out.println("Even Sum = " + evenSum);
        System.out.println("Odd Sum = " + oddSum);
        
    }

    
}
