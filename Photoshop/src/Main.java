import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.plaf.SliderUI;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JButton;

public class Main {
	// this should be the name of your image
		private String file_name = "02_timessquare_julienneschaer-001__x_large.jpg";
		
		// the name of the new image
		private String output_name;
		
		// the array of pixels that represents the image
		private Color[][] pixels;
		
		// width and height of the image
		private int w,h;
		
		// used to find the distance between two colors
		private double distance;
		
		// basic colors to use in closest_colors()
		private Color[] color_list = {Color.BLUE, Color.RED,Color.ORANGE, Color.MAGENTA, 
				Color.BLACK, Color.WHITE, Color.GREEN, Color.YELLOW, Color.CYAN};
		
		//for global access
		JLabel lblImage;
		
		JSlider slider;
		
		//for using blur with button and slider(set to false) alone, blur can still be used without this, but not on its own
		///---------------------
		boolean event = false;
		///---------------------
		
		
		File my_file;
		
		BufferedImage image;
		
		// for each pixel, do the following: multiply each of it's 8 neighbor's values
		// by -1, multiply the pixel's own value by 8, and sum these 9 values. 
		// Set the pixel's color to this new value.
		public void edge() {
			output_name = "edged_" + file_name;
			//manually copy pixels array
			Color[][] copy = Copy(pixels);
			//keep track of current color for algorithm 
			Color current;
			int red = 0;
			int green = 0;
			int blue = 0;
			for (int i = 1; i < h-1; i++) {
				for (int j = 1; j < w-1; j++) {
					//*look at blur. start and end are similar to blur, except we know that we will
					//only have a 3x3 square to loop over so some values are set
					//for the condition, one is substituted into input in the condition of blur
					//which gives this condition
					for (int i2 = i-1; i2 <= 2 + (i-1); i2++) {
						for (int j2 = j-1; j2 <= 2 + (j-1); j2++) {
							//sum all pixels except middle pixel
							if(i != i2 || j != j2) {
								red += copy[i2][j2].getRed() * -1;
								green += copy[i2][j2].getGreen() * -1;
								blue += copy[i2][j2].getBlue() * -1;
							}
						}
					}
					
					current = pixels[i][j];
					
					red = red+(current.getRed()*8);
					green = green+(current.getGreen()*8);
					blue =  blue+(current.getBlue()*8);
					
					//since we are multiplying by -1 and 8, number will be either negative
					//or very big so they are simplified to the range 0-255
					red = red > 255 ? 255 : red;
					red = red < 0 ? 0 : red;
					green = green > 255 ? 255 : green;
					green = green < 0 ? 0 : green;
					blue = blue > 255 ? 255 : blue;
					blue = blue < 0 ? 0 : blue;
					
					pixels[i][j] = new Color(red, green, blue);
					//reset for next pixel
					red = 0;
					green = 0;
					blue = 0;
				}
			}
			
		}
		
		public Color[][] Copy(Color[][] arr){
			//manually copy array
			Color[][] result = new Color[arr.length][arr[0].length];
			for (int i = 0; i < arr.length; i++) {
				for (int j = 0; j < arr[i].length; j++) {
					result[i][j] = arr[i][j];
				}
			}
			return result;
		}
		
		
		// for each pixel, average it's rgb value with the 8 pixels surrounding it.
		// set the pixel's rgb value to this average. If the pixel is on an edge of 
		// the image, don't do anything
		public void blur() {
			//copy so i take the averages from the original picture not the edited version
			Color[][] copy = pixels.clone();
			output_name = "blurred_" + file_name;
			int input = slider.getValue();
			int redAvg = 0;
			int greenAvg = 0;
			int blueAvg = 0;
			int counter = 0;
			//input of slider is integers from 1 to 10
			for (int i = input; i < h-input; i++) {
				for (int j = input; j < w-input; j++) {
					//two loops for the box inside to add the averages
					//this is a square box (ixi) so all properties are the same for columns and rows
					//the start index is the number of pixels before the middle(current [i][j]) pixel which is that minus the input from the slider 
					//with the end, we are shifting the kernel one step across the 2d array.
					//the end is the index of the ending of the first box(input * 2) + the current index minus the input(Will increase as i and j increase)
						for (int i2 = i-input; i2 < (input*2) + (i-input); i2++) {
							for(int j2 = j-input; j2 < (input*2) + (j-input); j2++) {
								redAvg += copy[i2][j2].getRed();
								greenAvg += copy[i2][j2].getGreen();
								blueAvg += copy[i2][j2].getBlue();
								//counter for dividing to find the average after summing(# of pixels)
								counter++;
							}
						}
						//averages
						redAvg = redAvg / counter;
						greenAvg = greenAvg / counter;
						blueAvg = blueAvg / counter;
						
						counter = 0;
						
						pixels[i][j] = new Color(redAvg, greenAvg, blueAvg);
						
						redAvg = 0;
						greenAvg = 0;
						blueAvg = 0;
					
				}
			}
			
			//section below is taken from run method to create and display the blurred pic
			
			BufferedImage new_image = new BufferedImage(image.getWidth(),
					image.getHeight(), BufferedImage.TYPE_INT_ARGB);
			create_new_image(new_image);
			output_name = output_name.substring(0, output_name.length()-4) + ".png";
			File output_file = new File("Images/" + output_name);
			try {
				ImageIO.write(new_image, "png", output_file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ImageIcon image = new ImageIcon("Images/blurred_02_timessquare_julienneschaer-001__x_large.png");
			lblImage.setIcon(image);
			frame.getContentPane().repaint();
			
			
			
		}
		
		
		// for each pixel, find the basic color that is closest to 
		// the pixel's rgb value. Set the pixel's rgb value to this
		// basic color
		public void closest_colors() {
			//initial value is first color in colors array
			Color minColor = color_list[0];
			//initial value is the distance between color of first pixel and first color in colors array
			double minDiff = distance(pixels[0][0], color_list[0]);
			output_name = "replaced_colors_" + file_name;
			//looping over all pixels
			for (int i = 0; i < h; i++) {
				for (int j = 0; j < w; j++) {
					//looping over colors array
					for (int j2 = 0; j2 < color_list.length; j2++) {
						//Find least difference: if there is a new minimum difference between two colors, update the min var to that value
						//and update the color var to that color in colors list which had the least distance
						if(minDiff > distance(pixels[i][j], color_list[j2])) {
							minDiff = distance(pixels[i][j], color_list[j2]);
							minColor = color_list[j2];
						}
					}
					//reset minDiff and set to infinite
					minDiff = Double.MAX_VALUE;
					//apply new color
					pixels[i][j] = minColor;
				}
			}
			
		} 
		
		// calculate the distance between two colors. Think of each 
		// color as a 3d point (r,g,b), then use the distance formula
		// this can be used in closest_colors(), but does not have to be
		public double distance(Color c1, Color c2) {
			//distance formula for rgb colors
			//to find distance between colors
			// sqrt of ((red2 - red1) ^ 2 + .....)
			return Math.sqrt(Math.pow(c2.getRed()-c1.getRed(), 2) + Math.pow(c2.getGreen()-c1.getGreen(), 2) + Math.pow(c2.getBlue()-c1.getBlue(), 2));
		}
		
		
		// negate the image setting each pixel to its "opposite". 
		// The opposite is found by subtracting r, g, and b from 255.
		public void negate() {
			output_name = "negated_" + file_name;
			for (int i = 0; i < h; i++) {
				for (int j = 0; j < w; j++) {
					Color c = pixels[i][j];
					pixels[i][j] = new Color(255-c.getRed(), 255-c.getGreen(), 255-c.getBlue());
				}
			}
	
		}
		
		
		// flip an image horizontally. It should look as if you're holding 
		// the image in front of a mirror.
		public void flip() {
			output_name = "flipped_" + file_name;
			//only loop halfway horizontally -> perform two changes per iteration
			//set pixel to its mirror, and the mirror to the current one
			//mirror found by subtracting j - 1 from width  
			for (int i = 0; i < h; i++) {
				for (int j = 0; j < w/2; j++) {
					//copy to keep track of original pixel
					Color c = pixels[i][j];
					pixels[i][j] = pixels[i][w-j-1];
					pixels[i][w-j-1] = c;
					
				}
			}
			
			
		}
		
		//Algorithm: Add to the original value of the pixel, half of the value
		//of this pixel minus the value of the one before(diagonally -> i-- and j--)
		public void sharpen(int threshold) {
			//0 threshold is subtle sharpening.
			int red = 0, green = 0, blue = 0;
			output_name = "sharpened_" + file_name;
			//leaving at least 1 pixel offset top/left and two pixel offset bottom/right
			//because algorithm uses 2 previous values
			for (int i = 1+threshold; i < h-(2+threshold); i++) {
				for (int j = 1+threshold; j < w-(2+threshold); j++) {
					
					red = (int)(pixels[i][j].getRed() + 0.5 * (pixels[i][j].getRed() - pixels[i-(1+threshold)][j-(1+threshold)].getRed()));
					green = (int)(pixels[i][j].getGreen() + 0.5 * (pixels[i][j].getGreen() - pixels[i-(1+threshold)][j-(1+threshold)].getGreen()));
					blue = (int)(pixels[i][j].getBlue() + 0.5 * (pixels[i][j].getBlue() - pixels[i-(1+threshold)][j-(1+threshold)].getBlue()));
					
					//keep max value 255 and min value 0
					//change accordingly
					red = red > 255 ? 255 : red;
					red = red < 0 ? 0 : red;
					green = green > 255 ? 255 : green;
					green = green < 0 ? 0 : green;
					blue = blue > 255 ? 255 : blue;
					blue = blue < 0 ? 0 : blue;
					
					pixels[i][j] = new Color(red, green, blue);
					
				}
			}
		}
		
		// method to run the chosen image modification. For the basic
		// criteria, you should only run one method at a time, but feel free
		// to run several and explore what happens
		public void run() {
			
			//**IMP** To run the following functions, set event = true at the top
			//to run blur, set event = false and interact with button and slider
		
			
			//flip();
			
			//negate();
			
			//closest_colors();
			
			//sharpen(3);
			//0 -> subtle, 5 -> extreme
			
			//blur is called with button.
			
			edge();
		}
		
		//________________________________________________________________//
		// ***** STOP - YOU ARE NOT RESPONSIBLE FOR ANYTHING BELOW HERE *****
		
		public void create_pixel_array(BufferedImage image) {
			w = image.getWidth();
			h = image.getHeight();
			pixels = new Color[h][];
			for (int i = 0; i < h; i++) {
				pixels[i] = new Color[w];
				for (int j = 0; j < w; j++) {
					pixels[i][j] = new Color(image.getRGB(j,i));
				}
			}
		}
		
		public void create_new_image(BufferedImage new_image) {
			for (int i = 0; i < h; i++) {
				for (int j = 0; j < w; j++) {
					new_image.setRGB(j, i, pixels[i][j].getRGB());
				}
			}
			ImageIcon icon = new ImageIcon(new_image);
			lblImage.setIcon(icon);
			
			
		}
	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main window = new Main();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Main() {
		initialize();
		
			try {
				my_file = new File("Images/" + file_name);
				image = ImageIO.read(my_file);
				create_pixel_array(image);
				if(event) {
					run();
					BufferedImage new_image = new BufferedImage(image.getWidth(),
							image.getHeight(), BufferedImage.TYPE_INT_ARGB);
					create_new_image(new_image);
					output_name = output_name.substring(0, output_name.length()-4) + ".png";
					File output_file = new File("Images/" + output_name);
					ImageIO.write(new_image, "png", output_file);
				}
				
			}
			catch(IOException e){
				System.err.println(e.getMessage());
			}
		
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(50, 50, 2000, 1500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		
		slider = new JSlider();
		slider.setValue(3);
		slider.setMinimum(1);
		slider.setMajorTickSpacing(1);
		slider.setMaximum(10);
		slider.setPaintTicks(true);
		slider.setSnapToTicks(true);
		slider.setBounds(1666, 129, 200, 26);
		panel.add(slider);
		
		lblImage = new JLabel("");
		lblImage.setBounds(27, 39, 1751, 887);
		panel.add(lblImage);
		ImageIcon image = new ImageIcon("Images/02_timessquare_julienneschaer-001__x_large.jpg");
		lblImage.setIcon(image);
		
		JButton btnBlur = new JButton("Blur");
		btnBlur.setBounds(1719, 203, 97, 25);
		panel.add(btnBlur);
		btnBlur.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				//System.out.println("d");
				blur();
				
			}
		});
		
	}
}
