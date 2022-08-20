package csvmover;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JFileChooser;
import javax.swing.JComboBox;
import org.json.easy.dom.JSONArray;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JLabel;
import javax.swing.JButton;
import org.json.easy.dom.JSONObject;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.LinkedList;
import java.io.FileNotFoundException;
import org.json.easy.serialization.JSONReader;

import static java.awt.Toolkit.getDefaultToolkit;
import static javax.swing.SwingUtilities.invokeLater;
import static org.json.easy.serialization.JSONSerializer.deserializeArray;

/**
 * CVS Columns Mover
 */
public class CSVMover extends JFrame
{
	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Contains source directory
	 */
	private final JTextField src;

	/**
	 * File chooser for directory selection
	 */
	private final JFileChooser fc;

	/**
	 * Work mode selection
	 */
	private final JComboBox<String> mode;
	
	/**
	 * Work modes
	 */
	private final JSONArray modes;

	/**
	 * Alerts display window
	 */
	private final Alert alert;

	/**
	 * Window construction
	 */
	public CSVMover()
	{
		super("Przesuwacz kolumn");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(600, 300);
		final Dimension dim = getDefaultToolkit().getScreenSize();
		setLocation(dim.width / 2 - getSize().width / 2, dim.height / 2 - getSize().height / 2);
		setLayout(new GridBagLayout());
		final GridBagConstraints c = new GridBagConstraints();
		fc = new JFileChooser();
		alert = new Alert();
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setMultiSelectionEnabled(false);
		c.gridx = 0;
		c.gridy = 0;
		modes = loadModes();
		
		if (modes != null)
		{
			mode = new JComboBox<String>(loadModesNames(modes));
			add(mode, c);
			++c.gridy;
		}
		else
		{
			mode = null;
		}
		
		add(new JLabel("Plik z danymi:"), c);
		++c.gridy;
		src = new JTextField(40);
		add(src, c);
		++c.gridx;
		final JButton s = new JButton("Przeglądaj");
		s.addActionListener(e -> invokeLater(() -> getFile(src, s)));
		add(s, c);
		--c.gridx;
		++c.gridy;
		final JButton b = new JButton("Do dzieła!");

		if (modes != null)
		{
			b.addActionListener(e -> invokeLater(() -> {
				b.setEnabled(false);
				move(src.getText(), modes.getObjectElement(mode.getSelectedIndex()));
				b.setEnabled(true);
			}));	
		}

		add(b, c);
		setResizable(false);
		setVisible(true);
	}

	/**
	 * Choosing a file
	 * 
	 * @param target Text field to which the directory will be injected
	 * @param parent Parent button for file chooser
	 */
	private void getFile(final JTextField target, final JButton parent)
	{	
		final int val = fc.showOpenDialog(parent);

		if (val == JFileChooser.APPROVE_OPTION)
		{
			target.setText(fc.getSelectedFile().getAbsolutePath());
		}
	}

	/**
	 * Moves the columns in source file
	 * 
	 * @param file Source file
	 * @param config Selected work mode config
	 */
	private void move(final String file, final JSONObject config)
	{	
		if (file == null || !file.endsWith(".csv"))
		{
			alert("Podany plik nie jest plikiem csv!");
			return;
		}
		
		final String[][] data = separate(readLines(file));
		
		if (data == null)
		{
			return;
		}
		
		final Header[] cols = createColumns(data[0], config.getArrayField("columns"));
		
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file)))
		{
			for (int i = 0; i < cols.length; ++i)
			{
				if (i != 0)
				{
					writer.append(';');
				}
				
				writer.append(cols[i].name);
			}
			
			writer.append('\n');
			
			for (int i = 1; i < data.length; ++i)
			{
				for (int j = 0; j < cols.length; ++j)
				{
					final Header tmp = cols[j];
					
					if (j != 0)
					{
						writer.append(';');
					}
					
					writer.append(tmp.from == null || tmp.from >= data[i].length ? "" : data[i][tmp.from]);
				}
				
				writer.append('\n');
			}
		}
		catch (IOException i)
		{
			alert("Wystąpił błąd podczas zapisywania pliku!");
			return;
		}
		catch (Exception e)
		{
			alert("Wystąpił nieznany błąd podczas zapisywania pliku!");
			return;
		}
		
		alert("Proces przebiegł bez zakłóceń.");
	}
	
	/**
	 * Reads all file lines
	 * 
	 * @param file File to read
	 * @return File lines or null if an error occured
	 */
	private String[] readLines(final String file)
	{
		try (BufferedReader reader = new BufferedReader(new FileReader(file)))
		{
			final LinkedList<String> lines = new LinkedList<String>();
			String line;
			
			while ((line = reader.readLine()) != null)
			{
				lines.add(line);
			}
			
			return lines.toArray(new String[lines.size()]);
		}
		catch (FileNotFoundException f)
		{
			alert("Nie znaleziono podanego pliku!");
		}
		catch (IOException i)
		{
			alert("Wystąpił błąd podczas wczytywania pliku!");
		}
		catch (Exception e)
		{
			alert("Wystąpił nieznany błąd podczas wczytywania pliku!");
		}
		
		return null;
	}
	
	/**
	 * Separates the lines
	 * 
	 * @param lines Lines to separate
	 * @return Separated lines
	 */
	private String[][] separate(final String[] lines)
	{
		if (lines == null)
		{
			return null;
		}
		
		final String[][] res = new String[lines.length][];
		
		for (int i = 0; i < res.length; ++i)
		{
			res[i] = lines[i].split(";");
		}
		
		return res;
	}

	/**
	 * Displays an alert window with message
	 * 
	 * @param message Message to display
	 */
	private void alert(final String message)
	{
		alert.show(message);
	}
	
	/**
	 * Loads avaiable work modes
	 * 
	 * @return Loaded modes
	 */
	private JSONArray loadModes()
	{
		try (JSONReader reader = new JSONReader(new FileReader("polisy.json")))
		{
			return deserializeArray(reader);
		}
		catch (FileNotFoundException f)
		{
			alert("Nie znaleziono pliku z dostępnymi polisami!");
		}
		
		return null;
	}
	
	/**
	 * Loads work modes names
	 * 
	 * @param arr Loaded modes
	 * @return Loaded modes names
	 */
	private String[] loadModesNames(final JSONArray arr)
	{
		final LinkedList<String> names = new LinkedList<String>();
		arr.forEach(val -> names.add(val.asObject().getStringField("name")));
		return names.toArray(new String[names.size()]);
	}
	
	/**
	 * Generates column offsets map
	 * 
	 * @param headers Headers from collected data
	 * @param columns Desired columns
	 * @return Column offsets map
	 */
	private Header[] createColumns(final String[] headers, final JSONArray columns)
	{
		final LinkedList<Header> res = new LinkedList<Header>();
		
		if (columns != null)
		{	
			columns.forEach(val -> {
				final JSONObject obj = val.asObject();
				final Header h = new Header(obj.getStringField("name"));
				res.add(h);
				
				for (int i = 0; i < headers.length; ++i)
				{
					if (headers[i].equals(obj.getStringField("from")))
					{
						h.from = i;
						i = headers.length;
					}
				}
			});
		}
		
		return res.toArray(new Header[res.size()]);
	}
}
