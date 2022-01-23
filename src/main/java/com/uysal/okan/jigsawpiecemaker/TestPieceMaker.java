package com.uysal.okan.jigsawpiecemaker;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

public class TestPieceMaker {

	public static void main(String[] args) throws IOException {
		JigsawPieceMaker jigsawPieceMaker = new JigsawPieceMaker(new File("src/test/resources/doga.jpg"), 1);
        List<Piece> pieces = jigsawPieceMaker.createPieces();
        for(int i = 0; i < pieces.size(); i++) {
        	BufferedImage bi = new BufferedImage(pieces.get(i).getSize().width, pieces.get(i).getSize().height, BufferedImage.TYPE_INT_ARGB); 
            Graphics g = bi.createGraphics();
            pieces.get(i).paint(g);  //this == JComponent
            g.dispose();
            try{ImageIO.write(bi,"png",new File("out/test" + i + ".png"));}catch (Exception e) {e.printStackTrace();}
        }

	}

}
