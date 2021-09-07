package universal_aplication;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;

public class UniversalAppJDBC {
	
	public static void main(String[] args) {
		
		new MarcoAplicacion();
	}
}
class MarcoAplicacion extends JFrame{
	
	public MarcoAplicacion() {
		
		setSize(600, 400);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		add(new LaminaAplicacion());
		setVisible(true);
		
	}
}
class LaminaAplicacion extends JPanel{
	
	private JTextArea texto;
	private JComboBox combo;
	private Connection conexion = null;
	private FileReader entrada;
	private JScrollPane scroll;
	private String[] datos = new String [4];
	
	public LaminaAplicacion() {
		
		setLayout(new BorderLayout());
		
		texto = new JTextArea();
		scroll = new JScrollPane(texto);
		add(scroll, BorderLayout.CENTER);
		
		combo = new JComboBox();
		add(combo, BorderLayout.NORTH);
		
		conectar();
		obtenerTablas();
		
		combo.setSelectedItem(null);
		combo.addActionListener(new Evento());
		
	}
	private void conectar() {
		
		try {
			
			entrada = new FileReader("config-data.txt");
			
		}
		catch(IOException e) {
			
			JOptionPane.showMessageDialog(this, "Select config-data.txt.");
			
			JFileChooser chooser = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("TXT Files", "txt");
			chooser.setFileFilter(filter);
			
			int returnVal = chooser.showOpenDialog(this);
			
			if(returnVal == JFileChooser.APPROVE_OPTION) {
			    
				try {
					
					entrada = new FileReader(chooser.getSelectedFile().getAbsoluteFile());
					
				} catch (FileNotFoundException e1) {
					
					e1.printStackTrace();
					
				}
				
			}else {
				
				chooser.cancelSelection();
				System.exit(0);
			}
		}
		
		try {
			BufferedReader buffer = new BufferedReader(entrada);
			
			for(int i=0; i<=3; i++) {
				
				datos[i]=buffer.readLine();
			}
			
			conexion=DriverManager.getConnection(datos[0], datos[1], datos[2]);	
		}catch(Exception e) {
			
			e.printStackTrace();
		}
	}
	private void obtenerTablas() {
		
		ResultSet rs = null;
		
		try {
			
		DatabaseMetaData data = conexion.getMetaData();
		rs = data.getTables(datos[3], null, null, null);
		
		while(rs.next()) {
			
			combo.addItem(rs.getString("TABLE_NAME"));
		}
		
		}catch(Exception e) {
			
			e.printStackTrace();
		}finally {
			
			try {
				rs.close();
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
		}
	}
	private class Evento implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
			
			String campo = (String) combo.getSelectedItem();
			mostrarInfoTablas(campo);
		}
	}
	private void mostrarInfoTablas(String tabla) {
		
		texto.setText("");
		
		ArrayList<String> campos = new ArrayList<String>();
		String consulta = "SELECT * FROM " + tabla + ";";
		
		try {
			
			Statement state = conexion.createStatement();
			ResultSet rs = state.executeQuery(consulta);
			ResultSetMetaData rsmd = rs.getMetaData();
			
			for(int i=1; i<=rsmd.getColumnCount(); i++) {
				
				campos.add(rsmd.getColumnName(i));
			}
			
			while(rs.next()) {
				
				for(String c: campos) {
					
					texto.append(rs.getString(c) + " ");
				}
				texto.append("\n");
			}
			
		}catch(Exception e) {
			
			e.printStackTrace();
		}
	}
}