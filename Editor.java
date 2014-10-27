import java.util.*;
import java.awt.image.*;
import javax.imageio.*;
import java.io.*;

public class Editor {
	public static final String[] COLOR = {"Red", "Green", "Blue"};

	public static void main(String[] args) {
		System.out.println("           Welcome to command line image editor.           ");
		System.out.println("           =====================================           ");
		System.out.println("Type 'help' for usage information or, if you know how to use it, type your command at the prompt.");

		//initialize scanner for all user interaction
		Scanner console = new Scanner(System.in);

		//enter primary loop
		interact(console);
	}

	//The main loop that reads user input and calls methods of the image
	public static void interact(Scanner console) {
		//The sole TheImage instance this program uses.
		TheImage im1 = null;

		while (true) {
			System.out.println("Enter command:");
			//read user input as a string and convert to lower case
			String command = console.next().toLowerCase();
			//read extraneous input
			console.nextLine();

			//This block of if / else-if statements controls the flow of the
			//program based on user input. There are other ways to do this
			//but for a project of this scale, if-statements are a decent option.
			if (command.equals("help")) {
				displayHelp();
			} else if (command.equals("load")) {
				im1 = loadImage(console);
				if (im1 == null) {
					System.out.println("Image load failed.");
				} else {
					System.out.println("Image successfully loaded.");
				}
			} else if (command.equals("save")) {
				if (im1 == null) {
					System.out.println("No loaded image to write.");
				} else {
					System.out.println("Enter the file path where the image should be saved:");
					if (im1.writeImage(new File(console.nextLine().toLowerCase()))) {
						System.out.println("Image successfully saved.");
					}
				}
			} else if (command.equals("quit")) {
				System.out.println("Terminating.");
				break;

			//The rest of these clauses handle the operations
			//that can be performed on an image
			} else if (command.equals("flip-horiz")) {
				if (im1 != null) {
					im1.flipHorizontal();
				} else {
					System.out.println("No image loaded to flip.");
				}
			} else if (command.equals("flip-vert")) {
				if (im1 != null) {
					im1.flipVertical();
				} else {
					System.out.println("No image loaded to flip.");
				}
			} else if (command.equals("invert")) {
				if (im1 != null) {
					im1.invert();
				} else {
					System.out.println("No image loaded to invert.");
				}
			} else if (command.equals("replace")) {
				if (im1 != null) {
					//Get additional user input
					System.out.println("Enter integers for the color to replace:");
					int[] oldColor = getColor(console);
					if (oldColor == null) {
						continue;
					}
					System.out.println("Enter integers for the new color:");
					int[] newColor = getColor(console);
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
					im1.replaceColor(oldColor, newColor, range);

				} else {
					System.out.println("No image loaded to replace.");
				}
			} else {
				System.out.println("command '" + command + "' not understood. Type 'help' for usage information.");
			}
		}
	}


	//Reads in three integer values representing the red, green, and blue
	//channels of a color. Also does error checking to keep values between 0 and 255.
	public static int[] getColor(Scanner console) {
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
	}

	//Tries to load an image file into a TheImage instance.
	//Returns null if load fails.
	public static TheImage loadImage(Scanner console) {
		//load file
		System.out.println("Enter the name of the file you wish to load:");

		BufferedImage bi = null;
		InputStream in = null;

		//get file path from user
		File f = new File(console.nextLine());

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

		return new TheImage(bi);

	}

}
