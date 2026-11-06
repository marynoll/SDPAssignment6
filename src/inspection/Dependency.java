package inspection;

public record Dependency(String name, String version, boolean vulnerable) {

    public Dependency {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Dependency name is required");
        }
    }

    public boolean hasVersion() {
        return version != null && !version.isBlank();
    }
}