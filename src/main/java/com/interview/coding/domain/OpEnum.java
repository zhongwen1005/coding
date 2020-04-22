package com.interview.coding.domain;

/**
 * @author Zhongwen Zhao
 * @version 1.0
 * @date 4/22/2020 5:25 PM
 */

public enum OpEnum {
    New(0, "A"),
    Cancel(1, "C"),
    Modify(2, "M");

    OpEnum(Integer op, String desc) {
        this.op = op;
        this.desc = desc;
    }
    Integer op;
    String desc;

    Integer getOp() {
        return op;
    }
    String getDesc() {
        return desc;
    }
}
