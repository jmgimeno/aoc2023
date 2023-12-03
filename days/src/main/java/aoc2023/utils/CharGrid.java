package aoc2023.utils;

import java.util.List;

public class CharGrid {
    protected int height;
    protected final int width;
    protected final char[][] points;

    public CharGrid(List<String> data) {
        height = data.size();
        width = data.get(0).length();
        points = new char[height + 2][width + 2];
        fillPoints(data);
    }

    private void fillPoints(List<String> data) {
        for (int y = 0; y < height + 2; y++) {
            for (int x = 0; x < width + 2; x++) {
                if (x == 0 || y == 0 || x == width + 1 || y == height + 1) {
                    points[y][x] = '.';
                } else {
                    points[y][x] = data.get(y - 1).charAt(x - 1);
                }
            }
        }
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        for (int y = 0; y < height + 2; y++) {
            for (int x = 0; x < width + 2; x++) {
                sb.append(points[y][x]);
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
