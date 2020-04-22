package com.interview.coding;

/**
 * @author Zhongwen Zhao
 * @version 1.0
 * @date 4/22/2020 4:53 PM
 */

import com.interview.coding.domain.ExchangeItem;
import com.interview.coding.domain.ICall;
import com.interview.coding.domain.OpEnum;
import com.interview.coding.domain.TypeEnum;
import com.interview.coding.util.BPlusTree;

import java.math.BigDecimal;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

public class ExchangeCenter implements ICall {

    public static void main(String[] args) throws InterruptedException {

        // not consider multi-thread env
        BPlusTree<ArrayBlockingQueue<ExchangeItem>, BigDecimal> b = new BPlusTree<>(4);

        // for test only
        int removeIndex = 3;
        ExchangeItem removeItem = null;

        for (int i = 0; i < 10; i++) {
            String orderId = String.format("S-%03d", i);
            ExchangeItem p = new ExchangeItem(new BigDecimal(i), orderId,
                    OpEnum.New, TypeEnum.Sell, new Random().nextInt(100));
            ArrayBlockingQueue<ExchangeItem> node = new ArrayBlockingQueue<>(100);
            node.put(p);
            b.insert(node, p.getPrice());
            if (i == removeIndex) {
                // for test only
                removeItem = new ExchangeItem(p);
            }
        }
        System.out.println("Initial Set: ");
        b.print(new ExchangeCenter());

        System.out.println("Cancel one SellItem: ");
        ArrayBlockingQueue<ExchangeItem> cancelNode = b.find(new BigDecimal(removeIndex));
        /**
         * Rewrite hash() & equals()
         * Considering on one exact price, there won't be substantial ASK & BID
         * Considering no Modification on the ASK & BID
         * if that's the case, should use treeMap instead
         * If no item found, means the order had been processed
         */
        cancelNode.remove(removeItem);
        removeItem.setOp(OpEnum.Cancel);
        System.out.println(removeItem.toString());

        System.out.println("Buyers Set: ");
        for (int k = 0; k < 10; k++) {
            // find price
            ArrayBlockingQueue<ExchangeItem> node = null;

            ExchangeItem head = null;
            // To Buy
            Integer toDealQuantity = new Random().nextInt(100);
            String orderId = String.format("B-%03d", k);
            ExchangeItem p = new ExchangeItem(new BigDecimal(k), orderId,
                    OpEnum.New, TypeEnum.Buy, toDealQuantity);
            System.out.println(p.toString());

            node = b.find(new BigDecimal(k));
            if ((node == null) || ((head = node.peek()) == null)) {
                node = new ArrayBlockingQueue<>(100);
                node.put(p);
                b.insert(node, p.getPrice());
                continue;
            }

            // match, subtract, if not enough, add
            // no matter current item is 'S' or 'B'
            int leftQuantity = toDealQuantity;
            while (leftQuantity > 0) {
                int currentQuantity = head.getQuantity();
                // if match exactly
                if (leftQuantity == currentQuantity) {
                    // finished
                    node.poll();
                    break;
                }

                if (leftQuantity < currentQuantity) {
                    // subtract from current item
                    currentQuantity -= toDealQuantity;
                    head.setQuantity(currentQuantity);
                    // current item remains
                    break;
                }

                leftQuantity = toDealQuantity - currentQuantity;
                node.poll();

                head = node.peek();
                if (head == null) {
                    // no more, add left quantity to the head of FIFO queue
                    orderId = String.format("B-%03d", k);
                    p = new ExchangeItem(new BigDecimal(k), orderId,
                            OpEnum.New, TypeEnum.Buy, leftQuantity);
                    node.put(p);
                    break;
                }
            }

            // TODO - If aggressive cases, need to fetch subsequent nodes
            //        and support range search

            // TODO
            if (node.isEmpty()) {
                // remove node
            }
        }

        System.out.println("After Merge: ");
        b.print(new ExchangeCenter());

        // output
        long time2 = System.nanoTime();
        ArrayBlockingQueue<ExchangeItem> p1 = b.find(new BigDecimal(2));
        long time3 = System.nanoTime();
        System.out.println("Query time: " + (time3 - time2));
        if (p1 != null && !p1.isEmpty()) {
            System.out.println(p1.peek().toString());
        }
    }

    @Override
    public void call(Object o) {
        ArrayBlockingQueue<ExchangeItem> node = (ArrayBlockingQueue<ExchangeItem>)o;
        ExchangeItem item = node.peek();
        if (item != null) {
            System.out.println(node.peek().toString());
        }
    }
}
