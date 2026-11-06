package inspection;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ElementTest {

    static class RecordingVisitor implements ProjectVisitor {
        final List<String> calls = new ArrayList<>();

        public void visit(SourceCodeFile file) { calls.add("source"); }
        public void visit(TestFile file) { calls.add("test"); }
        public void visit(ConfigurationFile file) { calls.add("config"); }
        public void visit(DependencyFile file) { calls.add("dependency"); }
        public void visit(Directory directory) { calls.add("directory"); }
    }

    @Test
    void eachElementCallsItsOwnVisitMethod() {
        RecordingVisitor visitor = new RecordingVisitor();

        // the variable type is ProjectElement, so Java must decide at runtime (double dispatch)
        List<ProjectElement> elements = List.of(
                Builders.source("A.java", 10, 1),
                Builders.test("ATest.java", 5, "A.java"),
                Builders.safeConfig("app.properties"),
                new DependencyFile("pom.xml", List.of()),
                new Directory("src"));
        for (ProjectElement element : elements) {
            element.accept(visitor);
        }

        assertEquals(List.of("source", "test", "config", "dependency", "directory"), visitor.calls);
    }

    @Test
    void directoryCanHoldNestedDirectories() {
        Directory inner = new Directory("inner").add(Builders.source("A.java", 10, 1));
        Directory outer = new Directory("outer").add(inner);

        assertEquals(1, outer.children().size());
        assertSame(inner, outer.children().get(0));
    }

    @Test
    void childrenListCannotBeChangedFromOutside() {
        Directory directory = new Directory("src");
        assertThrows(UnsupportedOperationException.class,
                () -> directory.children().add(Builders.source("A.java", 10, 1)));
    }

    @Test
    void negativeLinesOfCodeAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new SourceCodeFile("A.java", -1, 1, false, false));
    }

    @Test
    void blankFileNameIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new ConfigurationFile(" ", false, true, false));
    }

    @Test
    void nullChildIsRejected() {
        Directory directory = new Directory("src");
        assertThrows(IllegalArgumentException.class, () -> directory.add(null));
    }

    @Test
    void dependencyWithoutVersionIsDetected() {
        assertFalse(new Dependency("gson", null, false).hasVersion());
        assertFalse(new Dependency("gson", " ", false).hasVersion());
        assertTrue(new Dependency("gson", "2.10", false).hasVersion());
    }
}