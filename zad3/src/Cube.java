import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.*;


/**
 * @author Adam Kisielewski
 * version 1.0
 */

@WebServlet(name = "servlet131726")
public class Cube extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * @param request  HttpServletRequest which should contain parameters: a, b, db
     * @param response HttpServletResponse
     * @throws ServletException when something bad happened with servlet
     * @throws IOException      when could not get output stream
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        manageRequest(request, response);
    }

    /**
     * @param request  HttpServletRequest which should contain parameters: a, b, db
     * @param response HttpServletResponse
     * @throws ServletException when something bad happened with servlet
     * @throws IOException      when could not get output stream
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        manageRequest(request, response);
    }

    /**
     * @param request  HttpServletRequest which should contain parameters: a, b, db
     * @param response HttpServletResponse
     * @throws ServletException when something bad happened with servlet
     * @throws IOException      when could not get output stream
     */
    private void manageRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        float edgeLength = Float.parseFloat(request.getParameter("a"));
        float density = Float.parseFloat(request.getParameter("b"));
        String databaseConnectionString = request.getParameter("db");

        DataLoader dataLoader = new DataLoader(databaseConnectionString);
        List<Sphere> spheres;
        try {
            spheres = dataLoader.loadData();
        } catch (SQLException e) {
            throw new ServletException("Some error occurred: " + e.getMessage());
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

        /**
         * @return volume of this sphere
         */
        double getVolume() {
            return 4f / 3f * Math.PI * Math.pow(radius, 3);
        }

        /**
         * @return map representing point inside this sphere
         */
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

        private static final String QUERY = "select id, x0, y0, z0, r from Rtable";

        private final String database;
        private List<Sphere> spheres;

        DataLoader(String database) {
            this.database = database;
            spheres = new ArrayList<>();
        }

        /**
         * @return list of Sphere from database
         * @throws SQLException when some error occurs with database connection
         */
        List<Sphere> loadData() throws SQLException {
            Connection conn = DriverManager.getConnection(database);
            Statement qr = conn.createStatement();
            ResultSet dataBaseSpheres = qr.executeQuery(QUERY);

            while (dataBaseSpheres.next()) {
                Sphere sphere = new Sphere(dataBaseSpheres);
                spheres.add(sphere);
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

        /**
         * @return origin volume of cube
         */
        private double getOriginVolume() {
            return Math.pow(edgeLength, 3d);
        }

        /**
         * @param spheres list of elements for each is common volume with cube calculated
         * @return mass of cube
         */
        double calcMass(List<Sphere> spheres) {
            double originVolume = getOriginVolume();
            for (Sphere sphere : spheres) {
                originVolume -= calcCommonVolume(sphere);
            }

            return originVolume * density;
        }

        /**
         * @param sphere calc common volume sphere and cube
         * @return common volume
         */
        private double calcCommonVolume(Sphere sphere) {
            int counter = 0;
            for (int i = 0; i < NUMBER_OF_POINTS; i++) {
                Map<String, Float> point = sphere.generatePointInside();
                if (isPointInsideCube(point)) {
                    counter++;
                }
            }
            return sphere.getVolume() * counter / NUMBER_OF_POINTS;
        }

        /**
         * @param point inside sphere
         * @return true if point is inside cube, false otherwise
         */
        private boolean isPointInsideCube(Map<String, Float> point) {
            return point.get("x") <= edgeLength &&
                    point.get("y") <= edgeLength &&
                    point.get("z") <= edgeLength;
        }
    }
}