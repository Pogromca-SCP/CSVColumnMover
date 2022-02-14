package csvmover;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JFileChooser;
import javax.swing.JComboBox;
import org.json.simple.JSONArray;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionEvent;
import org.json.simple.JSONObject;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.LinkedList;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.json.simple.parser.JSONParser;

import java.io.BufferedWriter;
import java.io.FileWriter;

import static java.awt.Toolkit.getDefaultToolkit;
import static javax.swing.SwingUtilities.invokeLater;

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
	private JTextField src;

	/**
	 * File chooser for directory selection
	 */
	private JFileChooser fc;

	/**
	 * Work mode selection
	 */
	private JComboBox<String> mode;
	
	/**
	 * Work modes
	 */
	private JSONArray modes;

	/**
	 * Alerts display window
	 */
	private Alert alert;

	/**
	 * Window construction
	 */
	public CSVMover()
	{
		super("");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(600, 300);
		Dimension dim = getDefaultToolkit().getScreenSize();
		setLocation(dim.width / 2 - getSize().width / 2, dim.height / 2 - getSize().height / 2);
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
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
		
		add(new JLabel("Plik z danymi:"), c);
		++c.gridy;
		src = new JTextField(40);
		add(src, c);
		++c.gridx;
		JButton s = new JButton("Przeglądaj");
		s.addActionListener((ActionEvent e) -> invokeLater(() -> getFile(src, s)));
		add(s, c);
		--c.gridx;
		++c.gridy;
		JButton b = new JButton("Do dzieła!");

		if (modes != null)
		{
			b.addActionListener((ActionEvent e) -> invokeLater(() -> {
				b.setEnabled(false);
				move(src.getText(), get(modes, mode.getSelectedIndex()));
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
	private void getFile(JTextField target, JButton parent)
	{	
		int val = fc.showOpenDialog(parent);

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
	private void move(String file, JSONObject config)
	{	
		if (file == null || !file.endsWith(".csv"))
		{
			alert("Podany plik nie jest plikiem csv!");
			return;
		}
		
		if (config == null)
		{
			alert("Podana polisa jest błędna!");
			return;
		}
		
		String[][] data = separate(readLines(file));
		
		if (data == null)
		{
			return;
		}
		
		Header[] cols = createColumns(data[0], get(config, "columns"));
		
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file + ".csv")))
		{
			for (Header h : cols)
			{
				writer.append(h.name);
				writer.append(';');
			}
			
			writer.append('\n');
			
			for (int i = 1; i < data.length; ++i)
			{
				for (int j = 0; j < cols.length; ++j)
				{
					Header tmp = cols[j];
					writer.append(tmp.from == null ? "" : data[i][tmp.from]);
					writer.append(';');
				}
				
				writer.append('\n');
			}
		}
		catch (Exception e)
		{
			alert("Wystąpił błąd i proces został zatrzymany.");
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
	private String[] readLines(String file)
	{
		try (BufferedReader reader = new BufferedReader(new FileReader(file)))
		{
			LinkedList<String> lines = new LinkedList<String>();
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
			alert("Wystąpił błąd i proces został zatrzymany.");
		}
		catch (Exception e)
		{
			alert("Wystąpił błąd i proces został zatrzymany.");
		}
		
		return null;
	}
	
	/**
	 * Separates the lines
	 * 
	 * @param lines Lines to separate
	 * @return Separated lines
	 */
	private String[][] separate(String[] lines)
	{
		if (lines == null)
		{
			return null;
		}
		
		String[][] res = new String[lines.length][];
		
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
	private void alert(String message)
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
		try
		{
			Object obj = new JSONParser().parse(new FileReader("polisy.json"));
			return (JSONArray) obj;
		}
		catch (FileNotFoundException f)
		{
			alert("Nie znaleziono pliku z dostępnymi polisami!");
		}
		catch (Exception e)
		{
			alert("Wystąpił błąd przy wczytywaniu dostępnych polis!");
		}
		
		return null;
	}
	
	/**
	 * Loads work modes names
	 * 
	 * @param arr Loaded modes
	 * @return Loaded modes names
	 */
	private String[] loadModesNames(JSONArray arr)
	{
		LinkedList<String> names = new LinkedList<String>();
		
		for (Object o : arr)
		{
			try
			{
				JSONObject obj = (JSONObject) o;
				names.add((String) obj.get("name"));
			}
			catch (Exception e) {}
		}
		
		return names.toArray(new String[names.size()]);
	}
	
	/**
	 * Safely gets JSONObject element
	 * 
	 * @param obj Object to use
	 * @param name Property name
	 * @return Selected element
	 */
	@SuppressWarnings("unchecked")
	private <T> T get(JSONObject obj, String name)
	{
		try
		{
			return (T) obj.get(name);
		}
		catch (Exception e)
		{
			return null;
		}
	}
	
	/**
	 * Safely gets JSONArray element
	 * 
	 * @param arr Array to use
	 * @param index Element index
	 * @return Selected element
	 */
	@SuppressWarnings("unchecked")
	private <T> T get(JSONArray arr, int index)
	{
		try
		{
			return (T) arr.get(index);
		}
		catch (Exception e)
		{
			return null;
		}
	}
	
	/**
	 * Generates column offsets map
	 * 
	 * @param headers Headers from collected data
	 * @param columns Desired columns
	 * @return Column offsets map
	 */
	private Header[] createColumns(String[] headers, JSONArray columns)
	{
		LinkedList<Header> res = new LinkedList<Header>();
		
		if (columns != null)
		{
			for (Object o : columns)
			{
				try
				{
					JSONObject obj = (JSONObject) o;
					Header h = new Header(get(obj, "name"));
					res.add(h);
					
					for (int i = 0; i < headers.length; ++i)
					{
						if (headers[i].equals(get(obj, "from")))
						{
							h.from = i;
							i = headers.length;
						}
					}
				}
				catch (Exception e) {}
			}
		}
		
		return res.toArray(new Header[res.size()]);
	}
}
