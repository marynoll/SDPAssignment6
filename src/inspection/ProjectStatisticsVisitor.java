package inspection;

public class ProjectStatisticsVisitor implements ProjectVisitor {

    private int totalFiles = 0;
    private int sourceFiles = 0;
    private int testFiles = 0;
    private int sourceLines = 0;
    private int testLines = 0;
    private int testCount = 0;
    private int dependencyCount = 0;
    private int complexitySum = 0;

    @Override
    public void visit(SourceCodeFile file) {
        totalFiles++;
        sourceFiles++;
        sourceLines += file.linesOfCode();
        complexitySum += file.complexity();
    }

    @Override
    public void visit(TestFile file) {
        totalFiles++;
        testFiles++;
        testLines += file.linesOfCode();
        testCount += file.testCount();
    }

    @Override
    public void visit(ConfigurationFile file) {
        totalFiles++;
    }

    @Override
    public void visit(DependencyFile file) {
        totalFiles++;
        dependencyCount += file.dependencies().size();
    }

    @Override
    public void visit(Directory directory) {
        for (ProjectElement child : directory.children()) {
            child.accept(this);
        }
    }

    public int getTotalFiles() {
        return totalFiles;
    }

    public int getSourceFiles() {
        return sourceFiles;
    }

    public int getTestFiles() {
        return testFiles;
    }

    public int getSourceLines() {
        return sourceLines;
    }

    public int getTestLines() {
        return testLines;
    }

    public int getTestCount() {
        return testCount;
    }

    public int getDependencyCount() {
        return dependencyCount;
    }

    public double getAverageComplexity() {
        return sourceFiles == 0 ? 0 : (double) complexitySum / sourceFiles;
    }

    public double getTestSourceRatio() {
        return sourceLines == 0 ? 0 : (double) testLines / sourceLines;
    }

    public String getReport() {
        return String.format(
                "PROJECT STATISTICS%n"
                        + "  Total files: %d (source %d, test %d)%n"
                        + "  Lines of source code: %d%n"
                        + "  Lines of test code: %d%n"
                        + "  Number of tests: %d%n"
                        + "  Dependencies: %d%n"
                        + "  Average complexity: %.1f%n"
                        + "  Test/source ratio: %.2f%n",
                totalFiles, sourceFiles, testFiles, sourceLines, testLines,
                testCount, dependencyCount, getAverageComplexity(), getTestSourceRatio());
    }
}