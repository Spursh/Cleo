package screen_saver;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * The control logic and main display for screen saver.
 * 
 * @author Spursh Ujjawal
 * @version 1.0
 * @since 2017-10-24
 */
public class BallCollisionResponse {
	private final int UPDATE_RATE = 30; // Frames per second (fps)
	private final float EPSILON_TIME = 1e-2f; // Threshold for zero time
	private final int TIMER_INTERVAL = 10;
	private int numberOfBalls = 11; // Number of balls
	private Ball[] balls = new Ball[numberOfBalls];
	private Canvas canvas;
	private DesktopScreen box; // The desktop screen containing the balls
	private int canvasWidth; // Screen width
	private int canvasHeight; // Screen height

	/**
	 * Constructor to create the UI components and initialize the screen saver
	 * objects. Set the canvas to fill the screen.
	 */

	public BallCollisionResponse() {

		balls[0] = new Ball(100, 410, 25, 3, 34);
		balls[1] = new Ball(80, 350, 25, 3, -114);
		balls[2] = new Ball(530, 400, 25, 3, 14);
		balls[3] = new Ball(400, 400, 25, 3, 14);
		balls[4] = new Ball(400, 50, 25, 3, -47);
		balls[5] = new Ball(480, 320, 25, 3, 47);
		balls[6] = new Ball(80, 150, 25, 3, -114);
		balls[7] = new Ball(100, 240, 25, 3, 60);
		balls[8] = new Ball(250, 380, 25, 3, -42);
		balls[9] = new Ball(200, 80, 25, 3, -84);
		balls[10] = new Ball(500, 170, 25, 3, -42);

		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setText("Animator");
		shell.setLayout(new FillLayout());
		canvas = new Canvas(shell, SWT.NO_BACKGROUND);

		canvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent event) {
				// Create the image to fill the canvas
				Image image = new Image(shell.getDisplay(), canvas.getBounds());
				// Set up the offscreen gc
				GC gcImage = new GC(image);
				canvasWidth = image.getBounds().width;
				canvasHeight = image.getBounds().height;
				box = new DesktopScreen(0, 0, canvasWidth, canvasHeight);
				gcImage.setBackground(event.gc.getBackground());
				gcImage.fillRectangle(image.getBounds());
				gcImage.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_RED));
				box.draw(gcImage);
				for (int i = 0; i < numberOfBalls; i++) {
					balls[i].draw(gcImage);
				}
				// Draw the offscreen buffer to the screen
				event.gc.drawImage(image, 0, 0);
				image.dispose();
				gcImage.dispose();
			}
		});

		shell.open();
		Runnable runnable = new Runnable() {
			public void run() {
				// Start the ball bouncing
				startScreenSaver();
				display.timerExec(TIMER_INTERVAL, this);
			}
		};
		display.timerExec(TIMER_INTERVAL, runnable);

		// run the event loop as long as the window is open
		while (!shell.isDisposed()) {
			// read the next OS event queue and transfer it to a SWT event
			if (!display.readAndDispatch()) {
				// if there are currently no other OS event to process
				// sleep until the next OS event is available
				display.sleep();
			}
		}
		// Kill the timer
		display.timerExec(-1, runnable);
		// disposes all associated windows and their components
		display.dispose();
	}

	/** Start the ball bouncing. */
	public void startScreenSaver() {
		// Run the screen saver logic in its own thread.
		long beginTimeMillis, timeTakenMillis, timeLeftMillis;
		beginTimeMillis = System.currentTimeMillis();

		// Execute one screen saver step
		screenSaverUpdate();
		// Refresh the display
		canvas.redraw();

		// Provide the necessary delay to meet the target rate
		timeTakenMillis = System.currentTimeMillis() - beginTimeMillis;
		timeLeftMillis = 1000L / UPDATE_RATE - timeTakenMillis;
		if (timeLeftMillis < 5)
			timeLeftMillis = 5; // Set a minimum
	}

	/**
	 * One screen saver time-step. Update the screen saver objects, with proper
	 * collision detection and response.
	 */
	public void screenSaverUpdate() {
		float timeLeft = 1.0f; // One time-step to begin with

		// Repeat until the one time-step is up
		do {
			// Find the earliest collision up to timeLeft among all objects
			float tMin = timeLeft;

			// Check collision between two balls
			for (int i = 0; i < numberOfBalls; i++) {
				for (int j = 0; j < numberOfBalls; j++) {
					if (i < j) {
						balls[i].intersect(balls[j], tMin);
						if (balls[i].earliestCollisionResponse.t < tMin) {
							tMin = balls[i].earliestCollisionResponse.t;
						}
					}
				}
			}
			// Check collision between the balls and the box
			for (int i = 0; i < numberOfBalls; i++) {
				balls[i].intersect(box, tMin);
				if (balls[i].earliestCollisionResponse.t < tMin) {
					tMin = balls[i].earliestCollisionResponse.t;
				}
			}

			// Update all the balls up to the detected earliest collision time tMin,
			// or timeLeft if there is no collision.
			for (int i = 0; i < numberOfBalls; i++) {
				balls[i].update(tMin);
			}

			timeLeft -= tMin; // Subtract the time consumed and repeat
		} while (timeLeft > EPSILON_TIME); // Ignore remaining time less than threshold
	}
}
