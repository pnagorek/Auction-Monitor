package com.nagorek.auctionmonitor.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.nagorek.auctionmonitor.model.AuctionMonitorEngine;

public class AuctionMonitorWindow extends JFrame{

	private static final long serialVersionUID = 1L;
	private static final String title = "AuctionMonitor"; 

	protected int width = 1200;
	protected int height = width*3/5;
	protected final int columns = 7;
	protected final int rows = 7;
	protected final int numRows = 100;

	private String[][] optionsTab = {
			{"Department", "combo"}, 
			{"Price", "text2", "From: ", "To: "}, 
			{"Type of auction", "radio", "buy now", "bidding", "both"}, 
			{"Condition", "radio", "new", "secondhand", "both"}, 
			{"Location", "text", "Location"}
	};

	private String[] columnNames = {"ID", "Name", "'Buy now' price", "Current price", "Condition", "Location", "Time left", "Quantity"};
	private int cpw = width/columns*columnNames.length/10;
	private int[] columnsWidth = new int[]{cpw, cpw*3, cpw, cpw, cpw, cpw, cpw, cpw};

	private JPanel[] panels = new JPanel[optionsTab.length];
	private JLabel imageLabel;
	private DefaultTableModel model;

	private AuctionMonitorEngine ame;
	
	private Map<String, String> searchResult = new HashMap<>();

	public AuctionMonitorWindow() throws Exception{

		ame = new AuctionMonitorEngine();

		model = new DefaultTableModel(numRows, columnNames.length) ;
		model.setColumnIdentifiers(columnNames);
		
		init();
		
		int i = 0;
		Elements results = ame.quickSearch("msi rx 480");
		for(Element e : results){
			if(i != 0){
				String data = ame.grabData(e.attr("href"));
				searchResult.put(data.split("<>")[0], data.substring(data.split("<>").length+2, data.length()));
				model.setValueAt(data.split("<>")[0], i, 0);
				model.setValueAt(data.split("<>")[1], i, 1);
				model.setValueAt(data.split("<>")[2], i, 2);
				model.setValueAt(data.split("<>")[3], i, 3);
				model.setValueAt(data.split("<>")[4], i, 4);
				model.setValueAt(data.split("<>")[5], i, 5);
				model.setValueAt(data.split("<>")[6], i, 6);
				model.setValueAt(data.split("<>")[7], i, 7);
				System.out.println(data.split("<>")[8]); //zdjecie
			}
			i++;
		}
	}

	private void init(){

		setSize(width, height);
		setTitle(title);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);

		GridBagLayout gridbag = new GridBagLayout();

		int[] col = new int[columns];
		for(int i = 0; i < columns; i++){
			col[i] = width/columns - 30;
		}
		int[] row = new int[rows];
		for(int i = 0; i < rows; i++){
			row[i] = height/rows - 10;
		}
		gridbag.columnWidths = col;
		gridbag.rowHeights = row;
		setLayout(gridbag);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;

		c.gridwidth = 5;
		c.gridheight = 6;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(0, 5, 5, 5);

		JTable table = new JTable(model);	
		for(int i = 0; i < model.getColumnCount(); i++){
			table.getColumnModel().getColumn(i).setPreferredWidth(columnsWidth[i]);
		}
		table.setFillsViewportHeight(true);
		table.setAutoCreateRowSorter(true);
		JScrollPane scroll = new JScrollPane(table);
		add(scroll, c);

		c.gridwidth = 1;
		c.gridheight = 1;
		c.gridy = 6;	
		c.insets = new Insets(0, 5, 0, 5);
			
		for(int i = 0; i < optionsTab.length; i++){
			c.gridx = i;
			JPanel panel = new JPanel();
			panels[i] = panel;
			panel.setBorder(BorderFactory.createTitledBorder(optionsTab[i][0]));
			panel.setPreferredSize(new Dimension(width/columns, height/rows));
			switch(optionsTab[i][1]){
				case "combo" : createComboBox(panel, ame.getDepartments()/*new String[]{""}*/);
				break;
				case "radio" : createRadioButtons(panel, optionsTab[i]);
				break;
				case "text" : createTextField(panel, optionsTab[i][2]);
				break;
				case "text2" : createTextField(panel, optionsTab[i][2]);
				createTextField(panel, optionsTab[i][3]);
				break;
				default : break;
			}
			add(panel, c);
		}
		
		c.gridx = 5;
		c.gridy = 0;
		c.gridwidth = 2;
		c.gridheight = 3;
		c.insets = new Insets(0, 0, 0, 5);


		imageLabel = new JLabel(new ImageIcon("res/picture.png"));
		JScrollPane pane = new JScrollPane(imageLabel);
		add(pane, c);
		
		imageLabel.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(final MouseEvent e) {
				if (e.getClickCount() == 2){
					JDialog dialog = new JDialog();
					JLabel label = new JLabel(imageLabel.getIcon());
					dialog.setLocationRelativeTo(null);
					dialog.add(label);
					dialog.pack();
					dialog.setVisible(true);
				}
			}
		});

		table.addMouseListener(new MouseAdapter() { //test
			@Override
			public void mouseClicked(final MouseEvent e) {
				if (e.getClickCount() == 1) {
					JTable jTable= (JTable)e.getSource();
					int row = jTable.getSelectedRow();
					String valueInCell = (String)jTable.getValueAt(row, 0);
					String r = searchResult.get(valueInCell);
					r = r.split("<>")[7];
					loadImage(r);
				}
			}
		});

		




		setLocationRelativeTo(null);
		setVisible(true);		
	}
	
	private void createRadioButtons(JComponent c, String[] text){
		ButtonGroup group = new ButtonGroup();
		for(int i = 2; i < text.length; i++){
			JRadioButton button = new JRadioButton(text[i]);
			button.addActionListener(null);
			button.setSelected(i == text.length-1 ? true : false);
			group.add(button);
			c.setLayout(new BoxLayout(c, BoxLayout.PAGE_AXIS));
			c.add(button);
		}		
	}

	private void createComboBox(JComponent c, String[] tab){
		JComboBox<String> combo = new JComboBox<>(tab);
		combo.addActionListener(null);
		c.add(combo);
	}

	private void createTextField(JComponent c, String text){
		JTextField field = new JTextField(12);
		field.setText(text);
		field.setForeground(Color.LIGHT_GRAY);
		field.addFocusListener(new FocusListener(){

			@Override
			public void focusGained(FocusEvent e) {
				field.setText("");
				field.setForeground(Color.BLACK);
			}

			@Override
			public void focusLost(FocusEvent e) {
				if(field.getText().equals("")){
					field.setText(text);
					field.setForeground(Color.LIGHT_GRAY);
				}
			}			
		});

		c.add(field);
	}

	private void loadImage(String url){
		try {
			BufferedImage image = ImageIO.read(new URL(url));
			ImageIcon ii = new ImageIcon(image);
			imageLabel.setIcon(ii);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
