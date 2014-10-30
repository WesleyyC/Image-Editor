import java.util.*;
import java.awt.image.*;
import javax.imageio.*;
import java.io.*;

public class Editor {
	public static final String[] COLOR = {"Red", "Green", "Blue"};
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
					image.brighten(Double.parseDouble(console.nextLine()));
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
					if (oldColor == null) {
						continue;
					}
					System.out.println("Enter integers for the new color:");
					int[] newColor = getColor();
					if (newColor == null) {
						continue;
					}
					System.out.println("Enter an integer specifying how large a range of colors to replace:");
					int range;
					try {
						range = console.nextInt();
					} catch (InputMismatchException e) {
						System.out.println("Input was not a valid input type for range");
						continue;
					}

					//if program reaches this point, input is valid, so call replace
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
	public static int[] getColor() {
		int[] rgb = new int[3];
		for (int i = 0; i < 3; i ++) {
			System.out.println(COLOR[i] + " (0 - 255):");
			try {
				rgb[i] = console.nextInt();
				if (rgb[i] < 0 || rgb[i] > 255) {
					System.out.println("Input out of range 0 - 255");
					return null;
				}
			} catch (InputMismatchException e) {
				System.out.println("Inproper input type.");
				return null;
			}
		}
		return rgb;
	}

	//Prints usage information for this program.
	public static void displayHelp() {
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
	}

	//Tries to load an image file into a TheImage instance.
	//Returns null if load fails.
	public static TheImage loadImage() {
		//load file
		System.out.println("Enter the name of the file you wish to load:");

		BufferedImage bi = null;
		InputStream in = null;

		//get file path from user
		String tmp = console.nextLine().trim();
		File f = new File(tmp);

		//initialize stream from file
		try {
			in = new FileInputStream(f);
		} catch (FileNotFoundException e) {
			System.out.println("Couldn't find file " + f.getAbsolutePath());
			return null;
		}

		//read stream into BufferedImage
		try {
			bi = ImageIO.read(in);
		} catch (IOException e) {
			System.out.println("Failed to read FileInputStream.");
			return null;
		}

		return new TheImage(bi, f);

		//Todo: Add println "Image load failed."
		//Todo: add System.out.println("Image successfully loaded.");

	}

	private static void saveImageHelper() {
		System.out.println("Would you like to save under the same directory as the source image? y/n");
		String saveOption = console.nextLine().toLowerCase();
		boolean saveResult;

		switch (saveOption) {
			case "y":
				saveResult = image.writeImage(null);
				break;
			case "n":
				System.out.println("Please enter a FILE PATH that you would like to save image to");
				saveResult = image.writeImage(console.nextLine().trim());
				break;
			default:
				System.out.println("Sorry, we do not understand your input. Aborting save.");
				saveResult = false;
				break;
		}

		if (saveResult == true) {
			System.out.println("Image successfully saved.");
		} else {
			System.out.println("Image saving failed.");
		}
	}

}
