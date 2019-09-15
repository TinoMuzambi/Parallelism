/**
 * Tino Muzambi
 * 2019/08/23 21:21
 * Representation of a wind vector
 */
public class Vector {

    double x, y;

    Vector() {
    }

    Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "Vector{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
