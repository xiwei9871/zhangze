package com.manqiaotechnology.executionosapi.modules.market.enums;

public enum ImportanceLevel {
    LOW(1),
    MEDIUM(2),
    HIGH(3),
    URGENT(4);

    private final int rank;

    ImportanceLevel(int rank) {
        this.rank = rank;
    }

    public int rank() {
        return rank;
    }

    public static ImportanceLevel max(ImportanceLevel left, ImportanceLevel right) {
        if (left == null) {
            return right;
        }
        if (right == null) {
            return left;
        }
        return left.rank >= right.rank ? left : right;
    }
}
