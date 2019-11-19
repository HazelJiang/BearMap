package bearmaps.utils.pq;

import java.util.ArrayList;
import java.util.NoSuchElementException;

/* A MinHeap class of Comparable elements backed by an ArrayList. */
public class MinHeap<E extends Comparable<E>> {

    /* An ArrayList that stores the elements in this MinHeap. */
    private ArrayList<E> contents;
    private int size = 0;

    /* Initializes an empty MinHeap. */
    public MinHeap() {
        contents = new ArrayList<>();
        contents.add(null);
    }

    /* Returns the element at index INDEX, and null if it is out of bounds. */
    private E getElement(int index) {
        if (index >= contents.size()) {
            return null;
        } else {
            return contents.get(index);
        }
    }

    private boolean isChild(int index) {
        if (index >= (size / 2) && index <= size) {
            return true;
        }
        return false;
    }

    /* Sets the element at index INDEX to ELEMENT. If the ArrayList is not big
       enough, add elements until it is the right size. */
    private void setElement(int index, E element) {
        while (index >= contents.size()) {
            contents.add(null);
        }
        contents.set(index, element);
    }

    /* Swaps the elements at the two indices. */
    private void swap(int index1, int index2) {
        E element1 = getElement(index1);
        E element2 = getElement(index2);
        setElement(index2, element1);
        setElement(index1, element2);
    }

    /* Prints out the underlying heap sideways. Use for debugging. */
    @Override
    public String toString() {
        return toStringHelper(1, "");
    }

    /* Recursive helper method for toString. */
    private String toStringHelper(int index, String soFar) {
        if (getElement(index) == null) {
            return "";
        } else {
            String toReturn = "";
            int rightChild = getRightOf(index);
            toReturn += toStringHelper(rightChild, "        " + soFar);
            if (getElement(rightChild) != null) {
                toReturn += soFar + "    /";
            }
            toReturn += "\n" + soFar + getElement(index) + "\n";
            int leftChild = getLeftOf(index);
            if (getElement(leftChild) != null) {
                toReturn += soFar + "    \\";
            }
            toReturn += toStringHelper(leftChild, "        " + soFar);
            return toReturn;
        }
    }

    /* Returns the index of the left child of the element at index INDEX. */
    private int getLeftOf(int index) {
        return 2 * index;
    }

    /* Returns the index of the right child of the element at index INDEX. */
    private int getRightOf(int index) {
        return index * 2 + 1;
    }

    /* Returns the index of the parent of the element at index INDEX. */
    private int getParentOf(int index) {
        return index / 2;
    }

    /* Returns the index of the smaller element. At least one index has a
       non-null element. If the elements are equal, return either index. */
    private int min(int index1, int index2) {
        E itemFirst = getElement(index1);
        E itemSecond = getElement(index2);
        if (itemFirst != null && itemSecond != null) {
            int compareResult = itemFirst.compareTo(itemSecond);
            if (compareResult == 0) {
                return index1;
            }
            if (compareResult < 0) {
                return index1;
            } else {
                return index2;
            }
        } else if (itemFirst == null && itemSecond != null) {
            return index2;
        } else {
            return index1;
        }
    }

    /* Returns but does not remove the smallest element in the MinHeap. */
    public E findMin() {
        return getElement(1);
    }

    /* Bubbles up the element currently at index INDEX. */
    private void bubbleUp(int index) {
        while (index > 1) {
            int parentNodeIndex = getParentOf(index);
            if (contents.get(index).compareTo(contents.get(parentNodeIndex)) < 0) {
                swap(index, parentNodeIndex);
            } else {
                break;
            }
            index = parentNodeIndex;
        }
    }


    /* Bubbles down the element currently at index INDEX. */
    private void bubbleDown(int index) {
        if (index >= contents.size() - 1) {
            return;
        }
        int lowerNodeIndexLeft = getLeftOf(index);
        int lowerNodeIndexRight = getRightOf(index);
        int minIndex = index;
        if (lowerNodeIndexLeft <= contents.size() - 1) {
            if (getElement(lowerNodeIndexLeft).compareTo(getElement(minIndex)) < 0) {
                minIndex = lowerNodeIndexLeft;
            }
        }
        if (lowerNodeIndexRight <= contents.size() - 1) {
            if (getElement(lowerNodeIndexRight).compareTo(getElement(minIndex)) < 0) {
                minIndex = lowerNodeIndexRight;
            }
        }
        if (minIndex != index) {
            swap(index, minIndex);
            bubbleDown(minIndex);
        }

    }

    /* Returns the number of elements in the MinHeap. */
    public int size() {
        size = 0;
        for (E elem : contents) {
            if (elem != null) {
                size++;
            }
        }
        return size;
    }

    /* Inserts ELEMENT into the MinHeap. If ELEMENT is already in the MinHeap,
       throw an IllegalArgumentException.*/
    public void insert(E element) {
        if (contains(element)) {
            throw new IllegalArgumentException();
        }
        contents.add(element);
        size++;
        int index = contents.size() - 1;
        bubbleUp(index);
    }

    /* Returns and removes the smallest element in the MinHeap. */
    public E removeMin() {
        if (getElement(1) == null || contents.equals(null)) {
            return null;
        }
        E root = contents.get(1);
        swap(1, contents.size() - 1);
        contents.remove(contents.size() - 1);
        size--;
        bubbleDown(1);
        return root;
    }

    /* Replaces and updates the position of ELEMENT inside the MinHeap, which
       may have been mutated since the initial insert. If a copy of ELEMENT does
       not exist in the MinHeap, throw a NoSuchElementException. Item equality
       should be checked using .equals(), not ==. */
    public void update(E element) {
        if (contents.contains(element)) {
            int index = contents.indexOf(element);
            swap(index, contents.size() - 1);
            contents.remove(contents.size() - 1);
            size--;
            bubbleDown(index);
            insert(element);
        } else {
            throw new NoSuchElementException();
        }

    }

    /* Returns true if ELEMENT is contained in the MinHeap. Item equality should
       be checked using .equals(), not ==. */
    public boolean contains(E element) {
        if (contents.contains(element)) {
            return true;
        }
        return false;
    }
}
