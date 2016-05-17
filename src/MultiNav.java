import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

/**
 * Created by zain on 5/17/16.
 */
public class MultiNav {
    private static final String API_KEY = "AIzaSyDTYWXGYzKpp1ytWUIL1ogc7SZWYtHWFr4";
    public static void main(String[] args) {
        try {
//            Scanner scanner = new Scanner(System.in);
//            System.out.println("Enter a starting point.");
//            String start = scanner.nextLine();
//            start = start.replaceAll(" ", "+");
            ArrayList<String> destinations = new ArrayList<>();
//            boolean flag = false;
//            int index = 0;
//            while (!flag) {
//                System.out.println("Enter where you want to go: (-1 if done)");
//                String curr = scanner.nextLine();
//                if (curr.equals("-1")) {
//                    flag = true;
//                } else {
//                    curr = curr.replaceAll(" ", "+");
//                    destinations.add(curr);
//                }
//            }
//            destinations.add(0, start);
            String zainAddr = "2352 Tallapoosa Drive Duluth GA 30097";
            String tazzyAddr = "2775 Shelter Cove Duluth GA 30096";
            String starbucksAddr = "35674 Fremont Blvd Fremont CA 94536";
            String dardaAddr = "296 Barber Ct Milpitas CA 95035";
            zainAddr = zainAddr.replaceAll(" ", "+");
            tazzyAddr = tazzyAddr.replaceAll(" ", "+");
            starbucksAddr = starbucksAddr.replaceAll(" ", "+");
            dardaAddr = dardaAddr.replaceAll(" ", "+");
            destinations.add(zainAddr);
            destinations.add(tazzyAddr);
            destinations.add(starbucksAddr);
            destinations.add(dardaAddr);
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
                    System.out.print(distanceMatrix[i][j] / 60 + " ");
                }
                System.out.println();
            }
            MultiNav.tsp(distanceMatrix);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void tsp(double adjacencyMatrix[][])
    {
        int numberOfNodes = adjacencyMatrix[1].length - 1;
        Stack<Integer> stack = new Stack<>();
        int[] visited = new int[numberOfNodes + 1];
        visited[1] = 1;
        stack.push(1);
        int element, dst = 0, i;
        double min = Integer.MAX_VALUE;
        boolean minFlag = false;
        System.out.print(1 + "\t");

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
                continue;
            }
            stack.pop();
        }
    }
}
