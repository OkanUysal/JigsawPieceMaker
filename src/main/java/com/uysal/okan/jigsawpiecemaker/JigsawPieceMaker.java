package com.uysal.okan.jigsawpiecemaker;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JPanel;


public class JigsawPieceMaker extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int pieceCount;
	
	private final Size puzzleSize;
	
	private BufferedImage image = null;
	
	private Random random = new Random();
	
	public JigsawPieceMaker(File image, int pieceCount) {
		System.out.println("Working Directory = " + System.getProperty("user.dir"));
		setJigsawImage(image);
		setJigsawPieceCount(pieceCount);
		SizePanel sizePanel = new SizePanel();
		puzzleSize = new TemplateSizeImpl(this.pieceCount, new Template40());
	}
	
	public boolean setJigsawImage(File file) {
		try {
			if(file.exists()) {
				this.image = ImageIO.read(file);
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public int setJigsawPieceCount(int pieceCount) {
		if( pieceCount < 4)
			this.pieceCount = 4;
		else if(pieceCount > 5000)
			this.pieceCount = 5000;
		else
			this.pieceCount = pieceCount;
		return this.pieceCount;
	}
	
	public List<Piece> createPieces() throws IOException {
        List<Piece> result = new ArrayList<Piece>();

        final int COUNT = puzzleSize.getCount();
        final int SX = puzzleSize.getSizeX();
        final int SY = puzzleSize.getSizeY();
        final int BORDER = puzzleSize.getBorderWidth();
        final int OVER = puzzleSize.getOverlap();

        final int X;
        final int Y;
        if (image == null) {
            Y = (int) Math.sqrt(COUNT);
            X = Math.round((float)COUNT / Y);
        } else {
            int width = image.getWidth() + BORDER + BORDER;
            int height = image.getHeight() + BORDER + BORDER;
            double area = (double)(width*height)/(COUNT);
            double min = Math.sqrt(area);
            X = (int) Math.round(width/min);
            Y = (int) Math.round(height/min);
            System.out.printf("Pieces: %dx%d=%d (%dx%d)%n", X, Y, X*Y, SX, SY);
            AffineTransform scale = AffineTransform.getScaleInstance(
                (double) (X*SX-BORDER-BORDER) / image.getWidth(),
                (double) (Y*SY-BORDER-BORDER) / image.getHeight());
            AffineTransformOp op = new AffineTransformOp(scale, AffineTransformOp.TYPE_BICUBIC);
            image = op.filter(image, null);
            if (BORDER > 0) {
                width = BORDER + image.getWidth() + BORDER;
                height = BORDER + image.getHeight() + BORDER;
                BufferedImage border = new BufferedImage(width, height, image.getType());
                Graphics2D gg = border.createGraphics();
                try {
                    gg.setComposite(AlphaComposite.Src);
                    Color base = new Color(200, 150, 100);
                    gg.setColor(base);
                    gg.fill3DRect(0, 0, width, height, true);
                    if (BORDER >= 3) {
                        gg.fill3DRect(1, 1, width-2, height-2, true);
                        if (BORDER >= 5) {
                            gg.fill3DRect(2, 2, width-4, height-4, true);
                        }
                    }
                    gg.drawImage(image, BORDER, BORDER, null);
                    image = border;
                } finally {
                    gg.dispose();
                }
            }
        }
        
        puzzleSize.width(X);
        puzzleSize.height(Y);
        
        int type = 11;
        switch (type) {
            case 10:
            case 11:
            case 12:
            case 13:
            case 20:
            case 21:
            case 22:
            case 23:
            {  // maskS
                int rule = type % 10;
                Piece[][] quad = new Piece[X][Y];
                boolean[][] used = new boolean[X][Y];
                int free = X * Y;
                Piece[] list = new Piece[free];

                Polygon[][] shapes = createShapes(X, Y);
                BufferedImage[][] masks = createMasks(X, Y, shapes);

                Rectangle base = new Rectangle(OVER, OVER, SX, SY);
                for (int i = 0; i < X; i++) {
                    for (int j = 0; j < Y; j++) {
                        int x = SX * i - OVER;
                        int y = SY * j - OVER;
                        Direction dir;
                        switch (random.nextInt(4)) {
                            case 1: dir = Direction.EAST; break;
                            case 2: dir = Direction.SOUTH; break;
                            case 3: dir = Direction.WEST; break;
                            default: dir = Direction.NORTH; break;
                        }                        
                        MaskPiece piece = new MaskPiece(x, y, dir, masks[i][j], image, x, y, rule);
                        piece.setName(String.format("%dx%d", j, i));
                        piece.setBackground(Color.getHSBColor((float)i/X, 0.25f+0.75f*j/Y, 0.8f));
                        quad[i][j] = piece;
                        if (i > 0)
                            piece.addNeighbour(quad[i-1][j]);
                        if (j > 0)
                            piece.addNeighbour(quad[i][j-1]);

                        piece.setLocation(SX-OVER + i*(SX+15), SY-OVER + j*(SY+15));

                        int r = random.nextInt(free);
                        for (int i1 = 0; i1 < X && r >= 0; i1++) {
                            for (int j1 = 0; j1 < Y && r >= 0; j1++) {
                                if (! used[i1][j1]) {
                                    if (r == 0) {
                                        used[i1][j1] = true;
                                        list[j1 + i1*Y] = piece;
                                        free -= 1;
                                        int x1 = SX-OVER + i1*(SX+18) + 5*(j1%2);
                                        int y1 = SY-OVER + j1*(SY+18) + 5*(i1%2);
                                        piece.setLocation(x1, y1);
                                    }
                                    r -= 1;
                                }
                            }
                        }
                    }
                }
                result.addAll(Arrays.asList(list));
            } break;

            default:
                System.err.println("unknown type " + type);
                System.exit(-1);
        }

        if (type < 10 || type > 29) {
            for (int i = 0; i < result.size(); i++) {
                Piece piece = result.get(i);
                piece.setForeground(Color.BLACK);
                piece.setBackground(COLORS[i % COLORS.length]);
            }
        }
        return result;
    }
	
	private Polygon[][] createShapes(int X, int Y) {
        final int BASE = puzzleSize.getBaseVariation();
        System.out.println("BASE: " + BASE);
        final int SX = puzzleSize.getSizeX();
        final int SY = puzzleSize.getSizeY();
        
        Polygon[][] shapes = new Polygon[X][Y];
        int[][] dx = new int[X+1][Y+1];
        int[][] dy = new int[X+1][Y+1];
        for (int j = 1; j < Y; j++) {
            dy[0][j] = rnd(0, BASE);
        }
        for (int i = 0; i < X; i++) {
            if (i+1 < X) {
                dx[i+1][0] = rnd(0, BASE);
            }
            for (int j = 0; j < Y; j++) {
                if (i+1 < X) {
                    dx[i+1][j+1] = rnd(0, BASE);
                }
                if (j+1 < Y) {
                    dy[i+1][j+1] = rnd(0, BASE);
                }
                Polygon shape = new Polygon();
                shape.addPoint(dx[i][j], dy[i][j]);
                shape.addPoint(SX+dx[i+1][j], dy[i+1][j]);
                shape.addPoint(SX+dx[i+1][j+1], SY+dy[i+1][j+1]);
                shape.addPoint(dx[i][j+1], SY+dy[i][j+1]);
                shape.addPoint(dx[i][j], dy[i][j]);
                shapes[i][j] = shape;
            }
        }
        return shapes;
    }
	
	
	private BufferedImage[][] createMasks(int X, int Y, Polygon[][] shapes) {
        final int BASE = puzzleSize.getBaseVariation();
        final int OVER = puzzleSize.getOverlap();
        final int SX = puzzleSize.getSizeX();
        final int SY = puzzleSize.getSizeY();
        final int PEGWIDTH = puzzleSize.getPegWidth();
        final int PEGLENGTH = puzzleSize.getPegLength();
        final int PEGRADIUS = puzzleSize.getPegRadius();
        final int PEGPOSDELTA = puzzleSize.getPegPositionDelta();
        final int PEGRADIUSDELTA = puzzleSize.getPegRadiusDelta();
        final int PEGHEIGHTDELTA = puzzleSize.getPegHeightDelta();

        BufferedImage[][] masks = new BufferedImage[X][Y];
        Color fillColor = new Color(128, 128, 128, 255);
        Color transpColor = new Color(128, 128, 128, 0);

        for (int i = 0; i < X; i++) {
            for (int j = 0; j < Y; j++) {
                BufferedImage m = new BufferedImage(OVER+SX+OVER, OVER+SY+OVER, BufferedImage.TYPE_INT_ARGB);
                Graphics2D gg = m.createGraphics();
                try {
                    gg.setComposite(AlphaComposite.Src);
                    gg.translate(OVER, OVER);
                    gg.setColor(fillColor);
                    gg.fill(shapes[i][j]);
                } finally {
                    gg.dispose();
                }
                masks[i][j] = m;
            }
        }
// if (true) return masks;        

        BufferedImage tmp;

        tmp = new BufferedImage(SX, BASE+OVER, BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < X; i++) {
            for (int j = 1; j < Y; j++) {
                Graphics2D gg = tmp.createGraphics();
                try {
                    gg.setComposite(AlphaComposite.Src);
                    gg.setColor(transpColor);
                    gg.fillRect(0, 0, tmp.getWidth(), tmp.getHeight());
                    gg.setColor(fillColor);
                    int extra = random.nextInt(100);
                    if (extra < 3) {
                        int x = rnd(SX/2, 2*PEGPOSDELTA);
                        int w = rnd(PEGRADIUS+PEGRADIUS, PEGRADIUSDELTA);
                        int h = rnd(PEGRADIUS+PEGRADIUS, PEGHEIGHTDELTA);
                        gg.fillOval(x-w, BASE-h, w+w, h+h);
                    } else {
                        int x = rnd(SX/2, PEGPOSDELTA);
                        gg.fillRect(x-PEGWIDTH, 0, PEGWIDTH+PEGWIDTH, BASE+10);
                        int w = rnd(PEGRADIUS+2, PEGRADIUSDELTA);
                        int h = rnd(PEGRADIUS+PEGRADIUS, PEGHEIGHTDELTA);
                        gg.fillOval(x-w, BASE+rnd(PEGLENGTH+1,1), w+w, h);
                    }
                } finally {
                    gg.dispose();
                }
                Graphics2D g0 = masks[i][j-1].createGraphics();
                Graphics2D g1 = masks[i][j].createGraphics();
                try {
                    if (random.nextInt(100) < (20+60*((i+j)%2))) {
                        g0.setComposite(AlphaComposite.SrcOver);
                        g1.setComposite(AlphaComposite.DstOut);
                        g0.drawImage(tmp, OVER, OVER+SY-BASE, this);
                        g1.drawImage(tmp, OVER, OVER-BASE, this);
                    } else {
                        g0.setComposite(AlphaComposite.DstOut);
                        g0.scale(1.0, -1.0);
                        g1.setComposite(AlphaComposite.SrcOver);
                        g1.scale(1.0, -1.0);
                        g0.drawImage(tmp, OVER, -OVER-SY-BASE, this);
                        g1.drawImage(tmp, OVER, -OVER-BASE, this);
                    }
                } finally {
                    g1.dispose();
                    g0.dispose();
                }
            }
        }
        tmp = new BufferedImage(BASE+OVER, SY, BufferedImage.TYPE_INT_ARGB);
        for (int i = 1; i < X; i++) {
            for (int j = 0; j < Y; j++) {
                Graphics2D gg = tmp.createGraphics();
                try {
                    gg.setComposite(AlphaComposite.Src);
                    gg.setColor(transpColor);
                    gg.fillRect(0, 0, tmp.getWidth(), tmp.getHeight());
                    gg.setColor(fillColor);
                    int extra = random.nextInt(100);
                    if (extra < 3) {
                        int y = rnd(SY/2, 2*PEGPOSDELTA);
                        int w = rnd(PEGRADIUS+PEGRADIUS, PEGHEIGHTDELTA);
                        int h = rnd(PEGRADIUS+PEGRADIUS, PEGRADIUSDELTA);
                        gg.fillOval(BASE-w, y-h, w+w, h+h);
                    } else if (extra < 999) {
                        int y = rnd(SY/2, PEGPOSDELTA);
                        gg.fillRect(0, y-PEGWIDTH, BASE+10, PEGWIDTH+PEGWIDTH);
                        int w = rnd(PEGRADIUS+PEGRADIUS, PEGHEIGHTDELTA);
                        int h = rnd(PEGRADIUS+2, PEGRADIUSDELTA);
                        gg.fillOval(BASE+rnd(PEGLENGTH+1,1), y-h, w, h+h);
                    }
                } finally {
                    gg.dispose();
                }
                Graphics2D g0 = masks[i-1][j].createGraphics();
                Graphics2D g1 = masks[i][j].createGraphics();
                try {
                    if (random.nextInt(100) > (20+60*((i+j)%2))) {
                        g0.setComposite(AlphaComposite.SrcOver);
                        g1.setComposite(AlphaComposite.DstOut);
                        g0.drawImage(tmp, OVER+SX-BASE, OVER, this);
                        g1.drawImage(tmp, OVER-BASE, OVER, this);
                    } else {
                        g0.setComposite(AlphaComposite.DstOut);
                        g0.scale(-1.0, 1.0);
                        g1.setComposite(AlphaComposite.SrcOver);
                        g1.scale(-1.0, 1.0);
                        g0.drawImage(tmp, -OVER-SX-BASE, OVER, this);
                        g1.drawImage(tmp, -OVER-BASE, OVER, this);
                    }
                } finally {
                    g1.dispose();
                    g0.dispose();
                }
            }
        }

        final int EDGECOLOR = puzzleSize.getEdgeColorChange();
        long time = System.nanoTime();
        for (int i = 0; i < X; i++) {
            for (int j = 0; j < Y; j++) {
                BufferedImage mask = masks[i][j];
                for (int x = 1; x < mask.getWidth()-1; x++) {
                    for (int y = 1; y < mask.getHeight()-1; y++) {
                        int rgb = mask.getRGB(x, y);
                        if (rgb != 0) {
                            if (mask.getRGB(x-1, y) == 0 || mask.getRGB(x, y-1) == 0) {
                                mask.setRGB(x, y, rgb - EDGECOLOR);
                            } else if (mask.getRGB(x+1, y) == 0 || mask.getRGB(x, y+1) == 0) {
                                mask.setRGB(x, y, rgb + EDGECOLOR);
                            }
                        }
                    }
                }
            }
        }
        time -= System.nanoTime();
        System.out.printf("time: %.3f ms%n", time / -1e6);

        return masks;
    }
	
	private int rnd(int mean, int delta) {
        int value;
        if (delta < 0) {
            value = mean + random.nextInt(-delta-delta+1) - -delta;
        } else if (delta % 2 == 0) {
            value = mean + 2*(random.nextInt(delta/2+delta/2+1) - delta/2);
        } else {
            value = mean + random.nextInt(delta+delta+1) - delta;
        }
        return value;
    }
	
	private static final Color[] COLORS = new Color[] {
	        Color.RED,
	        Color.GREEN,
	        Color.BLUE,
	        Color.YELLOW.darker().darker(),
	        Color.MAGENTA,
	        Color.GRAY,
	        Color.CYAN,
	        Color.ORANGE};
	
	
}
