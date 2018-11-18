package pl.jrj.data;

import javax.ejb.EJB;
import java.util.ArrayList;
import java.util.List;

public class EbClient {

    private Straight straight;
    private List<MassPoint> points = new ArrayList<>();

    @EJB
    public IDataMonitor dataMonitor;

    public static void main(String args[]) {
        System.out.println(new EbClient().calculate());
    }

    private double calculate() {
        straight = new Straight(dataMonitor.next(), dataMonitor.next(), dataMonitor.next());
        while (dataMonitor.hasNext()) {
            points.add(new MassPoint(dataMonitor.next(), dataMonitor.next(), dataMonitor.next(), dataMonitor.next()));
        }

        return points.stream().mapToDouble(point -> straight.calcDistance(point) * point.getMass()).sum();
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
            Point vectorPoints = new Point(p2.getX() - other.getX(),
                    p2.getY() - other.getY(),
                    p2.getZ() - other.getZ());
            Point vectorMul = new Point(p2.getY() * vectorPoints.getZ(),
                    p2.getZ() * vectorPoints.getX(),
                    p2.getX() * vectorPoints.getY());

            return Math.sqrt(Math.pow(vectorMul.getX(), 2) + Math.pow(vectorMul.getY(), 2) + Math.pow(vectorMul.getZ(), 2))
                    /
                    Math.sqrt(Math.pow(vectorPoints.getX(), 2) + Math.pow(vectorPoints.getY(), 2) + Math.pow(vectorPoints.getZ(), 2));
        }


    }


}
