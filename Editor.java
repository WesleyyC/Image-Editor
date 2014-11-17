import java.util.*;
import java.awt.image.*;
import javax.imageio.*;
import java.io.*;

public class Editor {
	private static final String[] COLOR = {"Red", "Green", "Blue"};
	private static Scanner console;
	private static TheImage image;

	public static void main(String[] args) {
		ConsolePrinter.WHITE.print("\n");
		ConsolePrinter.WHITE.print("               =====================================           ");
		ConsolePrinter.WHITE.print("               Welcome to command line image editor.           ");
		ConsolePrinter.WHITE.print("               =====================================           ");
		ConsolePrinter.WHITE.print("\n");
		ConsolePrinter.WHITE.print(">>>>>Type 'help' for usage information and 'load' for loading image.<<<<<<<");

		console = new Scanner(System.in);
		interact();
	}

	//The main loop that reads user input and calls methods of the image
	private static void interact() {
		image = null;
		String command;
		boolean hasQuit = false;

		while (image == null && !hasQuit) {
			ConsolePrinter.CYAN.print("Enter command:");
			command = console.nextLine().toLowerCase().trim();

			switch (command) {
				case "help":
					displayHelp();
					break;
				case "load":
					image = loadImage();
					break;
				case "quit":
					ConsolePrinter.WHITE.print("Terminating.");
					hasQuit = true;
					console.close();
					break;
				default:
					ConsolePrinter.RED.print("Please load an image before proceeding to other commands. Type help if you need help.");
					break;
			}
		}

		while (!hasQuit) {
			ConsolePrinter.CYAN.print("Enter command:");
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
					ConsolePrinter.YELLOW.print("From 1-3, indicate the level of the brighten:  ");
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
					ConsolePrinter.YELLOW.print("Enter integers for the color to replace:");
					int[] oldColor = getColor();
		
					ConsolePrinter.YELLOW.print("Enter integers for the new color:");
					int[] newColor = getColor();
					
					ConsolePrinter.YELLOW.print("Enter an integer specifying how large a range of colors to replace:");

					while (!console.hasNextInt()) {
						console.next();
						ConsolePrinter.RED.print("Input was not a valid input type for range. Try again.");
						ConsolePrinter.YELLOW.print("Enter an integer specifying how large a range of colors to replace:");
					}
					int range = console.nextInt();

					//flushes the empty line generated from nextInt()... waiting for better way to handle this
					console.nextLine();

					image.replaceColor(oldColor, newColor, range);
					break;
				case "rollback":
					rollbackHelper();
					break;
				case "quit":
					ConsolePrinter.WHITE.print("Terminating.");
					hasQuit = true;
					console.close();
					break;
				default:
					ConsolePrinter.RED.print("command '" + command + "' not found. Type 'help' for usage information.");
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
				ConsolePrinter.YELLOW.print(COLOR[i] + " (0 - 255):");
				
				if (console.hasNextInt()) {
					rgb[i] = console.nextInt();

					if (rgb[i] < 0 || rgb[i] > 255) {
						ConsolePrinter.RED.print("Input out of range 0 - 255, try again.");
					} else {
						isValid = true;
					}
				} else {
					//skip the garbage in the console
					console.next();
					ConsolePrinter.RED.print("Improper input type. Try again.");
				}	
			} // end while loop 
		} // end for loop
		
		return rgb;
	}

	//Prints usage information for this program.
	private static void displayHelp() {
		ConsolePrinter.PURPLE.print("Usage:");
		ConsolePrinter.PURPLE.print("'help' -- Displays this list of commands.");
		ConsolePrinter.PURPLE.print("'quit' -- Causes the program to terminate.");
		ConsolePrinter.PURPLE.print("'load' -- Prompts for image file name and loads that image.\n" +
								"Replaces any image currently in memory.");
		ConsolePrinter.PURPLE.print("'save' -- Prompts for filename and writes current image to file.");
		ConsolePrinter.PURPLE.print("'flip-horiz' -- Calls the flipHorizontal() method of the current image.");
		ConsolePrinter.PURPLE.print("'flip-vert' -- Calls the flipVertical() method of the current image.");
		ConsolePrinter.PURPLE.print("'invert' -- Calls the invert() method of the current image.");
		ConsolePrinter.PURPLE.print("'replace' -- Calls the replaceColor() method of the current image.\n" +
							"Subsequently prompts for two colors and a range.");
		ConsolePrinter.PURPLE.print("'crop' -- Calls the crop() method of the current image.\n" +
							"Crop the image as a squre at the center.");
		ConsolePrinter.PURPLE.print("'brighten' -- Calls the brighten() method of the current image.\n" +
							"Subsequently prompts for the increase level from 1-3.");
	}

	//Tries to load an image file into a TheImage instance.
	//Returns null if load fails.
	private static TheImage loadImage() {
		//load file
		ConsolePrinter.YELLOW.print("Enter the name of the file you wish to load:");
		//get file path from user
		String filePath = console.nextLine().trim();
		File sourceFile = new File(filePath);
		BufferedImage bi = null;

		try(InputStream in = new FileInputStream(sourceFile)){
			//read stream into BufferedImage
			bi = ImageIO.read(in);

		} catch (FileNotFoundException e) {
			ConsolePrinter.RED.print("Couldn't find file " + sourceFile.getAbsolutePath());
			ConsolePrinter.RED.print("Please make sure your pet did not eat it.");
			return loadImage();
		} catch (IOException e) {
			ConsolePrinter.RED.print("Image load failed.");
			return null;
		}

		ConsolePrinter.GREEN.print("Image successfully loaded.");
		return new TheImage(bi, sourceFile);
	}

	private static double doubleInput(){
		double number;

		try{
			number = Double.parseDouble(console.nextLine());
			if(number >=1 && number <=3){
				return number;
			}else{
				ConsolePrinter.RED.print("Sorry. The level has to be between 1-3, but it can has decimal: ");
				// Use a recursive function instead of a loop to simplify.
				// Do not reuse the code on mobile device.
				return doubleInput();
			}
		} catch(NumberFormatException e){
			ConsolePrinter.RED.print("Sorry. Please input a number between 1-3: ");
			// Use a recursive function instead of a loop to simplify.
			// Do not reuse the code on mobile device.
			return doubleInput();
		}

	}

	private static void saveImageHelper() {
		boolean hasSaved = false;

		while (!hasSaved) {
			ConsolePrinter.YELLOW.print("Type in the directory path where you wish to save your image.");
			ConsolePrinter.YELLOW.print("Simply hit 'Enter' with empty input to save under the same directory of the source image.");
			String savePath = console.nextLine().trim();

			hasSaved = image.writeImage(savePath);
		} // end while

		ConsolePrinter.GREEN.print("Image successfully saved.");
	} // end saveImageHelper

	private static void rollbackHelper(){
		if (image.hasUnsavedWork() && !image.getLabel().isEmpty()) {
			ConsolePrinter.YELLOW.print("Are you sure you want to discard all the unsaved changes? y/s");
			String confirmation = console.nextLine().toLowerCase().trim();
		
			if (confirmation.equals("y")) {
				image.rollback();
			} else {
				//Just to play on the safe side, this will include "n" and any other random input
				ConsolePrinter.RED.print("Cancelled rollback.");
			}
		} else if (!image.hasUnsavedWork() && !image.getLabel().isEmpty()) {
			ConsolePrinter.BLUE.print("Looks like you don't want to edit the image on top of the previously saved changes. Wiping out...");
			image.rollback();
		} else if (!image.hasUnsavedWork() && image.getLabel().isEmpty()) {
			ConsolePrinter.RED.print("You didn't modify the original image at all. Rollback nothing.");
		} else {
			//case where there is unsaved work but the label is empty... something went wrong!
			ConsolePrinter.RED.print("Woops, the status for your image is inconsistent. Something has gone wrong.");
			ConsolePrinter.RED.print("We recommend you to use command 'save' to save whatever is in there just to be safe.");
			ConsolePrinter.RED.print("We would appreciate it if you report this back to us.");
			ConsolePrinter.RED.print("Cancelled rollback");
		}
	}

}
