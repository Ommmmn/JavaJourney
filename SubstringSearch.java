// palindromeCheck



// public class PalindromeCheck {
//     public static boolean isPalindrome(String str) {
//         int left = 0, right = str.length() - 1;
//         while (left < right) {
//             if (str.charAt(left) != str.charAt(right)) {
//                 return false;
//             }
//             left++;
//             right--;
//         }
//         return true;
//     }

//     public static void main(String[] args) {
//         String s = "madam";
//         System.out.println(isPalindrome(s)); // true
//     }
// }


// anagram 

// import java.util.Arrays;

// public class AnagramCheck {
//     public static boolean isAnagram(String s1, String s2){
//         if (s1.length() != s2.length()) return false;
//         char [] arr1 = s1.toCharArray();
//         char [] arr2 = s2.toCharArray();

//         Arrays.sort(arr1);
//         Arrays.sort(arr2);
//         return Arrays.equals(arr1,arr2);


//     }


// public static void main(String[] args) {
//         String s1 = "listen", s2 = "silent";
//         System.out.println(isAnagram(s1, s2)); // true
//     }
// }


// Frequency Count 
// import java.util.HashMap;

// public class FrequencyCount {
//     public static void charFrequency(String str) {
//         HashMap<Character, Integer> freq = new HashMap<>();
//         for (char c : str.toCharArray()) {
//             freq.put(c, freq.getOrDefault(c, 0) + 1);
//         }
//         System.out.println(freq);
//     }

//     public static void main(String[] args) {
//         charFrequency("banana");
//     }
// }


// substring search

public class SubstringSearch {

    public static boolean containsSubstring(String str, String sub) {
        return str.contains(sub);
    }

    public static void main (String[] args){
        String str = "hello world";
        String sub = "world";
        System.out.println(containsSubstring(str, sub)); // 
    }
    
}



