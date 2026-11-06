package inspection;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class TechnicalDebtVisitorTest {

    private TechnicalDebtVisitor measure(ProjectElement element) {
        TechnicalDebtVisitor visitor = new TechnicalDebtVisitor();
        element.accept(visitor);
        return visitor;
    }

    @Test
    void complexAndLongFileCostsTwoHours() {TechnicalDebtVisitor visitor = measure(Builders.source("Big.java", 320, 12));

        assertEquals(120, visitor.getTotalMinutes());
        assertEquals(2.0, visitor.getTotalHours(), 0.001);
    }

    @Test
    void fileExactlyOnTheLimitsHasNoDebt() {
        assertEquals(0, measure(Builders.source("Ok.java", 300, 10)).getTotalMinutes());
    }

    @Test
    void otherElementTypesAreIgnored() {
        Directory project = new Directory("p")
                .add(Builders.test("ATest.java", 999, "A.java"))
                .add(Builders.safeConfig("app.properties"))
                .add(new DependencyFile("pom.xml", List.of()));

        assertEquals(0, measure(project).getTotalMinutes());
    }

    @Test
    void debtIsAddedUpAcrossNestedDirectories() {
        Directory project = new Directory("p")
                .add(Builders.source("A.java", 100, 11))
                .add(new Directory("sub").add(Builders.source("B.java", 100, 12)));

        assertEquals(90, measure(project).getTotalMinutes());
    }

    @Test
    void fintechProjectDebtIsTwoHours() {
        assertEquals(120, measure(SampleProject.fintech()).getTotalMinutes());
    }
}