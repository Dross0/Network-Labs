package ru.gaidamaka.game.cell;

public class Point {
    private final int x;
    private final int y;

    public Point(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object p){
        if (this == p){
            return true;
        }
        if (!(p instanceof Point)){
            return false;
        }
        Point tmp = (Point) p;
        return x == tmp.x && y == tmp.y;
    }

    @Override
    public int hashCode(){
        int hash = 7;
        hash = 71 * hash + x;
        hash = 71 * hash + y;
        return hash;
    }
}
