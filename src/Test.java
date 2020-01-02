public class Test {
    private String scriptLine;
    private String group;
    private int points;

    public Test(String scriptLine, String group, int points) {
        this.scriptLine = scriptLine;
        this.group = group;
        this.points = points;
    }

    public boolean isGenerated() {
        return this.scriptLine != null;
    }

    public String getScriptLine() {
        return scriptLine;
    }

    public String getGroup() {
        return group;
    }

    public int getPoints() {
        return points;
    }
}
