package bearmaps.utils.graph;

import bearmaps.utils.pq.MinHeap;
import bearmaps.utils.pq.MinHeapPQ;
import bearmaps.utils.ps.WeirdPointSet;
import edu.princeton.cs.algs4.Stopwatch;

import java.util.*;

public class AStarSolver<Vertex> implements ShortestPathsSolver<Vertex> {
    private SolverOutcome outcome;
    private double solutionWeight;
    private List<Vertex> solution = new ArrayList<>();
    private double timeSpent;
    private int numStatesExplored;

    public AStarSolver(AStarGraph<Vertex> input, Vertex start, Vertex end, double timeout) {
        if (start == null) {
            throw new IllegalArgumentException("Arguments cannot be null");
        }
        if (input.neighbors(start).size() == 0) {
            throw new IllegalArgumentException("Vertex not in graph");
        }
        Stopwatch sw = new Stopwatch();
        Set<Vertex> hasVisited = new HashSet<>(){{add(start);}};
        MinHeapPQ<Vertex> pq = new MinHeapPQ<>();
        HashMap<Vertex, Double> disTo = new HashMap<>();
        HashMap<Vertex, Vertex> edgeTo = new HashMap<>();
        pq.insert(start, input.estimatedDistanceToGoal(start, end));
        disTo.put(start, 0.0);
        while (!pq.isEmpty()) {
            Vertex node = pq.poll();
            hasVisited.add(node);
            if (sw.elapsedTime() >= timeout) {
                outcome = SolverOutcome.TIMEOUT;
                timeSpent = sw.elapsedTime();
                return;
            }
            if (node.equals(end)) {
                outcome = SolverOutcome.SOLVED;
                timeSpent = sw.elapsedTime();
                LinkedList<Vertex> returnPath = new LinkedList<>();
                for (Vertex curr = end; !curr.equals(start); curr = edgeTo.get(curr)) {
                    returnPath.add(curr);
                }
                returnPath.add(start);
                for (int i = returnPath.size() - 1; i >= 0; i-- ) {
                    solution.add(returnPath.get(i));

                }
                return;
            }
            numStatesExplored++;
            for (WeightedEdge edge : input.neighbors(node)) {
                if(!hasVisited.contains(edge.to())) {
                    Vertex from = (Vertex) edge.from();
                    Vertex to = (Vertex) edge.to();
                    //edgeTo.put(from, to);
                    //disTo.put(to,edge.weight());
                    if (!disTo.containsKey(to)
                            || (disTo.get(from) + edge.weight()) < disTo.get(to)) {
                        disTo.put(to, disTo.get(from) + edge.weight());
                        edgeTo.put(to, from);
                        double newProprity = disTo.get(to) + input.estimatedDistanceToGoal(to, end);
                        if (pq.contains(to)) {
                            pq.changePriority(to, newProprity);
                        } else {
                            pq.insert(to, newProprity);
                        }
                    }
                }
            }
        }
        outcome = SolverOutcome.UNSOLVABLE;
        timeSpent = sw.elapsedTime();
    }

//    public class VertexNode implements Comparator<VertexNode> {
//        private Vertex vertex;
//        private double distance;
//        private List<Vertex> path;
//
//        public List<Vertex> getPath() {
//            return path;
//        }
//        public VertexNode() {}
//        public VertexNode(Vertex vertex, double dist, List<Vertex> path) {
//            this.distance = dist;
//            this.vertex = vertex;
//            this.path = path;
//        }
//        @Override
//        public int compare(VertexNode v1, VertexNode v2) {
//            if (v1.distance > v2.distance) {
//                return 1;
//            }
//            if (v1.distance < v2.distance) {
//                return -1;
//            }
//            return 0;
//        }
//
//    }

    /**
     * Returns one of SolverOutcome.SOLVED, SolverOutcome.TIMEOUT, or SolverOutcome.UNSOLVABLE.
     */
    public SolverOutcome outcome() {
        return this.outcome;
    }

    public List<Vertex> solution() {
        return this.solution;
    }
    public double solutionWeight() {
        return this.solutionWeight;
    }
    /**
     * The total number of priority queue poll() operations.
     * Should be the number of states explored so far if result was TIMEOUT or UNSOLVABLE.
     */
    public int numStatesExplored() {
        return this.numStatesExplored;
    }
    public double explorationTime() {
        return this.timeSpent;
    }

}
