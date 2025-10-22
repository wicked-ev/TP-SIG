public class Segment {

    public double[] p1;
    public double[] p2;
    public double length;

    public Segment(double[] p1, double[] p2) {
        this.p1 = p1;
        this.p2 = p2;
        this.length = Math.sqrt(Math.pow(p1[0] - p2[0], 2) + Math.pow(p1[1] - p2[1], 2));
    }

    @Override
    public String toString() {
        return String.format("Segment[(%.2f, %.2f) → (%.2f, %.2f)], length=%.2f",
                p1[0], p1[1], p2[0], p2[1], length);
    }

}