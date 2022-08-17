
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.Axis;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Photoshop extends Application {

	@SuppressWarnings({ "unchecked", "unchecked", "unchecked", "unchecked", "rawtypes", "unchecked" })
	@Override
	public void start(Stage stage) throws FileNotFoundException {

		stage.setTitle("Photoshop");

		// Read the image
		Image image = new Image(new FileInputStream("raytrace.jpg"));

		// Create the graphical view of the image
		ImageView imageView = new ImageView(image);

		// Create the simple GUI
		Button invert_button = new Button("Invert");
		Label gammaLabel = new Label("Input gamma power:");
		TextField gammaText = new TextField();
		Button gamma_button = new Button("Gamma Correct");
		Button contrast_button = new Button("Contrast Stretching");
		Button histogram_button = new Button("Histogram");
		Button histogram_equalisation = new Button("Equalisation");
		final ToggleGroup group = new ToggleGroup();

		RadioButton rb1 = new RadioButton("Red");
		rb1.setToggleGroup(group);
		rb1.setSelected(true);
		RadioButton rb2 = new RadioButton("Green");
		rb2.setToggleGroup(group);
		RadioButton rb3 = new RadioButton("Blue");
		rb3.setToggleGroup(group);
		RadioButton rb4 = new RadioButton("Brightness");
		rb4.setToggleGroup(group);

		Button cc_button = new Button("Cross Correlation");
		Button resetButton = new Button("Reset");

		// Defining X axis
		NumberAxis xAxis = new NumberAxis(0, 255, 15);
		xAxis.setLabel("r");

		// Defining y axis
		NumberAxis yAxis = new NumberAxis(0, 255, 15);
		yAxis.setLabel("s");

		@SuppressWarnings("rawtypes")
		LineChart<Number, Number> lineChart = new LineChart<Number, Number>(xAxis, yAxis);
		lineChart.setAnimated(false);
		lineChart.setTitle("Set Contrast Stretching Values");
		@SuppressWarnings("rawtypes")
		XYChart.Series<Number, Number> series = new XYChart.Series<>();
		series.getData().add(new XYChart.Data(-10.0, -10.0));
		series.getData().add(new XYChart.Data(50.0, 20.0));
		series.getData().add(new XYChart.Data(200.0, 225.0));
		series.getData().add(new XYChart.Data(265.0, 265.0));
		lineChart.getData().addAll(series);

		for (Data<Number, Number> data : series.getData()) {
			Node node = data.getNode();
			node.setCursor(Cursor.HAND);
			node.setOnMouseDragged(e -> {
				Point2D pointInScene = new Point2D(e.getSceneX(), e.getSceneY());
				double xAxisLoc = xAxis.sceneToLocal(pointInScene).getX() + 1;
				double yAxisLoc = xAxis.sceneToLocal(pointInScene).getY() * -1.29;
				Number x = xAxis.getValueForDisplay(xAxisLoc);
				Number y = xAxis.getValueForDisplay(yAxisLoc);
				data.setXValue(x);
				data.setYValue(y);
			});

		}

		// Defining X axis
		Axis<String> xAxisHist = new CategoryAxis();
		xAxis.setLabel("Pixel intensity");

		// Defining y axis
		NumberAxis yAxisHist = new NumberAxis(0, 65000, 5000);
		yAxis.setLabel("Frequency");

		@SuppressWarnings("rawtypes")
		BarChart<String, Number> barChartHist = new BarChart<String, Number>(xAxisHist, yAxisHist);
		// lineChartHist.setAnimated(false);
		barChartHist.setTitle("RGBHistogram");
		@SuppressWarnings("rawtypes")
		Series seriesHist = new XYChart.Series();
		barChartHist.getData().addAll(seriesHist);

		// Defining X axis
		Axis<String> xAxisHistEqualisation = new CategoryAxis();
		xAxis.setLabel("Pixel intensity");

		// Defining y axis
		NumberAxis yAxisHistEqualisation = new NumberAxis(0, 300000, 25000);
		yAxis.setLabel("Frequency");

		@SuppressWarnings("rawtypes")
		BarChart<String, Number> barChartHistEqualisation = new BarChart<String, Number>(xAxisHistEqualisation,
				yAxisHistEqualisation);
		// lineChartHist.setAnimated(false);
		barChartHistEqualisation.setTitle("Histogram Equalisation");
		@SuppressWarnings("rawtypes")
		Series seriesHistEqualisation = new XYChart.Series();
		barChartHistEqualisation.getData().addAll(seriesHistEqualisation);

		// Add all the event handlers (this is a minimal GUI - you may try to do
		// better)
		invert_button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("Invert");
				// At this point, "image" will be the original image
				// imageView is the graphical representation of an image
				// imageView.getImage() is the currently displayed image

				// Let's invert the currently displayed image by calling the
				// invert function
				// later in the code
				Image inverted_image = ImageInverter(imageView.getImage());
				// Update the GUI so the new image is displayed
				imageView.setImage(inverted_image);
			}
		});

		gamma_button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("Gamma Correction");

				if (gammaText.getText().length() != 0) {
					// If a value is entered increase/decrease the gamma the
					// input amount.
					double gammaValue = Double.valueOf(gammaText.getText());
					// Call the function to change the gamma.
					Image gamma_image = ImageGamma(imageView.getImage(), gammaValue);
					// Update the GUI so the new image is displayed.
					imageView.setImage(gamma_image);
				}
			}
		});

		contrast_button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {

				double r1 = (double) series.getData().get(1).getXValue();
				double s1 = (double) series.getData().get(1).getYValue();
				double r2 = (double) series.getData().get(2).getXValue();
				double s2 = (double) series.getData().get(2).getYValue();

				System.out.println("Contrast Stretching");
				// Call the function to change the gamma.
				Image contrastImage = ImageContrast(imageView.getImage(), r1, s1, r2, s2);
				// Update the GUI so the new image is displayed.
				imageView.setImage(contrastImage);
			}
		});

		histogram_button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("Histogram");
				String colourSelected;
				if (rb1.isSelected()) {
					colourSelected = "Red";
				} else if (rb2.isSelected()) {
					colourSelected = "Green";
				} else if (rb3.isSelected()) {
					colourSelected = "Blue";
				} else {
					colourSelected = "Brightness";
				}

				ImageHistogram(imageView.getImage(), barChartHist, seriesHist, colourSelected);

				// Update the GUI so the new image is displayed.
				// imageView.setImage(histogramImage);
			}
		});

		histogram_equalisation.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("Histogram Equalisation");

				Image histogramImage = HistogramEqualisation(imageView.getImage(), barChartHistEqualisation,
						seriesHistEqualisation);

				// Update the GUI so the new image is displayed.
				imageView.setImage(histogramImage);
			}
		});

		cc_button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("Cross Correlation");
				Image ccImage = CCImage(imageView.getImage());

				// Update the GUI so the new image is displayed.
				imageView.setImage(ccImage);
			}

		});

		resetButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("Reset");
				imageView.setImage(image);
			}
		});

		// Using a flow pane
		FlowPane root = new FlowPane();
		// Gaps between buttons
		root.setVgap(10);
		root.setHgap(5);

		// Add all the buttons and the image for the GUI
		root.getChildren().addAll(invert_button, gammaLabel, gammaText, gamma_button, contrast_button, histogram_button,
				rb1, rb2, rb3, rb4, histogram_equalisation, cc_button, resetButton, imageView, lineChart, barChartHist,
				barChartHistEqualisation);

		// Display to user
		Scene scene = new Scene(root, 1300, 1000);
		stage.setScene(scene);
		stage.show();
	}

	private Image CCImage(Image image) {
		double red;
		double blue;
		double green;
		double greyVal;

		double[][] filter = new double[5][5];

		// Find the width and height of the image to be processing
		int width = (int) image.getWidth();
		int height = (int) image.getHeight();
		// Get an interface to read from the original image passed as the
		// parameter to the function.
		WritableImage ccImage = new WritableImage(width, height);
		// Get an interface to write to that image memory
		PixelWriter ccImageWriter = ccImage.getPixelWriter();
		// Get an interface to read from the original image passed as the
		// parameter to the function.
		PixelReader imageReader = image.getPixelReader();
		// Iterate over all pixels
		for (int y = 4; y < height - 4; y++) {
			for (int x = 4; x < width - 4; x++) {
				// For each pixel, get the colour
				Color color = imageReader.getColor(x, y);

				for (int i = y - 2; y < y + 2; y++) {
					for (int j = x - 2; x < j + 2 - 4; x++) {
						Color colorFilter = imageReader.getColor(j, i);
						red = (int) (color.getRed() * 255);
						blue = (int) (color.getBlue() * 255);
						green = (int) (color.getGreen() * 255);
						greyVal = (red + blue + green) / 3;

						if ((j == x - 2 && i == y - 2) || (j == x + 2 && i == y + 2) || (j == x + 2 && i == y - 2)
								|| (j == x - 2 && i == y + 2)) {
							greyVal = greyVal * -4;
						} else if ((j == x - 1 && i == y - 2) || (j == x - 2 && i == y - 1)
								|| (j == x + 1 && i == y + 2) || (j == x + 2 && i == y + 1)
								|| (j == x + 2 && i == y - 1) || (j == x + 1 && i == y - 2)
								|| (j == x - 2 && i == y + 1) || (j == x - 1 && i == y + 2)) {
							greyVal = greyVal * -1;
						} else if ((j == x - 1 && i == y - 1) || (j == x + 1 && i == y + 1)
								|| (j == x + 1 && i == y - 1) || (j == x - 1 && i == y + 1)) {
							greyVal = greyVal * 2;
						} else if ((j == x - 1 && i == 0) || (j == x + 1 && i == 0) || (j == 0 && i == y - 1)
								|| (j == 0 && i == y + 1)) {
							greyVal = greyVal * 3;
						} else if (j == 0 && i == 0) {
							greyVal = greyVal * 4;
						}

						filter[(j - x) + 2][(i - y) + 2] = greyVal;
					}
				}

				double maxVal = filter[0][0];
				for (int j = 0; j < filter.length; j++) {
					for (int i = 0; i < filter[j].length; i++) {
						if (filter[j][i] > maxVal) {
							maxVal = filter[j][i];
						}
					}
				}

				double minVal = filter[0][0];
				for (int j = 0; j < filter.length; j++) {
					for (int i = 0; i < filter[j].length; i++) {
						if (filter[j][i] < minVal) {
							minVal = filter[j][i];
						}
					}
				}

				for (int j = 0; j < filter.length; j++) {
					for (int i = 0; i < filter[j].length; i++) {
						filter[j][i] = (((filter[j][i] - minVal) * 255) / (maxVal - minVal));
					}
				}
				
				for (int j = 0; j < filter.length; j++) {
					for (int i = 0; i < filter[j].length; i++) {
						color = Color.color(filter[j][i] / 255.0, filter[j][i] / 255.0, filter[j][i] / 255.0);

						ccImageWriter.setColor(x - 2, y - 2, color);
					}
				}

			}
		}
		return ccImage;
	}

	// Function of change gamma

	public Image HistogramEqualisation(Image image, BarChart<String, Number> barChartHistEqualisation,
			Series<Number, Number> seriesHistEqualisation) {

		// Initialisation of RGB values ready for initialisation.
		double red;
		double blue;
		double green;
		double greyVal;
		double[] greyScale = new double[256];

		double[] histogramEqualisation = new double[256];
		int[] newMapping = new int[256];

		// Find the width and height of the image to be processing
		int width = (int) image.getWidth();
		int height = (int) image.getHeight();
		// Get an interface to read from the original image passed as the
		// parameter to the function.
		WritableImage equalisedImage = new WritableImage(width, height);
		// Get an interface to write to that image memory
		PixelWriter equalisedImageWriter = equalisedImage.getPixelWriter();
		// Get an interface to read from the original image passed as the
		// parameter to the function.
		PixelReader imageReader = image.getPixelReader();
		// Iterate over all pixels
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				// For each pixel, get the colour
				Color color = imageReader.getColor(x, y);

				// also need to declare/assign red, h, w, etc.
				red = (int) (color.getRed() * 255);
				blue = (int) (color.getBlue() * 255);
				green = (int) (color.getGreen() * 255);
				greyScale[((int) (red + blue + green) / 3)] += 1;
			}
		}

		for (int j = 0; j < 256; j++) {
			if (j == 0) {
				histogramEqualisation[0] = (int) greyScale[j];
			} else {
				histogramEqualisation[j] = (histogramEqualisation[j - 1] + greyScale[j]);
			}
		}

		for (int k = 0; k < 256; k++) {
			newMapping[k] = (int) ((255) * (histogramEqualisation[k] / 288470));
		}

		for (int i = 0; i < 256; i++) {
			seriesHistEqualisation.getData().add(new XYChart.Data(Integer.toString(i), histogramEqualisation[i]));
		}

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				// For each pixel, get the colour
				Color color = imageReader.getColor(x, y);

				// also need to declare/assign red, h, w, etc.
				red = (int) (color.getRed() * 255);
				blue = (int) (color.getBlue() * 255);
				green = (int) (color.getGreen() * 255);
				greyVal = (red + blue + green) / 3;

				color = Color.color(newMapping[(int) greyVal] / 255.0, newMapping[(int) greyVal] / 255.0,
						newMapping[(int) greyVal] / 255.0);

				equalisedImageWriter.setColor(x, y, color);

			}
		}
		return equalisedImage;
	}

	// Function of change gamma
	@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
	public void ImageHistogram(Image image, BarChart<String, Number> barChartHist, Series<Number, Number> seriesHist,
			String colourSelected) {

		// Initialisation of RGB values ready for initialisation.
		double red;
		double blue;
		double green;
		double brightness;

		int[][] histogram = new int[256][4];

		// Find the width and height of the image to be processing
		int width = (int) image.getWidth();
		int height = (int) image.getHeight();
		// Get an interface to read from the original image passed as the
		// parameter to the function.
		WritableImage equalisedImage = new WritableImage(width, height);
		// Get an interface to write to that image memory
		PixelWriter equalisedImageWriter = equalisedImage.getPixelWriter();
		// Get an interface to read from the original image passed as the
		// parameter to the function.
		PixelReader imageReader = image.getPixelReader();
		// Iterate over all pixels
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				// For each pixel, get the colour
				Color color = imageReader.getColor(x, y);

				// also need to declare/assign red, h, w, etc.
				red = (int) (color.getRed() * 255);
				blue = (int) (color.getBlue() * 255);
				green = (int) (color.getGreen() * 255);
				brightness = (int) (color.getBrightness() * 255);

				histogram[(int) red][0]++; // [0] for red, [1] green
				histogram[(int) green][1]++;
				histogram[(int) blue][2]++;
				histogram[(int) brightness][3]++;
			}
		}

		for (int i = 0; i < 256; i++) {
			if (colourSelected == "Red") {
				seriesHist.getData().add(new XYChart.Data(Integer.toString(i), histogram[i][0]));
			} else if (colourSelected == "Green") {
				seriesHist.getData().add(new XYChart.Data(Integer.toString(i), histogram[i][1]));
			} else if (colourSelected == "Blue") {
				seriesHist.getData().add(new XYChart.Data(Integer.toString(i), histogram[i][2]));
			} else {
				seriesHist.getData().add(new XYChart.Data(Integer.toString(i), histogram[i][3]));
			}
		}
	}

	// Function of change gamma
	public Image ImageContrast(Image image, double r1, double s1, double r2, double s2) {

		// Initialisation of RGB values ready for initialisation.
		double red;
		double blue;
		double green;

		// Find the width and height of the image to be processing
		int width = (int) image.getWidth();
		int height = (int) image.getHeight();
		// Create a new image of that width and height
		WritableImage contrastImage = new WritableImage(width, height);
		// Get an interface to write to that image memory
		PixelWriter contrastImageWriter = contrastImage.getPixelWriter();
		// Get an interface to read from the original image passed as the
		// parameter to the function.
		PixelReader imageReader = image.getPixelReader();

		// Iterate over all pixels
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				// For each pixel, get the colour
				Color color = imageReader.getColor(x, y);
				// Do something (increase/decrease gamma) - the getColor
				// function returns colours as
				// 0..1 doubles (then change the exponent the value is raised
				// to, to get the new value)

				red = color.getRed() * 255.0;
				blue = color.getBlue() * 255.0;
				green = color.getGreen() * 255.0;

				if (red < r1) {
					red = ((s1 / r1) * red);
				} else if (r1 <= red && red <= r2) {
					red = ((((s2 - s1) / (r2 - r1)) * (red - r1)) + s1);
				} else {
					red = ((((255.0 - s2) / (255.0 - r2)) * (red - r2)) + s2);
				}

				if (blue < r1) {
					blue = ((s1 / r1) * blue);
				} else if (r1 <= blue && blue <= r2) {
					blue = ((((s2 - s1) / (r2 - r1)) * (blue - r1)) + s1);
				} else {
					blue = ((((255.0 - s2) / (255.0 - r2)) * (blue - r2)) + s2);
				}

				if (green < r1) {
					green = ((s1 / r1) * green);
				} else if (r1 <= green && green <= r2) {
					green = ((((s2 - s1) / (r2 - r1)) * (green - r1)) + s1);
				} else {
					green = ((((255.0 - s2) / (255.0 - r2)) * (green - r2)) + s2);
				}

				// Set the overall colour based upon new individual colour
				// values for the individual pixel.
				color = Color.color(red / 255.0, green / 255.0, blue / 255.0);

				// Make changes to the interface with new colour.
				contrastImageWriter.setColor(x, y, color);
			}
		}
		// Return the changed image.
		return contrastImage;
	}

	// Function of change gamma
	public Image ImageGamma(Image image, double gammaValue) {

		// Initialisation of RGB values ready for initialisation.
		double red;
		double blue;
		double green;

		double[] pixelLookUpTable = new double[256];

		for (int i = 0; i < 256; i++) {
			double tempVal = Math.pow(((double) i / 255), (1.0 / gammaValue));
			pixelLookUpTable[i] = tempVal;
		}

		// Find the width and height of the image to be processing
		int width = (int) image.getWidth();
		int height = (int) image.getHeight();
		// Create a new image of that width and height
		WritableImage gammaImage = new WritableImage(width, height);
		// Get an interface to write to that image memory
		PixelWriter gammaImageWriter = gammaImage.getPixelWriter();
		// Get an interface to read from the original image passed as the
		// parameter to the function.
		PixelReader imageReader = image.getPixelReader();

		// Iterate over all pixels
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				// For each pixel, get the colour
				Color color = imageReader.getColor(x, y);
				// Do something (increase/decrease gamma) - the getColor
				// function returns colours as
				// 0..1 doubles (then change the exponent the value is raised
				// to, to get the new value)

				red = pixelLookUpTable[(int) (color.getRed() * 255)];
				green = pixelLookUpTable[(int) (color.getGreen() * 255)];
				blue = pixelLookUpTable[(int) (color.getBlue() * 255)];

				// Set the overall colour based upon new individual colour
				// values for the individual pixel.
				color = Color.color(red, green, blue);

				// Make changes to the interface with new colour.
				gammaImageWriter.setColor(x, y, color);
			}
		}
		// Return the changed image.
		return gammaImage;
	}

	// Example function of invert
	public Image ImageInverter(Image image) {
		// Find the width and height of the image to be process
		int width = (int) image.getWidth();
		int height = (int) image.getHeight();
		// Create a new image of that width and height
		WritableImage inverted_image = new WritableImage(width, height);
		// Get an interface to write to that image memory
		PixelWriter inverted_image_writer = inverted_image.getPixelWriter();
		// Get an interface to read from the original image passed as the
		// parameter to
		// the function
		PixelReader image_reader = image.getPixelReader();

		// Iterate over all pixels
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				// For each pixel, get the colour
				Color color = image_reader.getColor(x, y);
				// Do something (in this case invert) - the getColor function
				// returns colours as
				// 0..1 doubles (we could multiply by 255 if we want 0-255
				// colours)
				color = Color.color(1.0 - color.getRed(), 1.0 - color.getGreen(), 1.0 - color.getBlue());
				// Note: for gamma correction you may not need the divide by 255
				// since getColor
				// already returns 0-1, nor may you need multiply by 255 since
				// the Color.color
				// function consumes 0-1 doubles.

				// Apply the new colour
				inverted_image_writer.setColor(x, y, color);
			}
		}
		return inverted_image;
	}

	public static void main(String[] args) {
		launch();
	}

}