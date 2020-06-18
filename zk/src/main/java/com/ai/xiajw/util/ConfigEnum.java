package com.ai.xiajw.util;

public enum ConfigEnum {
    CONNECT_STRING("connect-string"),
    LATCHER_PATH("latcher-path");

    private String key;

    ConfigEnum(String key) {
        this.key = key;
    }

    public String toString(){
        return key;
    }
}
