import java.io.PrintWriter;
import java.util.*;

public class ContestDownloader {
    public static void main(String[] args) throws Exception {
        String key = args[0];
        String secret = args[1];
        String contestId = args[2];
        String contestPath = args[3];

        PolygonSession session = new PolygonSession(key, secret);

        LinkedHashMap<String, String> problems = session.getContestProblems(contestId);
        for (Map.Entry<String, String> entry : problems.entrySet()) {
            String problemName = entry.getKey();
            String problemId = entry.getValue();

            String problemScript = session.getProblemScript(problemId, "tests");
            ArrayList<Test> problemTests = session.getProblemTests(problemId, "tests");
            ArrayList<String> packages = session.getReadyProblemPackages(problemId);

            if (packages.isEmpty()) {
                System.out.println("There are no packages for problem " + problemId + ". Skipping...");
                continue;
            }

            session.getProblemPackage(problemId, packages.get(0), contestPath + "/" + problemName + ".zip");

            TreeSet<Integer> emptyTests = new TreeSet<>();
            StringBuilder newScript = new StringBuilder();
            for (int i = 1; i <= problemTests.size(); i++) {
                emptyTests.add(i);
            }

            System.out.println(problemTests.size());

            PrintWriter writer = new PrintWriter(contestPath + "/" + problemName + ".solution");
            writer.write(session.getProblemMainCorrectSolution(problemId));
            writer.close();

            int testId = 0;
            for (Test test : problemTests) {
                testId++;
                if (!test.isGenerated()) {
                    emptyTests.remove(testId);
                }
            }

            for (String command : problemScript.split("\n")) {
                int begin = command.indexOf('{');
                int end = command.indexOf('}');

                if (begin == -1 && end == -1) {
                    continue;
                }

                newScript.append(contestPath).append("/problems/").append(problemName).append("/").append(command.substring(0, begin).replace(">", "")).append("\n");

                String range = command.substring(begin + 1, end);
                String[] rangeLimits = range.split("-");

                if (rangeLimits.length == 1) {
                    emptyTests.remove(Integer.valueOf(rangeLimits[0]));
                } else {
                    begin = Integer.valueOf(rangeLimits[0]);
                    end = Integer.valueOf(rangeLimits[1]);

                    for (int id = begin; id <= end; id++) {
                        emptyTests.remove(id);
                    }
                }
            }

            for (String command : problemScript.split("\n")) {
                if (command.isEmpty()) {
                    continue;
                }

                int begin = command.indexOf('{');
                int end = command.indexOf('}');

                if (begin != -1 && end != -1) {
                    continue;
                }

                testId = emptyTests.first();
                emptyTests.remove(testId);

                newScript.append(contestPath).append("/problems/").append(problemName).append("/").append(
                        command.replace("$", String.valueOf(testId))).append("\n");
            }

            if (newScript.toString().isEmpty()) {
                continue;
            }

            writer = new PrintWriter(contestPath + "/" + problemName + ".script");
            writer.write(newScript.toString());
            writer.close();
        }
    }
}
