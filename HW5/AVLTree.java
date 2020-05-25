import java.util.List;
import java.util.ArrayList;

public class AVLTree<T extends Comparable<T>> implements AVLTreeInterface<T> {
	private AVLTreeNode<T> root;

	public AVLTree() {
		clear();
	}

	@Override
	public void clear() {
		root = null;
	}

	@Override
	public boolean isEmpty() {
		return root == null;
	}

	@Override
	public void insert(T item) {
		root = insertItem(root, item);
	}

	private AVLTreeNode<T> insertItem(AVLTreeNode<T> root, T item) {
		if (root == null) {
			return new AVLTreeNode<>(item);
		} else if (root.getItem().firstItem().equals(item)) {
			root.getItem().add(item); //동일한 item이 존재할 경우 linkedlist에 삽입
		} else if (root.getItem().firstItem().compareTo(item) > 0) {
			root.setLeft(insertItem(root.getLeft(), item));

			//왼쪽에 삽입했으므로 왼쪽이 더 긴 경우만 존재
			if (root.getBalance() > 1) {
				if (root.getLeft().getBalance() < 0)
					root.setLeft(rotateLeft(root.getLeft())); //Double rotation의 경우
				root = rotateRight(root);
			}
		} else {
			root.setRight(insertItem(root.getRight(), item));

			//오른쪽에 삽입했으므로 오른쪽이 더 긴 경우만 존재
			if (root.getBalance() < -1) {
				if (root.getRight().getBalance() > 0)
					root.setRight(rotateRight(root.getRight())); //Double rotation의 경우
				root = rotateLeft(root);
			}
		}

		return root;
	}

	private AVLTreeNode<T> rotateLeft(AVLTreeNode<T> root) {		
		AVLTreeNode<T> newRoot = root.getRight();
		root.setRight(newRoot.getLeft());
		newRoot.setLeft(root);

		return newRoot;
	}

	private AVLTreeNode<T> rotateRight(AVLTreeNode<T> root) {		
		AVLTreeNode<T> newRoot = root.getLeft();
		root.setLeft(newRoot.getRight());
		newRoot.setRight(root);

		return newRoot;
	}

	@Override
	public MyList<T> search(T key) {
		return searchItem(root, key);
	}

	private MyList<T> searchItem(AVLTreeNode<T> root, T key) {
		if (root == null) {
			return new MyLinkedList<>();
		} else {
			if (root.getItem().firstItem().equals(key)) {
				return root.getItem();
			} else if (root.getItem().firstItem().compareTo(key) > 0) {
				return searchItem(root.getLeft(), key);
			} else {
				return searchItem(root.getRight(), key);
			}
		}
	}

	@Override
	public List<T> getAll() {
		return preorder(root);
	}

	private List<T> preorder(AVLTreeNode<T> root) {
		List<T> result = new ArrayList<>();
		
		if (root != null) {
			result.add(root.getItem().firstItem()); //첫번째 item만 있어도 출력할 수 있으므로
			result.addAll(preorder(root.getLeft()));
			result.addAll(preorder(root.getRight()));
		}

		return result;
	}
}


class AVLTreeNode<T> {
	private MyList<T> item;
	private AVLTreeNode<T> lChild;
	private AVLTreeNode<T> rChild;
	private int lHeight;
	private int rHeight;

	public AVLTreeNode() {
		item = new MyLinkedList<>();
		lChild = null;
		rChild = null;
		lHeight = 0;
		rHeight = 0;
	}

	public AVLTreeNode(T firstItem) {
		item = new MyLinkedList<>(firstItem);
		lChild = null;
		rChild = null;
		lHeight = 0;
		rHeight = 0;
	}

	public MyList<T> getItem() {
		return item;
	}

	public void setLeft(AVLTreeNode<T> left) {
		lChild = left;

		if (lChild != null) {
			lHeight = lChild.lHeight > lChild.rHeight ? lChild.lHeight : lChild.rHeight;
			lHeight++;	
		} else {
			lHeight = 0;
		}
	}

	public void setRight(AVLTreeNode<T> right) {
		rChild = right;

		if (rChild != null) {	
			rHeight = rChild.lHeight > rChild.rHeight ? rChild.lHeight : rChild.rHeight;
			rHeight++;
		} else {
			rHeight = 0;
		}
	}

	public AVLTreeNode<T> getLeft() {
		return lChild;
	}

	public AVLTreeNode<T> getRight() {
		return rChild;
	}

	public int getBalance() {
		return lHeight - rHeight;
	}
}