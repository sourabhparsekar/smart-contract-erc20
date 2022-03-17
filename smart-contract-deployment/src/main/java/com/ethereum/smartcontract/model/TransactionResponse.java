package com.ethereum.smartcontract.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionResponse {

    String message;
    String id;
    Object data;

    public TransactionResponse(String message, String id) {
        this.message = message;
        this.id = id;
    }

    public TransactionResponse(String message, String id, Object data) {
        this.message = message;
        this.id = id;
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public String getId() {
        return id;
    }

    public Object getData() {
        return data;
    }
}
