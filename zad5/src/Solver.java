import pl.jrj.dsm.IBlockRemote;
import pl.jrj.dsm.IDSManagerRemote;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Adam Kisielewski
 * @version 0.1-SNAPSHOT
 */

public class Solver extends HttpServlet {

    private String connectionString;
    private IBlockRemote blockRemote;
    private IDSManagerRemote idsManagerRemote;

    /**
     * @param req  instance of HttpServletRequest
     * @param resp instance of HttpServletResponse
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletOutputStream out = resp.getOutputStream();
        out.print(String.format("%.5f", solve(req.getParameter("t"))));
    }

    /**
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletOutputStream out = resp.getOutputStream();
        out.print(String.format("%.5f", solve(req.getParameter("t"))));
    }

    /**
     * @param table
     * @return
     */
    private double solve(String table) {
        double res = 0d;
        try {
            connect();
            List<Point> points = generateListFromDatebase(table);
            res = blockRemote.calcVolume(points);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * @throws NamingException
     */
    private void connect() throws NamingException {
        InitialContext context = new InitialContext();
        this.idsManagerRemote = (IDSManagerRemote) context.lookup("java:global/ejb-project/DSManager!pl.jrj.dsm.IDSManagerRemote");
        this.connectionString = idsManagerRemote.getDS();
        this.blockRemote = (IBlockRemote) context.lookup("java:global/ejb-project/Block!pl.jrj.dsm.IBlockRemote");
    }

    /**
     * @param table name of table in database
     * @return List of points from database for given table
     * @throws SQLException
     */
    private List<Point> generateListFromDatebase(String table) throws SQLException {
        Connection conn = DriverManager.getConnection(connectionString);
        Statement qr = conn.createStatement();
        ResultSet pointsFromDatabase = qr.executeQuery("select * from " + table);

        List<Point> points = new ArrayList<>();
        for (int i = 1; true; i++) {
            if (!pointsFromDatabase.next()) {
                break;
            }
            points.add(new Point(pointsFromDatabase));
        }
        conn.close();
        return points;
    }


    /**
     * Represents point in 3D and entity in database
     */
    public class Point implements Serializable {
        private final double x;
        private final double y;
        private final double z;

        Point(ResultSet resultSet) throws SQLException {
            this.x = resultSet.getInt("X");
            this.y = resultSet.getInt("y");
            this.z = resultSet.getFloat("Z");
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public double getZ() {
            return z;
        }
    }
}
