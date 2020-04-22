package com.interview.coding.util;

/**
 * @author Zhongwen Zhao
 * @version 1.0
 * @date 4/22/2020 4:58 PM
 */

import com.interview.coding.domain.ICall;

/**
 * B Plus Tree
 *
 * @param <T> Value
 * @param <V> Key, Has to be comparable
 */
public class BPlusTree <T, V extends Comparable<V>>{
    /**
     * B+ order
     */

    private Integer bTreeOrder;
    /**
     * B+ non-root Maximum number
     */
    private Integer maxNumber;

    private Node<T, V> root;

    private LeafNode<T, V> left;

    public BPlusTree(){
        this(3);
    }

    public BPlusTree(Integer bTreeOrder){
        this.bTreeOrder = bTreeOrder;
        this.maxNumber = bTreeOrder + 1;
        this.root = new LeafNode<T, V>();
        this.left = null;
    }

    /**
     * Query
      */
    public T find(V key){
        T t = this.root.find(key);
        if(t == null){
            System.out.println("Key not exists!");
        }
        return t;
    }

    /**
     * Insert
     */
    public void insert(T value, V key){
        if (key == null) {
            return;
        }
        Node<T, V> t = this.root.insert(value, key);
        if (t != null) {
            this.root = t;
        }
        this.left = (LeafNode<T, V>)this.root.refreshLeft();
    }

    public LeafNode<T, V> print(ICall call) {
        if (left == null) {
            return null;
        }

        LeafNode<T, V> cursor = new LeafNode<>();
        cursor = left;

        while (true){
            for (Object item : cursor.values) {
                if (item != null) {
                    call.call((T) item);
                }
            }
            if (cursor.right == null) {
                break;
            }
            cursor = cursor.right;
        }
        return null;
    }

    abstract class Node<T, V extends Comparable<V>>{
        protected Node<T, V> parent;
        protected Node<T, V>[] childs;
        // leaf node number
        protected Integer number;
        protected Object keys[];

        public Node(){
            this.keys = new Object[maxNumber];
            this.childs = new Node[maxNumber];
            this.number = 0;
            this.parent = null;
        }

        abstract T find(V key);

        abstract Node<T, V> insert(T value, V key);

        abstract LeafNode<T, V> refreshLeft();
    }


    /**
     * Non-root node
     * @param <T>
     * @param <V>
     */

    class BPlusNode <T, V extends Comparable<V>> extends Node<T, V>{

        public BPlusNode() {
            super();
        }

        /**
         * recursive lookup
         * @param key
         * @return
         */
        @Override
        T find(V key) {
            int i = 0;
            while(i < this.number){
                if (key.compareTo((V) this.keys[i]) <= 0) {
                    break;
                }
                i++;
            }
            if (this.number == i) {
                return null;
            }
            return this.childs[i].find(key);
        }

        /**
         * recursive insert
         * @param value
         * @param key
         */
        @Override
        Node<T, V> insert(T value, V key) {
            int i = 0;
            while(i < this.number){
                if(key.compareTo((V) this.keys[i]) < 0)
                    break;
                i++;
            }
            if(key.compareTo((V) this.keys[this.number - 1]) >= 0) {
                i--;
            }
            return this.childs[i].insert(value, key);
        }

        @Override
        LeafNode<T, V> refreshLeft() {
            return this.childs[0].refreshLeft();
        }

        /**
         * Re-balance Parent
         * @param node1
         * @param node2
         * @param key
         */
        Node<T, V> insertNode(Node<T, V> node1, Node<T, V> node2, V key){

            V oldKey = null;
            if (this.number > 0) {
                oldKey = (V) this.keys[this.number - 1];
            }

            if(key == null || this.number <= 0){
                this.keys[0] = node1.keys[node1.number - 1];
                this.keys[1] = node2.keys[node2.number - 1];
                this.childs[0] = node1;
                this.childs[1] = node2;
                this.number += 2;
                return this;
            }
            int i = 0;
            while(key.compareTo((V)this.keys[i]) != 0){
                i++;
            }
            this.keys[i] = node1.keys[node1.number - 1];
            this.childs[i] = node1;

            Object tempKeys[] = new Object[maxNumber];
            Object tempChilds[] = new Node[maxNumber];

            System.arraycopy(this.keys, 0, tempKeys, 0, i + 1);
            System.arraycopy(this.childs, 0, tempChilds, 0, i + 1);
            System.arraycopy(this.keys, i + 1, tempKeys, i + 2, this.number - i - 1);
            System.arraycopy(this.childs, i + 1, tempChilds, i + 2, this.number - i - 1);
            tempKeys[i + 1] = node2.keys[node2.number - 1];
            tempChilds[i + 1] = node2;

            this.number++;

            if(this.number <= bTreeOrder){
                System.arraycopy(tempKeys, 0, this.keys, 0, this.number);
                System.arraycopy(tempChilds, 0, this.childs, 0, this.number);

                return null;
            }

            Integer middle = this.number / 2;

            BPlusNode<T, V> tempNode = new BPlusNode<T, V>();
            tempNode.number = this.number - middle;
            tempNode.parent = this.parent;
            if (this.parent == null) {
                BPlusNode<T, V> tempBPlusNode = new BPlusNode<>();
                tempNode.parent = tempBPlusNode;
                this.parent = tempBPlusNode;
                oldKey = null;
            }
            System.arraycopy(tempKeys, middle, tempNode.keys, 0, tempNode.number);
            System.arraycopy(tempChilds, middle, tempNode.childs, 0, tempNode.number);
            for(int j = 0; j < tempNode.number; j++){
                tempNode.childs[j].parent = tempNode;
            }

            this.number = middle;
            this.keys = new Object[maxNumber];
            this.childs = new Node[maxNumber];
            System.arraycopy(tempKeys, 0, this.keys, 0, middle);
            System.arraycopy(tempChilds, 0, this.childs, 0, middle);

            BPlusNode<T, V> parentNode = (BPlusNode<T, V>)this.parent;
            return parentNode.insertNode(this, tempNode, oldKey);
        }

    }

    /**
     * Leaf Node
     * @param <T>
     * @param <V>
     */
    class LeafNode <T, V extends Comparable<V>> extends Node<T, V> {

        protected Object values[];
        protected LeafNode left;
        protected LeafNode right;

        public LeafNode(){
            super();
            this.values = new Object[maxNumber];
            this.left = null;
            this.right = null;
        }

        @Override
        T find(V key) {
            if (this.number <=0) {
                return null;
            }

            Integer left = 0;
            Integer right = this.number;

            Integer middle = (left + right) / 2;

            while (left < right){
                V middleKey = (V) this.keys[middle];
                if (key.compareTo(middleKey) == 0) {
                    return (T) this.values[middle];
                } else if (key.compareTo(middleKey) < 0) {
                    right = middle;
                } else {
                    left = middle;
                }
                middle = (left + right) / 2;
            }
            return null;
        }

        /**
         * Insert
         * @param value
         * @param key
         */
        @Override
        Node<T, V> insert(T value, V key) {
            V oldKey = null;
            if (this.number > 0) {
                oldKey = (V) this.keys[this.number - 1];
            }
            int i = 0;
            while (i < this.number){
                if (key.compareTo((V) this.keys[i]) < 0) {
                    break;
                }
                i++;
            }

            Object tempKeys[] = new Object[maxNumber];
            Object tempValues[] = new Object[maxNumber];
            System.arraycopy(this.keys, 0, tempKeys, 0, i);
            System.arraycopy(this.values, 0, tempValues, 0, i);
            System.arraycopy(this.keys, i, tempKeys, i + 1, this.number - i);
            System.arraycopy(this.values, i, tempValues, i + 1, this.number - i);
            tempKeys[i] = key;
            tempValues[i] = value;

            this.number++;

            if(this.number <= bTreeOrder){
                System.arraycopy(tempKeys, 0, this.keys, 0, this.number);
                System.arraycopy(tempValues, 0, this.values, 0, this.number);

                Node node = this;
                while (node.parent != null){
                    V tempkey = (V)node.keys[node.number - 1];
                    if(tempkey.compareTo((V)node.parent.keys[node.parent.number - 1]) > 0){
                        node.parent.keys[node.parent.number - 1] = tempkey;
                        node = node.parent;
                    }
                    else {
                        break;
                    }
                }

                return null;
            }

            Integer middle = this.number / 2;

            LeafNode<T, V> tempNode = new LeafNode<T, V>();
            tempNode.number = this.number - middle;
            tempNode.parent = this.parent;
            if (this.parent == null) {
                BPlusNode<T, V> tempBPlusNode = new BPlusNode<>();
                tempNode.parent = tempBPlusNode;
                this.parent = tempBPlusNode;
                oldKey = null;
            }
            System.arraycopy(tempKeys, middle, tempNode.keys, 0, tempNode.number);
            System.arraycopy(tempValues, middle, tempNode.values, 0, tempNode.number);

            this.number = middle;
            this.keys = new Object[maxNumber];
            this.values = new Object[maxNumber];
            System.arraycopy(tempKeys, 0, this.keys, 0, middle);
            System.arraycopy(tempValues, 0, this.values, 0, middle);

            this.right = tempNode;
            tempNode.left = this;

            BPlusNode<T, V> parentNode = (BPlusNode<T, V>)this.parent;
            return parentNode.insertNode(this, tempNode, oldKey);
        }

        @Override
        LeafNode<T, V> refreshLeft() {
            if (this.number <= 0) {
                return null;
            }
            return this;
        }
    }
}



