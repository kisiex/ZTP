
import java.sql.SQLException;

/**
 * @author Adam Kisielewski
 * version 1.0
 */

public class Main {

    private final String database;
    private final int vertex;

    public Main(String database, int vertex) {
        this.database = database;
        this.vertex = vertex;
    }

    public static void main(String[] args) {

        String reason = validateArgs(args);
        if (!reason.isEmpty()) {
            System.err.println("Cannot run program, reason " + reason);
            System.exit(1);
        }

        Main main = new Main(args[0], Integer.parseInt(args[1]));
        main.run();
    }

    private void run() {
        Path path = new Path(vertex, database);
        float result = 0.0f;
        try {
            result = path.calculatePath();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        System.out.printf("Koszt : %.3f", result);
    }

    private static String validateArgs(String[] args) {
        String reason = "";

        if (args.length != 2) {
            reason = "Incorrect arguments. Call: java Main <connection_string> <index>";
        }
        try {
            Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            reason = e.getMessage();
        }

        return reason;
    }

}
