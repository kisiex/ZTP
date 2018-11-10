import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@WebServlet(name = "servlet131726")
public class Cube extends HttpServlet {

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
        float edgeLength = Float.parseFloat(request.getParameter("a"));
        float density = Float.parseFloat(request.getParameter("b"));
        String databaseConnectionString = request.getParameter("db");

        DataLoader dataLoader = new DataLoader(databaseConnectionString);
        try {
            dataLoader.loadData();
        } catch (SQLException e) {
            throw new ServletException("Cannot connect to database using given connection string, cause: " + e.getMessage());
        }

        double mass = new CubeMassCalculator(edgeLength, density).calcMass(dataLoader.getSpheres());

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

        protected double getVolume() {
            return 4f / 3f * Math.PI * Math.pow(radius, 3);
        }
    }

    private class DataLoader {

        private static final String QUERY = "select id, x0, y0, p0, r from Rtable";
        private String database;
        private List<Sphere> spheres;

        public DataLoader(String database) {
            this.database = database;
            spheres = new ArrayList<>();
        }

        public void loadData() throws SQLException {
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
        }

        public List<Sphere> getSpheres() {
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

        public double calcMass(List<Sphere> spheres) {
            AtomicReference<Double> originVolume = new AtomicReference<>(getOriginVolume());
            spheres.forEach(sphere -> originVolume.updateAndGet(v -> (v - calcCommonVolume(sphere))));

            return originVolume.get() * density;
        }

        private Double calcCommonVolume(Sphere sphere) {

            return 0d;
        }
    }

}