package com.uysal.okan.jigsawpiecemaker;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

public class TestPieceMaker {

	public static void main(String[] args) throws IOException {
		JigsawPieceMaker jigsawPieceMaker = new JigsawPieceMaker(new File("src/test/resources/doga.jpg"), 15);
        jigsawPieceMaker.createPieces();
        jigsawPieceMaker.writePieces("out");

	}

}
