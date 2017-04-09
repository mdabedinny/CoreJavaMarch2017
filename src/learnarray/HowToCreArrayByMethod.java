package learnarray;

import java.util.Scanner;

/**
 * Created by mdislam on 4/9/17.
 */
public class HowToCreArrayByMethod {

    public int[] setArray(int limit){
        int [] numArray = new int[limit];
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter value");
        for (int i=0; i<numArray.length; i++){
            numArray[i] =sc.nextInt();
        }

       return numArray;
    }

    public void printArray(){

    }
}
