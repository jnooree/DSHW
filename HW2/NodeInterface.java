public interface NodeInterface<T> {
    public T getItem();
    
    public void setItem(T item);

    public void setPrev(Node<T> prev);
    
    public void setNext(Node<T> next);

    public Node<T> getPrev();

    public Node<T> getNext();
    
    public void insertNext(T obj);

    public void insertPrev(T obj);
    
    public void remove();
}