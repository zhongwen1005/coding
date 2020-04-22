package com.interview.coding.domain;

/**
 * @author Zhongwen Zhao
 * @version 1.0
 * @date 4/22/2020 5:03 PM
 */

public interface IQueue <E> {
    public int  getSize();
    public boolean isEmpty();
    public void enqueue(E e);
    public E dequeue();
    public E getFront();
    public E getRear();
    public void clear();
}

