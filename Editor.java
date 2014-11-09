import java.util.*;
import java.awt.image.*;
import javax.imageio.*;
import java.io.*;

public class Editor {
	// ANSI COLOR
// USAGE: System.out.println(ANSI_RED + "This text is red!" + ANSI_RESET);
//========================================================//
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_PURPLE = "\u001B[35m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_WHITE = "\u001B[37m";
//========================================================//

	private static final String[] COLOR = {"Red", "Green", "Blue"};
	private static Scanner console;
	private static TheImage image;

	public static void main(String[] args) {
		System.out.println();
		System.out.println("           =====================================           ");
		System.out.println("           Welcome to command line image editor.           ");
		System.out.println("           =====================================           ");
		System.out.println();
		System.out.println(">>>>>Type 'help' for usage information and 'load' for loading image.<<<<<<<");

		console = new Scanner(System.in);
		interact();
	}

	//The main loop that reads user input and calls methods of the image
	private static void interact() {
		image = null;
		String command;
		boolean hasQuit = false;

		while (image == null && !hasQuit) {
			System.out.println("Enter command:");
			command = console.nextLine().toLowerCase().trim();

			switch (command) {
				case "help":
					displayHelp();
					break;
				case "load":
					image = loadImage();
					break;
				case "quit":
					System.out.println("Terminating.");
					hasQuit = true;
					break;
				default:
					System.out.println("Please load an image before proceeding to other commands. Type help if you need help.");
					break;
			}
		}

		while (!hasQuit) {
			System.out.println("Enter command:");
			command = console.nextLine().toLowerCase().trim();

			switch (command) {
				case "help":
					displayHelp();
					break;
				case "load":
					image = loadImage();
					break;
				case "save":
					saveImageHelper();
					break;
				case "flip-horiz":
					image.flipHorizontal();
					break;
				case "brighten":
					System.out.print("From 1-3, indicate the level of the brighten:  ");
					image.brighten(doubleInput());
					break;
				case "crop":
					image.crop();
					break;
				case "flip-vert":
					image.flipVertical();
					break;
				case "invert":
					image.invert();
					break;
				case "replace":
					//Get additional user input
					System.out.println("Enter integers for the color to replace:");
					int[] oldColor = getColor();
		
					System.out.println("Enter integers for the new color:");
					int[] newColor = getColor();
					
					System.out.println("Enter an integer specifying how large a range of colors to replace:");

					while (!console.hasNextInt()) {
						console.next();
						System.out.println("Input was not a valid input type for range. Try again.");
						System.out.println("Enter an integer specifying how large a range of colors to replace:");
					}

					int range = console.nextInt();
					image.replaceColor(oldColor, newColor, range);
					break;
				case "quit":
					System.out.println("Terminating.");
					hasQuit = true;
					break;
				default:
					System.out.println("command '" + command + "' not found. Type 'help' for usage information.");
					break;
			}
		}
	}


	//Reads in three integer values representing the red, green, and blue
	//channels of a color. Also does error checking to keep values between 0 and 255.
	private static int[] getColor() {
		int[] rgb = new int[3];
		for (int i = 0; i < 3; i ++) {
			boolean isValid = false;

			while(!isValid) {
				System.out.println(COLOR[i] + " (0 - 255):");
				
				if (console.hasNextInt()) {
					rgb[i] = console.nextInt();

					if (rgb[i] < 0 || rgb[i] > 255) {
						System.out.println("Input out of range 0 - 255, try again.");
					} else {
						isValid = true;
					}
				} else {
					//skip the garbage in the console
					console.next();
					System.out.println("Improper input type. Try again.");
				}	
			} // end while loop 
		} // end for loop
		
		return rgb;
	}

	//Prints usage information for this program.
	private static void displayHelp() {
		System.out.println("Usage:");
		System.out.println("'help' -- Displays this list of commands.");
		System.out.println("'quit' -- Causes the program to terminate.");
		System.out.println("'load' -- Prompts for image file name and loads that image.\n" +
								"Replaces any image currently in memory.");
		System.out.println("'save' -- Prompts for filename and writes current image to file.");
		System.out.println("'flip-horiz' -- Calls the flipHorizontal() method of the current image.");
		System.out.println("'flip-vert' -- Calls the flipVertical() method of the current image.");
		System.out.println("'invert' -- Calls the invert() method of the current image.");
		System.out.println("'replace' -- Calls the replaceColor() method of the current image.\n" +
							"Subsequently prompts for two colors and a range.");
		System.out.println("'crop' -- Calls the crop() method of the current image.\n" +
							"Crop the image as a squre at the center.");
		System.out.println("'brighten' -- Calls the brighten() method of the current image.\n" +
							"Subsequently prompts for the increase level from 1-3.");
	}

	//Tries to load an image file into a TheImage instance.
	//Returns null if load fails.
	private static TheImage loadImage() {
		//load file
		System.out.println("Enter the name of the file you wish to load:");

		BufferedImage bi = null;
		InputStream in = null;

		//get file path from user
		String tmp = console.nextLine().trim();
		File f = new File(tmp);

		try {
			//initialize stream from file
			in = new FileInputStream(f);

			//read stream into BufferedImage
			bi = ImageIO.read(in);

		} catch (FileNotFoundException e) {
			System.out.println("Couldn't find file " + f.getAbsolutePath());
			System.out.println("Please make sure your pet did not eat it.");
			return loadImage();
		} catch (IOException e) {
			System.out.println("Image load failed.");
			return null;
		}

		System.out.println("Image successfully loaded.");
		return new TheImage(bi, f);
	}

	public static double doubleInput(){
		double number;

		try{
			number = Double.parseDouble(console.nextLine());
			if(number >=1 && number <=3){
				return number;
			}else{
				System.out.print("Sorry. The level has to be between 1-3, but it can has decimal: ");
				// Use a recursive function instead of a loop to simplify.
				// Do not reuse the code on mobile device.
				return doubleInput();
			}
		} catch(NumberFormatException e){
			System.out.print("Sorry. Please input a number between 1-3: ");
			// Use a recursive function instead of a loop to simplify.
			// Do not reuse the code on mobile device.
			return doubleInput();
		}

	}

	private static void saveImageHelper() {
		boolean hasSaved = false;

		while (!hasSaved) {
			System.out.println("Type in the directory path where you wish to save your image.");
			System.out.println("Simply hit 'Enter' with empty input to save under the same directory of the source image.");
			String savePath = console.nextLine().trim();

			hasSaved = image.writeImage(savePath);
		} // end while

		System.out.println("Image successfully saved.");
	} // end saveImageHelper

}
