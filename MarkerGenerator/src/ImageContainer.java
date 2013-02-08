import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

public class ImageContainer extends Component {

	Image img;

	public ImageContainer(Image generateMarker) {
		img = generateMarker;
	}

	public void paint(Graphics g) {
		g.drawImage(img, 0, 0, null);
	}

	public Dimension getPreferredSize() {
		if (img == null) {
			return new Dimension(100, 100);
		} else {
			return new Dimension(img.getWidth(null), img.getHeight(null));
		}
	}
}
