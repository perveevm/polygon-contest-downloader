import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class PolygonSession {
    private final String key;
    private final String secret;

    private final CloseableHttpClient client = HttpClients.createDefault();
    private static final String ALPHABET = "abcdefghijklmnopqrtsuvwxyz";

    PolygonSession(String key, String secret) {
        this.key = key;
        this.secret = secret;
    }

    public String getProblemScript(String problemId, String testset) throws Exception {
        List<NameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair("problemId", problemId));
        parameters.add(new BasicNameValuePair("testset", testset));
        return getResponseAsString(parameters, "problem.script");
    }

    public String getProblemMainCorrectSolution(String problemId) throws Exception {
        List<NameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair("problemId", problemId));
        String response = getResponseAsString(parameters, "problem.solutions");

        JSONObject json = new JSONObject(response);
        JSONArray jsonSolutions = json.getJSONArray("result");

        System.out.println(response);

        for (int i = 0; i < jsonSolutions.length(); i++) {
            JSONObject solution = jsonSolutions.getJSONObject(i);
            String name = solution.getString("name");
            String tag = solution.getString("tag");

            if (tag.equals("MA")) {
                return name;
            }
        }

        return null;
    }

    public ArrayList<Test> getProblemTests(String problemId, String testset) throws Exception {
        List<NameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair("problemId", problemId));
        parameters.add(new BasicNameValuePair("testset", testset));
        String response = getResponseAsString(parameters, "problem.tests");

        JSONObject json = new JSONObject(response);
        JSONArray jsonTests = json.getJSONArray("result");
        ArrayList<Test> tests = new ArrayList<>();
        for (int i = 0; i < jsonTests.length(); i++) {
            JSONObject test = jsonTests.getJSONObject(i);

            boolean isGenerated = !test.getBoolean("manual");
            boolean hasGroup = test.has("group");
            boolean hasPoints = test.has("points");

            int points = 0;
            String group = null;
            String scriptLine = null;

            if (isGenerated) {
                scriptLine = test.getString("scriptLine");
                System.out.println(scriptLine);
            }
            if (hasGroup) {
                group = test.getString("group");
            }
            if (hasPoints) {
                points = (int)test.getDouble("points");
            }

            tests.add(new Test(scriptLine, group, points));
        }

        return tests;
    }

    public void getProblemPackage(String problemId, String packageId, String fileName) throws Exception {
        List<NameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair("problemId", problemId));
        parameters.add(new BasicNameValuePair("packageId", packageId));

        InputStream problemPackage = getResponseAsFile(parameters, "problem.package");

        if (problemPackage == null) {
            return;
        }

        FileOutputStream out = new FileOutputStream(new File(fileName));
        int inByte;
        while ((inByte = problemPackage.read()) != -1) {
            out.write(inByte);
        }

        problemPackage.close();
        out.close();
    }

    public ArrayList<String> getReadyProblemPackages(String problemId) throws Exception {
        List<NameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair("problemId", problemId));

        String json = getResponseAsString(parameters, "problem.packages");

        if (json == null) {
            return null;
        }

        JSONObject jsonPackages = new JSONObject(json);
        JSONArray jsonPackagesList = jsonPackages.getJSONArray("result");

        ArrayList<String> readyPackages = new ArrayList<>();
        for (int i = 0; i < jsonPackagesList.length(); i++) {
            JSONObject jsonCurrentPackage = jsonPackagesList.getJSONObject(i);
            if (jsonCurrentPackage.getString("state").equals("READY")) {
                readyPackages.add(String.valueOf(jsonCurrentPackage.getInt("id")));
            }
        }

        return readyPackages;
    }

    public LinkedHashMap<String, String> getContestProblems(String contestId) throws Exception {
        List<NameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair("contestId", contestId));

        String json = getResponseAsString(parameters, "contest.problems");

        if (json == null) {
            return null;
        }

        System.out.println(json);

        JSONObject jsonContest = new JSONObject(json);
        JSONObject jsonProblems = jsonContest.getJSONObject("result");

        LinkedHashMap<String, String> problems = new LinkedHashMap<>();
        for (String key : jsonProblems.keySet()) {
            JSONObject problem = jsonProblems.getJSONObject(key);
            String problemId = String.valueOf(problem.getInt("id"));
            problems.put(key, problemId);
        }

        return problems;
    }

    private InputStream getResponseAsFile(List<NameValuePair> additionalParameters, String method) throws Exception {
        HttpEntity response = null;
        while (true) {
            try {
                response = getResponse(additionalParameters, method);
            } catch (Exception e) {
                System.out.println("Error happened! Trying once more...");
            }
            break;
        }

        if (response == null) {
            return null;
        } else {
            return response.getContent();
        }
    }

    private String getResponseAsString(List<NameValuePair> additionalParameters, String method) throws Exception {
        HttpEntity response = getResponse(additionalParameters, method);

        if (response == null) {
            return null;
        } else {
            return EntityUtils.toString(response);
        }
    }

    private HttpEntity getResponse(List<NameValuePair> additionalParameters, String method) throws Exception {
        String time = String.valueOf(System.currentTimeMillis() / 1000);

        List<NameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair("apiKey", key));
        parameters.add(new BasicNameValuePair("time", time));

        parameters.addAll(additionalParameters);

        String apiSig = generateApiSig(method, parameters);
        parameters.add(new BasicNameValuePair("apiSig", apiSig));

        return sendPost("https://polygon.codeforces.com/api/" + method, parameters);
    }

    private HttpEntity sendPost(String url, List<NameValuePair> parameters) throws UnsupportedEncodingException {
        HttpPost post = new HttpPost(url);
        post.setEntity(new UrlEncodedFormEntity(parameters));

        try {
            CloseableHttpResponse response = client.execute(post);
            return response.getEntity();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String generateApiSig(String methodName, List<NameValuePair> parameters) {
        StringBuilder rand = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            rand.append(ALPHABET.charAt(new Random().nextInt(26)));
        }

        StringBuilder apiSig = new StringBuilder();
        apiSig.append(rand);
        apiSig.append('/');
        apiSig.append(methodName);
        apiSig.append('?');

        parameters.sort((o1, o2) -> {
            if (o1.getName().compareTo(o2.getName()) == 0) {
                return o1.getValue().compareTo(o2.getValue());
            } else {
                return o1.getName().compareTo(o2.getName());
            }
        });

        for (NameValuePair param : parameters) {
            apiSig.append(param.getName());
            apiSig.append('=');
            apiSig.append(param.getValue());
            apiSig.append('&');
        }

        apiSig.deleteCharAt(apiSig.length() - 1);
        apiSig.append('#');
        apiSig.append(secret);

        return rand + org.apache.commons.codec.digest.DigestUtils.sha512Hex(apiSig.toString());
    }
}
