# Ex2 – Map2D & GUI  
**Intro2CS – 2026A**

## 📌 Overview
This assignment implements a 2D grid map (`Map2D`) together with a full interactive GUI based on **StdDraw**.  
The project supports drawing, coloring, filling, path-finding, saving/loading maps, and includes comprehensive JUnit tests.

The solution follows a clear separation of responsibilities:
- **Map2D / Map** – data structure and algorithms
- **Ex2_GUI** – graphical interface and user interaction
- **JUnit tests** – validation of logic and robustness (non-GUI)

---

## 🗺️ Map2D – Core Functionality

The `Map2D` interface represents a 2D raster of integers.

### Basic Operations
- `getWidth()`, `getHeight()`
- `getPixel(x,y)` / `setPixel(x,y,value)`
- `isInside(Pixel2D p)`

### Drawing Algorithms
- `drawLine(p1, p2, color)`
- `drawRect(p1, p2, color)`
- `drawCircle(center, radius, color)`

### Flood Fill
- `fill(startPixel, newColor, cyclic)`
  - Supports cyclic and non-cyclic maps

### Shortest Path (BFS)
- `shortestPath(p1, p2, obstacleColor, cyclic)`
- Uses BFS to compute the shortest valid path
- Returns `null` if no path exists

---

## 🎨 Ex2_GUI – Graphical User Interface

The GUI is implemented using **StdDraw** and provides an interactive editor for `Map2D`.

### Visual Features
- Grid-based display (one cell = one square)
- Colors are rendered according to cell values
- Grid overlay for clarity

### Interaction
- **Mouse**
  - Click to draw pixels
  - Two clicks for shapes (line / rectangle / circle / shortest path)
- **Modes**
  - Pixel
  - Fill
  - Line
  - Rectangle
  - Circle
  - Shortest Path

### Menus (Native Menu Bar)
The GUI integrates with the **existing StdDraw menu bar** (not drawn inside the canvas):

- **File**
  - Save map to file (`map.txt`)
  - Load map from file
- **Color**
  - Yellow, Red, Green, Blue, White, Black, Gray
- **Tools**
  - Pixel
  - Fill
  - Line
  - Rectangle
  - Circle
  - Shortest Path

> Menu integration is done via reflection on StdDraw’s internal JFrame, so all menus appear on the same top system menu bar.

---

## 🚧 Shortest Path – Non-Override Logic

To prevent the shortest path from overwriting existing colored areas:
- Only **WHITE cells are considered walkable**
- All other colors are treated as obstacles
- A temporary `Map2D` is built internally for path computation
- The resulting path is painted **only on white cells**

This guarantees:
- No color collisions
- Correct visual behavior
- Deterministic BFS logic

---

## 💾 Saving & Loading Maps

Maps are serialized using Java Object Serialization:
- `saveMap(Map2D map, String filename)`
- `loadMap(String filename)`

The GUI automatically:
- Loads `map.txt` on startup (if exists)
- Creates a default map otherwise

---

## 🧪 Testing Strategy

### Map Tests (`MapTest`)
- Initialization
- Equality
- Pixel access
- Flood fill
- Drawing algorithms
- Shortest path (existence & blocking)
- Performance (large maps)

### GUI Tests (`Ex2_GUITest`)
GUI visuals are **not** tested (by design).  
Instead, *smoke tests* verify that:
- `drawMap` does not crash
- `saveMap` / `loadMap` work correctly
- GUI-related logic does not throw exceptions

This approach matches best practices and course expectations.

---

## 🛠️ Technologies Used
- Java
- StdDraw (Princeton)
- JUnit 5
- Swing (menu integration)

---

## ✅ Summary

This project delivers:
- A fully functional 2D map editor
- Correct algorithmic implementations
- Clean GUI integration
- Robust test coverage
- Clear separation of logic and presentation

The solution is stable, extensible, and suitable for grading in Intro2CS.

---
