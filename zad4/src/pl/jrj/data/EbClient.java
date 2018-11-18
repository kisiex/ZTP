package pl.jrj.data;

import javax.ejb.EJB;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EbClient {

    private static final Level level = Level.FINER;
    private static final Logger log = Logger.getLogger("EbClient");
    private static final Handler handler = new ConsoleHandler();

    private Straight straight;
    private List<MassPoint> points = new ArrayList<>();

    @EJB
    public IDataMonitor dataMonitor;

    public static void main(String args[]) {
        log.setLevel(level);
        handler.setLevel(level);
        log.addHandler(handler);
        System.out.println(new EbClient().calculate());
    }

    private double calculate() {
        log.fine("Calculating");
        straight = new Straight(dataMonitor.next(), dataMonitor.next(), dataMonitor.next());
        log.fine("got straight");
        while (dataMonitor.hasNext()) {
            points.add(new MassPoint(dataMonitor.next(), dataMonitor.next(), dataMonitor.next(), dataMonitor.next()));
            log.fine("got point: #" + points.size());
        }

        return points.stream().mapToDouble(point -> Math.pow(straight.calcDistance(point), 2) * point.getMass()).sum();
    }

    class Point {

        private final double x;
        private final double y;
        private final double z;

        double getX() {
            return x;
        }

        double getY() {
            return y;
        }

        double getZ() {
            return z;
        }

        Point(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public String toString() {
            return "Point{" +
                    "x=" + x +
                    ", y=" + y +
                    ", z=" + z +
                    '}';
        }
    }

    class MassPoint extends Point {

        private final double mass;

        MassPoint(double x, double y, double z, double mass) {
            super(x, y, z);
            this.mass = mass;
        }

        double getMass() {
            return mass;
        }
    }

    private class Straight {

        private final double a;
        private final double b;
        private final double c;
        private final Point p2;

        Straight(double a, double b, double c) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.p2 = new Point(2 * b, -3 * a, 9 * a / (2 * c));
        }

        private double calcDistance(Point other) {
            log.fine("point: " + other.toString());
            Point vectorPoints = new Point(other.getX() - p2.getX(),
                    other.getY() - p2.getY(),
                    other.getZ() - p2.getZ());

            log.fine("VectorPoints: " + vectorPoints.toString());
            Point vectorMul = new Point(p2.getY() * vectorPoints.getZ() - p2.getZ() * vectorPoints.getY(),
                    p2.getZ() * vectorPoints.getX() - p2.getX() * vectorPoints.getZ(),
                    p2.getX() * vectorPoints.getY() - p2.getY() * vectorPoints.getX());
            log.fine("vectorMul: " + vectorMul.toString());


            double distance = Math.sqrt(Math.pow(vectorMul.getX(), 2) + Math.pow(vectorMul.getY(), 2) + Math.pow(vectorMul.getZ(), 2))
                    /
                    Math.sqrt(Math.pow(p2.getX(), 2) + Math.pow(p2.getY(), 2) + Math.pow(p2.getZ(), 2));

            log.fine("Distance: " + distance);

            return distance;
        }
    }
}
