package inspection;

import java.util.List;

public record DependencyFile(String name, List<Dependency> dependencies) implements ProjectElement {

    public DependencyFile {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("File name is required");
        }
        if (dependencies == null) {
            throw new IllegalArgumentException("Dependency list is required: " + name);
        }
        dependencies = List.copyOf(dependencies);
    }

    @Override
    public void accept(ProjectVisitor visitor) {
        visitor.visit(this);
    }
}