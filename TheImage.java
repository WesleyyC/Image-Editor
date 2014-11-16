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
	private String label = null;

	// Constructor.
	public TheImage (BufferedImage image, File sourceImg) {
		this.im = image;
		this.sourceImg = sourceImg;
		height = im.getHeight();
		width = im.getWidth();
		ConsolePrinter.BLUE.print("Width: " + width);
		ConsolePrinter.BLUE.print("Height: " + height);

		ConsolePrinter.BLUE.print("Getting pixel values from packed data...");
		unpackPixels();
		label = "";
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

		label += "flipHorz-";
		ConsolePrinter.GREEN.print("Flipped Horizontally.");
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

		label += "flipVert-";
		ConsolePrinter.GREEN.print("Flipped Vertically.");
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

		label += "invert-";
		ConsolePrinter.GREEN.print("Inverted.");
	}

	// Replace the color in a certain range.
	public void replaceColor(int[] oldColor, int[] newColor, int range)
	{
		for (int i = 0; i < pixelData.length; i++)
		{
			for (int j = 0; j < pixelData[i].length; j++)
			{
				boolean isRedIn = Math.abs( pixelData[i][j][0] - oldColor[0] ) < range;
				boolean isGreenIn = Math.abs(pixelData[i][j][1] - oldColor[1]) < range;
				boolean isBlueIn = Math.abs(pixelData[i][j][2] - oldColor[2]) < range;

				// Every channel needs to be in the range.
				if ( isRedIn && isGreenIn && isBlueIn)
				{
					pixelData[i][j] = newColor;
				}
			}
		}

		label += "replaceColr-";
		ConsolePrinter.GREEN.print("Replaced color");
	}

	//Writes the current buffered image to a new image file 
	//First pack the pixelData into a 1D array of ints, then call the write() method in the ImageIO class.
	public boolean writeImage(String directoryPath) {
		//Validate and prepare file path to write image to
		if (directoryPath.isEmpty()) {
			directoryPath = sourceImg.getParent();
		} else {
			File directory = new File(directoryPath);
			try {
				if (!directory.isDirectory()) {
					ConsolePrinter.RED.print("The path you typed in was not a valid directory path. Please try again.");
					return false;
				}
			} catch(SecurityException e) {
				ConsolePrinter.RED.print("You don't have access to this directory. Please try another location.");
				return false;
			}
		}

		File saveImg = new File(directoryPath + "/" + label + sourceImg.getName());
		//get the format of the source image by looking at its extension
		String format = sourceImg.getName().substring(sourceImg.getName().lastIndexOf('.') + 1).toLowerCase();

		//put pixelData into packedData
		packPixels();
		//Write new packed array back into BufferedImage
		//bi.setRGB(startX, startY, w, h, rgbArray, offset, scansize)
		im.setRGB(0, 0, width, height, packedData, 0, width);

		try{
			ImageIO.write(im, format, saveImg);
		} catch (IOException e) {
			ConsolePrinter.RED.print("Something went wrong when attempting to save the edited image. Please try again.");
			return false;
		}

		return true;
	}

	// Crop the image as a square in the center.
	public void crop(){
		int startX = 0;
		int startY = 0;
		int sideLength;
		if(width < height){
			startY = (height - width) / 2;
			sideLength = width;
		}else{
			startX = (width - height) / 2;
			sideLength = height;
		}

		im = im.getSubimage(startX, startY, sideLength, sideLength);
		updateImage();

		label += "crop-";
		ConsolePrinter.GREEN.print("Cropped.");
	}

	// Brighten the function of different level.
	public void brighten(double level)
	{
		//unreadable code: magic number
		//Dear Wesley, would you please make these numbers constants in the class?
		double factor = 1.17 + level * 0.03;
		double onset = 12 + 3 * level;

		RescaleOp rescaleOp = new RescaleOp((float)factor, (int)onset, null);

		rescaleOp.filter(im, im);

		updateImage();

		label += "brighten-";
		ConsolePrinter.GREEN.print("Brightened.");
	}

	//Uses bitwise operations to convert one integer into four channels,
	//each with a range from 0 to 255.
	public void unpackPixels() {
		packedData = im.getRGB(0, 0, width, height, null, 0, width);

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

	// Called whenever buffered image is changed by external library
	private void updateImage(){
		height = im.getHeight();
		width = im.getWidth();
		unpackPixels();
	}

	//Uses bitwise operations to convert four integer (ranging from 0 to 255)
	//into a single integer for use with the BufferedImage class.
	private void packPixels() {
		ConsolePrinter.BLUE.print("putting pixel values in packed format...");

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
