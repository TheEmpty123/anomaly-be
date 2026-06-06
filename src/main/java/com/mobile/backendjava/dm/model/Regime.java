package com.mobile.backendjava.dm.model;

public enum Regime {
    VENTURE,
    FLEXIBLE,
    ENDURING;

    public static Regime fromString(String value) {
        if (value == null) return null;
        String v = value.trim().toUpperCase();
        for (Regime r : Regime.values()) {
            if (r.name().equals(v)) {
                return r;
            }
        }
        return null;
    }
}
