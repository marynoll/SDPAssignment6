package inspection;

public record ConfigurationFile(
        String name,
        boolean debugEnabled,
        boolean sslEnabled,
        boolean plainTextPassword) implements ProjectElement {

    public ConfigurationFile {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("File name is required");
        }
    }

    @Override
    public void accept(ProjectVisitor visitor) {
        visitor.visit(this);
    }
}
