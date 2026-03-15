package ma.farmsense.enums;

public enum QualityGrade {
    A, B, C, D;

    public static QualityGrade fromString(String value) {
        return valueOf(value.toUpperCase());
    }
}
