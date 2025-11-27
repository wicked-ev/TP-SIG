import java.util.LinkedList;

public class Zone {
    public double id;

    public LinkedList<double[]> points = new LinkedList<>();

    public Zone(double id) {
        this.id = id;
    }

    public void addPoint(double[] point) {
        points.add(point);
    }
}
