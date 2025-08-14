// import java.util.*;

// public class practicearray {

    
        // crateing an array
        // datatype arrayname[] = new datatype[size];
        // int marks[] = new int[50];


        // int numbers[] = {1, 2, 3}; 

        // String fruits[] = {"apple", "banana", "cherry"};


        // int marks[] = new int[5];

        // Scanner sc = new Scanner(System.in);

        // // int phy;
        // // 

        // System.out.println("length of array: " + marks.length);


        // marks[0] = sc.nextInt();
        // marks[1] = sc.nextInt();
        // marks[2] = sc.nextInt();


        // System.out.println("Marks in Physics: " + marks[0]);
        // System.out.println("Marks in Chemistry " + marks[1]);
        // System.out.println("Marks in Maths " + marks[2]);


        //  int percentage = (marks[0] + marks[1] + marks[2]) / 3;
        //  System.out.println("Percentage: " + percentage + "%");


//         public static void update(int marks[]) {

//             for (int i =0; i < marks.length; i++) {
//                 marks[i] = marks[i] + 1;
//             }
        
//         }

//     public static void main(String[] args) {

//         int marks[] = {97, 98 ,99};
//         update(marks);


//         //print our marks
//         for (int i = 0; i < marks.length; i++) {
//             System.out.println( marks[i] + " ");
//         }

        



        
//     }
    
// }


// Linear Search

// public static int linearsearch(int numbers[], int key) {

//     for (int i = 0; i< numbers.length; i++){
//         if(numbers[i] == key) {
//             return i; 
            
//     }
// }

//         // logic to be implemented
//         return -1;
//     }

//     public static void main(String[] args) {
//         int numbers[] = {2, 4, 6, 8, 10, 12, 14, 16, 18, 20};
        
//         int key = 22;
//         String keyMenu = "Pasta";

//         int index = linearsearch(numbers, key);
//         if (index == -1) {
//             System.out.println("Key not found");
//         } else {
//             System.out.println("Key found at index: " + index);
        
//         }
//     }



//     public static int linearsearch(String menu[], String key) {
//         for (int i = 0; i < menu.length; i++) {
//             if (menu[i].equals(key)) {
//                 return i; // return index as int
//             }
//         }
//         return -1; // if not found
//     }

//     public static void main(String[] args) {
//         String menu[] = {"Pasta", "Pizza", "Burger", "Salad", "Sushi"};
//         String keyMenu = "Pasta";

//         int index = linearsearch(menu, keyMenu);
//         if (index == -1) {
//             System.out.println("Key not found");
//         } else {
//             System.out.println("Key found at index: " + index);
//         }
//     }
// }

// largest numbers in array

// public static int largest(int numbers[]) {
//     int largest = Integer.MIN_VALUE; 
//     int smallest = Integer.MAX_VALUE;
//     for (int i = 0; i < numbers.length; i++) {
//         if (numbers[i] > largest){
//             largest = numbers[i];
//             }

//             if (numbers[i] < smallest) {
            
//             largest = numbers[i];
//             }
//     }
//     System.out.println("Smallest number in the array: " + smallest );
//     return largest; 
// }
    



// public static void main(String[] args) {
    
//     int numbers[] = {5, 10, 15, 8};

//     System.out.println("Largest number in the array: " + largest(numbers));
    
// }
// }


// Binary Search




// }

// Linear Search
// public class LinearSearch {
//     public static int linearSearch(int[] arr, int target) {
//         for (int i = 0; i < arr.length; i++) {
//             if (arr[i] == target) {
//                 return i; // Found, return index
//             }
//         }
//         return -1; // Not found
//     }

//     public static void main(String[] args) {
//         int[] nums = {5, 3, 7, 1, 4};
//         int target = 7;
//         int result = linearSearch(nums, target);
//         System.out.println(result != -1 ? "Found at index " + result : "Not found");
//     }
// }



// 

// Binary Ssearch 

// public class BinarySearch {
//     public static int binarySearch(int[] arr, int target) {
//         int low = 0, high = arr.length - 1;
//         while (low <= high) {
//             int mid = low + (high - low) / 2; // Avoid overflow
//             if (arr[mid] == target) return mid;
//             else if (arr[mid] < target) low = mid + 1;
//             else high = mid - 1;
//         }
//         return -1;
//     }

//     public static void main(String[] args) {
//         int[] nums = {1, 3, 4, 5, 7, 9};
//         int target = 5;
//         int result = binarySearch(nums, target);
//         System.out.println(result != -1 ? "Found at index " + result : "Not found");
//     }
// }

// bubble sort


// public class BubbleSort {

//         public static void bubblesort(int [] arr) {
//                 for (int i = 0; i < arr.length - 1; i++) {
//                         for (int j =0; j < arr.length -i -1; j++){
//                                 if (arr[j] > arr[j +1]) {
//                                         int tempp = arr[j];
//                                 arr[j] = arr[j + 1];
//                                 arr[j + 1] = tempp;
//                                 }
//                         }
//                 }
//         }

//         public static void main(String [] args) {
//                 int [] nums = {5, 3, 8, 4, 2};
//                 bubblesort(nums);
//                 for (int num : nums) {
//                         System.out.print(num + " ");
//                 }
//                 }
        
// }

// Java Built-in Sort – O(n log n)

// import java.util.Arrays;

// public class BuiltInSort {
//         public static void main(String [] args) {
//                 int [] nums = {5, 3, 8, 4, 2};
//                 Arrays.sort(nums);
//                 System.out.println(Arrays.toString(nums));
// }
// }


// Reverse an array in Java
// import java.util.Arrays;

// public class ReverseArray {
//     public static void reverse(int[] arr) {
//         int start = 0, end = arr.length - 1;
//         while (start < end) {
//             int temp = arr[start];
//             arr[start] = arr[end];
//             arr[end] = temp;
//             start++;
//             end--;
//         }
//     }

//     public static void main(String[] args) {
//         int[] nums = {1, 2, 3, 4, 5};
//         reverse(nums);
//         System.out.println(Arrays.toString(nums));
//     }
// }


// rotate an array in Java

// import java.util.Arrays;

// public class RotateArray {
//     public static void rotate(int[] arr, int k) {
//         k = k % arr.length; // Handle k > length
//         reverse(arr, 0, arr.length - 1);
//         reverse(arr, 0, k - 1);
//         reverse(arr, k, arr.length - 1);
//     }

//     private static void reverse(int[] arr, int start, int end) {
//         while (start < end) {
//             int temp = arr[start];
//             arr[start] = arr[end];
//             arr[end] = temp;
//             start++;
//             end--;
//         }
//     }

//     public static void main(String[] args) {
//         int[] nums = {1, 2, 3, 4, 5, 6, 7};
//         rotate(nums, 3);
//         System.out.println(Arrays.toString(nums));
//     }
// }

// Two-pointer problem


public class TwoPointerSum {
    public static boolean hasPairWithSum(int[] arr, int target) {
        int left = 0, right = arr.length - 1;
        while (left < right) {
            int sum = arr[left] + arr[right];
            if (sum == target) return true;
            else if (sum < target) left++;
            else right--;
        }
        return false;
    }

    public static void main(String[] args) {
        int[] nums = {1, 2, 4, 5, 7, 11};
        int target = 9;
        System.out.println(hasPairWithSum(nums, target)); // true
    }
}

