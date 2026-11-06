package inspection;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ComplianceVisitorTest {

    private ComplianceVisitor check(ProjectElement element) {
        ComplianceVisitor visitor = new ComplianceVisitor();
        element.accept(visitor);
        return visitor;
    }

    @Test
    void fintechProjectHasFiveViolations() {
        ComplianceVisitor visitor = check(SampleProject.fintech());
        List<String> violations = visitor.getViolations();

        assertEquals(5, violations.size());
        assertTrue(violations.contains("Exposed credentials in PaymentService.java"));
        assertTrue(violations.contains("Exposed credentials in application.properties"));
        assertTrue(violations.contains("Prohibited dependency log4j in pom.xml"));
        assertTrue(violations.contains("Dependency gson has no version in pom.xml"));
        assertTrue(violations.contains("Production file UserService.java has no test"));
        assertFalse(visitor.isCompliant());
    }

    @Test
    void cleanProjectIsCompliant() {
        Directory project = new Directory("p")
                .add(Builders.source("A.java", 100, 5))
                .add(Builders.test("ATest.java", 50, "A.java"))
                .add(new DependencyFile("pom.xml", List.of(new Dependency("gson", "2.10", false))));

        assertTrue(check(project).isCompliant());
    }

    @Test
    void sourceFileWithoutTestIsReported() {
        ComplianceVisitor visitor = check(new Directory("p").add(Builders.source("A.java", 100, 5)));

        assertEquals(List.of("Production file A.java has no test"), visitor.getViolations());
    }

    @Test
    void testInAnotherDirectoryStillCounts() {
        // the tests folder comes BEFORE the src folder, so the order must not matter
        Directory project = new Directory("p")
                .add(new Directory("tests").add(Builders.test("ATest.java", 50, "A.java")))
                .add(new Directory("src").add(Builders.source("A.java", 100, 5)));

        assertTrue(check(project).isCompliant());
    }

    @Test
    void missingDependencyVersionIsReported() {
        ComplianceVisitor visitor = check(new DependencyFile("pom.xml", List.of(
                new Dependency("gson", "", false))));

        assertEquals(1, visitor.getViolations().size());
        assertTrue(visitor.getViolations().get(0).contains("has no version"));
    }

    @Test
    void prohibitedListCanBeChanged() {
        DependencyFile pom = new DependencyFile("pom.xml", List.of(
                new Dependency("log4j", "1.2", false),
                new Dependency("commons-collections", "3.2", false)));

        ComplianceVisitor custom = new ComplianceVisitor(Set.of("commons-collections"));
        pom.accept(custom);

        assertEquals(1, custom.getViolations().size());
        assertTrue(custom.getViolations().get(0).contains("commons-collections"));
    }

    @Test
    void askingForViolationsTwiceGivesTheSameAnswer() {
        ComplianceVisitor visitor = check(new Directory("p").add(Builders.source("A.java", 100, 5)));

        assertEquals(visitor.getViolations(), visitor.getViolations());
        assertEquals(1, visitor.getViolations().size());
    }
}