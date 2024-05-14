import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class Main {

    private static GraphDemo graph;

    public static String readFile(String filePath) {
        StringBuilder contentBuilder = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                contentBuilder.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String input = contentBuilder.toString().toLowerCase();
        String output = input.replaceAll("[^a-z]", " ");
        output = output.trim().replaceAll(" +", " ");
        return output;
    }

    public static void main(String[] args) {
        //从文本文件读取数据，进行处理（忽略非字母字符，标点符号和大小写，等等）
        //并在图形结构中存储处理后的数据。每个单词可以表示为图的节点，每个单词之间的关系可以表示为边。
        //请注意，这可能需要实现一个输入/输出(IO)函数，读入文本文件，返回清理和格式化后的字符串数组。
        String content = readFile("shuru.txt");

        graph = new GraphDemo(100);
        graph.create_graph(content);
        //展示生成的有向图。可以通过遍历节点和边的方式实现这一步骤，将每个节点和它的邻居一起输出。
        showDirectedGraph(graph);
        //实现queryBridgeWords函数，查询两个单词的桥接词
        String word1 = "explore";
        String word2 = "new";
        String result = queryBridgeWords(word1, word2);
        System.out.println(result);
        //实现generateNewText函数，根据bridge word生成新文本

        //启动实现calcShortestPath函数，计算两个单词之间的最短路径

        //实现randomWalk，随机游走
    }
    //显示图
    public static void showDirectedGraph(GraphDemo graph){

    }

    public static String queryBridgeWords(String word1, String word2) {
        if (!graph.nameToIndex.containsKey(word1)) {
            return "No \"" + word1 + "\" in the graph!";
        }
        if (!graph.nameToIndex.containsKey(word2)) {
            return "No \"" + word2 + "\" in the graph!";
        }

        int word1Index = graph.nameToIndex.get(word1);
        int word2Index = graph.nameToIndex.get(word2);

        List<String> bridgeWords = new ArrayList<>();
        for (int neighborIndex : graph.adj.get(word1Index)) {
            for (int neighborNeighborIndex : graph.adj.get(neighborIndex)) {
                if (neighborNeighborIndex == word2Index) {
                    bridgeWords.add(graph.indexToName.get(neighborIndex));
                }
            }
        }

        if (bridgeWords.isEmpty()) {
            return "No bridge words from \"" + word1 + "\" to \"" + word2 + "\"!";
        } else {
            return "The bridge words from \"" + word1 + "\" to \"" + word2 + "\" are: " + String.join(", ", bridgeWords) + ".";
        }
    }
}