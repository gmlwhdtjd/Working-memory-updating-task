package WMUT;

import javax.swing.JPanel;
import javax.swing.SwingWorker;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.NumberFormat;

import javax.swing.JLabel;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JTextField;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;

public class StartLayer extends JPanel {
	private static final long serialVersionUID = 646385095896724474L;

	File wordSetFile;
	
	private JTextField subjectNumberTextField;
	private JFormattedTextField numberOfRepeatTextField;
	private JFormattedTextField studyTimeTextField;
	private JFormattedTextField CTI_TimeTextField;
	private JFormattedTextField encodingTimeTextField;
	private JFormattedTextField recallTimeTextField;
	private JFormattedTextField typingTimeTextField;
	
	/**
	 * Create the panel.
	 */
	public StartLayer() {
		MainFrame mainFrame = MainFrame.getInstance();
		
		//setting 값을 불러옴
		int defaultStudyTime = mainFrame.prefs.getInt("study", 3000);
		int defaultCTI_Time = mainFrame.prefs.getInt("CTI", 200);
		int defaultEncodingTime = mainFrame.prefs.getInt("encoding", 5000);
		int defaultRecallTime = mainFrame.prefs.getInt("recall", 4000);
		int defaultTypingTime = mainFrame.prefs.getInt("typing", 5000);
		int defaultNumberOfRepeat = mainFrame.prefs.getInt("repeat", 10);

		setLayout(new BorderLayout(0, 0));

		// 메뉴바
		JMenuBar menuBar = new JMenuBar();
		add(menuBar, BorderLayout.NORTH);

		JMenu mnWMUT = new JMenu("WMUT");
		menuBar.add(mnWMUT);
		
		JMenuItem menuItemAbout = new JMenuItem("About");
		menuItemAbout.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, 
						"<html>" + 
						"Working memory updating task - " + mainFrame.programVersion + "<br>" + 
						"<br>" + 
						"Developer<br>" +
						"Lee Hui-Jong<br>" +
						"Email : gmlwhdtjd@naver.com<br>" +
						"GitHub : https://github.com/gmlwhdtjd<br>" +
						"<br>" + 
						"\"Nanum font\" provided by Naver is applied to this program.<br>" + 
						"프로그램에는 네이버에서 제공한 나눔글꼴이 적용되어 있습니다.<br>" + 
						"<br>" + 
						"Copyright © 2017. Lee Hui-Jong All rights reserved.<br>" + 
						"</html>", "About", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		
		mnWMUT.add(menuItemAbout);

		JMenuItem menuItemExit = new JMenuItem("Exit");
		menuItemExit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		mnWMUT.add(menuItemExit);
		
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		JMenuItem mntmHowToMake = new JMenuItem("How to make word set file");
		mntmHowToMake.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, 
						"<html>" + 
						"Word Set 만드는 방법<br>" + 
						"<br>" + 
						"1. Microsoft Excel을 이용하여 만드는 방법<br>" + 
						"&nbsp; &nbsp;- 새 통합 문서를 생성<br>" + 
						"&nbsp; &nbsp;- A열에 단어를 차례로 빈칸 없이 입력<br>" + 
						"&nbsp; &nbsp;- 파일/다른 이름으로 저장<br>" + 
						"&nbsp; &nbsp;- 파일 형식을 CSV UTF-8 (쉼표로 분리)(.csv) 선택<br>" + 
						"&nbsp; &nbsp;- Word sets 폴더에 저장<br>" + 
						"</html>", "help", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		
		mnHelp.add(mntmHowToMake);
		// 메뉴바 끝

		// 메인 설정 Panel

		JPanel centerPanel = new JPanel();
		add(centerPanel, BorderLayout.CENTER);
		GridBagLayout gbl_centerPanel = new GridBagLayout();
		gbl_centerPanel.columnWidths = new int[] { 20, 100, 90, 140, 90, 20};
		gbl_centerPanel.rowHeights = new int[] {30, 30, 30, 30, 30, 5, 30};
		centerPanel.setLayout(gbl_centerPanel);

		JLabel subjectNumberLabel = new JLabel("피 실험자 번호 :");
		subjectNumberLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_subjectNumberLabel = new GridBagConstraints();
		gbc_subjectNumberLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_subjectNumberLabel.insets = new Insets(0, 0, 5, 5);
		gbc_subjectNumberLabel.gridx = 1;
		gbc_subjectNumberLabel.gridy = 0;
		centerPanel.add(subjectNumberLabel, gbc_subjectNumberLabel);

		subjectNumberTextField = new JTextField();
		GridBagConstraints gbc_subjectNumberTextField = new GridBagConstraints();
		gbc_subjectNumberTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_subjectNumberTextField.insets = new Insets(0, 0, 5, 5);
		gbc_subjectNumberTextField.gridx = 2;
		gbc_subjectNumberTextField.gridy = 0;
		centerPanel.add(subjectNumberTextField, gbc_subjectNumberTextField);
		
		JLabel unitLabel = new JLabel("단위 : ms");
		unitLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_unitLabel = new GridBagConstraints();
		gbc_unitLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_unitLabel.insets = new Insets(0, 0, 5, 5);
		gbc_unitLabel.gridx = 4;
		gbc_unitLabel.gridy = 1;
		centerPanel.add(unitLabel, gbc_unitLabel);
		
		NumberFormat numberFormatter = NumberFormat.getNumberInstance();
		numberFormatter.setParseIntegerOnly(true);
		numberFormatter.setGroupingUsed(false);

		JLabel studyTimeLabel = new JLabel("Study Time :");
		studyTimeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_studyTimeLabel = new GridBagConstraints();
		gbc_studyTimeLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_studyTimeLabel.insets = new Insets(0, 0, 5, 5);
		gbc_studyTimeLabel.gridx = 1;
		gbc_studyTimeLabel.gridy = 2;
		centerPanel.add(studyTimeLabel, gbc_studyTimeLabel);

		studyTimeTextField = new JFormattedTextField(numberFormatter);
		studyTimeTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		studyTimeTextField.setValue(new Integer(defaultStudyTime));
	
		GridBagConstraints gbc_studyTimeTextField = new GridBagConstraints();
		gbc_studyTimeTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_studyTimeTextField.insets = new Insets(0, 0, 5, 5);
		gbc_studyTimeTextField.gridx = 2;
		gbc_studyTimeTextField.gridy = 2;
		centerPanel.add(studyTimeTextField, gbc_studyTimeTextField);

		JLabel numberOfRepeatLabel = new JLabel("반복 횟수 :");
		numberOfRepeatLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_numberOfRepeatLabel = new GridBagConstraints();
		gbc_numberOfRepeatLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_numberOfRepeatLabel.insets = new Insets(0, 0, 5, 5);
		gbc_numberOfRepeatLabel.gridx = 3;
		gbc_numberOfRepeatLabel.gridy = 2;
		centerPanel.add(numberOfRepeatLabel, gbc_numberOfRepeatLabel);

		numberOfRepeatTextField = new JFormattedTextField(numberFormatter);
		numberOfRepeatTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		numberOfRepeatTextField.setValue(defaultNumberOfRepeat);
		GridBagConstraints gbc_numberOfRepeatTextField = new GridBagConstraints();
		gbc_numberOfRepeatTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_numberOfRepeatTextField.insets = new Insets(0, 0, 5, 5);
		gbc_numberOfRepeatTextField.gridx = 4;
		gbc_numberOfRepeatTextField.gridy = 2;
		centerPanel.add(numberOfRepeatTextField, gbc_numberOfRepeatTextField);

		JLabel CTI_TimeLabel = new JLabel("CTI Time :");
		CTI_TimeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_CTI_TimeLabel = new GridBagConstraints();
		gbc_CTI_TimeLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_CTI_TimeLabel.insets = new Insets(0, 0, 5, 5);
		gbc_CTI_TimeLabel.gridx = 1;
		gbc_CTI_TimeLabel.gridy = 3;
		centerPanel.add(CTI_TimeLabel, gbc_CTI_TimeLabel);

		CTI_TimeTextField = new JFormattedTextField(numberFormatter);
		CTI_TimeTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		CTI_TimeTextField.setValue(defaultCTI_Time);
		GridBagConstraints gbc_CTI_TimeTextField = new GridBagConstraints();
		gbc_CTI_TimeTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_CTI_TimeTextField.insets = new Insets(0, 0, 5, 5);
		gbc_CTI_TimeTextField.gridx = 2;
		gbc_CTI_TimeTextField.gridy = 3;
		centerPanel.add(CTI_TimeTextField, gbc_CTI_TimeTextField);

		JLabel encodingTimeLabel = new JLabel("Encoding Time :");
		encodingTimeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_encodingTimeLabel = new GridBagConstraints();
		gbc_encodingTimeLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_encodingTimeLabel.insets = new Insets(0, 0, 5, 5);
		gbc_encodingTimeLabel.gridx = 3;
		gbc_encodingTimeLabel.gridy = 3;
		centerPanel.add(encodingTimeLabel, gbc_encodingTimeLabel);

		encodingTimeTextField = new JFormattedTextField(numberFormatter);
		encodingTimeTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		encodingTimeTextField.setValue(defaultEncodingTime);
		
		GridBagConstraints gbc_encodingTimeTextField = new GridBagConstraints();
		gbc_encodingTimeTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_encodingTimeTextField.insets = new Insets(0, 0, 5, 5);
		gbc_encodingTimeTextField.gridx = 4;
		gbc_encodingTimeTextField.gridy = 3;
		centerPanel.add(encodingTimeTextField, gbc_encodingTimeTextField);

		JLabel recallTimeLabel = new JLabel("Recall Time :");
		recallTimeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_recallTimeLabel = new GridBagConstraints();
		gbc_recallTimeLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_recallTimeLabel.insets = new Insets(0, 0, 5, 5);
		gbc_recallTimeLabel.gridx = 1;
		gbc_recallTimeLabel.gridy = 4;
		centerPanel.add(recallTimeLabel, gbc_recallTimeLabel);

		recallTimeTextField = new JFormattedTextField(numberFormatter);
		recallTimeTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		recallTimeTextField.setValue(defaultRecallTime);
		GridBagConstraints gbc_recallTimeTextField = new GridBagConstraints();
		gbc_recallTimeTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_recallTimeTextField.insets = new Insets(0, 0, 5, 5);
		gbc_recallTimeTextField.gridx = 2;
		gbc_recallTimeTextField.gridy = 4;
		centerPanel.add(recallTimeTextField, gbc_recallTimeTextField);

		JLabel typingTimeLabel = new JLabel("Typing Time :");
		typingTimeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_typingTimeLabel = new GridBagConstraints();
		gbc_typingTimeLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_typingTimeLabel.insets = new Insets(0, 0, 5, 5);
		gbc_typingTimeLabel.gridx = 3;
		gbc_typingTimeLabel.gridy = 4;
		centerPanel.add(typingTimeLabel, gbc_typingTimeLabel);

		typingTimeTextField = new JFormattedTextField(numberFormatter);
		typingTimeTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		typingTimeTextField.setValue(defaultTypingTime);
		GridBagConstraints gbc_typingTimeTextField = new GridBagConstraints();
		gbc_typingTimeTextField.insets = new Insets(0, 0, 5, 5);
		gbc_typingTimeTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_typingTimeTextField.gridx = 4;
		gbc_typingTimeTextField.gridy = 4;
		centerPanel.add(typingTimeTextField, gbc_typingTimeTextField);

		JLabel wordSetLabel = new JLabel("Word Set :");
		GridBagConstraints gbc_wordSetLabel = new GridBagConstraints();
		gbc_wordSetLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_wordSetLabel.insets = new Insets(0, 0, 0, 5);
		gbc_wordSetLabel.gridx = 1;
		gbc_wordSetLabel.gridy = 6;
		centerPanel.add(wordSetLabel, gbc_wordSetLabel);
		wordSetLabel.setHorizontalAlignment(SwingConstants.RIGHT);

		JTextField fileNameLabel = new JTextField("");
		fileNameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_fileNameLabel = new GridBagConstraints();
		gbc_fileNameLabel.gridwidth = 2;
		gbc_fileNameLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_fileNameLabel.insets = new Insets(0, 0, 0, 5);
		gbc_fileNameLabel.gridx = 2;
		gbc_fileNameLabel.gridy = 6;
		centerPanel.add(fileNameLabel, gbc_fileNameLabel);

		JButton wordSetOpenButton = new JButton("열기");
		GridBagConstraints gbc_wordSetOpenButton = new GridBagConstraints();
		gbc_wordSetOpenButton.insets = new Insets(0, 0, 0, 5);
		gbc_wordSetOpenButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_wordSetOpenButton.gridx = 4;
		gbc_wordSetOpenButton.gridy = 6;
		centerPanel.add(wordSetOpenButton, gbc_wordSetOpenButton);

		wordSetOpenButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser("Word Sets");
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fileChooser.setMultiSelectionEnabled(false);
				fileChooser.setFileFilter(new FileNameExtensionFilter("CSV & Text file", "csv", "txt"));
				
				int returnVal = fileChooser.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					wordSetFile = fileChooser.getSelectedFile();
					fileNameLabel.setText(wordSetFile.getName());
				}
			}
		});
		// 설정

		// Start바
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.setBorder(new EmptyBorder(0, 0, 10, 10));
		add(buttonPanel, BorderLayout.SOUTH);

		JButton startTestButton = new JButton("Start Test");

		startTestButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				mainFrame.setLoadingLayer();

				new SwingWorker<Void, Void>() {
					WordSet wSet;
					boolean loadingSoccess;
					
					int settingValues[] = new int[6];

					@Override
					protected Void doInBackground() throws Exception {
						
						settingValues[0] = ((Number) studyTimeTextField.getValue()).intValue();
						settingValues[1] = ((Number) CTI_TimeTextField.getValue()).intValue();
						settingValues[2] = ((Number) encodingTimeTextField.getValue()).intValue();
						settingValues[3] = ((Number) recallTimeTextField.getValue()).intValue();
						settingValues[4] = ((Number) typingTimeTextField.getValue()).intValue();
						settingValues[5] = ((Number) numberOfRepeatTextField.getValue()).intValue();
						
						mainFrame.prefs.putInt("study", settingValues[0]);
						mainFrame.prefs.putInt("CTI", settingValues[1]);
						mainFrame.prefs.putInt("encoding", settingValues[2]);
						mainFrame.prefs.putInt("recall", settingValues[3]);
						mainFrame.prefs.putInt("typing", settingValues[4]);
						mainFrame.prefs.putInt("repeat", settingValues[5]);
						
						wSet = new WordSet();
						loadingSoccess = wSet.loadingFromFile(wordSetFile);

						return null;
					}

					@Override
					protected void done() {
						if (loadingSoccess) {
							String testString = " ";
							while (!testString.matches("^[ㄱ-ㅎㅏ-ㅣ가-힣]*$")) {
								String result = JOptionPane.showInputDialog(null, "원활한 테스트를 위해 한글을 입력해 주세요.", "", JOptionPane.WARNING_MESSAGE);
								testString = (result == null ? " " : result);
							}

							SwingUtilities.invokeLater(new Runnable() {

								@Override
								public void run() {
									try {
										JFrame testFrame = new TestFrame(wSet, subjectNumberTextField.getText(), settingValues);
										testFrame.setVisible(true);
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							});
						} else {
							SwingUtilities.invokeLater(new Runnable() {

								@Override
								public void run() {
									JPanel newLayer = new StartLayer();
									mainFrame.setLayer(newLayer);

									JOptionPane.showMessageDialog(null, "File Not Found.", "Errer", JOptionPane.ERROR_MESSAGE);
								}
							});
						}
					}
				}.execute();
			}
		});

		buttonPanel.add(startTestButton);
		// Start바 끝
	}
}