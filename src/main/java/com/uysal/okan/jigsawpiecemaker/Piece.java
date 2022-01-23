package com.uysal.okan.jigsawpiecemaker;

import static java.awt.event.MouseEvent.*;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

public abstract class Piece extends JComponent{

    private final int tileX;
    private final int tileY;
    
    private Direction dir;
    
    private final Set<Piece> neighbours;
    private final Set<Piece> connected;
   
    
    private boolean selected;

    private int pressedX;
    private int pressedY;
    
    transient int id;

    protected Piece(int x, int y) {
        this(x, y, Direction.NORTH);
    }
    
    protected Piece(int x, int y, Direction d) {
        if (d == null)
            throw new IllegalArgumentException("null d");
        
        tileX = x;
        tileY = y;
        dir = d;
        
        neighbours = new HashSet<Piece>();
        connected = new HashSet<Piece>();
    }
    
    void setDir(Direction dir) {
        this.dir = dir;
    }
    
    public Direction getDir() {
        return dir;
    }
    
    public Point getTileOffset() {
        int x;
        int y;
        switch (dir) {
            case NORTH: x = tileX; y = tileY; break;
            case EAST: x = tileY; y = -tileX; break;
            case SOUTH: x = -tileX; y = -tileY; break;
            case WEST: x = -tileY; y = tileX; break;
            default: throw new RuntimeException("dir: " + dir);
        }
        return new Point(getX() - x, getY() - y);
    }
    
    public void addNeighbour(Piece p) {
        neighbours.add(p);
        p.neighbours.add(this);
    }
    
    public Set<Piece> getNeighbours() {
        return new HashSet<Piece>(neighbours);
    }
    
    public void connect(Piece piece) {
        connected.add(piece);
        piece.connected.add(this);
//        for (Piece mine : getGroup()) {
//            for (Piece other : piece.getGroup()) {
//                if (mine == other)
//                    continue;
//                for (Piece neighbour : mine.getNeighbours()) {
//                    if (other == neighbour) {
//                        connected.add(other);
//                        other.connected.add(this);
//                    }
//                }
//            }
//        }
    }
    
    public void connectOLD(Piece p) {
        if (connected.contains(p)) {
            return;
        }
        int size;
        size = this.connected.size();
        Piece[] thisGroup = this.connected.toArray(new Piece[size+1]);
        thisGroup[size] = this;
        size = p.connected.size();
        Piece[] otherGroup = p.connected.toArray(new Piece[size+1]);
        otherGroup[size] = p;
        for (Piece mine : thisGroup) {
            for (Piece other : otherGroup) {
                mine.connected.add(other);
                other.connected.add(mine);
            }
        }
    }
    
    public void disconnect(Piece p) {
        this.connected.remove(p);
        p.connected.remove(this);
    }
    
    public Set<Piece> getConnected() {
        return new HashSet<Piece>(connected);
    }
    
    public Set<Piece> getGroup() {
        HashSet<Piece> done = new HashSet<Piece>();
        List<Piece> open = new ArrayList<Piece>();
        open.add(this);
        while (!open.isEmpty()) {
            Piece piece = open.remove(0);
            done.add(piece);
            for (Piece c : piece.getConnected()) {
                if (!done.contains(c) && !open.contains(c)) {
                    open.add(c);
                }
            }
        }
        return done;
    }
    
    public void unselect( ) {
        selected = false;
    }
    
    public void select() {
        selected = true;
    }
    public boolean isSelected() {
        return selected;
    }
    
    @Override
    protected abstract void paintComponent(Graphics g);
    
    @Override
    public abstract boolean contains(int x, int y);
    

    private void rotate(boolean clockwise) {
        if (clockwise) {
            dir = dir.getNext();
        } else {
            dir = dir.getPrev();
        }
        Point myOffset = getTileOffset();
        for (Piece piece : getGroup() ) {
            if (piece != this) {
                piece.dir = dir;
                Point offset = piece.getTileOffset();
                int dx = offset.x - myOffset.x;
                int dy = offset.y - myOffset.y;
                piece.setLocation(piece.getX()-dx, piece.getY()-dy);
            }
            piece.repaint();
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        Piece other = (Piece) obj;
        return other.tileX == this.tileX && other.tileY == this.tileY;
    }
    
    @Override
    public int hashCode() {
        return 17 * tileX + tileY;
    }
    
    @Override
    public String toString() {
        return String.format("%3d %3d %1.1s", tileX, tileY, dir);
    }
    
}
