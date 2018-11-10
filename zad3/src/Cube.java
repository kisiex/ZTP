import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "servlet131726")
public class Cube extends HttpServlet {

    private Logger logger = Logger.getLogger("Cube");
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        manageRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        manageRequest(request, response);
    }

    private void manageRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        logger.setLevel(Level.FINE);
        logger.fine("aaaa");
        logger.finer("aa41241414a");

        float edgeLength = Float.parseFloat(request.getParameter("a"));
        float density = Float.parseFloat(request.getParameter("b"));
        String databaseConnectionString = request.getParameter("db");

        DataLoader dataLoader = new DataLoader(databaseConnectionString);
        List<Sphere> spheres;
        try {
            spheres = dataLoader.loadData();
        } catch (SQLException e) {
            throw new ServletException("Cannot connect to database using given connection string, cause: " + e.getMessage());
        }

        double mass = new CubeMassCalculator(edgeLength, density).calcMass(spheres);

        ServletOutputStream out = response.getOutputStream();
        out.print(String.format("%.3f", mass));
    }

    private class Sphere {

        private int id;
        private float x0;
        private float y0;
        private float z0;
        private float radius;

        Sphere(ResultSet dataBaseSphere) {
            try {
                this.id = dataBaseSphere.getInt("id");
                this.x0 = dataBaseSphere.getFloat("x0");
                this.y0 = dataBaseSphere.getFloat("y0");
                this.z0 = dataBaseSphere.getFloat("z0");
                this.radius = dataBaseSphere.getFloat("r");
            } catch (SQLException e) {
                throw new RuntimeException("Cannot parse data from database, cause: " + e.getMessage());
            }
        }

        double getVolume() {
            return 4f / 3f * Math.PI * Math.pow(radius, 3);
        }

        Map<String, Float> generatePointInside() {
            Map<String, Float> point = new HashMap<>();
            Random random = new Random();
            point.put("x", (random.nextFloat() % 2 * radius) - radius + x0);
            point.put("y", (random.nextFloat() % 2 * radius) - radius + y0);
            point.put("z", (random.nextFloat() % 2 * radius) - radius + z0);

            return point;
        }
    }

    private class DataLoader {

        private static final String QUERY = "select id, x0, y0, p0, r from Rtable";
        private String database;
        private List<Sphere> spheres;

        DataLoader(String database) {
            this.database = database;
            spheres = new ArrayList<>();
        }

        List<Sphere> loadData() throws SQLException {
            Connection conn = DriverManager.getConnection(database);
            Statement qr = conn.createStatement();
            ResultSet dataBaseSpheres = qr.executeQuery(QUERY);

            for (int i = 1; true; i++) {
                if (!dataBaseSpheres.next()) {
                    break;
                }
                spheres.add(new Sphere(dataBaseSpheres));
            }
            conn.close();
            return spheres;
        }

    }

    private class CubeMassCalculator {

        private static final int NUMBER_OF_POINTS = 10000;

        private float edgeLength;
        private float density;

        CubeMassCalculator(float edgeLength, float cubeDensity) {
            this.edgeLength = edgeLength;
            this.density = cubeDensity;
        }

        private double getOriginVolume() {
            return Math.pow(edgeLength, 3d);
        }

        double calcMass(List<Sphere> spheres) {
            AtomicReference<Double> originVolume = new AtomicReference<>(getOriginVolume());
            spheres.forEach(sphere -> originVolume.updateAndGet(v -> (v - calcCommonVolume(sphere))));

            return originVolume.get() * density;
        }

        private Double calcCommonVolume(Sphere sphere) {
            int counter = 0;
            for (int i = 0; i < NUMBER_OF_POINTS; i++) {
                Map<String, Float> point = sphere.generatePointInside();
                if (isPointInsideCube(point)) {
                    counter++;
                }
            }
            return sphere.getVolume() * counter / NUMBER_OF_POINTS;
        }

        private boolean isPointInsideCube(Map<String, Float> point) {
            return point.get("x") <= edgeLength &&
                    point.get("y") <= edgeLength &&
                    point.get("z") <= edgeLength;
        }
    }

}