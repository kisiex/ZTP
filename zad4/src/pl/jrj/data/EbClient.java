package pl.jrj.data;

import javax.ejb.EJB;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EbClient {

    @EJB
    public IDataMonitor dataMonitor;

    public static void main(String args[]) {

        List<Double> numbers = Arrays.stream(args).map(Double::parseDouble).collect(Collectors.toList());

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

        Point(Point copy) {
            this.x = copy.getX();
            this.y = copy.getY();
            this.z = copy.getZ();
        }
    }

    private class Straight {

        private final double a;
        private final double b;
        private final double c;

        private final Point p1 = new Point(0, 0, 0);
        private final Point p2;
        private final Point vector;
        private final Point vectorOut;

        public Straight(double a, double b, double c) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.p2 = new Point(2 * b, -3 * a, 9 * a / (2 * c));
            this.vector = new Point(p2);
            this.vectorOut = new Point(p2.getY() * p2.getZ(), p2.getZ() * p2.getX(), p2.getX() * p2.getY());
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
