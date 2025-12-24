package assignments.Ex2;
import java.io.Serializable;
/**
 * This class represents a 2D map (int[w][h]) as a "screen" or a raster matrix or maze over integers.
 * This is the main class needed to be implemented.
 *
 * @author boaz.benmoshe
 *
 */
public class Map implements Map2D, Serializable {

	private int[][] _map;

	/**
	 * Constructs a w*h 2D raster map with an init value v.
	 * @param w
	 * @param h
	 * @param v
	 */
	public Map(int w, int h, int v) {init(w, h, v);}
	/**
	 * Constructs a square map (size*size).
	 * @param size
	 */
	public Map(int size) {this(size,size, 0);}
	
	/**
	 * Constructs a map from a given 2D array.
	 * @param data
	 */
	public Map(int[][] data) {
		init(data);
	}
	@Override
	public void init(int w, int h, int v) {
		_map = new int [w][h];
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				_map[x][y] = v;
			}
		}
	}
	@Override
	public void init(int[][] arr) {
		if (!validateArray(arr)) {
			throw new RuntimeException("Illegal map array");
		}
		int w = arr.length;
		int h = arr[0].length;
		_map = new int[w][h];
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				_map[x][y] = arr[x][y];
			}
		}
	}
	@Override
	public int[][] getMap() {
		int[][] ans = null;

		return ans;
	}
	@Override
	public int getWidth() {
		return _map.length;
    }
	@Override
	public int getHeight() {
		return _map[0].length;
    }
	@Override
	public int getPixel(int x, int y) {
		if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) {
			throw new RuntimeException("Pixel coordinates out of bounds: (" + x + "," + y + ")");
		}
		return _map[x][y];
	}

	@Override
	public int getPixel(Pixel2D p) {
		if (p == null) {
			throw new RuntimeException("Pixel cannot be null");
		}

		if (!isInside(p)) {
			throw new RuntimeException("Pixel coordinates out of bounds: (" + p.getX() + "," + p.getY() + ")");
		}

		return _map[p.getX()][p.getY()];
	}

	@Override
	public void setPixel(int x, int y, int v) {
		if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) {
			throw new RuntimeException("Pixel coordinates out of bounds: (" + x + "," + y + ")");
		}
		_map[x][y] = v;
	}

	@Override
	public void setPixel(Pixel2D p, int v) {
		if (p == null) {
			throw new RuntimeException("Pixel cannot be null");
		}

		int x = p.getX();
		int y = p.getY();

		setPixel(x, y, v);
	}


	@Override
	public boolean isInside(Pixel2D p) {
		boolean ans = true;

		if (p == null) {
			ans = false;
		}
		else {
			int x = p.getX();
			int y = p.getY();

			if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) {
				ans = false;
			}
		}

		return ans;
	}



	@Override
	public boolean sameDimensions(Map2D other) {
		boolean ans = true;

		if (other == null) {
			ans = false;
		}
		else if (this.getWidth() != other.getWidth()) {
			ans = false;
		}
		else if (this.getHeight() != other.getHeight()) {
			ans = false;
		}

		return ans;
	}


	@Override
    public void addMap2D(Map2D p) {
		if (!sameDimensions(p)) {
			throw new RuntimeException("Map is not same dimensions");
		}
		for (int x = 0; x < p.getWidth(); x++) {
			for (int y = 0; y < p.getHeight(); y++) {
				this._map[x][y] += p.getPixel(x, y);
			}
		}
    }

    @Override
    public void mul(double scalar) {
		for (int x = 0; x < _map.length; x++) {
			for (int y = 0; y < _map[x].length; y++) {
				_map[x][y] = (int)(_map[x][y] * scalar);
			}
		}
    }

    @Override
    public void rescale(double sx, double sy) {

    }

    @Override
    public void drawCircle(Pixel2D center, double rad, int color) {

    }

    @Override
    public void drawLine(Pixel2D p1, Pixel2D p2, int color) {

    }

    @Override
    public void drawRect(Pixel2D p1, Pixel2D p2, int color) {

    }

    @Override
    public boolean equals(Object ob) {
        boolean ans = false;

        return ans;
    }
	@Override
	/** 
	 * Fills this map with the new color (new_v) starting from p.
	 * https://en.wikipedia.org/wiki/Flood_fill
	 */
	public int fill(Pixel2D xy, int new_v,  boolean cyclic) {
		int ans = -1;

		return ans;
	}

	@Override
	/**
	 * BFS like shortest the computation based on iterative raster implementation of BFS, see:
	 * https://en.wikipedia.org/wiki/Breadth-first_search
	 */
	public Pixel2D[] shortestPath(Pixel2D p1, Pixel2D p2, int obsColor, boolean cyclic) {
		Pixel2D[] ans = null;  // the result.

		return ans;
	}
    @Override
    public Map2D allDistance(Pixel2D start, int obsColor, boolean cyclic) {
        Map2D ans = null;  // the result.

        return ans;
    }
	////////////////////// Private Methods ///////////////////////

	private boolean validateArray(int[][] arr) {
		boolean ans = true;

		if (arr == null) {
			ans = false;
		}

		else if (arr.length == 0 || arr[0].length == 0) {
			ans = false;
		}

		else {
			int h = arr[0].length;
			for (int i = 1; i < arr.length; i++) {
				if (arr[i].length != h) {
					ans = false;
					break;
				}
			}
		}

		return ans;
	}


}
