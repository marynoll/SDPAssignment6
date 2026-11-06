package inspection;

public record TestFile(
        String name,
        int linesOfCode,
        int testCount,
        String testedFile) implements ProjectElement {

    public TestFile {
        if (name == null || name.isBlank() || testedFile == null || testedFile.isBlank()) {
            throw new IllegalArgumentException("Test file name and tested file are required");
        }
        if (linesOfCode < 0 || testCount < 0) {
            throw new IllegalArgumentException("Lines and test count must not be negative: " + name);
        }
    }

    @Override
    public void accept(ProjectVisitor visitor) {
        visitor.visit(this);
    }
}