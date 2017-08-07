package WMUT;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.io.InputStream;
import java.util.prefs.Preferences;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class MainFrame extends JFrame {
	private static final long serialVersionUID = 6928734264952432657L;
	
	String programVersion = "1.1.1";
	
	Font font;
	Preferences prefs = Preferences.userNodeForPackage(MainFrame.class);
			
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				MainFrame mainFrame = MainFrame.getInstance();
				mainFrame.setContentPane(new StartLayer());
				mainFrame.setVisible(true);
				
				File dirForResult = new File("Results");
				if (!dirForResult.exists()) {
					if (!dirForResult.mkdirs())
						System.err.println("디렉토리 생성 실패");
				}
				File dirForWordSet = new File("Word Sets");
				if (!dirForWordSet.exists()) {
					if (!dirForWordSet.mkdirs())
						System.err.println("디렉토리 생성 실패");
				}
			}
		});
	}
	
	/**
	 * 싱글톤 관련 메서드
	 */
	private static class Singleton {
		private static final MainFrame instance = new MainFrame();
	}

	public static MainFrame getInstance() {
		return Singleton.instance;
	}

	/**
	 * 메인 글래스 생성자
	 */
	private MainFrame() {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				setTitle("Working memory updating task -" + programVersion);
				setPreferredSize(new Dimension(460, 350));
				setForeground(Color.WHITE);
				setBackground(Color.WHITE);
				setResizable(false);

				try {
					InputStream is = getClass().getResourceAsStream("/NanumGothic.ttc");
					font = Font.createFont(Font.TRUETYPE_FONT, is);
				} catch (Exception e) {
					e.printStackTrace();
				}

				pack();
			}
		});
	}

	public void setLayer(JPanel newLayer) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				setContentPane(newLayer);

				newLayer.setFocusable(true);
				newLayer.requestFocusInWindow();

				setVisible(true);
			}
		});
	}

	public void setLoadingLayer() {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				JPanel nowLoadingLayer = new JPanel();
				nowLoadingLayer.setLayout(new BorderLayout());
				nowLoadingLayer.setBackground(Color.WHITE);

				ImageIcon loading = new ImageIcon(MainFrame.class.getResource("/ajax-loader.gif"));
				JLabel nowLoadingLable = new JLabel("Now loading", loading, JLabel.CENTER);
				nowLoadingLable.setFont(font.deriveFont(Font.PLAIN, 15));
				nowLoadingLable.setHorizontalAlignment(SwingConstants.CENTER);
				nowLoadingLayer.add(nowLoadingLable);

				setLayer(nowLoadingLayer);
			}
		});
	}
}