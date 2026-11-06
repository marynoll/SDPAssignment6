package inspection;

import java.util.ArrayList;
import java.util.List;

public class TechnicalDebtVisitor implements ProjectVisitor {

    private static final int COMPLEXITY_LIMIT = 10;
    private static final int MINUTES_PER_EXTRA_COMPLEXITY = 30;
    private static final int LONG_FILE_LINES = 300;
    private static final int LONG_FILE_MINUTES = 60;

    private final List<String> details = new ArrayList<>();
    private int totalMinutes = 0;

    @Override
    public void visit(SourceCodeFile file) {
        int minutes = 0;
        if (file.complexity() > COMPLEXITY_LIMIT) {
            minutes += (file.complexity() - COMPLEXITY_LIMIT) * MINUTES_PER_EXTRA_COMPLEXITY;
        }
        if (file.linesOfCode() > LONG_FILE_LINES) {
            minutes += LONG_FILE_MINUTES;
        }
        if (minutes > 0) {
            details.add(file.name() + ": " + minutes + " min");
            totalMinutes += minutes;
        }
    }

    @Override
    public void visit(TestFile file) {
        // not relevant for technical debt
    }

    @Override
    public void visit(ConfigurationFile file) {
        // not relevant for technical debt
    }

    @Override
    public void visit(DependencyFile file) {
        // not relevant for technical debt
    }

    @Override
    public void visit(Directory directory) {
        for (ProjectElement child : directory.children()) {
            child.accept(this);
        }
    }

    public int getTotalMinutes() {
        return totalMinutes;
    }

    public double getTotalHours() {
        return totalMinutes / 60.0;
    }

    public String getReport() {
        StringBuilder sb = new StringBuilder("TECHNICAL DEBT\n");
        for (String detail : details) {
            sb.append("  ").append(detail).append("\n");
        }
        sb.append(String.format("  Total estimated debt: %d min (%.1f hours)%n", totalMinutes, getTotalHours()));
        return sb.toString();
    }
}
