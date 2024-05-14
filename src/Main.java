import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
        Scanner scanner = new Scanner(System.in);
        int choice = 0;
        while (choice != 1){
            System.out.print("Please Enter Your Choice: ");
            choice = scanner.nextInt();
            switch (choice){
                case 1:
                    System.out.println("Exiting program.");
                    break;
                case 2:
                    //展示生成的有向图。可以通过遍历节点和边的方式实现这一步骤，将每个节点和它的邻居一起输出。
                    showDirectedGraph(graph);
                    break;
                case 3:
                    scanner.nextLine(); // 清空输入缓冲区
                    //实现queryBridgeWords函数，查询两个单词的桥接词
                    System.out.print("Enter word1: ");
                    String word1 = scanner.next().toLowerCase();
                    System.out.print("Enter word2: ");
                    String word2 = scanner.next().toLowerCase();
                    String result = queryBridgeWords(word1, word2);
                    System.out.println(result);
                    break;
                case 4:
                    scanner.nextLine(); // 清空输入缓冲区
                    //实现generateNewText函数，根据bridge word生成新文本
                    System.out.print("Enter input text: ");
                    String inputText = scanner.nextLine();

                    String newText = generateNewText(inputText);
                    System.out.println("Generated text: " + newText);
                    break;
                case 5:
                    //启动实现calcShortestPath函数，计算两个单词之间的最短路径
                    break;
                case 6:
                    //实现randomWalk，随机游走
                    break;
                default:
                    System.out.println("Invalid choice.");
                    break;
            }

        }
    }
    //显示图
    // 绘制有向图
    public static void showDirectedGraph(GraphDemo graph) {
        // 创建 JFrame 窗口
        JFrame frame = new JFrame("Graph Visualization");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        // 获取窗口的中心点坐标
        int centerX = frame.getWidth() / 2;
        int centerY = frame.getHeight() / 2;

        // 在 JFrame 中绘制图形
        frame.getContentPane().add(new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                // Draw nodes
                List<String> nodes = graph.getNode();
                int node_num = nodes.size();
                int r = node_num * 10;
                Map<String, Integer> map = graph.getnameToIndex();
                for (String node : nodes) {
                    double x = centerX + -r * Math.sin(((double) map.get(node) / node_num) * 2 * Math.PI);  // Get node's x coordinate
                    double y = centerY + r * Math.cos(((double) map.get(node) / node_num) * 2 * Math.PI); // Get node's y coordinate
                    String label = node; // Get node's label

                    // Draw node as a circle
                    g.setColor(Color.PINK);
                    g.fillOval((int) x, (int) y, 20, 20); // Assuming node is represented by a circle with diameter 20

                    // Draw label
                    g.setColor(Color.BLACK);
                    g.drawString(label, (int) x + 25, (int) y); // Draw label next to the node
                }

                // Draw edges
                for (String node : nodes) {
                    Iterable<String> neighbors = graph.adj(node);
                    List<String> neighborList = StreamSupport.stream(neighbors.spliterator(), false)
                            .collect(Collectors.toList());
                    String start = node;
                    for (String end : neighborList) {
                        double startx = centerX + -r * Math.sin(((double) map.get(start) / node_num) * 2 * Math.PI);
                        double starty = centerY + r * Math.cos(((double) map.get(start) / node_num) * 2 * Math.PI);
                        double endx = centerX + -r * Math.sin(((double) map.get(end) / node_num) * 2 * Math.PI);
                        double endy = centerY + r * Math.cos(((double) map.get(end) / node_num) * 2 * Math.PI);
                        g.setColor(Color.RED);
                        //g.drawLine((int) startx + 10, (int) starty + 10, (int) endx + 10, (int) endy + 10); // Assuming nodes are represented by circles with diameter 20
                        drawShortenedLine(g, (int) startx+10, (int) starty+10, (int) endx+10, (int) endy+10);

                        int midpointX = (int)( startx +  endx) / 2;
                        int midpointY = (int)( starty +  endy) / 2;
                        g.drawString(Integer.toString(graph.getEdgeWeight(start,end)), midpointX, midpointY);
                    }
                }
            }
            void drawArrow(Graphics g, int x1, int y1, int x2, int y2) {
                g.setColor(Color.BLACK);
                // Calculate angle of line
                double angle = Math.atan2(y2 - y1, x2 - x1);

                // Draw arrow head parts
                int len = 6; // length of the arrow head
                double arrAngle1 = angle + Math.PI / 6; // angle of one side of the arrow head
                double arrAngle2 = angle - Math.PI / 6; // angle of the other side of the arrow head

                g.drawLine(x2, y2, x2 - (int) (len * Math.cos(arrAngle1)), y2 - (int) (len * Math.sin(arrAngle1)));
                g.drawLine(x2, y2, x2 - (int) (len * Math.cos(arrAngle2)), y2 - (int) (len * Math.sin(arrAngle2)));
            }
            void drawShortenedLine(Graphics g, int x1, int y1, int x2, int y2) {
                // Calculate angle of line
                double angle = Math.atan2(y2 - y1, x2 - x1);

                // Calculate shortened end point
                int shortLen = 10; // length to shorten
                int shortX = x2 - (int) (shortLen * Math.cos(angle));
                int shortY = y2 - (int) (shortLen * Math.sin(angle));

                // Draw line
                g.drawLine(x1, y1, shortX, shortY);
                //Draw arrow
                drawArrow(g,x1,y1,shortX,shortY);
            }
        });

        // 显示 JFrame
        frame.setVisible(true);
    }

    public static List<String> getBridgeWords(String word1, String word2) {
        List<String> bridgeWords = new ArrayList<>();
        word1 = word1.toLowerCase();
        word2 = word2.toLowerCase();
        if (!graph.nameToIndex.containsKey(word1) || !graph.nameToIndex.containsKey(word2)) {
            return bridgeWords;
        }

        int word1Index = graph.nameToIndex.get(word1);
        int word2Index = graph.nameToIndex.get(word2);

        for (int neighborIndex : graph.adj.get(word1Index)) {
            for (int neighborNeighborIndex : graph.adj.get(neighborIndex)) {
                if (neighborNeighborIndex == word2Index) {
                    bridgeWords.add(graph.indexToName.get(neighborIndex));
                }
            }
        }

        return bridgeWords;
    }

    public static String queryBridgeWords(String word1, String word2) {
        if (!graph.nameToIndex.containsKey(word1) && !graph.nameToIndex.containsKey(word2)) {
            return "No \"" + word1 + "\" and \"" + word2 + "\" in the graph!";
        }
        if (!graph.nameToIndex.containsKey(word1)) {
            return "No \"" + word1 + "\" in the graph!";
        }
        if (!graph.nameToIndex.containsKey(word2)) {
            return "No \"" + word2 + "\" in the graph!";
        }

        List<String> bridgeWords = getBridgeWords(word1, word2);

        if (bridgeWords.isEmpty()) {
            return "No bridge words from \"" + word1 + "\" to \"" + word2 + "\"!";
        } else {
            return "The bridge words from \"" + word1 + "\" to \"" + word2 + "\" are: " + String.join(", ", bridgeWords) + ".";
        }
    }

    public static String generateNewText(String inputText) {
        String[] words = inputText.split("\\s+");
        StringBuilder newTextBuilder = new StringBuilder();

        for (int i = 0; i < words.length - 1; i++) {
            String word1 = words[i];
            String word2 = words[i + 1];
            newTextBuilder.append(word1).append(" ");

            List<String> bridgeWords = getBridgeWords(word1, word2);
            if (!bridgeWords.isEmpty()) {
                // 随机选择一个桥接词插入
                Random random = new Random();
                String bridgeWord = bridgeWords.get(random.nextInt(bridgeWords.size()));
                newTextBuilder.append(bridgeWord).append(" ");
            }
        }

        // 添加最后一个单词
        newTextBuilder.append(words[words.length - 1]);

        return newTextBuilder.toString();
    }
}