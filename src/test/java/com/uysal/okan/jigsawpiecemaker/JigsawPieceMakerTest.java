package com.uysal.okan.jigsawpiecemaker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

public class JigsawPieceMakerTest {
	@Test public void testSetJigsawImageMethod() {
        JigsawPieceMaker jigsawPieceMaker = new JigsawPieceMaker();
        assertTrue(jigsawPieceMaker.setJigsawImage(new File("src/test/resources/doga.jpg")));
    }
	
	@Test public void testSetJigsawImageMethod2() {
        JigsawPieceMaker jigsawPieceMaker = new JigsawPieceMaker();
        assertFalse(jigsawPieceMaker.setJigsawImage(new File("src/test/resources/doga2.jpg")));
    }
	
	@Test public void testSetJigsawImageMethod3() {
        JigsawPieceMaker jigsawPieceMaker = new JigsawPieceMaker();
        assertFalse(jigsawPieceMaker.setJigsawImage(new File("src/test/resources/temp.txt")));
    }
	
	@Test public void testSetJigsawPieceCountMethod() {
        JigsawPieceMaker jigsawPieceMaker = new JigsawPieceMaker();
        assertEquals(4, jigsawPieceMaker.setJigsawPieceCount(2));
    }
	
	@Test public void testSetJigsawPieceCountMethod2() {
        JigsawPieceMaker jigsawPieceMaker = new JigsawPieceMaker();
        assertEquals(5000, jigsawPieceMaker.setJigsawPieceCount(6000));
    }
	
	@Test public void testSetJigsawPieceCountMethod3() {
        JigsawPieceMaker jigsawPieceMaker = new JigsawPieceMaker();
        assertEquals(50, jigsawPieceMaker.setJigsawPieceCount(50));
    }
}
