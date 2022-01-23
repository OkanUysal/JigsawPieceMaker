package com.uysal.okan.jigsawpiecemaker;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public abstract class Template {

    public static Template get(String name) {
		switch (name) {
		    case "40": return new Template40();
			default: throw new RuntimeException("unknown template: " + name);
		}
	}
	
    public static Template read(ObjectInputStream input) throws IOException {
        String name = input.readUTF();
        return get(name);
    }
    
    protected Template() {
    }
    
    public void write(ObjectOutputStream output) throws IOException {
        output.writeUTF(getClass().getSimpleName().substring(8));
    }
	
    public abstract int getSizeX();
    public abstract int getSizeY();
    
    public abstract int getOverlap();
    public abstract int getBaseVariation();
    
    public abstract int getBorderWidth();
    
    public abstract int getPegWidth();
    public abstract int getPegLength();
    public abstract int getPegRadius();
    
    public abstract int getPegPositionDelta();
    public abstract int getPegRadiusDelta();
    public abstract int getPegHeightDelta();
    
    public abstract int getEdgeColorChange();
}
