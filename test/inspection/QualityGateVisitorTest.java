package inspection;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class QualityGateVisitorTest {

    private QualityGateVisitor check(ProjectElement element) {
        QualityGateVisitor gate = new QualityGateVisitor();
        element.accept(gate);
        return gate;
    }

    private Directory goodProject() {
        return new Directory("p")
                .add(Builders.source("A.java", 100, 5))
                .add(Builders.test("ATest.java", 50, "A.java"));
    }

    @Test
    void goodProjectPasses() {
        QualityGateVisitor gate = check(goodProject());

        assertTrue(gate.isReleaseReady());
        assertTrue(gate.getReport().contains("FINAL RESULT: PASSED"));
    }

    @Test
    void failsWhenTestRatioIsTooLow() {
        Directory project = new Directory("p")
                .add(Builders.source("A.java", 100, 5))
                .add(Builders.test("ATest.java", 20, "A.java"));

        QualityGateVisitor gate = check(project);

        assertTrue(gate.securityPassed());
        assertTrue(gate.complexityPassed());
        assertFalse(gate.testsPassed());
        assertFalse(gate.isReleaseReady());
        assertTrue(gate.getReport().contains("Tests: FAIL"));
        assertTrue(gate.getReport().contains("FINAL RESULT: FAILED"));
    }

    @Test
    void failsOnCriticalSecurityIssue() {
        Directory project = new Directory("p")
                .add(new SourceCodeFile("A.java", 100, 5, true, false))
                .add(Builders.test("ATest.java", 50, "A.java"));

        QualityGateVisitor gate = check(project);

        assertFalse(gate.securityPassed());
        assertFalse(gate.isReleaseReady());
        assertTrue(gate.getReport().contains("Security: FAIL"));
    }

    @Test
    void failsWhenAverageComplexityIsAboveTen() {
        Directory project = new Directory("p")
                .add(Builders.source("A.java", 100, 11))
                .add(Builders.test("ATest.java", 50, "A.java"));

        assertFalse(check(project).complexityPassed());
    }

    @Test
    void exactlyOnTheLimitsStillPasses() {
        Directory project = new Directory("p")
                .add(Builders.source("A.java", 100, 10))
                .add(Builders.test("ATest.java", 40, "A.java"));

        assertTrue(check(project).isReleaseReady());
    }

    @Test
    void warningsAloneDoNotFailTheGate() {
        Directory project = goodProject()
                .add(new ConfigurationFile("app.properties", true, true, false)); // only a warning

        assertTrue(check(project).isReleaseReady());
    }

    @Test
    void emptyProjectFailsBecauseThereAreNoTests() {
        QualityGateVisitor gate = check(new Directory("empty"));

        assertFalse(gate.testsPassed());
        assertFalse(gate.isReleaseReady());
    }

    @Test
    void fintechProjectFailsLikeTheAssignmentExample() {
        String report = check(SampleProject.fintech()).getReport();

        assertTrue(report.contains("Security: FAIL"));
        assertTrue(report.contains("Complexity: PASS"));
        assertTrue(report.contains("Tests: FAIL"));
        assertTrue(report.contains("FINAL RESULT: FAILED"));
    }
}