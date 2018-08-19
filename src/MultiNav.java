import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InterfaceAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

/**
 * @author Zain on 5/17/16.
 */
public class MultiNav {
    private static final String API_KEY = "AIzaSyDTYWXGYzKpp1ytWUIL1ogc7SZWYtHWFr4";
    public static void main(String[] args) {
        try {
            ArrayList<String> destinations = new ArrayList<>();
            // User Input
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter a starting point.");
            String start = scanner.nextLine();
            start = start.replaceAll(" ", "+");
            boolean flag = false;
            int index = 0;
            while (!flag) {
                System.out.println("Enter where you want to go: (-1 if done)");
                String curr = scanner.nextLine();
                if (curr.equals("-1")) {
                    flag = true;
                } else {
                    curr = curr.replaceAll(" ", "+");
                    destinations.add(curr);
                }
            }
            destinations.add(0, start);
            double[][] distanceMatrix = new double[destinations.size() + 1][destinations.size() + 1];
            for (int i = 1; i <= destinations.size(); i++) {
                for (int j = 1; j <= destinations.size(); j++) {
                    if (i != j) {
                        String currURL = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + destinations.get(i - 1) + "&destinations=" + destinations.get(j - 1) + "&key=" + API_KEY;
                        URL url = new URL(currURL);
                        HttpURLConnection request = (HttpURLConnection) url.openConnection();
                        request.connect();

                        // Convert to a JSON object to print data
                        JsonParser jp = new JsonParser(); //from gson
                        JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent())); //Convert the input stream to a json element
                        JsonObject rootObj = root.getAsJsonObject(); //May be an array, may be an object.
                        JsonObject rowsObj = rootObj.getAsJsonArray("rows").get(0).getAsJsonObject();
                        System.out.println(rootObj.toString());
                        String distance = rowsObj.get("elements").getAsJsonArray().get(0).getAsJsonObject().get("duration").getAsJsonObject().get("value").getAsString();
                        distanceMatrix[i][j] = Double.parseDouble(distance);
                    }
                }
            }
            for (int i = 1; i <= destinations.size(); i++) {
                for (int j = 1; j <= destinations.size(); j++) {
                    System.out.print(distanceMatrix[i][j] + " ");
                }
                System.out.println();
            }
            MultiNav.tsp(distanceMatrix, destinations);
            MultiNav.permutationComputation(distanceMatrix, destinations);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void permutationComputation(double distanceMatrix[][], ArrayList<String> destinations) {
        List<List<Integer>> possiblePaths = new ArrayList<>();
        List<Integer> nums = new ArrayList<>();
        for (int i = 1; i <= destinations.size(); i++) {
            nums.add(i);
        }
        permute(nums, 0, possiblePaths);
        System.out.println(possiblePaths);
        List<Integer> minList = new ArrayList<>();
        double minDist = Double.MAX_VALUE;
        for (int i = 0; i < possiblePaths.size(); i++) {
            List<Integer> currList = possiblePaths.get(i);
            double currDist = 0.0;
            for (int j = 0; j < currList.size() - 1; j++) {
                int iIndex = currList.get(j);
                int jIndex = currList.get(j + 1);
                System.out.println(iIndex + " " + jIndex + " " + distanceMatrix[iIndex][jIndex]);
                currDist += distanceMatrix[iIndex][jIndex];
            }
            System.out.println(currDist);
            System.out.println(currList);
            if (currDist < minDist) {
                minDist = currDist;
                minList = currList;
            }
        }
        System.out.println(minList);
        System.out.println("\n\nOrder of addresses to visit:");
        int ind = 1;
        for (Integer x : minList) {
            System.out.println(ind++ + ") " + destinations.get(x - 1).replaceAll("\\+", " "));
        }
    }

    private static List<List<Integer>> permute(List<Integer> nums, int start, List<List<Integer>> possiblePaths) {
        if (start == nums.size()) {
            List<Integer> curr = new ArrayList<>();
            for (Integer i : nums) {
                curr.add(i);
            }
            if (curr.get(0) == 1) {
                possiblePaths.add(curr);
            }
            return possiblePaths;
        }
        for (int i = start; i < nums.size(); i++) {
            nums = swap(nums, start, i);
            possiblePaths = permute(nums, start + 1, possiblePaths);
            nums = swap(nums, start, i);
        }
        return possiblePaths;
    }

    private static List<Integer> swap(List<Integer> nums, int start, int i) {
        int temp = nums.get(start);
        nums.set(start, nums.get(i));
        nums.set(i, temp);
        return nums;
    }

    /**
     * tsp approximation using the greedy nearest neighbors method
     * src: http://www.sanfoundry.com/java-program-implement-traveling-salesman-problem-using-nearest-neighbour-algorithm/
     * Outputs the order the TSP-Approx algorithm suggests
     * @param adjacencyMatrix that represents the edges in the graph
     * @param destinations holds the list of addresses
     */
    public static void tsp(double adjacencyMatrix[][], ArrayList<String> destinations)
    {
        int numberOfNodes = adjacencyMatrix[1].length - 1;
        ArrayList<Integer> locationIndices = new ArrayList<>();
        Stack<Integer> stack = new Stack<>();
        int[] visited = new int[numberOfNodes + 1];
        visited[1] = 1;
        stack.push(1);
        int element, dst = 0, i;
        double min = Integer.MAX_VALUE;
        boolean minFlag = false;
        System.out.print(1 + "\t");
        locationIndices.add(0);

        while (!stack.isEmpty())
        {
            element = stack.peek();
            i = 1;
            min = Integer.MAX_VALUE;
            while (i <= numberOfNodes)
            {
                if (adjacencyMatrix[element][i] > 1 && visited[i] == 0)
                {
                    if (min > adjacencyMatrix[element][i])
                    {
                        min = adjacencyMatrix[element][i];
                        dst = i;
                        minFlag = true;
                    }
                }
                i++;
            }
            if (minFlag)
            {
                visited[dst] = 1;
                stack.push(dst);
                System.out.print(dst + "\t");
                minFlag = false;
                locationIndices.add(dst - 1);
                continue;
            }
            stack.pop();
        }
        System.out.println("\n\nOrder of addresses to visit:");
        int ind = 1;
        for (Integer x : locationIndices) {
            System.out.println(ind++ + ") " + destinations.get(x).replaceAll("\\+", " "));
        }
    }
}
