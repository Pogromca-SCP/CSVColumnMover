package csvmover;

import static javax.swing.SwingUtilities.invokeLater;

/**
 * Contains execution entry point
 */
public class Main
{
	/**
	 * Application entry point
	 * 
	 * @param args Application arguments
	 */
	public static void main(String[] args)
	{
		invokeLater(() -> {
			new CSVMover();
		});
	}
}
