package inspection;

import java.util.ArrayList;
import java.util.List;

public class SecurityAuditVisitor implements ProjectVisitor {

    private final List<String> issues = new ArrayList<>();
    private int criticalCount = 0;
    private int warningCount = 0;

    @Override
    public void visit(SourceCodeFile file) {
        if (file.hasHardcodedCredentials()) {
            critical("Hardcoded credentials in " + file.name());
        }
        if (file.usesDangerousApi()) {
            warning("High-risk source file: " + file.name());
        }
    }

    @Override
    public void visit(TestFile file) {
    }

    @Override
    public void visit(ConfigurationFile file) {
        if (file.plainTextPassword()) {
            critical("Plain-text password in " + file.name());
        }
        if (file.debugEnabled()) {
            warning("Debug mode enabled in " + file.name());
        }
        if (!file.sslEnabled()) {
            warning("SSL disabled in " + file.name());
        }
    }

    @Override
    public void visit(DependencyFile file) {
        for (Dependency dependency : file.dependencies()) {
            if (dependency.vulnerable()) {
                critical("Vulnerable dependency " + dependency.name() + " in " + file.name());
            }
        }
    }

    @Override
    public void visit(Directory directory) {
        for (ProjectElement child : directory.children()) {
            child.accept(this);
        }
    }

    private void critical(String message) {
        issues.add("[CRITICAL] " + message);
        criticalCount++;
    }

    private void warning(String message) {
        issues.add("[WARNING] " + message);
        warningCount++;
    }

    public List<String> getIssues() {
        return List.copyOf(issues);
    }

    public int getCriticalCount() {
        return criticalCount;
    }

    public int getWarningCount() {
        return warningCount;
    }

    public int getScore() {
        return Math.max(0, 100 - 25 * criticalCount - 10 * warningCount);
    }

    public String getReport() {
        StringBuilder sb = new StringBuilder("SECURITY AUDIT\n");
        for (String issue : issues) {
            sb.append("  ").append(issue).append("\n");
        }
        sb.append("  Critical: ").append(criticalCount)
                .append(", warnings: ").append(warningCount)
                .append(", security score: ").append(getScore()).append("/100\n");
        return sb.toString();
    }
}