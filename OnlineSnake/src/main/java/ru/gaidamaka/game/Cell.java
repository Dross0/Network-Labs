package ru.gaidamaka.game;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Cell {
    private final Point point;
    private CellType type;


    public Cell(int x, int y, @NotNull CellType type){
        this.point = new Point(x, y);
        this.type = Objects.requireNonNull(type, "Cell type cant be null");
    }

    public Cell(int x, int y){
        this(x, y, CellType.EMPTY);
    }

    public Cell(@NotNull Cell cell){
        this(cell.getX(), cell.getY(), cell.getType());
    }


    @NotNull
    public CellType getType() {
        return type;
    }

    public int getX() {
        return point.getX();
    }

    public int getY() {
        return point.getY();
    }

    public Point asPoint(){
        return point;
    }

    public void setType(@NotNull CellType type){
        this.type = Objects.requireNonNull(type, "Cell type cant be null");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cell cell = (Cell) o;
        return point.equals(cell.point) &&
                type == cell.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(point, type);
    }
}
