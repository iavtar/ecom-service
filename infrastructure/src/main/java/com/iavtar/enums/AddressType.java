package com.iavtar.enums;

public enum AddressType {

    HOME("HOME"),
    OFFICE("OFFICE"),

    OTHER("OTHER");

    public String value;

    AddressType(String value) {
    }

    public String getValue() {
        return value;
    }

}
