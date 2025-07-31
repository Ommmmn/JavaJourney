public class OOPS {

    public static void main(String[] args) {

        Pen pen1 = new Pen();
        pen1.setcolor("Blue");
        System.out.println(pen1.getcolor());  // Correct method call

        pen1.settip(5);
        System.out.println(pen1.gettip());    // Added and called gettip()
    }
}       // BankAccount myAccount = new BankAccount();
        // myAccount.username = "john_doe"; // Accessing public variable
        // myAccount.password = "securePassword"; 

        

  
// ATTRIBUTES AND FUNCTIONS


class Pen {
    private String color;  // private: only accessible inside Pen
    private int tip;

    // Getter for color
    String getcolor() {
        return this.color;
    }

    // Setter for color
    void setcolor(String newcolor) {
        color = newcolor;
    }

    // Getter for tip
    int gettip() {
        return this.tip;
    }

    // Setter for tip
    void settip(int newtip) {
        tip = newtip;
    }
}

// class Student {
//     String name;
//     int age;
//     float percentage;

//     void setName(String newName) {
//         name = newName;
//     }

//     void setAge(int newAge) {
//         age = newAge;
//     }

//     void calPercentage(int phy, int chem, int math) {
//         percentage = (phy + chem + math) / 3.0f;
//     }
// }

// Access Modifiers

// class BankAccount {
//     public String username; // Public access modifier
//     private String password; // Private access modifier
//     public void setPassword(String newPassword) {
//         password = newPassword; // Method to set the private variable
//     }

// }



// getter and setter methods



    


    


