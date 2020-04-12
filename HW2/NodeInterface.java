public interface NodeInterface<T> {
    public T getItem();
    
    public void setItem(T item);
    
    public void setNext(Node<T> next);

    public Node<T> getNext();
    
    public void insertNext(T obj);
    
    public void removeNext();
}

/*
    private T item;
    private Node<T> next;

    public Node(T obj) {
        this.item = obj;
        this.next = null;
    }
    
    public Node(T obj, Node<T> next) {
        this.item = obj;
        this.next = next;
    }
*/