package inspection;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ProjectStatisticsVisitorTest {

    private ProjectStatisticsVisitor count(ProjectElement element) {
        ProjectStatisticsVisitor visitor = new ProjectStatisticsVisitor();
        element.accept(visitor);
        return visitor;
    }

    @Test
    void countsTheFintechProject() {
        ProjectStatisticsVisitor s = count(SampleProject.fintech());

        assertEquals(5, s.getTotalFiles());
        assertEquals(2, s.getSourceFiles());
        assertEquals(1, s.getTestFiles());
        assertEquals(500, s.getSourceLines());
        assertEquals(120, s.getTestLines());
        assertEquals(8, s.getTestCount());
        assertEquals(5, s.getDependencyCount());
        assertEquals(9.0, s.getAverageComplexity(), 0.001);
        assertEquals(0.24, s.getTestSourceRatio(), 0.001);
    }

    @Test
    void emptyDirectoryGivesZerosWithoutDividingByZero() {
        ProjectStatisticsVisitor s = count(new Directory("empty"));

        assertEquals(0, s.getTotalFiles());
        assertEquals(0.0, s.getAverageComplexity(), 0.001);
        assertEquals(0.0, s.getTestSourceRatio(), 0.001);
    }

    @Test
    void countsFilesInNestedDirectories() {
        Directory level3 = new Directory("c").add(Builders.source("C.java", 30, 3));
        Directory level2 = new Directory("b").add(Builders.source("B.java", 20, 2)).add(level3);
        Directory level1 = new Directory("a").add(Builders.source("A.java", 10, 1)).add(level2);

        ProjectStatisticsVisitor s = count(level1);

        assertEquals(3, s.getTotalFiles());
        assertEquals(60, s.getSourceLines());
        assertEquals(2.0, s.getAverageComplexity(), 0.001);
    }

    @Test
    void averageComplexityIsOverSourceFilesOnly() {
        Directory project = new Directory("p")
                .add(Builders.source("A.java", 10, 4))
                .add(Builders.source("B.java", 10, 8))
                .add(Builders.test("ATest.java", 10, "A.java"))
                .add(Builders.safeConfig("app.properties"));

        assertEquals(6.0, count(project).getAverageComplexity(), 0.001);
    }

    @Test
    void veryDeepDirectoryStructureIsProcessed() {
        Directory root = new Directory("level0");
        Directory current = root;
        for (int i = 1; i <= 200; i++) {
            Directory next = new Directory("level" + i);
            current.add(next);
            current = next;
        }
        current.add(Builders.source("Deep.java", 10, 1));

        assertEquals(1, count(root).getTotalFiles());
    }
}