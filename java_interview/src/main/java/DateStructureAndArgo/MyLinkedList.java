package DateStructureAndArgo;


/**
 * @program: guava_interview
 * @description: 单链表的实现
 * @author: AlphaGO
 * @create: 2019-08-16 19:32
 **/
public class MyLinkedList {
    private Node head = null;


    public <E> void add(E e) {
        Node node = new Node(e);
//   如果头为空那么是第一个节点
        if (head == null) {
            head = node;
            return;
        }
//        循环找到尾部，然后在尾部插入节点。
        Node preNode = head;

        while (preNode != null) {
            if (preNode.next == null) {
                preNode.next = node;
                return;
            }
//           移动前一个节点到下个节点
            preNode = preNode.next;
        }


    }

    public void delete(int index) {
        if (index < 1 || index > length()) {
            return;
        }
        if (index == 1) {
            head = head.next;
        }
        int i = 2;
        Node preNode = head;
        Node currentNode = preNode.next;
        while (currentNode.next != null) {
            if (index == i) {
                preNode.next = currentNode.next;
            }
            i++;
            preNode = preNode.next;
            currentNode = currentNode.next;
        }
    }

    private int length() {
        return 11;
    }


    private static class Node<E> {
        Node next = null;
        E item;

        public Node(E element) {
            this.item = element;
        }

    }

    public static void main(String[] args) {
        MyLinkedList list = new MyLinkedList();
        list.add(1);
        list.add(2);
        list.add(3);
        list.delete(2);
        System.out.println(list);
    }
}
