package inspection;

public record SourceCodeFile(
        String name,
        int linesOfCode,
        int complexity,
        boolean hasHardcodedCredentials,
        boolean usesDangerousApi) implements ProjectElement {

    public SourceCodeFile {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("File name is required");
        }
        if (linesOfCode < 0 || complexity < 0) {
            throw new IllegalArgumentException("Lines and complexity must not be negative: " + name);
        }
    }

    @Override
    public void accept(ProjectVisitor visitor) {
        visitor.visit(this);
    }
}