package inspection;

import java.util.List;

public class SampleProject {

    public static Directory fintech() {
        Directory src = new Directory("src")
                .add(new SourceCodeFile("PaymentService.java", 320, 12, true, false))
                .add(new SourceCodeFile("UserService.java", 180, 6, false, true));

        Directory tests = new Directory("tests")
                .add(new TestFile("PaymentServiceTest.java", 120, 8, "PaymentService.java"));

        Directory config = new Directory("config")
                .add(new ConfigurationFile("application.properties", true, true, true));

        DependencyFile pom = new DependencyFile("pom.xml", List.of(
                new Dependency("spring-boot", "2.7.0", false),
                new Dependency("jackson-databind", "2.9.8", true),
                new Dependency("junit-jupiter", "5.9.0", false),
                new Dependency("log4j", "1.2.17", false),
                new Dependency("gson", null, false)));

        return new Directory("fintech-project")
                .add(src)
                .add(tests)
                .add(config)
                .add(pom);
    }
}