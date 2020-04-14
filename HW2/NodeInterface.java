public interface NodeInterface<T> {
    public T getItem();
    
    public void setItem(T item);

    public void setPrev(Node<T> prev);
    
    public void setNext(Node<T> next);

    public Node<T> getPrev();

    public Node<T> getNext();
    
    public void insertNext(T obj);
    
    public void removeNext();
}

/*
    public Node(T obj) {
        this.item = obj;
        this.prev = this;
        this.next = this;
    }
    
    public Node(T obj, Node<T> prev, Node<T> next) {
        this.item = obj;
        this.prev = prev;
        this.next = next;
    }
*/