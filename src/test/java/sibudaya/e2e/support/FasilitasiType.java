package sibudaya.e2e.support;

public enum FasilitasiType {
    PENTAS(1, "pentas"),
    HIBAH(2, "hibah");

    private final int jenisId;
    private final String label;

    FasilitasiType(int jenisId, String label) {
        this.jenisId = jenisId;
        this.label = label;
    }

    public int jenisId() {
        return jenisId;
    }

    public String label() {
        return label;
    }

    public static FasilitasiType fromLabel(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Missing fasilitasi type");
        }
        return switch (value.trim().toLowerCase()) {
            case "pentas" -> PENTAS;
            case "hibah" -> HIBAH;
            default -> throw new IllegalArgumentException("Unknown fasilitasi type: " + value);
        };
    }
}
