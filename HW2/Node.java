public class Node<T> implements NodeInterface<T> {
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
    
    @Override
    public final T getItem() {
    	return item;
    }
    
    @Override
    public final void setItem(T item) {
    	this.item = item;
    }
    
    @Override
    public final void setNext(Node<T> next) {
    	this.next = next;
    }
    
    @Override
    public Node<T> getNext() {
    	return this.next;
    }
    
    @Override
    public final void insertNext(T obj) {
        this.setNext(new Node<>(obj, this.getNext()));
    }
    
    @Override
    public final void removeNext() {
		this.setNext(this.getNext().getNext());
    }
}