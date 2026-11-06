package inspection;

class Builders {

    static SourceCodeFile source(String name, int lines, int complexity) {
        return new SourceCodeFile(name, lines, complexity, false, false);
    }

    static TestFile test(String name, int lines, String testedFile) {
        return new TestFile(name, lines, 5, testedFile);
    }

    static ConfigurationFile safeConfig(String name) {
        return new ConfigurationFile(name, false, true, false);
    }
}