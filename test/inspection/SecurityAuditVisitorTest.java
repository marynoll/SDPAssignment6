package inspection;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class SecurityAuditVisitorTest {

    private SecurityAuditVisitor audit(ProjectElement element) {
        SecurityAuditVisitor visitor = new SecurityAuditVisitor();
        element.accept(visitor);
        return visitor;
    }

    @Test
    void cleanProjectGets100Points() {
        SecurityAuditVisitor visitor = audit(new Directory("p")
                .add(Builders.source("A.java", 100, 5))
                .add(Builders.safeConfig("app.properties")));

        assertEquals(0, visitor.getCriticalCount());
        assertEquals(100, visitor.getScore());
    }

    @Test
    void detectsHardcodedCredentialsInSourceCode() {
        SecurityAuditVisitor visitor = audit(new SourceCodeFile("Pay.java", 100, 5, true, false));

        assertEquals(1, visitor.getCriticalCount());
        assertEquals(75, visitor.getScore());
        assertTrue(visitor.getIssues().get(0).contains("Hardcoded credentials in Pay.java"));
    }

    @Test
    void detectsVulnerableDependencyButNotSafeOne() {
        SecurityAuditVisitor visitor = audit(new DependencyFile("pom.xml", List.of(
                new Dependency("good", "1.0", false),
                new Dependency("bad", "0.1", true))));

        assertEquals(1, visitor.getCriticalCount());
        assertTrue(visitor.getIssues().get(0).contains("bad"));
    }

    @Test
    void detectsInsecureConfigurationAndHighRiskFile() {
        Directory project = new Directory("p")
                .add(new ConfigurationFile("app.properties", true, false, false)) // debug on + ssl off
                .add(new SourceCodeFile("Shell.java", 50, 3, false, true));        // dangerous api
        SecurityAuditVisitor visitor = audit(project);

        assertEquals(0, visitor.getCriticalCount());
        assertEquals(3, visitor.getWarningCount());
        assertEquals(70, visitor.getScore());
    }

    @Test
    void plainTextPasswordInConfigIsCritical() {
        SecurityAuditVisitor visitor = audit(new ConfigurationFile("app.properties", false, true, true));
        assertEquals(1, visitor.getCriticalCount());
    }

    @Test
    void scoreNeverGoesBelowZero() {
        Directory project = new Directory("p");
        for (int i = 0; i < 6; i++) {
            project.add(new SourceCodeFile("F" + i + ".java", 10, 1, true, false));
        }
        assertEquals(0, audit(project).getScore());
    }

    @Test
    void testFilesAreIgnored() {
        assertEquals(100, audit(Builders.test("ATest.java", 50, "A.java")).getScore());
    }

    @Test
    void emptyDirectoryHasNoIssues() {
        SecurityAuditVisitor visitor = audit(new Directory("empty"));
        assertTrue(visitor.getIssues().isEmpty());
        assertEquals(100, visitor.getScore());
    }
}