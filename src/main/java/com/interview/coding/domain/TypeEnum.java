package com.interview.coding.domain;

/**
 * @author Zhongwen Zhao
 * @version 1.0
 * @date 4/22/2020 5:33 PM
 */
public enum TypeEnum {
    Sell(0, "S"),
    Buy(1, "B");

    TypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }
    Integer type;
    String desc;

    Integer getType() {
        return type;
    }
    String getDesc() {
        return desc;
    }
}
