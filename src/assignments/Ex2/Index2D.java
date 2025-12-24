package assignments.Ex2;

public class Index2D implements Pixel2D {

    private int _x;
    private int _y;

    public Index2D(int w, int h) {
       _x = w ;
       _y = h;
    }

    public Index2D(Pixel2D other) {
        _x = other.getX();
        _y = other.getY();
    }

    @Override
    public int getX() {
        return _x;
    }

    @Override
    public int getY() {
        return _y;
    }

    @Override
    public double distance2D(Pixel2D p2) {
        int dx = _x - p2.getX();
        int dy = _y - p2.getY();
        return Math.sqrt(dx*dx + dy*dy);
    }

    @Override
    public String toString() {
        String ans = "(" + _x + ", " + _y + ")";
        return ans;
    }

    @Override
    public boolean equals(Object p) {
        boolean ans = false;
        if (this == p) {
            ans = true;
        } else if (p != null && p instanceof Index2D) {
            Index2D other = (Index2D) p;
            if (_x == other.getX() && _y == other.getY()) {
                ans = true;
            }
        }

        return ans;
    }

}
