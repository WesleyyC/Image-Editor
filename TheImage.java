// This is a program that edits a picture.

import java.awt.image.*;
import javax.imageio.*;
import java.io.*;

public class TheImage {

	public BufferedImage im = null;
	public int[] packedData = null;
	public int[][][] pixelData = null; 	// Unit be modified.
	public int height = 0;
	public int width = 0;
	public int startX = 0;
	public int startY = 0;

	// Constructor.
	public TheImage (BufferedImage image) {
		im = image;
		height = im.getHeight();
		width = im.getWidth();
		System.out.println(width);
		System.out.println(height);
		packedData = im.getRGB(0, 0, width, height, null, 0, width);
		unpackPixels();
	}

	// Flip the picture horizontally.
	public void flipHorizontal()
	{
		for (int i = 0; i < pixelData.length; i++)
		{
			for (int j = 0; j <= (pixelData[i].length - 1)/2; j++)	// only need to go through half-way point.
			{
				int k = pixelData[i].length - 1 - j;
				int[] temp = pixelData[i][j];
				pixelData[i][j] = pixelData[i][k];
				pixelData[i][k] = temp;
			}
		}

		System.out.println("Flipped Horizontally.");
	}

	// Flip the picture vertically.
	public void flipVertical()
	{
		for (int i = 0; i <= (pixelData.length - 1)/2; i++)	// only need to go through half-way point.
		{
			for (int j = 0; j < pixelData[i].length; j++)
			{
				int k = pixelData.length - 1 - i;
				int[] temp = pixelData[i][j];
				pixelData[i][j] = pixelData[k][j];
				pixelData[k][j] = temp;
			}
		}

		System.out.println("Flipped Vertically.");
	}

	// Invert the color of the picture.
	public void invert()
	{
		for (int i = 0; i < pixelData.length; i++)
		{
			for (int j = 0; j < pixelData[i].length; j++)
			{
				pixelData[i][j][0] = Math.abs (pixelData[i][j][0] - 255);
				pixelData[i][j][1] = Math.abs (pixelData[i][j][1] - 255);
				pixelData[i][j][2] = Math.abs (pixelData[i][j][2] - 255);
			}
		}

		System.out.println("Inverted.");
	}

	public void brighten()
	{
		RescaleOp rescaleOp = new RescaleOp(1.2f, 15, null);
		rescaleOp.filter(im, im);

		System.out.println("Brighten.");

		updatePixel();
	}

	private void updatePixel(){
		packedData = im.getRGB(0, 0, width, height, null, 0, width);
		pixelData = null; 	// Release memory first
		unpackPixels();
	}

	// Replace the color in a certain range.
	public void replaceColor(int[] oldColor, int[] newColor, int range)
	{
		for (int i = 0; i < pixelData.length; i++)
		{
			for (int j = 0; j < pixelData[i].length; j++)
			{
				// Every channel needs to be in the range.
				if (oldColor[0] - range < pixelData[i][j][0] && pixelData[i][j][0] < oldColor[0] + range && oldColor[1] - range < pixelData[i][j][1] && pixelData[i][j][1] < oldColor[1] + range && oldColor[2] - range < pixelData[i][j][2] && pixelData[i][j][2] < oldColor[2] + range)
				{
					pixelData[i][j] = newColor;
				}
			}
		}

		System.out.println("Replaced color");
	}

	//Writes the current data in pixelData to a .png image by first packing
	//the data into a 1D array of ints, then calling the write() method of
	//the ImageIO class.
	public boolean writeImage(File file) {
		//put pixelData into packedData
		packPixels();

		//Write new packed array back into BufferedImage
		//bi.setRGB(startX, startY, w, h, rgbArray, offset, scansize)
		im.setRGB(0, 0, width, height, packedData, 0, width);

		try{
			ImageIO.write(im, "jpg", file);
		} catch (IOException e) {
			System.out.println("Writing image failed.");
			return false;
		}
		return true;
	}

	//Uses bitwise operations to convert one integer into four channels,
	//each with a range from 0 to 255.
	public void unpackPixels() {
		System.out.println("Getting pixel values from packed data...");

		//This is a rows x columns array. That is, it is an array of rows.
		pixelData = new int[height][width][3];

		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col ++) {
				pixelData[row][col][0] = (packedData[(row * width) + col] >> 16) & 0xff;
				pixelData[row][col][1] = (packedData[(row * width) + col] >> 8) & 0xff;
				pixelData[row][col][2] = (packedData[(row * width) + col]) & 0xff;
			}
		}
	}

	// Crop the image as a squre in the center.
	public void crop(){
		if(width<height){
			startY=(height-width)/2;
			height = width;
		}else{
			startX=(width-height)/2;
			width = height;
		}

		im = im.getSubimage(startX,startY, width, height);

		System.out.println("Cropped.");
	}

	//Uses bitwise operations to convert four integer (ranging from 0 to 255)
	//into a single integer for use with the BufferedImage class.
	public void packPixels() {
		System.out.println("putting pixel values in packed format...");

		// Pack the data from the startX and startY coordinate
		for (int row = startY; row < height+startY; row ++) {
			for (int col = startX; col < width+startX; col ++) {
				packedData[((row-startY) * width) + (col-startX)] = ((255 & 0xFF) << 24) | //alpha
	            ((pixelData[row][col][0] & 0xFF) << 16) | //red
	            ((pixelData[row][col][1] & 0xFF) << 8)  | //green
	            ((pixelData[row][col][2] & 0xFF) << 0); //blue
			}
		}
	}
}
