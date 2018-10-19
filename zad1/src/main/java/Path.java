import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Path {

    private static final int FIRST_VERTEX = 1;
    private static final String QUERY = "select x, y, p from gtable2";

    private final int targetVertex;
    private final String database;

    private List<Float> costs;
    private Map<Integer, Edge> edges;
    private List<List<Edge>> paths;
    private List<Edge> visited;

    public Path(int vertex, String database) {
        this.database = database;
        this.targetVertex = vertex;
        this.edges = new HashMap<>();
        this.costs = new ArrayList<>();
        this.paths = new ArrayList<>();
        this.visited = new ArrayList<>();
    }

    public float calculatePath() throws SQLException {
        float result = 0.0f;

        loadData();
        findPaths();
        result = findTheBestPath();

        return result;
    }

    private void loadData() throws SQLException {
        Connection conn = DriverManager.getConnection(database);
        Statement qr = conn.createStatement();
        ResultSet edgesFromDatabase = qr.executeQuery(QUERY);

        for (int i = 1; true; i++) {
            if (!edgesFromDatabase.next()) {
                break;
            }
            edges.put(i, new Edge(edgesFromDatabase));
        }
        conn.close();
    }

    private void findPaths() {
        List<Edge> path = new ArrayList<>();
        processVertex(null, FIRST_VERTEX, path);
        //System.out.println("Found paths: " + paths.size());
    }

    private List<Edge> processVertex(Edge edge, int vertex, List<Edge> path) {
        //System.out.println("Current vertex: " + vertex);
        if (edge != null) {
            path.add(edge);
        }
        if (vertex == this.targetVertex) {
            return path;
        }

        List<Edge> neighbours = findNeighbours(vertex);

        //System.out.println("Current vertex has " + neighbours.size() + " neighbours");
        neighbours.forEach(i -> {
            List<Edge> somePath = processVertex(i, i.getY(), createCopy(path));
            if (somePath != null) {
                paths.add(somePath);
            }
        });

        return null;
    }

    private List<Edge> findNeighbours(int id) {
        List<Edge> edges = new ArrayList<>();

        for (Map.Entry<Integer, Edge> entry : this.edges.entrySet()) {
            Edge edge = entry.getValue();
            if (edge.getX() == id && !visited.contains(edge)) {
                edges.add(edge);
                visited.add(edge);
            }
        }
        return edges;
    }

    private List<Edge> createCopy(List<Edge> list) {
        List<Edge> copy = new ArrayList<>();
        list.forEach(it -> copy.add(it.createCopy()));

        return copy;
    }

    private float findTheBestPath() {
        float cost = Float.MAX_VALUE;
        for (List<Edge> path : paths) {
            float c = calcPathCost(path);
            if (c < cost) {
                cost = c;
            }
        }
        return cost;
    }

    private float calcPathCost(List<Edge> path) {

        float cost = 0.0f;
        float a = 0.0f;
        float b = 0.0f;
        for (Edge e : path) {
            //System.out.println(e.returnBasicInfo());
            cost += 1 / (float) e.getX();
//            if(a == 0.0f){
//               a = e.getP();
//            }
        }
        //System.out.println("Cost:" + cost);
        //System.out.println("--------------");

        return cost;
    }

    class Edge {

        private final int x;
        private final int y;
        private final float p;

        private Edge(Edge other) {
            this.x = other.getX();
            this.y = other.getY();
            this.p = other.getP();
        }

        private Edge(ResultSet resultSet) {
            try {
                this.x = resultSet.getInt("X");
                this.y = resultSet.getInt("y");
                this.p = resultSet.getFloat("p");
            } catch (SQLException e) {
                throw new RuntimeException("Cannot parse data from database, cause: " + e.getMessage());
            }
        }

        int getX() {
            return x;
        }

        int getY() {
            return y;
        }

        float getP() {
            return p;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof Edge)) {
                return false;
            }
            Edge edge = (Edge) other;
            return y == edge.y && x == edge.x && p == edge.p;
        }

        String returnBasicInfo() {
            return "[X = " + this.x +
                    ", Y = " + this.y +
                    ", P = " + this.p + "]";
        }

        Edge createCopy() {
            return new Edge(this);
        }
    }
}
