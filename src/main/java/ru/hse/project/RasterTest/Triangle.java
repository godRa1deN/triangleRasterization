package ru.hse.project.RasterTest;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Triangle {
    private int x1, y1, x2, y2, x3, y3;
    private double lightCoefficient = 0;
    private int[] pixelColor = new int[]{0, 0, 255};

    public double getLightCoefficient() {
        return lightCoefficient;
    }

    public void setLightCoefficient(double lightCoefficient) {
        this.lightCoefficient = lightCoefficient;
    }

    public int[] getPixelColor() {
        this.calculatePixelColor();
        return pixelColor;
    }


    public Triangle(int x1, int y1, int x2, int y2, int x3, int y3) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.x3 = x3;
        this.y3 = y3;
    }

    private void calculatePixelColor() {
        for (int i = 0; i < pixelColor.length; i++) {
            pixelColor[i] *= lightCoefficient;
        }
    }

    public List<Point> getTrianglePoints() {
        List<Point> points = new ArrayList<>();
        points.add(new Point(x1, y1));
        points.add(new Point(x2, y2));
        points.add(new Point(x3, y3));
        return points;
    }

    public void normalize() {
        if (y1 > y2) {
            int tmp = y1;
            y1 = y2;
            y2 = tmp;
            tmp = x1;
            x1 = x2;
            x2 = tmp;
        }
        if (y1 > y3) {
            int tmp = y1;
            y1 = y3;
            y3 = tmp;
            tmp = x1;
            x1 = x3;
            x3 = tmp;
        }
        if (y2 > y3) {
            int tmp = y2;
            y2 = y3;
            y3 = tmp;
            tmp = x2;
            x2 = x3;
            x3 = tmp;
        }
    }

    private static Point findSegmentsIntersection(Point p1, Point p2, Point p3, Point p4) {
        double x1 = p1.x, y1 = p1.y;
        double x2 = p2.x, y2 = p2.y;
        double x3 = p3.x, y3 = p3.y;
        double x4 = p4.x, y4 = p4.y;
        double d = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
        if (d == 0) { // отрезки параллельны
            return null;
        }
        double xi = ((x3 - x4) * (x1 * y2 - y1 * x2) - (x1 - x2) * (x3 * y4 - y3 * x4)) / d;
        double yi = ((y3 - y4) * (x1 * y2 - y1 * x2) - (y1 - y2) * (x3 * y4 - y3 * x4)) / d;
        Point intersection = new Point((int) xi, (int) yi);
        if (xi < Math.min(x1, x2) || xi > Math.max(x1, x2) ||
                xi < Math.min(x3, x4) || xi > Math.max(x3, x4) ||
                yi < Math.min(y1, y2) || yi > Math.max(y1, y2) ||
                yi < Math.min(y3, y4) || yi > Math.max(y3, y4)) { // точка пересечения находится вне отрезков
            return null;
        }
        return intersection;
    }

    public List<Point> findTriangleCanvasIntersection(int canvas_width, int canvas_height) {
        List<Point> points = new ArrayList<>();
        TriangleHelper helper = new TriangleHelper();
        Point tp1 = new Point(x1, y1);
        Point tp2 = new Point(x2, y2);
        Point tp3 = new Point(x3, y3);
        Point[] triangles_points = new Point[] {tp1, tp2, tp3};
//        for (Point i : triangles_points) {
//            System.out.println(i);
//        }
        Point cp1 = new Point(0, 0);
        Point cp2 = new Point(0, canvas_height);
        Point cp3 = new Point(canvas_width, canvas_height);
        Point cp4 = new Point(canvas_width, 0);

        if (helper.pointInTriangle(cp1, tp1, tp2, tp3)) {
            points.add(cp1);
        }
        if (helper.pointInTriangle(cp2, tp1, tp2, tp3)) {
            points.add(cp2);
        }
        if (helper.pointInTriangle(cp3, tp1, tp2, tp3)) {
            points.add(cp3);
        }
        if (helper.pointInTriangle(cp4, tp1, tp2, tp3)) {
            points.add(cp4);
        }

        Point[] canvas_points = new Point[] {cp1, cp2, cp3, cp4};
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                Point result = findSegmentsIntersection(triangles_points[i % 3], triangles_points[(i + 1) % 3], canvas_points[j % 4],
                        canvas_points[(j + 1) % 4]);
                if (result != null) {
                    points.add(result);
                }
            }
        }
        return points;
    }


    public int[][] findCoordinates() {
        int [][] a = new int[y3 - y1][3];
        int x1_cross, x2_cross;
        int i = 0;
        for (int y = y1; y < y2; y++) {
            x1_cross = x1 + (x2 - x1) * (y - y1) / (y2 - y1);           //по подобию треугольников
            x2_cross = x1 + (x3 - x1) * (y - y1) / (y3 - y1);
            if (x1_cross < x2_cross) {
                a[i][0] = x1_cross;
                a[i][1] = x2_cross;
            } else {
                a[i][0] = x2_cross;
                a[i][1] = x1_cross;
            }
            a[i][2] = y;
            i++;
        }
        for (int y = y2; y < y3; y++) {
            x1_cross = x2 + (x3 - x2) * (y - y2) / (y3 - y2);           //по подобию треугольников
            x2_cross = x1 + (x3 - x1) * (y - y1) / (y3 - y1);
            if (x1_cross < x2_cross) {
                a[i][0] = x1_cross;
                a[i][1] = x2_cross;
            } else {
                a[i][0] = x2_cross;
                a[i][1] = x1_cross;
            }
            a[i][2] = y;
            i++;
        }
        return a;
    }

    @Override
    public String toString() {
        return "(" + x1 + ", " + y1 + ") , (" + x2 + ", " + y2 + "), (" + x3 + ", " + y3 + ")";
    }
}
