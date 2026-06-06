package com.mobile.backendjava.dm.model;

/**
 * Supported timeframes for market structure and sector performance queries.
 */
public enum Timeframe {
    ONE_M("1M"),
    THREE_M("3M"),
    SIX_M("6M"),
    ONE_Y("1Y");

    private final String code;

    Timeframe(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    /**
     * Case-insensitive parser accepting 1M, 3M, 6M, 1Y.
     * Returns null if the value is not recognized.
     */
    public static Timeframe fromString(String value) {
        if (value == null) return null;
        String v = value.trim().toUpperCase();
        for (Timeframe tf : Timeframe.values()) {
            if (tf.code.equals(v)) {
                return tf;
            }
        }
        return null;
    }
}
