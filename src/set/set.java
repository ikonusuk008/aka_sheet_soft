package set;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class set extends JFrame implements ActionListener {
	Properties prop = new Properties();

	JPanel panel = new JPanel(new GridLayout(1, 2));
	JTextField t_field = new JTextField();
	JLabel label = new JLabel("移動幅の設定:");
	JButton jButton = new JButton("変更");

	public set() throws HeadlessException {
		super();

		try {
			prop.load(new FileInputStream("./resource/prop1.properties"));
		} catch (FileNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		label.setFont(new Font("Serif",Font.BOLD ,20));

		panel.add(label);
		panel.add(t_field);
		panel.add(jButton);
		jButton.addActionListener(this);
		jButton.setFont(new Font("", Font.BOLD, 20));
		t_field.setText(prop.getProperty("hennsuu"));
		t_field.setHorizontalAlignment(JTextField.LEFT);

		t_field.setFont(new Font("", Font.BOLD, 20));

		ImageIcon icon = new ImageIcon("./resource/img/akasheet.jpg");
		this.setIconImage(icon.getImage());
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.add(panel);
		this.setSize(500, 80);
		this.setAlwaysOnTop(true);
		this.setVisible(true);
		this.setTitle("移動幅の設定");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO 自動生成されたメソッド・スタブ
		new set();
	}

	public void actionPerformed(ActionEvent arg0) {
		// TODO 自動生成されたメソッド・スタブ
		if (jButton == arg0.getSource()) {
			try {
				prop.load(new FileInputStream("./resource/prop1.properties"));
			} catch (FileNotFoundException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}

			prop.setProperty("hennsuu", t_field.getText());

			try {
				prop.store(new FileOutputStream("./resource/prop1.properties"), "prop1");
			} catch (FileNotFoundException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
			System.exit(0);
		}
	}
}
