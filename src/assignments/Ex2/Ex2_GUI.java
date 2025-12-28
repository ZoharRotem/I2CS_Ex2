package assignments.Ex2;

import javax.swing.*;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;

public class Ex2_GUI {

    // ===== FILE =====
    private static final String DEFAULT_MAP_FILE = "map.txt";

    // ===== MODES =====
    private static final int MODE_PIXEL  = 0;
    private static final int MODE_LINE   = 1;
    private static final int MODE_RECT   = 2;
    private static final int MODE_CIRCLE = 3;
    private static final int MODE_FILL   = 4;
    private static final int MODE_PATH   = 5;

    // ===== COLORS (VALUES STORED IN MAP) =====
    private static final int C_BLACK  = -1;
    private static final int C_WHITE  = 0;
    private static final int C_GRAY   = 1;
    private static final int C_RED    = 2;
    private static final int C_GREEN  = 3;
    private static final int C_BLUE   = 4;
    private static final int C_YELLOW = 5;

    // ===== STATE =====
    private static int currentMode = MODE_PIXEL;
    private static int currentColor = C_GRAY;

    private static Pixel2D firstClick = null;
    private static Map2D map = null;

    private static boolean guiInited = false;
    private static int lastW = -1, lastH = -1;

    private static boolean nativeMenusAdded = false;
    private static boolean fileMenuItemsAdded = false;

    // ================== PUBLIC API ==================

    public static void drawMap(Map2D m) {
        if (m == null) return;

        initGUIIfNeeded(m);

        int w = m.getWidth();
        int h = m.getHeight();

        StdDraw.clear();
        StdDraw.enableDoubleBuffering();
        StdDraw.setPenRadius(0.0);

        // --- cells ---
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int v = m.getPixel(x, y);
                setPenColorByValue(v);
                StdDraw.filledSquare(x + 0.5, h - y - 0.5, 0.5);
            }
        }

        // --- grid ---
        StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
        StdDraw.setPenRadius(0.001);
        for (int x = 0; x <= w; x++) StdDraw.line(x, 0, x, h);
        for (int y = 0; y <= h; y++) StdDraw.line(0, y, w, y);
        StdDraw.setPenRadius(0.0);

        StdDraw.show();
    }

    public static Map2D loadMap(String mapFileName) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(mapFileName))) {
            return (Map2D) in.readObject();
        } catch (Exception e) {
            System.out.println("Failed to load map: " + e.getMessage());
            return null;
        }
    }

    public static void saveMap(Map2D m, String mapFileName) {
        if (m == null) return;

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(mapFileName))) {
            out.writeObject(m);
        } catch (Exception e) {
            System.out.println("Failed to save map: " + e.getMessage());
        }
    }

    // ================== MAIN ==================

    public static void main(String[] a) {
        map = loadMap(DEFAULT_MAP_FILE);

        if (map == null) {
            int[][] data = {
                    { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                    { 0,-1,-1,-1, 0, 0, 0, 0, 0, 0 },
                    { 0, 0, 0,-1, 0, 0, 0, 0, 0, 0 },
                    { 0, 0, 0,-1, 0, 0, 0, 0, 0, 0 },
                    { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                    { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }
            };
            map = new Map(data); // if your constructor differs, change only this line
            saveMap(map, DEFAULT_MAP_FILE);
        }

        drawMap(map);
        setupNativeMenusOnce();

        while (true) {
            handleMouse();
            StdDraw.pause(15);
        }
    }

    // ================== INPUT (MOUSE) ==================

    private static void handleMouse() {
        if (map == null) return;
        if (!StdDraw.isMousePressed()) return;

        int h = map.getHeight();

        int x = (int) StdDraw.mouseX();
        int y = (int) StdDraw.mouseY();
        y = h - 1 - y;

        Pixel2D p = new Index2D(x, y);
        if (!map.isInside(p)) {
            waitMouseRelease();
            return;
        }

        if (currentMode == MODE_PIXEL) {
            map.setPixel(p, currentColor);
        } else if (currentMode == MODE_FILL) {
            map.fill(p, currentColor, false);
        } else {
            if (firstClick == null) firstClick = p;
            else {
                applyTwoClickMode(firstClick, p);
                firstClick = null;
            }
        }

        drawMap(map);
        waitMouseRelease();
    }

    private static void applyTwoClickMode(Pixel2D p1, Pixel2D p2) {
        switch (currentMode) {
            case MODE_LINE:
                map.drawLine(p1, p2, currentColor);
                break;

            case MODE_RECT:
                map.drawRect(p1, p2, currentColor);
                break;

            case MODE_CIRCLE:
                map.drawCircle(p1, p1.distance2D(p2), currentColor);
                break;

            case MODE_PATH:
                applyShortestPathNoOverride(p1, p2);
                break;
        }
    }

    // Path should NOT override existing colors:
    // - only WHITE is walkable
    // - everything else is an obstacle
    private static void applyShortestPathNoOverride(Pixel2D p1, Pixel2D p2) {
        int w = map.getWidth();
        int h = map.getHeight();

        // temp map: WHITE walkable, everything else obstacle (BLACK)
        Map2D temp = new Map(w, h, C_WHITE); // if your ctor differs, change only this line

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int v = map.getPixel(x, y);
                temp.setPixel(x, y, (v == C_WHITE) ? C_WHITE : C_BLACK);
            }
        }

        // allow endpoints even if they were colored
        temp.setPixel(p1.getX(), p1.getY(), C_WHITE);
        temp.setPixel(p2.getX(), p2.getY(), C_WHITE);

        Pixel2D[] path = temp.shortestPath(p1, p2, C_BLACK, false);

        if (path != null) {
            for (Pixel2D q : path) {
                if (q.equals(p1) || q.equals(p2) || map.getPixel(q) == C_WHITE) {
                    map.setPixel(q, currentColor);
                }
            }
        }
    }

    // ================== NATIVE MENUS (TOP BAR) ==================

    private static void setupNativeMenusOnce() {
        if (nativeMenusAdded) return;
        nativeMenusAdded = true;

        try {
            JFrame frame = (JFrame) getStaticStdDrawField("frame", "FRAME");
            if (frame == null) {
                System.out.println("StdDraw frame not found (field name differs).");
                return;
            }

            JMenuBar bar = frame.getJMenuBar();
            if (bar == null) {
                bar = new JMenuBar();
                frame.setJMenuBar(bar);
            }

            addMenuIfMissing(bar, buildColorMenu(), "Color");
            addMenuIfMissing(bar, buildToolsMenu(), "Tools");
            addSaveLoadToExistingFileMenu(bar);

            frame.revalidate();
            frame.repaint();

        } catch (Exception e) {
            System.out.println("Failed to add native menus: " + e.getMessage());
        }
    }

    private static void addMenuIfMissing(JMenuBar bar, JMenu menu, String title) {
        if (findMenu(bar, title) == null) {
            bar.add(menu);
        }
    }

    private static JMenu buildColorMenu() {
        JMenu colorMenu = new JMenu("Color");
        addColorItem(colorMenu, "Yellow", C_YELLOW);
        addColorItem(colorMenu, "Red",    C_RED);
        addColorItem(colorMenu, "Green",  C_GREEN);
        addColorItem(colorMenu, "Blue",   C_BLUE);
        addColorItem(colorMenu, "White",  C_WHITE);
        addColorItem(colorMenu, "Black",  C_BLACK);
        addColorItem(colorMenu, "Gray",   C_GRAY);
        return colorMenu;
    }

    private static JMenu buildToolsMenu() {
        JMenu toolsMenu = new JMenu("Tools");
        addModeItem(toolsMenu, "Pixel", MODE_PIXEL);
        addModeItem(toolsMenu, "Fill", MODE_FILL);
        addModeItem(toolsMenu, "Line", MODE_LINE);
        addModeItem(toolsMenu, "Rect", MODE_RECT);
        addModeItem(toolsMenu, "Circle", MODE_CIRCLE);
        addModeItem(toolsMenu, "ShortestPath", MODE_PATH);
        return toolsMenu;
    }

    private static void addSaveLoadToExistingFileMenu(JMenuBar bar) {
        if (fileMenuItemsAdded) return;

        JMenu fileMenu = findMenu(bar, "File");
        if (fileMenu == null) return;

        // Avoid duplicates if StdDraw already has similar items
        if (hasMenuItem(fileMenu, "Save (map.txt)") || hasMenuItem(fileMenu, "Load (map.txt)")) {
            fileMenuItemsAdded = true;
            return;
        }

        fileMenu.addSeparator();

        JMenuItem save = new JMenuItem("Save (map.txt)");
        save.addActionListener(e -> saveMap(map, DEFAULT_MAP_FILE));

        JMenuItem load = new JMenuItem("Load (map.txt)");
        load.addActionListener(e -> {
            Map2D loaded = loadMap(DEFAULT_MAP_FILE);
            if (loaded != null) {
                map = loaded;
                firstClick = null;
                drawMap(map);
            }
        });

        fileMenu.add(save);
        fileMenu.add(load);

        fileMenuItemsAdded = true;
    }

    private static boolean hasMenuItem(JMenu menu, String text) {
        for (int i = 0; i < menu.getItemCount(); i++) {
            JMenuItem it = menu.getItem(i);
            if (it != null && text.equals(it.getText())) return true;
        }
        return false;
    }

    private static void addColorItem(JMenu menu, String name, int colorVal) {
        JMenuItem item = new JMenuItem(name);
        item.addActionListener(e -> {
            currentColor = colorVal;
            firstClick = null;
            drawMap(map);
        });
        menu.add(item);
    }

    private static void addModeItem(JMenu menu, String name, int mode) {
        JMenuItem item = new JMenuItem(name);
        item.addActionListener(e -> {
            currentMode = mode;
            firstClick = null;
            drawMap(map);
        });
        menu.add(item);
    }

    private static JMenu findMenu(JMenuBar bar, String title) {
        for (int i = 0; i < bar.getMenuCount(); i++) {
            JMenu m = bar.getMenu(i);
            if (m != null && title.equals(m.getText())) return m;
        }
        return null;
    }

    private static Object getStaticStdDrawField(String... names) throws Exception {
        Class<?> c = StdDraw.class;
        for (String n : names) {
            try {
                Field f = c.getDeclaredField(n);
                f.setAccessible(true);
                return f.get(null);
            } catch (NoSuchFieldException ignored) {}
        }
        return null;
    }

    // ================== GUI INIT ==================

    private static void initGUIIfNeeded(Map2D m) {
        int w = m.getWidth();
        int h = m.getHeight();

        if (!guiInited || w != lastW || h != lastH) {
            guiInited = true;
            lastW = w;
            lastH = h;

            StdDraw.setCanvasSize(900, 700);
            StdDraw.setXscale(0, w);
            StdDraw.setYscale(0, h);
            StdDraw.enableDoubleBuffering();
            StdDraw.clear();
        }
    }

    // ================== HELPERS ==================

    private static void setPenColorByValue(int v) {
        if (v == C_BLACK)  { StdDraw.setPenColor(StdDraw.BLACK); return; }
        if (v == C_WHITE)  { StdDraw.setPenColor(StdDraw.WHITE); return; }
        if (v == C_GRAY)   { StdDraw.setPenColor(StdDraw.GRAY); return; }
        if (v == C_RED)    { StdDraw.setPenColor(StdDraw.RED); return; }
        if (v == C_GREEN)  { StdDraw.setPenColor(StdDraw.GREEN); return; }
        if (v == C_BLUE)   { StdDraw.setPenColor(StdDraw.BLUE); return; }
        if (v == C_YELLOW) { StdDraw.setPenColor(StdDraw.YELLOW); return; }

        if (v < 0) StdDraw.setPenColor(StdDraw.BLACK);
        else if (v == 0) StdDraw.setPenColor(StdDraw.WHITE);
        else StdDraw.setPenColor(StdDraw.GRAY);
    }

    private static void waitMouseRelease() {
        while (StdDraw.isMousePressed()) StdDraw.pause(10);
    }
}
