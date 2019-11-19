package bearmaps;

import bearmaps.utils.graph.WeightedEdge;
import bearmaps.utils.graph.streetmap.Node;
import bearmaps.utils.graph.streetmap.StreetMapGraph;
import bearmaps.utils.ps.Point;
import bearmaps.utils.ps.WeirdPointSet;

import java.util.*;

/**
 * An augmented graph that is more powerful that a standard StreetMapGraph.
 * Specifically, it supports the following additional operations:
 *
 *
 * @author Alan Yao, Josh Hug, ________
 */
public class AugmentedStreetMapGraph extends StreetMapGraph {
    private HashMap<Point, Node> convertNodeToPoint = new HashMap<>();
    private List<Point> pointsInMap = new ArrayList<>();
    private List<Node> nodesInMap = new ArrayList<>();
    private MyTrieSet myTrieSet = new MyTrieSet();
    private HashMap<String, ArrayList<Node>> neighbor = new HashMap<>();

    public AugmentedStreetMapGraph(String dbPath) {
        super(dbPath);
        // You might find it helpful to uncomment the line below:
        List<Node> nodes = this.getNodes();
        for (Node singleNode : nodes) {
            if (singleNode.name() != null) {
                String cleanName = cleanString(singleNode.name());
                myTrieSet.add(cleanName);
                if (!neighbor.containsKey(cleanName)) {
                    ArrayList<Node> near = new ArrayList<>(){{add(singleNode);}};
                    neighbor.put(cleanName, near);
                } else {
                    neighbor.get(cleanName).add(singleNode);
                }
            }
            if (!this.neighbors(singleNode.id()).isEmpty()) {
                Point point = new Point(singleNode.lon(), singleNode.lat());
                pointsInMap.add(point);
                convertNodeToPoint.put(point, singleNode);
            }
        }
    }

    /**
     * For Project Part II
     * Returns the vertex closest to the given longitude and latitude.
     * @param lon The target longitude.
     * @param lat The target latitude.
     * @return The id of the node in the graph closest to the target.
     */
    public long closest(double lon, double lat) {
        WeirdPointSet pointSet = new WeirdPointSet(pointsInMap);
        Point result = pointSet.nearest(lon, lat);
        return convertNodeToPoint.get(result).id();
    }


    /**
     * For Project Part III (extra credit)
     * In linear time, collect all the names of OSM locations that prefix-match the query string.
     * @param prefix Prefix string to be searched for. Could be any case, with our without
     *               punctuation.
     * @return A <code>List</code> of the full names of locations whose cleaned name matches the
     * cleaned <code>prefix</code>.
     */
    public List<String> getLocationsByPrefix(String prefix) {
        //return new LinkedList<>();
        List<String> result = new ArrayList<>();
        List<String> locationNodes = myTrieSet.keysWithPrefix(prefix);
        for (String sub : locationNodes) {
            ArrayList<Node> get = neighbor.get(sub);
            if (!get.isEmpty()) {
                for (Node node : get) {
                    result.add(node.name());
                }
            }
        }
        return result;
    }

    /**
     * For Project Part III (extra credit)
     * Collect all locations that match a cleaned <code>locationName</code>, and return
     * information about each node that matches.
     * @param locationName A full name of a location searched for.
     * @return A list of locations whose cleaned name matches the
     * cleaned <code>locationName</code>, and each location is a map of parameters for the Json
     * response as specified: <br>
     * "lat" -> Number, The latitude of the node. <br>
     * "lon" -> Number, The longitude of the node. <br>
     * "name" -> String, The actual name of the node. <br>
     * "id" -> Number, The id of the node. <br>
     */
    public List<Map<String, Object>> getLocations(String locationName) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (neighbor.containsKey(locationName)) {
            for (Node node : neighbor.get(locationName)) {
                Map<String, Object> sub = new HashMap<>();
                sub.put("lat", node.lat());
                sub.put("lon", node.lon());
                sub.put("name", node.name());
                sub.put("id", node.id());
                result.add(sub);
            }
        }
        return result;
    }


    /**
     * Useful for Part III. Do not modify.
     * Helper to process strings into their "cleaned" form, ignoring punctuation and capitalization.
     * @param s Input string.
     * @return Cleaned string.
     */
    private static String cleanString(String s) {
        return s.replaceAll("[^a-zA-Z ]", "").toLowerCase();
    }

    public class MyTrieSet {
        private Node root = new Node();

        public void clear() {
            root = new Node();
        }

        public boolean contains(String key) {
            return containsHelper(root, key);
        }

        public boolean containsHelper(Node node, String key) {
            //key = key.toLowerCase();
            char[] charCheck = key.toCharArray();
            for (int i = 0; i < charCheck.length; i++) {
                if (node.children[charCheck[i]] == null) {
                    return false;
                }
                node = node.children[charCheck[i]];
                if ((i == charCheck.length - 1) && node.isLeaf) {
                    return true;
                }
            }
            return false;
        }

        public void add(String key) {
            if (key == null) {
                throw new IllegalArgumentException();
            }
            addHelper(root, key);
        }

        public void addHelper(Node node, String key) {
            //key = key.toLowerCase();
            char[] wordArray = key.toCharArray();
            for (int i = 0; i < wordArray.length; i++) {
                if (node.children[wordArray[i]] == null) {
                    node.children[wordArray[i]] = new Node();
                }
                node.children[wordArray[i]].prefixNum++;
                if (i == wordArray.length - 1) {
                    node.children[wordArray[i]].isLeaf = true;
                    node.children[wordArray[i]].countNum++;
                }
                node = node.children[wordArray[i]];
            }
        }

        public List<String> keysWithPrefix(String prefix) {
            //List<String> result = new ArrayList<>();
            return keysWithPrefixHelper(root, prefix);
        }

        public List<String> keysWithPrefixHelper(Node node, String prefix) {
            char[] prefixCheck = prefix.toLowerCase().toCharArray();
            for (int i = 0; i < prefixCheck.length; i++) {
                //int index = prefixCheck[i] - 'a';
                if (node.children[prefixCheck[i]] == null) {
                    return null;
                }
                node = node.children[prefixCheck[i]];
            }
            return traversalPrefix(node, prefix);
        }

        public List<String> traversalPrefix(Node node, String prefix) {
            List<String> result = new ArrayList<>();
            if (node != null) {
                if (node.isLeaf) {
                    result.add(prefix);
                }
                for (int i = 0; i < node.children.length; i++) {
                    if (node.children[i] != null) {
                        char curr = (char) i;
                        String temp = prefix + curr;
                        result.addAll(traversalPrefix(node.children[i], temp));
                    }
                }
            }
            return result;
        }

        public String longestPrefixOf(String key) {
            throw new UnsupportedOperationException();
        }

        private class Node {
            private static final int R = 256;
            private int prefixNum = 0;
            private int countNum = 0;
            private Node[] children = new Node[R];
            private boolean isLeaf;

            public Node() {
            }
        }
    }
}
