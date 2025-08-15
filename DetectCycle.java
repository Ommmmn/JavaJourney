// // singly linked list

// class Node {
//     int data;
//     Node next;
//     public Node(int data) {
//         this.data = data;
//         this.next = null;
//     }

// }


// public class SinglyLinkedList {
//     Node head;

//     public void insert(int data) {
//         Node newNode = new Node(data);
//         if (head == null) {
//             head = newNode;
//         } 
//         Node curr = head;
//         while(curr.next != null) {
//             curr = curr.next;
//         }
//         curr.next = newNode;
//     }

//     public void display() {
//         Node curr = head;
//         while (curr != null) {
//             System.out.print(curr.data + " ");
//             curr = curr.next;
//         }
//     }
            



// public static void main (String[] args){
//     SinglyLinkedList list = new SinglyLinkedList();

//     list.insert(1);
//     list.insert(2);
//     list.insert(3);
//     list.display();
// }
// }


// doubly linked list

// class DNode {
//     int data;
//     DNode next;
//     DNode prev;
//     DNode(int data) {
//         this.data = data;
//     }
// }

// public class DoublyLinkedList {
//     DNode head;

//     public void insert(int data) {
//         DNode newNode = new DNode(data);
//         if (head == null) {
//             head = newNode;
//             return;
//         }
//         DNode curr = head;
//         while (curr.next != null) {
//             curr = curr.next;
//         }
//         curr.next = newNode;
//         newNode.prev = curr;
//     }

//     public void display() {
//         DNode curr = head;
//         while (curr != null) {
//             System.out.print(curr.data + " <-> ");
//             curr = curr.next;
//         }
//         System.out.println("null");
//     }

//     public static void main(String[] args) {
//         DoublyLinkedList list = new DoublyLinkedList();
//         list.insert(10);
//         list.insert(20);
//         list.insert(30);
//         list.display(); // 10 <-> 20 <-> 30 <-> null
//     }
// }


// reverse a linkedlist

// public class ReverseLinkedList {
//     static class Node {
//         int data;
//         Node next;
//         Node(int data) { this.data = data; }
//     }

//     public static Node reverse(Node head) {
//         Node prev = null;
//         Node curr = head;
//         while (curr != null) {
//             Node nextNode = curr.next;
//             curr.next = prev;
//             prev = curr;
//             curr = nextNode;
//         }
//         return prev; // new head
//     }

//     public static void main(String[] args) {
//         Node head = new Node(1);
//         head.next = new Node(2);
//         head.next.next = new Node(3);

//         head = reverse(head);

//         Node curr = head;
//         while (curr != null) {
//             System.out.print(curr.data + " -> ");
//             curr = curr.next;
//         }
//     }
// }

// Detect Cycle in Linked List

public class DetectCycle {
    static class Node {
        int data;
        Node next;
        Node(int data) { this.data = data; }
    }

    public static boolean hasCycle(Node head) {
        Node slow = head;
        Node fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
            if (slow == fast) return true; // cycle detected
        }
        return false;
    }

    public static void main(String[] args) {
        Node head = new Node(1);
        head.next = new Node(2);
        head.next.next = new Node(3);
        head.next.next.next = head; // create cycle
        System.out.println(hasCycle(head)); // true
    }
}




    
