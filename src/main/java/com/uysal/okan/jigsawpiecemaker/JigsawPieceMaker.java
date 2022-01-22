package com.uysal.okan.jigsawpiecemaker;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class JigsawPieceMaker {
	
	private File image;
	private int pieceCount;

	public JigsawPieceMaker() {}
	
	public boolean setJigsawImage(File image) {
		try {
			if(image.exists() && ImageIO.read(image) != null) {
				this.image = image;
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
	
}
