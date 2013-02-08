import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Panel;
import java.awt.ScrollPane;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import sun.awt.VerticalBagLayout;

public class GeneratorWindow extends Applet {

	private TextField minMarker;
	private TextField maxMarker;
	private Panel outer;
	private File directory;

	public GeneratorWindow() {
		// super("Marker generator");
		// setSize(450, 450);
		// addWindowListener(new BasicWindowMonitor());

		outer = new Panel();

		Panel p = new Panel();
		p.setLayout(new FlowLayout(FlowLayout.LEFT));

		Button directoyButton = new Button("Choose target directory");
		directoyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chooseFolder();
			}

		});
		p.add(directoyButton);
		minMarker = new TextField("0");
		p.add(minMarker);
		maxMarker = new TextField("0");
		p.add(maxMarker);

		Button genButton = new Button("Generate Marker(s)");
		genButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (int markerNr = loadNumber(minMarker); markerNr <= loadNumber(maxMarker); markerNr++) {
					Panel p = new Panel();
					Image image = generateMarker(markerNr);
					p.add(new ImageContainer(image));
					p.add(generateSaveButtonFor(image, markerNr));
					outer.add(p);
				}
				validate();
			}

		});
		p.add(genButton);

		Button saveAllButton = new Button("Save all markers");
		saveAllButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Starting export");
				for (int markerNr = loadNumber(minMarker); markerNr <= loadNumber(maxMarker); markerNr++) {
					saveInChoosenDirectory(generateMarker(markerNr), markerNr);
				}
				System.out.println("Export done");
			}

		});
		p.add(saveAllButton);

		outer.add(p);
		outer.setLayout(new VerticalBagLayout());

		ScrollPane s = new ScrollPane();
		s.add(outer);

		s.setPreferredSize(new Dimension(500, 500));
		this.setPreferredSize(new Dimension(500, 500));

		add(s);

	}

	private void chooseFolder() {
		JFileChooser chooser = new JFileChooser();

		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int returnVal = chooser.showSaveDialog(GeneratorWindow.this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			directory = chooser.getSelectedFile();
		}
	}

	private Component generateSaveButtonFor(final Image i,
			final int markerNumber) {
		Button b = new Button("Save " + markerNumber + ".jpg");
		b.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				saveInChoosenDirectory(i, markerNumber);
			}

		});
		return b;
	}

	private void saveInChoosenDirectory(final Image i, final int markerNumber) {
		if (directory == null)
			chooseFolder();
		if (directory != null) {

			File file = new File(directory, "" + markerNumber + ".jpg");

			try {
				ImageIO.write(toBufferedImage(i), "jpg", file);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	private static BufferedImage toBufferedImage(Image src) {
		int w = src.getWidth(null);
		int h = src.getHeight(null);
		int type = BufferedImage.TYPE_INT_RGB; // other options
		BufferedImage dest = new BufferedImage(w, h, type);
		Graphics2D g2 = dest.createGraphics();
		g2.drawImage(src, 0, 0, null);
		g2.dispose();
		return dest;
	}

	private int loadNumber(TextField tf) {
		String text = tf.getText();
		int number = 0;
		try {
			number = Integer.parseInt(text);
			if (number > 4095)
				number = 4095;
		} catch (NumberFormatException e) {
			System.out.println("Not a correct number");
		}
		return number;
	}

	private Image generateMarker(int number) {

		int size = 8;
		int maxBitLength = 12;
		int maxIntNumber = 4095;

		int[][] matrix = genMatrix(Integer.toBinaryString(number), size,
				maxBitLength);

		int[] pixels = matrixToLinearList(matrix, size);

		Image i = createImage(new MemoryImageSource(size, size, pixels, 0, size));

		return i.getScaledInstance(200, 200, java.awt.Image.SCALE_DEFAULT);
	}

	private int[] matrixToLinearList(int[][] matrix, int size) {
		int[] result = new int[size * size];
		int index = 0;
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				result[index] = matrix[x][y];
				index++;
			}
		}
		return result;
	}

	private int[][] genMatrix(String markerCode, int size, int maxBitLength) {
		int[][] result = new int[size][size];

		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				result[x][y] = black();
			}
		}

		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				if (x % 8 == 0 || y % 8 == 0)
					result[x][y] = white();
				if (x % 8 == 7 || y % 8 == 7)
					result[x][y] = white();
			}
		}

		if (markerCode.length() < maxBitLength) {
			int lllll = markerCode.length();
			for (int i = 0; i < maxBitLength - lllll; i++) {
				markerCode = "0" + markerCode;
			}
		}

		int markerIndex = 0;

		for (int x = 3; x < size - 3; x++) {

			result[2][x] = getColorFor(markerCode.charAt(markerIndex));
			markerIndex++;
		}
		for (int x = 3; x < size - 3; x++) {
			for (int y = 2; y < size - 2; y++) {
				result[x][y] = getColorFor(markerCode.charAt(markerIndex));
				markerIndex++;
			}
		}

		for (int x = 3; x < size - 3; x++) {

			result[size - 3][x] = getColorFor(markerCode.charAt(markerIndex));
			markerIndex++;
		}

		result[5][2] = white();
		result[2][5] = white();
		result[5][5] = white();
		result[2][2] = black();

		return result;
	}

	private int getColorFor(char charAt) {
		if (charAt == '0')
			return white();
		else
			return black();
	}

	private int white() {
		return gray(255);
	}

	private int black() {
		return gray(0);
	}

	private int gray(int c) {
		return ((0xff << 24) | (c << 16) | (c << 8) | c);
	}

	public static void main(String args[]) {
		GeneratorWindow window = new GeneratorWindow();
		window.setVisible(true);
	}
}
