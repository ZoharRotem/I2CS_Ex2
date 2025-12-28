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
		int[][] oldMap = _map;
		int oldW = getWidth();
		int oldH = getHeight();
		int newWidth  = (int)(oldW * sx);
		int newHeight = (int)(oldH * sy);
		int[][] newMap = new int[newWidth][newHeight];
		for (int newX = 0; newX < newWidth; newX++) {
			for (int newY = 0; newY < newHeight; newY++) {
				int oldX = (int)(newX / sx);
				int oldY = (int)(newY / sy);
				newMap[newX][newY] = oldMap[oldX][oldY];
			}
		}
		_map = newMap;
	}


	@Override
	public void drawCircle(Pixel2D center, double rad, int newColor) {
		if (center == null) return;
		if (!isInside(center)) return;

		int cx = center.getX();
		int cy = center.getY();

		for (int x = 0; x < getWidth(); x++) {
			for (int y = 0; y < getHeight(); y++) {
				double dx = x - cx;
				double dy = y - cy;
				double dist = Math.sqrt(dx * dx + dy * dy);
				if (dist <= rad) {
					setPixel(x, y, newColor);
				}
			}
		}
	}


	@Override
	public void drawLine(Pixel2D p1, Pixel2D p2, int newColor) {
		if (p1 == null || p2 == null) return;
		if (!isInside(p1) || !isInside(p2)) return;

		int x1 = p1.getX(), y1 = p1.getY();
		int x2 = p2.getX(), y2 = p2.getY();

		if (x1 == x2 && y1 == y2) {
			setPixel(x1, y1, newColor);
			return;
		}

		int dx = Math.abs(x2 - x1);
		int dy = Math.abs(y2 - y1);

		if (dx >= dy) {
			if (x1 > x2) {
				drawLine(p2, p1, newColor);
				return;
			}
			double m = (double)(y2 - y1) / (double)(x2 - x1);
			for (int x = x1; x <= x2; x++) {
				double y = y1 + m * (x - x1);
				setPixel(x, (int)Math.round(y), newColor);
			}
		} else {
			if (y1 > y2) {
				drawLine(p2, p1, newColor);
				return;
			}
			double m = (double)(x2 - x1) / (double)(y2 - y1);
			for (int y = y1; y <= y2; y++) {
				double x = x1 + m * (y - y1);
				setPixel((int)Math.round(x), y, newColor);
			}
		}
	}


	@Override
	public void drawRect(Pixel2D p1, Pixel2D p2, int newColor) {
		if (p1 == null || p2 == null) return;
		if (!isInside(p1) || !isInside(p2)) return;

		int x1 = Math.min(p1.getX(), p2.getX());
		int x2 = Math.max(p1.getX(), p2.getX());
		int y1 = Math.min(p1.getY(), p2.getY());
		int y2 = Math.max(p1.getY(), p2.getY());

		for (int x = x1; x <= x2; x++) {
			for (int y = y1; y <= y2; y++) {
				setPixel(x, y, newColor);
			}
		}
	}


	@Override
	public boolean equals(Object ob) {
		boolean ans = true;
		if (this == ob) {
			ans = true;
		}
		else if (ob == null) {
			ans = false;
		}
		else if (!(ob instanceof Map2D)) {
			ans = false;
		}
		else {
			Map2D other = (Map2D) ob;
			if (!sameDimensions(other)) {
				ans = false;
			}
			else {
				int w = getWidth();
				int h = getHeight();
				for (int x = 0; x < w && ans; x++) {
					for (int y = 0; y < h && ans; y++) {
						if (this.getPixel(x, y) != other.getPixel(x, y)) {
							ans = false;
						}
					}
				}
			}
		}
		return ans;
	}

	/** 
	 * Fills this map with the new color (new_v) starting from p.
	 * https://en.wikipedia.org/wiki/Flood_fill
	 */
	@Override
	public int fill(Pixel2D p, int new_v, boolean cyclic) {
		if (p == null) return 0;
		if (!isInside(p)) return 0;

		int sx = p.getX(), sy = p.getY();
		int oldColor = getPixel(sx, sy);
		if (oldColor == new_v) return 0;

		int w = getWidth(), h = getHeight();
		boolean[][] visited = new boolean[w][h];

		int count = 0;

		int[] qx = new int[w * h];
		int[] qy = new int[w * h];
		int head = 0, tail = 0;

		qx[tail] = sx;
		qy[tail] = sy;
		tail++;
		visited[sx][sy] = true;

		while (head < tail) {
			int x = qx[head];
			int y = qy[head];
			head++;

			if (getPixel(x, y) != oldColor) continue;

			setPixel(x, y, new_v);
			count++;

			int[] nx = {x - 1, x + 1, x, x};
			int[] ny = {y, y, y - 1, y + 1};

			for (int i = 0; i < 4; i++) {
				int xx = nx[i], yy = ny[i];

				if (cyclic) {
					if (xx < 0) xx = w - 1;
					else if (xx >= w) xx = 0;
					if (yy < 0) yy = h - 1;
					else if (yy >= h) yy = 0;
				} else {
					if (xx < 0 || xx >= w || yy < 0 || yy >= h) continue;
				}

				if (!visited[xx][yy] && getPixel(xx, yy) == oldColor) {
					visited[xx][yy] = true;
					qx[tail] = xx;
					qy[tail] = yy;
					tail++;
				}
			}
		}

		return count;
	}


	/**
	 * BFS like shortest the computation based on iterative raster implementation of BFS, see:
		 * https://en.wikipedia.org/wiki/Breadth-first_search
	 */
	@Override
	public Pixel2D[] shortestPath(Pixel2D p1, Pixel2D p2, int obsColor, boolean cyclic) {
		if (p1 == null || p2 == null) return null;
		if (!isInside(p1) || !isInside(p2)) return null;

		int sx = p1.getX(), sy = p1.getY();
		int ex = p2.getX(), ey = p2.getY();

		if (getPixel(sx, sy) == obsColor || getPixel(ex, ey) == obsColor) return null;

		if (sx == ex && sy == ey) {
			return new Pixel2D[]{ new Index2D(sx, sy) };
		}

		int w = getWidth(), h = getHeight();
		int[][] tempMap = new int[w][h];

		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				tempMap[x][y] = (getPixel(x, y) == obsColor) ? -2 : -1;
			}
		}

		tempMap[sx][sy] = 0;

		int radius = 0;
		while (tempMap[ex][ey] == -1) {
			boolean progressed = spreadOneRadius(tempMap, radius, cyclic);
			if (!progressed) return null;
			radius++;
		}

		return backtrack(tempMap, sx, sy, ex, ey, cyclic);
	}

	@Override
	public Map2D allDistance(Pixel2D start, int obsColor, boolean cyclic) {
		if (start == null) return null;
		if (!isInside(start)) return null;

		int sx = start.getX(), sy = start.getY();
		if (getPixel(sx, sy) == obsColor) return null;

		int w = getWidth(), h = getHeight();
		int[][] dist = new int[w][h];

		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				dist[x][y] = -1;
			}
		}

		dist[sx][sy] = 0;

		int radius = 0;
		while (spreadOneRadiusAllDistance(dist, radius, obsColor, cyclic)) {
			radius++;
		}

		return new Map(dist);
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

	private boolean spreadOneRadius(int[][] map, int radius, boolean cyclic) {
		int w = getWidth(), h = getHeight();
		boolean changed = false;

		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				if (map[x][y] != radius) continue;

				changed |= tryMark(map, x - 1, y, radius + 1, cyclic);
				changed |= tryMark(map, x + 1, y, radius + 1, cyclic);
				changed |= tryMark(map, x, y - 1, radius + 1, cyclic);
				changed |= tryMark(map, x, y + 1, radius + 1, cyclic);
			}
		}
		return changed;
	}

	private boolean tryMark(int[][] map, int x, int y, int val, boolean cyclic) {
		int w = getWidth(), h = getHeight();

		if (cyclic) {
			if (x < 0) x = w - 1;
			else if (x >= w) x = 0;
			if (y < 0) y = h - 1;
			else if (y >= h) y = 0;
		} else {
			if (x < 0 || x >= w || y < 0 || y >= h) return false;
		}

		if (map[x][y] == -1) {
			map[x][y] = val;
			return true;
		}
		return false;
	}

	private Pixel2D[] backtrack(int[][] map, int sx, int sy, int ex, int ey, boolean cyclic) {
		int dist = map[ex][ey];
		if (dist < 0) return null;

		Pixel2D[] path = new Pixel2D[dist + 1];
		int x = ex, y = ey;

		for (int i = dist; i >= 0; i--) {
			path[i] = new Index2D(x, y);
			if (i == 0) break;

			int wanted = i - 1;

			if (hasValue(map, x - 1, y, wanted, cyclic)) {
				x = normX(x - 1, cyclic);
			} else if (hasValue(map, x + 1, y, wanted, cyclic)) {
				x = normX(x + 1, cyclic);
			} else if (hasValue(map, x, y - 1, wanted, cyclic)) {
				y = normY(y - 1, cyclic);
			} else if (hasValue(map, x, y + 1, wanted, cyclic)) {
				y = normY(y + 1, cyclic);
			} else {
				return null;
			}
		}

		if (path[0].getX() != sx || path[0].getY() != sy) return null;
		return path;
	}

	private boolean hasValue(int[][] map, int x, int y, int val, boolean cyclic) {
		int w = getWidth(), h = getHeight();

		if (cyclic) {
			if (x < 0) x = w - 1;
			else if (x >= w) x = 0;
			if (y < 0) y = h - 1;
			else if (y >= h) y = 0;
			return map[x][y] == val;
		} else {
			if (x < 0 || x >= w || y < 0 || y >= h) return false;
			return map[x][y] == val;
		}
	}

	private int normX(int x, boolean cyclic) {
		if (!cyclic) return x;
		int w = getWidth();
		if (x < 0) return w - 1;
		if (x >= w) return 0;
		return x;
	}

	private int normY(int y, boolean cyclic) {
		if (!cyclic) return y;
		int h = getHeight();
		if (y < 0) return h - 1;
		if (y >= h) return 0;
		return y;
	}

	private boolean spreadOneRadiusAllDistance(int[][] dist, int radius, int obsColor, boolean cyclic) {
		int w = getWidth(), h = getHeight();
		boolean changed = false;

		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				if (dist[x][y] != radius) continue;

				changed |= tryMarkAllDistance(dist, x - 1, y, radius + 1, obsColor, cyclic);
				changed |= tryMarkAllDistance(dist, x + 1, y, radius + 1, obsColor, cyclic);
				changed |= tryMarkAllDistance(dist, x, y - 1, radius + 1, obsColor, cyclic);
				changed |= tryMarkAllDistance(dist, x, y + 1, radius + 1, obsColor, cyclic);
			}
		}
		return changed;
	}

	private boolean tryMarkAllDistance(int[][] dist, int x, int y, int val, int obsColor, boolean cyclic) {
		int w = getWidth(), h = getHeight();

		if (cyclic) {
			if (x < 0) x = w - 1;
			else if (x >= w) x = 0;
			if (y < 0) y = h - 1;
			else if (y >= h) y = 0;
		} else {
			if (x < 0 || x >= w || y < 0 || y >= h) return false;
		}

		if (getPixel(x, y) == obsColor) return false;
		if (dist[x][y] != -1) return false;

		dist[x][y] = val;
		return true;
	}

}
