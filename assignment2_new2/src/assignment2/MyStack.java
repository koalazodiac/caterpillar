package assignment2;

import java.util.EmptyStackException;

public class MyStack<E> {
	private SNode<E> head;
	public 	int size;


	MyStack(){
		head  = null;
		size  = 0;
	}


	public void push(E element){
		SNode<E> newNode = new SNode<E>(element);
		size++;
		if (head == null){
			head = newNode;
		}
		else{
			newNode.next = head;
			head = newNode;
		}
	}


	public E pop(){
		if (size == 0){
			throw new EmptyStackException();
		}

		SNode<E> cur = head;
		size--;
		head = head.next;
		return cur.getElement();
	}


	public E peek(){
		if (head != null)
			return head.getElement();
		else
			throw new EmptyStackException();
	}


	public boolean empty() {
		return (size == 0);
	}


	public void clear() {
		head  = null;
		size  = 0;
	}

	public String toString() {
		if (this.empty())
			return "[]";
		String stack = "]";
		SNode<E> cur = this.head;
		while (cur != null) {
			stack = ", " + cur.element.toString() + stack;
			cur = cur.next;
		}

		return "[" + stack.substring(2);
	}


	private class SNode<T> {
		private T element;
		private	SNode<T> next;

		SNode(T element){
			this.element = element;
			next = null;
		}

		T getElement(){
			return this.element;
		}

	}

	public int size(){
		return size;
	}

}
