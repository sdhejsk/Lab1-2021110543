import java.util.*;

public class GraphDemo {

    protected Map<String, Integer> nameToIndex; // 节点名称到索引的映射
    protected List<String> indexToName;    // 索引到节点名称的映射
    protected List<List<Integer>> adj;     // 邻接表

    public GraphDemo(int V) {
        nameToIndex = new HashMap<>();
        indexToName = new ArrayList<>();
        adj = new ArrayList<>();
        for (int i = 0; i < V; i++) {
            adj.add(new ArrayList<>());
        }
    }

    public void addNode(String name) {
        if (!nameToIndex.containsKey(name)) {
            int index = nameToIndex.size();
            nameToIndex.put(name, index);
            indexToName.add(name);
        }
    }

    public void addEdge(String from, String to) {
        if (!nameToIndex.containsKey(from) || !nameToIndex.containsKey(to)) {
            throw new IllegalArgumentException("Node not found");
        }
        int fromIndex = nameToIndex.get(from);
        int toIndex = nameToIndex.get(to);
        adj.get(fromIndex).add(toIndex);
    }

    public int getEdgeWeight(String from, String to) {
        if (!nameToIndex.containsKey(from) || !nameToIndex.containsKey(to)) {
            throw new IllegalArgumentException("Node not found");
        }
        int fromIndex = nameToIndex.get(from);
        int toIndex = nameToIndex.get(to);
        int count = 0;
        for (int neighborIndex : adj.get(fromIndex)) {
            if (neighborIndex == toIndex) {
                count++;
            }
        }
        return count;
    }


    public Iterable<String> adj(String name) {
        if (!nameToIndex.containsKey(name)) {
            throw new IllegalArgumentException("Node not found");
        }
        List<String> neighbors = new ArrayList<>();
        int index = nameToIndex.get(name);
        for (int neighborIndex : adj.get(index)) {
            neighbors.add(indexToName.get(neighborIndex));
        }
        return neighbors;
    }

    public void create_graph(String content){
        String[] words = content.split(" ");  // 分隔字符串

        for (String word : words) {
            addNode(word);
        }
        for(int i =0; i<words.length-1; i++){
            addEdge(words[i],words[i+1]);
        }
    }
    public List<String> getNode(){
        return indexToName;
    }
    public Map<String, Integer> getnameToIndex(){
        return nameToIndex;
    }

    public List<List<Integer>> getAdj(){return adj;}

    public static void main(String[] args) {
        GraphDemo graph = new GraphDemo(5);
        graph.addNode("A");
        graph.addNode("B");
        graph.addNode("C");
        graph.addEdge("A", "B");
        graph.addEdge("A", "C");
        graph.addEdge("B", "C");
        graph.addEdge("B", "C"); // 添加一条权重为2的边

        System.out.println("Weight from A to B: " + graph.getEdgeWeight("A", "B")); // 输出1
        System.out.println("Weight from B to C: " + graph.getEdgeWeight("B", "C")); // 输出2
    }

}
