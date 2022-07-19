
import java.io.*;
import java.util.*;

public class Main {

    private static double FINAL_SCORE = 0;

    public static void main(String[] args) throws IOException {

        File trainingSet = new File("training_set.csv");
        File testingSet = new File("test_set.csv");

        kNN(3,trainingSet,testingSet);

        System.out.println("Input your data:");
        System.out.println("[v1,v2,...,vn]");

        Scanner sc = new Scanner(System.in);

        while (true) {
            String input = sc.nextLine();
            kNNInput(3,trainingSet,input);
        }

    }

    public static void kNN(int k, File trainSet, File testSet) throws IOException {
        if (k <= 0) {
            System.err.println("k must be a positive natural number");
            System.exit(-1);
        }

        List<List<String>> trainingSet = readCsvFile(trainSet);
        List<List<String>> testingSet = readCsvFile(testSet);

        for (int i = 0; i < testingSet.size(); i++) {
            TreeMap<Double, String> distances = new TreeMap<>();
            for (int j = 0; j < trainingSet.size(); j++) { // calculate the distance and store results

                List<Double> trainVectors = new ArrayList<>();
                for (int y = 0; y < trainingSet.get(j).size()-1; y++) {
                    trainVectors.add(Double.parseDouble(trainingSet.get(j).get(y)));
                }

                List<Double> testVectors = new ArrayList<>();
                for (int y = 0; y < testingSet.get(i).size()-1; y++) {
                    testVectors.add(Double.parseDouble(testingSet.get(i).get(y)));
                }

                if (trainVectors.size() != testVectors.size()) {
                    throw new IOException("Vectors must be of the same dimension");
                }
                double distance = calculateDistance(trainVectors, testVectors);

                distances.put(distance, trainingSet.get(j).get(trainingSet.get(0).size()-1));
            }

            HashMap<Double, String> kFirst = new HashMap<>(); // Store k first results in a separate map
            int counter = 0;
            for (Map.Entry<Double, String> entry : distances.entrySet()) {
                if (counter < k) {
                    kFirst.put(entry.getKey(), entry.getValue());
                    counter++;
                }
            }

            HashMap<String, Integer> occurrenceClass = new HashMap<>();
            for (Map.Entry<Double, String> entry : kFirst.entrySet()) {
                increment(occurrenceClass, entry.getValue()); // store classes along with the number of their occurrences
            }

            Map.Entry<String, Integer> maxEntry = null;

            for (Map.Entry<String, Integer> entry : occurrenceClass.entrySet())
            {
                if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)
                {
                    maxEntry = entry;
                }
            }

            String mostCommon = maxEntry.getKey();

            if (testingSet.get(i).get(testingSet.get(i).size()-1).equals(mostCommon)) {
                testingSet.get(i).add(mostCommon);
            }

            int beforeLastElement = testingSet.get(i).size()-2;
            int lastElement = testingSet.get(i).size()-1;


            if (testingSet.get(i).get(beforeLastElement).equals(testingSet.get(i).get(lastElement))) {
                FINAL_SCORE++;
            }
        }

        System.out.println("Accuracy = "+(FINAL_SCORE/45) * 100 + "%");

    }

    public static List<List<String>> readCsvFile(File file) throws IOException {
        List<List<String>> list = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(file));

        String line;
        while ((line = br.readLine()) != null) {
            String[] values = line.split(",");
            list.add(new ArrayList(Arrays.asList(values)));
        }

        return list;
    }

    public static double calculateDistance(List<Double> l1, List<Double> l2) {
        if (l1.size() != l2.size()) {
            return -1;
        } else {
            double result = 0;
            for (int i = 0; i < l1.size(); i++) {
                result += Math.pow(l1.get(i) - l2.get(i), 2);
            }
            return Math.sqrt(result);
        }
    }

    public static void kNNInput(int k, File trainingSet, String input) throws IOException {
        try {
            String[] data = input.split(",");
            List<String> stringVectors = new ArrayList<>(Arrays.asList(data));
            List<Double> vectors = new ArrayList<>();
            for (String s : stringVectors) {
                double d = Double.parseDouble(s);
                vectors.add(d);
            }

            List<List<String>> training = readCsvFile(trainingSet);
            HashMap<Double,String> distances = new HashMap<>();
            for (int i = 0; i < training.size(); i++) {
                List<Double> trainVectors = new ArrayList<>();
                for (int j = 0; j < training.get(i).size()-1; j++) {
                    trainVectors.add(Double.parseDouble(training.get(i).get(j)));
                }
                if (vectors.size() != trainVectors.size()) {
                    throw new IOException();
                }
                double distance = calculateDistance(vectors, trainVectors);
                distances.put(distance, training.get(i).get(training.get(i).size()-1));
            }
            TreeMap<Double, String> sortedDistances = new TreeMap<>();
            sortedDistances.putAll(distances);

            HashMap<Double, String> kFirst = new HashMap<>();
            int counter = 0;
            for (Map.Entry<Double,String> entry : sortedDistances.entrySet()) {
                if (counter < k) {
                    kFirst.put(entry.getKey(), entry.getValue());
                    counter++;
                }
            }

            HashMap<String, Integer> occurrenceClass = new HashMap<>();
            for (Map.Entry<Double, String> entry : kFirst.entrySet()) {
                increment(occurrenceClass, entry.getValue()); // store classes along with the number of their occurrences
            }

            Map.Entry<String, Integer> maxEntry = null;

            for (Map.Entry<String, Integer> entry : occurrenceClass.entrySet())
            {
                if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)
                {
                    maxEntry = entry;
                }
            }

            String mostCommon = maxEntry.getKey();
            System.out.println("This data matches "+mostCommon);
        } catch (IndexOutOfBoundsException e) {
            System.err.println("Vectors must be of the same dimension");
        } catch (NumberFormatException e) {
            System.err.println("Incorrect formatting");
        } catch (IOException e) {
            System.err.println("Vectors must be of the same dimension");
        }

    }

    public static<K> void increment(Map<K, Integer> map, K key)
    {
        map.putIfAbsent(key, 0);
        map.put(key, map.get(key) + 1);
    }

}