package inspection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Directory implements ProjectElement {

    private final String name;
    private final List<ProjectElement> children = new ArrayList<>();

    public Directory(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Directory name is required");
        }
        this.name = name;
    }

    public Directory add(ProjectElement child) {
        if (child == null) {
            throw new IllegalArgumentException("Child element must not be null");
        }
        children.add(child);
        return this;
    }

    public String name() {
        return name;
    }

    public List<ProjectElement> children() {
        return Collections.unmodifiableList(children);
    }

    @Override
    public void accept(ProjectVisitor visitor) {
        visitor.visit(this);
    }
}
