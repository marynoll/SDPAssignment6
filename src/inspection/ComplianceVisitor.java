package inspection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ComplianceVisitor implements ProjectVisitor {

    private final Set<String> prohibited;
    private final List<String> violations = new ArrayList<>();
    private final List<String> sourceNames = new ArrayList<>();
    private final Set<String> testedNames = new HashSet<>();

    public ComplianceVisitor() {
        this(Set.of("log4j", "struts"));
    }

    public ComplianceVisitor(Set<String> prohibited) {
        this.prohibited = Set.copyOf(prohibited);
    }

    @Override
    public void visit(SourceCodeFile file) {
        sourceNames.add(file.name());
        if (file.hasHardcodedCredentials()) {
            violations.add("Exposed credentials in " + file.name());
        }
    }

    @Override
    public void visit(TestFile file) {
        testedNames.add(file.testedFile());
    }

    @Override
    public void visit(ConfigurationFile file) {
        if (file.plainTextPassword()) {
            violations.add("Exposed credentials in " + file.name());
        }
    }

    @Override
    public void visit(DependencyFile file) {
        for (Dependency dependency : file.dependencies()) {
            if (!dependency.hasVersion()) {
                violations.add("Dependency " + dependency.name() + " has no version in " + file.name());
            }
            if (prohibited.contains(dependency.name())) {
                violations.add("Prohibited dependency " + dependency.name() + " in " + file.name());
            }
        }
    }

    @Override
    public void visit(Directory directory) {
        for (ProjectElement child : directory.children()) {
            child.accept(this);
        }
    }

    /** The "has a test" rule can only be checked after everything was visited, so it is done here. */
    public List<String> getViolations() {
        List<String> all = new ArrayList<>(violations);
        for (String sourceName : sourceNames) {
            if (!testedNames.contains(sourceName)) {
                all.add("Production file " + sourceName + " has no test");
            }
        }
        return all;
    }

    public boolean isCompliant() {
        return getViolations().isEmpty();
    }

    public String getReport() {
        List<String> all = getViolations();
        StringBuilder sb = new StringBuilder("COMPLIANCE CHECK\n");
        for (String violation : all) {
            sb.append("  VIOLATION: ").append(violation).append("\n");
        }
        sb.append("  Result: ").append(all.isEmpty() ? "COMPLIANT" : all.size() + " violation(s)").append("\n");
        return sb.toString();
    }
}