// This is a program that edits a picture.

import java.awt.image.*;
import javax.imageio.*;
import java.io.*;

public class TheImage {

	private BufferedImage im = null;
	private File sourceImg = null;
	private int[] packedData = null;
	private int[][][] pixelData = null; 	// Unit be modified.
	private int height = 0;
	private int width = 0;
	private int startX = 0;
	private int startY = 0;

	// Constructor.
	public TheImage (BufferedImage image, File sourceImg) {
		this.im = image;
		this.sourceImg = sourceImg;
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
	public boolean writeImage(String savePath) {
		if (savePath.isEmpty()) {
			savePath = sourceImg.getParent() + "/edited-" + sourceImg.getName();
		}
		File saveImg = new File(savePath);

		//put pixelData into packedData
		packPixels();

		//Write new packed array back into BufferedImage
		//bi.setRGB(startX, startY, w, h, rgbArray, offset, scansize)
		im.setRGB(0, 0, width, height, packedData, 0, width);

		// sourceImg.getName().substring(sourceImg.getName().lastIndexOf('.')+1).toLowerCase().trim() is the format of the sourceImg.
		try{
			ImageIO.write(im, sourceImg.getName().substring(sourceImg.getName().lastIndexOf('.')+1).toLowerCase().trim(), saveImg);
		} catch (IOException e) {
			System.out.println("IO exception encountered. Please ensure your file path is valid");
			return false;
		} catch (Exception e) {
			System.out.println("Exception:" + e.toString());
			return false;
		}

		return true;
	}

	// Crop the image as a squre in the	 center.
	public void crop(){
		if(width<height){
			startY=(height-width)/2;
			height = width;
		}else{
			startX=(width-height)/2;
			width = height;
		}

		im = im.getSubimage(startX,startY, width, height);
		updatePixel();

		System.out.println("Cropped.");
	}

	// Brighten the function of different level.
	public void brighten(double level)
	{
		double factor = 1.17 + level*0.03;
		double onset = 12 + 3*level;

		RescaleOp rescaleOp = new RescaleOp((float)factor, (int)onset, null);

		rescaleOp.filter(im, im);

		updatePixel();

		System.out.println("Brighten.");
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

	// Update the pixel array when using some higher archy library
	private void updatePixel(){
		packedData = im.getRGB(0, 0, width, height, null, 0, width);
		pixelData = null; 	// Release memory first
		unpackPixels();
	}


	//Uses bitwise operations to convert four integer (ranging from 0 to 255)
	//into a single integer for use with the BufferedImage class.
	public void packPixels() {
		System.out.println("putting pixel values in packed format...");

		// Pack the data from the startX and startY coordinate
		for (int row = 0; row < height; row ++) {
			for (int col = 0; col < width; col ++) {
				packedData[((row) * width) + (col)] = ((255 & 0xFF) << 24) | //alpha
	            ((pixelData[row][col][0] & 0xFF) << 16) | //red
	            ((pixelData[row][col][1] & 0xFF) << 8)  | //green
	            ((pixelData[row][col][2] & 0xFF) << 0); //blue
			}
		}
	}
}
