package com.leonp967.sweexpress.desafioJava.model;

import java.util.Arrays;

public enum ProcessorIdEnum {

    SALESMAN_PROCESSOR("001"),
    CUSTOMER_PROCESSOR("002"),
    SALES_PROCESSOR("003");

    String id;

    ProcessorIdEnum(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public static ProcessorIdEnum fromId(String id) {
        return Arrays.stream(values()).filter(processorId -> processorId.getId().equals(id)).findFirst().orElse(null);
    }
}
