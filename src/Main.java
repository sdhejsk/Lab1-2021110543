import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;
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

    public static void main(String[] args) throws InterruptedException {
        //从文本文件读取数据，进行处理（忽略非字母字符，标点符号和大小写，等等）
        //并在图形结构中存储处理后的数据。每个单词可以表示为图的节点，每个单词之间的关系可以表示为边。
        //请注意，这可能需要实现一个输入/输出(IO)函数，读入文本文件，返回清理和格式化后的字符串数组。
        String content = readFile("shuru.txt");

        graph = new GraphDemo(100);
        graph.create_graph(content);
        int choice = 0;
        Scanner scanner = new Scanner(System.in);
        while (choice != 1){
            System.out.print("############# Welcome to our program #############\n");
            System.out.print("1——Exiting program.\n");
            System.out.print("2——Show directed graphs.\n");
            System.out.print("3——Searching for bridging words.\n");
            System.out.print("4——Generate new text.\n");
            System.out.print("5——Calculate the shortest path.\n");
            System.out.print("6——Implement random walk.\n");
            System.out.print("###################################################\n");
            System.out.print("Please Enter Your Choice: ");
            choice = scanner.nextInt();
            switch (choice){
                case 1:
                    System.out.println("Exiting program.");
                    break;
                case 2:
                    //展示生成的有向图。可以通过遍历节点和边的方式实现这一步骤，将每个节点和它的邻居一起输出。
                    List<String> nonelist = new ArrayList<String>();
                    //设置JFrame frame
                    JFrame frame = new JFrame("Graph Visualization");
                    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    frame.setSize(1000, 800);

                    showDirectedGraph(graph,nonelist,0,frame);
                    //绘制图
                    //frame.dispose();
                    String saveFilePath = "graph_show.png";//设置路径
                    try {
                        // 创建 BufferedImage 实例
                        BufferedImage image = new BufferedImage(frame.getWidth() + 20, frame.getHeight() + 20, BufferedImage.TYPE_INT_ARGB);
                        Graphics2D g2d = image.createGraphics();
                        // 绘制图形到 BufferedImage
                        g2d.setColor(Color.WHITE);
                        g2d.fillRect(0, 0, image.getWidth(), image.getHeight());
                        frame.paint(g2d);
                        // 保存 BufferedImage 到文件
                        ImageIO.write(image, "png", new File(saveFilePath));
                        System.out.println("Graph saved as " + saveFilePath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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
                    scanner.nextLine(); // 清空输入缓冲区
                    //输入单词
                    System.out.print("Enter two word: ");
                    String line = scanner.nextLine().toLowerCase();
                    String[] words = line.split(" ");
                    String path;
                    //输入了两个单词
                    if(words.length>1)path = calcShortestPath(words[0], words[1]);
                    //只输入了一个单词
                    else path = calcShortestPath(words[0], " ");
                    //如果不可达
                    if(path.equals("NULL"))System.out.println("不可达");
                    else {
                        System.out.println(path);
                        // 使用正则表达式 "&" 或者 ","分隔path
                        String[] dataArray = path.split("[&,]");
                        // 将数组转换为 List
                        List<String> pathList = Arrays.asList(dataArray);
                        List<List<String>> allpathlist = new ArrayList<>();
                        //提取所有路径
                        for(String pt : pathList){
                            String[] patharray = pt.split("→");
                            List<String> apath = Arrays.asList(patharray);
                            allpathlist.add(apath);
                        }
                        //展示所有路径
                        for(List<String> a_path : allpathlist){
                            int length = 0;
                            for(int i =0;i<a_path.size()-1;i++){
                                length += graph.getEdgeWeight(a_path.get(i),a_path.get(i+1));
                            }
                            //创建JFrame类型
                            JFrame fr = new JFrame("Graph Visualization");
                            fr.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                            fr.setSize(1000, 800);
                            //展示路径
                            showDirectedGraph(graph,a_path,length,fr);
                        }
                    }
                    break;
                case 6:
                    //实现randomWalk，随机游走
                    String random_path = randomWalk();
                    //保存文件
                    writefile(random_path,"output.txt");
                    break;
                default:
                    System.out.println("Invalid choice.");
                    break;
            }

        }
    }
    //显示图
    // 绘制有向图
    public static void showDirectedGraph(GraphDemo graph, List<String> path,int length, JFrame frame) {

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
                int r = frame.getHeight() / 3 ;
                Map<String, Integer> map = graph.getnameToIndex();
                if(length!=0)g.drawString("path length: "+length, centerX-20, centerY - 150);
                for (String node : nodes) {
                    double x = centerX - r * Math.sin(((double) map.get(node) / node_num) * 2 * Math.PI);  // Get node's x coordinate
                    double y = centerY + r * Math.cos(((double) map.get(node) / node_num) * 2 * Math.PI); // Get node's y coordinate
                    String label = node; // Get node's label

                    // Draw node as a circle
                    if(path.contains(node))g.setColor(Color.GREEN);
                    else g.setColor(Color.PINK);
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
                        if(path.contains(start)&&path.contains(end)&&(path.indexOf(end)-path.indexOf(start)==1))g.setColor(Color.GREEN);
                        else g.setColor(Color.RED);
                        drawShortenedLine(g, (int) startx+10, (int) starty+10, (int) endx+10, (int) endy+10);

                        int midpointX = (int)( startx +  endx + 20)/2;
                        int midpointY = (int)( starty +  endy + 20)/2;
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

        // 等待 JFrame 完全显示
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getBridgeWords(String word1, String word2) {
        Set<String> bridgeWords = new HashSet<>();
        word1 = word1.toLowerCase();
        word2 = word2.toLowerCase();
        if (!graph.nameToIndex.containsKey(word1) || !graph.nameToIndex.containsKey(word2)) {
            return new ArrayList<>(bridgeWords);
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

        return new ArrayList<>(bridgeWords);
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
    //计算最短路径
    public static String calcShortestPath(String word1, String word2){
        List<List<Integer>> linjie = graph.getAdj();
        List<String> nodes = graph.getNode();
        List<List<String>> result = new ArrayList<>();
        List<String> path = new ArrayList<>();
        int[] shortest = new int[1];
        shortest[0] = Integer.MAX_VALUE;
        int from = nodes.indexOf(word1);
        if(from == -1) return word1 + " is not in the nodes.";

        StringBuilder sb = new StringBuilder();

        if(word2.equals(" ")){
            for(String target : nodes){
                if(target.equals(word1)) continue;
                int to = nodes.indexOf(target);
                result.clear();
                path.clear();
                path.add(word1);
                shortest[0] = Integer.MAX_VALUE;
                dfs(from, to, linjie, nodes, 0, result, path, shortest,graph);
                String last = " ";
                for(List<String> res : result){
                    if(res.get(res.size()-1).equals(last))sb.append(",");
                    else sb.append("&");
                    last = res.get(res.size()-1);
                    sb.append(String.join("→", res));
                }
            }
            sb.deleteCharAt(0); // Remove the last "&"
        }else{
            int to = nodes.indexOf(word2);
            if(to == -1) return word2 + " is not in the nodes.";

            path.add(word1);
            dfs(from, to, linjie, nodes, 0, result, path, shortest, graph);
            if(result.size()!=0) {
                for (List<String> res : result) {
                    sb.append(String.join("→", res));
                    sb.append(",");
                }
                sb.deleteCharAt(sb.length() - 1); // Remove the last ","
            }
            else return "NULL";
        }
        return sb.toString();
    }
    //深度优先搜索
    public static void dfs(int from, int to, List<List<Integer>> linjie, List<String> nodes,
             int len, List<List<String>> result, List<String> path, int[] shortest, GraphDemo graph){
        if(len > shortest[0]) return;
        if(from == to){
            if(len < shortest[0]){
                result.clear();
                shortest[0] = len;
            }
            //System.out.println("adding");
            result.add(new ArrayList<>(path)); // Add path to result if it has the shortest length
            return;
        }
        List<String> neilist = new ArrayList<String>();
        for(int next : linjie.get(from)){
            if(!neilist.contains(nodes.get(next))){
                neilist.add(nodes.get(next));
                //System.out.println(nodes.get(from)+"的邻居："+nodes.get(next));
                if(path.contains(nodes.get(next))) continue;
                path.add(nodes.get(next));
                dfs(next, to,  linjie, nodes,len + graph.getEdgeWeight(nodes.get(from), nodes.get(next)), result, path, shortest, graph);
                path.remove(path.size() - 1);
            }
        }
    }

    //随机游走
    public static String randomWalk() {
        List<List<Integer>> linjie = graph.getAdj();
        List<String> nodes = graph.getNode();
        Random random = new Random();
        List<String> have_edges = new ArrayList<>();
        int lastNodeIndex = random.nextInt(nodes.size());
        System.out.print(nodes.get(lastNodeIndex) + " ");

        boolean not_chongfu = true;
        StringBuilder path = new StringBuilder();
        path.append(nodes.get(lastNodeIndex));

        Object lock = new Object();

        // 创建并启动等待用户输入的线程
        UserInputListener userInputListener = new UserInputListener();
        Thread userInputThread = new Thread(userInputListener);
        userInputThread.start();

        // 主循环
        boolean exitLoop = false;

        while (!linjie.get(lastNodeIndex).isEmpty() && not_chongfu && !exitLoop) {
            int nextNodeIndex = linjie.get(lastNodeIndex).get(random.nextInt(linjie.get(lastNodeIndex).size()));
            System.out.print(nodes.get(nextNodeIndex) + " ");

            path.append(" ").append(nodes.get(nextNodeIndex));
            if (have_edges.contains(lastNodeIndex + "," + nextNodeIndex)) {
                not_chongfu = false;
            } else {
                have_edges.add(lastNodeIndex + "," + nextNodeIndex);
            }
            lastNodeIndex = nextNodeIndex;

            synchronized (lock) {
                try {
                    lock.wait(500); // 等待0.5秒或直到被notify/notifyAll唤醒
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }

            // 检查用户输入线程是否还在运行
            if (!userInputThread.isAlive() || userInputListener.isInterrupted()) {
                exitLoop = true;
            }
        }

        if (userInputListener.isInterrupted()) {
            System.out.println("\nUser input detected. Exiting random walk.");
        } else if (userInputThread.isAlive()) {
            userInputThread.interrupt();
        }

        System.out.println();
        return path.toString();
    }

    // 等待用户输入的线程类
    static class UserInputListener implements Runnable {
        private Scanner scan = new Scanner(System.in);
        private volatile boolean interrupted = false; // 标志变量

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                if (scan.hasNextLine()) {
                    String input = scan.nextLine();
                    interrupted = true; // 设置标志变量
                    break; // 用户输入后退出循环
                }
            }
        }

        public boolean isInterrupted() {
            return interrupted;
        }
    }

    //写文件
    public static void writefile(String content,String filePath){
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(content);
            System.out.println("成功写入文件！");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("写入文件时发生错误！");
        }
    }
}