//Node for circular doubly linked list
public class Node<T> implements NodeInterface<T> {
    private T item;
    private Node<T> prev;
    private Node<T> next;

    public Node() {
        this.item = null;
        this.prev = this;
        this.next = this;
    }

    public Node(T obj, Node<T> prev, Node<T> next) {
        this.item = obj;
        this.prev = prev;
        this.next = next;
    }
    
    @Override
    public final T getItem() {
        return item;
    }
    
    @Override
    public final void setItem(T item) {
        this.item = item;
    }
    
    @Override
    public final void setPrev(Node<T> prev) {
        this.prev = prev;
    }

    @Override
    public final void setNext(Node<T> next) {
        this.next = next;
    }

    @Override
    public Node<T> getPrev() {
        return this.prev;
    }
    
    @Override
    public Node<T> getNext() {
        return this.next;
    }
    
    @Override
    public final void insertNext(T obj) {
        Node<T> newNode = new Node<>(obj, this, this.getNext());

        this.getNext().setPrev(newNode);
        this.setNext(newNode);
    }

    @Override
    public final void insertPrev(T obj) {
        Node<T> newNode = new Node<>(obj, this.getPrev(), this);

        this.getPrev().setNext(newNode);
        this.setPrev(newNode);
    }
    
    @Override
    public final void removeNext() {
        Node<T> nnNode = this.getNext().getNext();

        this.setNext(nnNode);
        nnNode.setPrev(this);
    }
}
