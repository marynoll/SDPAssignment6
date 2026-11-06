package inspection;

public class Main {

    public static void main(String[] args) {
        Directory project = SampleProject.fintech();

        SecurityAuditVisitor security = new SecurityAuditVisitor();
        project.accept(security);
        System.out.println(security.getReport());

        ProjectStatisticsVisitor statistics = new ProjectStatisticsVisitor();
        project.accept(statistics);
        System.out.println(statistics.getReport());

        QualityGateVisitor qualityGate = new QualityGateVisitor();
        project.accept(qualityGate);
        System.out.println(qualityGate.getReport());

        ComplianceVisitor compliance = new ComplianceVisitor();
        project.accept(compliance);
        System.out.println(compliance.getReport());
    }
}
