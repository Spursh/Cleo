package screen_saver;

import org.eclipse.swt.graphics.GC;

/**
 * Desktop Screen, containing the bouncing balls.
 * 
 * @author	Spursh Ujjawal
 * @version 1.0
 * @since	2017-10-24 
 */
public class DesktopScreen {
	int minX, maxX, minY, maxY; // Box's bounds

	/** Constructors */
	public DesktopScreen(int x, int y, int width, int height) {
		minX = x;
		minY = y;
		maxX = x + width - 1;
		maxY = y + height - 1;
	}

	/** Set or reset the boundaries of the box. */
	public void set(int x, int y, int width, int height) {
		minX = x;
		minY = y;
		maxX = x + width - 1;
		maxY = y + height - 1;
	}

	/** Draw itself using the given graphic context. */
	public void draw(GC g) {
		g.drawRectangle(minX, minY, maxX - minX - 1, maxY - minY - 1);
	}
}
