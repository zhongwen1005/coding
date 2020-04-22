package com.interview.coding;

/**
 * @author Zhongwen Zhao
 * @version 1.0
 * @date 4/22/2020 6:03 PM
 */

import com.interview.coding.domain.ExchangeItem;
import com.interview.coding.domain.OpEnum;
import com.interview.coding.domain.TypeEnum;

import java.math.BigDecimal;
import java.util.*;

public class Trader {
    static LinkedHashMap<BigDecimal, List<ExchangeItem>> initialSet = null;
    static {
        initialSet = new LinkedHashMap<>(100);
        List<ExchangeItem> inputList = new ArrayList<>(100);
        BigDecimal key = new BigDecimal(110);
        inputList.add(new ExchangeItem(key, "111222333", OpEnum.New, TypeEnum.Sell, 5));
        key = new BigDecimal(110);
        inputList.add(new ExchangeItem(key, "111222334", OpEnum.New, TypeEnum.Sell, 3));
        initialSet.put(key, inputList);

        key = new BigDecimal(90);
        inputList = new ArrayList<>(100);
        inputList.add(new ExchangeItem(key, "111222335", OpEnum.New, TypeEnum.Sell, 10));
        key = new BigDecimal(90);
        inputList.add(new ExchangeItem(key, "111222335", OpEnum.New, TypeEnum.Sell, 2));
        initialSet.put(key, inputList);

        key = new BigDecimal(85);
        inputList = new ArrayList<>(100);
        inputList.add(new ExchangeItem(key, "111222335", OpEnum.New, TypeEnum.Sell, 6));
        initialSet.put(key, inputList);
    }

    static LinkedHashMap<BigDecimal, List<ExchangeItem>> inputSet = null;
    static {
        inputSet = new LinkedHashMap<>(100);
        BigDecimal key = new BigDecimal(110);
        List<ExchangeItem> inputList = new ArrayList<>(100);
        inputList.add(new ExchangeItem(key, "0", OpEnum.New, TypeEnum.Sell, 4));
        key = new BigDecimal(110);
        inputList.add(new ExchangeItem(key, "0", OpEnum.New, TypeEnum.Sell, 3));
        inputSet.put(key, inputList);

        key = new BigDecimal(108);
        inputList = new ArrayList<>(100);
        inputList.add(new ExchangeItem(key, "0", OpEnum.New, TypeEnum.Sell, 8));
        inputSet.put(key, inputList);

        key = new BigDecimal(90);
        inputList = new ArrayList<>(100);
        //inputList.add(new ExchangeItem(key, "0", OpEnum.New, TypeEnum.Sell, 10));
        key = new BigDecimal(90);
        inputList.add(new ExchangeItem(key, "0", OpEnum.New, TypeEnum.Sell, 2));
        inputSet.put(key, inputList);

        key = new BigDecimal(85);
        inputList = new ArrayList<>(100);
        inputList.add(new ExchangeItem(key, "0", OpEnum.New, TypeEnum.Sell, 6));
        inputSet.put(key, inputList);
    }

    public static void main(String[] args) {

        // Print Initial Set
        System.out.println("Initial Set: ");
        Iterator<Map.Entry<BigDecimal, List<ExchangeItem>>> initialIterator = initialSet.entrySet().iterator();
        while(initialIterator.hasNext()){
            Map.Entry<BigDecimal, List<ExchangeItem>> item = initialIterator.next();
            for(ExchangeItem i : item.getValue()) {
                System.out.println(i.toString());
            }
        }

        // Print Input Set
        System.out.println("Input Set: ");
        Iterator<Map.Entry<BigDecimal, List<ExchangeItem>>> inputIterator = inputSet.entrySet().iterator();
        while(inputIterator.hasNext()){
            Map.Entry<BigDecimal, List<ExchangeItem>> item = inputIterator.next();
            for(ExchangeItem i : item.getValue()) {
                System.out.println(i.toString());
            }
        }

        System.out.println("Result Set: ");

        // init two cursor, act as concurrent movement
        initialIterator = initialSet.entrySet().iterator();
        inputIterator = inputSet.entrySet().iterator();
        // inputSet is based on initialSet, so there should be existing orders
        while(initialIterator.hasNext()){
            Map.Entry<BigDecimal, List<ExchangeItem>> initialSetItem = initialIterator.next();

            // Process inputSet first, prevent the outer loop finished
            // get the first item in InputSet
            // assume match the outstanding orders
            while (inputIterator.hasNext()) {
                Map.Entry<BigDecimal, List<ExchangeItem>> inputSetItem = inputIterator.next();
                if (initialSet.get(inputSetItem.getKey()) == null){
                    // not exist, new order
                    for (ExchangeItem newItem : inputSetItem.getValue()) {
                        newItem.setOrderId(String.format("%s-%03d",
                                newItem.getOp(), new Random().nextInt(1000000)));
                        System.out.println(newItem);
                    }
                }
                break;
            }

            // check current initialSet item
            List<ExchangeItem> inputItemList = inputSet.get(initialSetItem.getKey());
            if (inputItemList == null){
                // not exist, cancelled order
                for (ExchangeItem cancelItem : initialSetItem.getValue()) {
                    cancelItem.setOp(OpEnum.Cancel);
                    System.out.println(cancelItem);
                }
                continue;
            }

            // both cursor point to the same Price
            // Assume inputList won't be empty
            // if not match, means cancelled orders
            ExchangeItem changeItem = null;
            ExchangeItem currentItem = null;
            int k = 0;
            int inputItemSize = inputItemList.size();
            for (; k < initialSetItem.getValue().size(); k++) {
                // get related changed order
                changeItem = null;
                if (k < inputItemSize) {
                    // if cancelled some order in inputSet
                    // the best option is to modify existing ones and cancel remaining orders
                    changeItem = inputItemList.get(k);
                }
                currentItem = initialSetItem.getValue().get(k);
                if (changeItem == null) {
                    // subsequent items in inputList should be cancelled
                    currentItem.setOp(OpEnum.Cancel);
                    System.out.println(currentItem);
                    continue;
                }

                // output changed order
                if (! changeItem.getQuantity().equals(currentItem.getQuantity())) {
                    currentItem.setOp(OpEnum.Modify);
                    currentItem.setQuantity(changeItem.getQuantity());
                    System.out.println(currentItem);
                    continue;
                }

                // skip unchanged order
            }

            for (; k < inputItemList.size(); k++) {
                // process remaining inputSetItem
                ExchangeItem newItem = inputItemList.get(k);
                newItem.setOrderId(String.format("%s-%03d",
                        newItem.getOp(), new Random().nextInt(1000000)));
                System.out.println(newItem);
            }
        }
    }
}
