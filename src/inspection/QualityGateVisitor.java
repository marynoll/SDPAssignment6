package inspection;

public class QualityGateVisitor implements ProjectVisitor {

    public static final double MAX_AVERAGE_COMPLEXITY = 10;
    public static final double MIN_TEST_RATIO = 0.40;

    private final SecurityAuditVisitor security = new SecurityAuditVisitor();
    private final ProjectStatisticsVisitor statistics = new ProjectStatisticsVisitor();

    @Override
    public void visit(SourceCodeFile file) {
        security.visit(file);
        statistics.visit(file);
    }

    @Override
    public void visit(TestFile file) {
        security.visit(file);
        statistics.visit(file);
    }

    @Override
    public void visit(ConfigurationFile file) {
        security.visit(file);
        statistics.visit(file);
    }

    @Override
    public void visit(DependencyFile file) {
        security.visit(file);
        statistics.visit(file);
    }

    @Override
    public void visit(Directory directory) {
        for (ProjectElement child : directory.children()) {
            child.accept(this);
        }
    }

    public boolean securityPassed() {
        return security.getCriticalCount() == 0;
    }

    public boolean complexityPassed() {
        return statistics.getAverageComplexity() <= MAX_AVERAGE_COMPLEXITY;
    }

    public boolean testsPassed() {
        return statistics.getTestSourceRatio() >= MIN_TEST_RATIO;
    }

    public boolean isReleaseReady() {
        return securityPassed() && complexityPassed() && testsPassed();
    }

    public String getReport() {
        return "QUALITY GATE\n"
                + "  Security: " + label(securityPassed()) + "\n"
                + "  Complexity: " + label(complexityPassed()) + "\n"
                + "  Tests: " + label(testsPassed()) + "\n"
                + "  FINAL RESULT: " + (isReleaseReady() ? "PASSED" : "FAILED") + "\n";
    }

    private String label(boolean passed) {
        return passed ? "PASS" : "FAIL";
    }
}