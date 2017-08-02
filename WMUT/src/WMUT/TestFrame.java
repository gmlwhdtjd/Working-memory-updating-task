package WMUT;

import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import java.awt.Color;

import java.awt.Component;
import java.awt.Cursor;

public class TestFrame extends JFrame {
	private static final long serialVersionUID = -8198210695190555614L;

	private Random random = new Random(System.currentTimeMillis());
	private MainFrame mainFrame = MainFrame.getInstance();

	private JLabel startLabel = new JLabel("Press space Bar to Start", SwingConstants.CENTER);

	private WordCard Cards[] = new WordCard[3];
	private String lastEncodingWords[] = new String[3];
	private Vector<String> outputStrings = new Vector<String>();

	private AtomicBoolean isStarting = new AtomicBoolean(false);
	private boolean isStarted = false;
	private boolean isCTI = false;
	private boolean isTesting = false;
	private boolean isEnding = false;

	private WordSet wordSet_;

	private String subjectNumber_;

	private int studyTime;
	private int CTI_Time;
	private int encodingTime;
	private int recallTime;
	private int typingTime;

	private int numberOfRepeat;

	private Thread startingThread = new Thread() {
		@Override
		public void run() {
			try {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						startLabel.setText("3");
					}
				});
				Thread.sleep(1000);
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						startLabel.setText("2");
					}
				});
				Thread.sleep(1000);
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						startLabel.setText("1");
					}
				});
				Thread.sleep(1000);
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						startLabel.setVisible(false);

						for (int i = 0; i < Cards.length; i++) {
							String word = wordSet_.getWordFor(i);
							lastEncodingWords[i] = word;

							Cards[i].setText(word);
							Cards[i].setVisible(true);
						}
						outputStrings.add("");
						outputStrings.add(", 왼쪽, 중앙, 오른쪽");
						outputStrings.add("처음 주어진 단어, " + lastEncodingWords[0] + ", " + lastEncodingWords[1] + ", "
								+ lastEncodingWords[2]);
					}
				});

				// 테스트 시작
				encodingThread.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
	private Thread encodingThread = new Thread() {

		private int selectedCard;

		@Override
		public void run() {
			isStarted = true;

			long reactionTime_Study = System.currentTimeMillis();

			try {
				Thread.sleep(studyTime);
			} catch (InterruptedException e) {
			}

			reactionTime_Study = System.currentTimeMillis() - reactionTime_Study;

			outputStrings.add("");
			outputStrings.add("Study Time, " + reactionTime_Study);
			outputStrings.add("반복횟수, 단어 위치, 단어, Encoding Time");

			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					for (int i = 0; i < Cards.length; i++) {
						Cards[i].setText("");
					}
				}
			});

			for (int i = 0; i < numberOfRepeat; i++) {

				// 새로운 단어를 보여줄 상자를 선택
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						selectedCard = random.nextInt(3);
						Cards[selectedCard].setSelection(true);
					}
				});

				isCTI = true;

				try {
					Thread.sleep(CTI_Time);
				} catch (InterruptedException e) {
				}

				isCTI = false;

				// 새로운 단어를 보여줌
				long reactionTime_Encoding = System.currentTimeMillis();

				String encodingWord = wordSet_.getWordFor(selectedCard);
				lastEncodingWords[selectedCard] = encodingWord;

				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						Cards[selectedCard].setText(encodingWord);
					}
				});

				try {
					Thread.sleep(encodingTime);
				} catch (InterruptedException e) {
				}

				reactionTime_Encoding = System.currentTimeMillis() - reactionTime_Encoding;

				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						Cards[selectedCard].setText("");
						Cards[selectedCard].setSelection(false);
					}
				});

				// 결과 저장
				String position = "";
				switch (selectedCard) {
				case 0:
					position = "왼쪽, ";
					break;
				case 1:
					position = "중앙, ";
					break;
				case 2:
					position = "오른쪽, ";
					break;
				}
				outputStrings.add("" + (i + 1) + ", " + position + encodingWord + ", " + reactionTime_Encoding);
			}
			isStarted = false;

			recallThread.start();
		}
	};
	private Thread recallThread = new Thread() {
		@Override
		public void run() {
			isTesting = true;

			int cardOrder[] = null;
			long reactionTime_recall[] = new long[3];
			long reactionTime_typing[] = new long[3];
			String testerAnswer[] = new String[3];

			switch (random.nextInt(6)) {
			case 0:
				cardOrder = new int[] { 0, 1, 2 };
				break;
			case 1:
				cardOrder = new int[] { 0, 2, 1 };
				break;
			case 2:
				cardOrder = new int[] { 1, 0, 2 };
				break;
			case 3:
				cardOrder = new int[] { 1, 2, 0 };
				break;
			case 4:
				cardOrder = new int[] { 2, 0, 1 };
				break;
			case 5:
				cardOrder = new int[] { 2, 1, 0 };
				break;
			default:
				break;
			}

			for (int i = 0; i < cardOrder.length; i++) {
				WordCard currnetCard = Cards[cardOrder[i]];
				boolean isTyped = false;

				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						currnetCard.setTestMode(true);
					}
				});

				reactionTime_recall[cardOrder[i]] = System.currentTimeMillis();

				try {
					Thread.sleep(recallTime);
				} catch (InterruptedException e) {
					isTyped = true;
				}
				
				reactionTime_recall[cardOrder[i]] = System.currentTimeMillis() - reactionTime_recall[cardOrder[i]];
				
				if (isTyped) {
					reactionTime_typing[cardOrder[i]] = System.currentTimeMillis();

					try {
						Thread.sleep(typingTime);
					} catch (InterruptedException e) {}

					reactionTime_typing[cardOrder[i]] = System.currentTimeMillis() - reactionTime_typing[cardOrder[i]];
					testerAnswer[cardOrder[i]] = currnetCard.getTextField().getText();
				}
				else {
					reactionTime_typing[cardOrder[i]] = 0;
					testerAnswer[cardOrder[i]] = "";
				}

				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						currnetCard.setTestMode(false);
						currnetCard.setText("");
					}
				});
			}

			outputStrings.add("");
			outputStrings.add("결과, 왼쪽, 중앙, 오른쪽");
			outputStrings.add("Recall Time, " + reactionTime_recall[0] + ", " + reactionTime_recall[1] + ", "
					+ reactionTime_recall[2]);
			outputStrings.add("Typing Time, " + reactionTime_typing[0] + ", " + reactionTime_typing[1] + ", "
					+ reactionTime_typing[2]);
			outputStrings
					.add("정답, " + lastEncodingWords[0] + ", " + lastEncodingWords[1] + ", " + lastEncodingWords[2]);
			outputStrings.add("답안, " + testerAnswer[0] + ", " + testerAnswer[1] + ", " + testerAnswer[2]);

			endingThread.start();
		}
	};
	private Thread endingThread = new Thread() {
		@Override
		public void run() {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					for (int i = 0; i < Cards.length; i++) {
						Cards[i].setVisible(false);
					}
					startLabel.setText("Thank you for participation");
					startLabel.setVisible(true);
				}
			});
			isEnding = true;
		};
	};

	
	/**
	 * Create the Frame.
	 * settingValues must contain six values.
	 * values order = studyTime, CTI_Time, encodingTime, recallTime, typingTime, numberOfRepeat.
	 * @throws Exception from settingValues
	 */
	public TestFrame(WordSet wordSet, String subjectNumber, int settingValues[]) throws Exception {
		setUndecorated(true);
		
		GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		device.setFullScreenWindow(this);
		
		// 변수 설정
		wordSet_ = wordSet;
		
		subjectNumber_ = subjectNumber;
		
		if (settingValues.length == 6) {
			studyTime = settingValues[0];
			CTI_Time = settingValues[1];
			encodingTime = settingValues[2];
			recallTime = settingValues[3];
			typingTime = settingValues[4];
			numberOfRepeat = settingValues[5];
		}
		else {
			throw new Exception("settingValues must contain six values");
		}	

	
		// output 세팅
		outputStrings.add("피실험자 번호, " + subjectNumber_);
		outputStrings.add("");
		outputStrings.add("설정값");
		outputStrings.add("Study Time, CTI Time, Encoding Time, Recall Time, Typing Time, 반복 횟수");
		outputStrings.add("" + studyTime + ", " + CTI_Time + ", " + encodingTime + ", " + recallTime + ", " + typingTime
				+ ", " + numberOfRepeat);

		// 레이아웃 세팅
		JPanel contentPanel = (JPanel) getContentPane();
		contentPanel.setBackground(Color.WHITE);
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		contentPanel.setFocusable(true);
		contentPanel.requestFocusInWindow();

		// Transparent 16 x 16 pixel cursor image.
		BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);

		// Create a new blank cursor.
		Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");

		// Set the blank cursor to the JFrame.
		contentPanel.setCursor(blankCursor);

		// 컨포넌트 생성
		Component verticalGlue_1 = Box.createVerticalGlue();
		Component verticalGlue_2 = Box.createVerticalGlue();

		Box horizontalBox = Box.createHorizontalBox();

		Component horizontalGlue_1 = Box.createHorizontalGlue();
		Component horizontalGlue_2 = Box.createHorizontalGlue();
		Component horizontalGlue_3 = Box.createHorizontalGlue();
		Component horizontalGlue_4 = Box.createHorizontalGlue();

		for (int i = 0; i < Cards.length; i++) {
			Cards[i] = new WordCard("", getSize().width);

			WordCard curCard = Cards[i];
			JTextField cardTextField = curCard.getTextField();

			// 모든 글자를 입력할때 인터럽트, 스페이스를 누르면 인터럽트, 단 아무것도 입력되지 않았을 경우는 무시함
			((AbstractDocument) cardTextField.getDocument()).setDocumentFilter(new DocumentFilter() {

				@Override
				public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) // 키보드에 의한 입력에만
																										// 반응
						throws BadLocationException {
					if (isTesting) {
						if (!curCard.isFirstInput.getAndSet(true)) { // 첫번째 입력을 디텍팅, testingThread로 인터럽트
							recallThread.interrupt();
						}
					}
					super.insertString(fb, offset, string, attr);
				}

				@Override
				public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
						throws BadLocationException {
					if (text.equals(" ")) { // 공백이 입력되었을때
						text = ""; // text field 에 절대로 공백이 키보드에 의해 입력되지 않음

						if (isTesting) {
							if (cardTextField.getText().length() > 0) { // 값이 없을때 끝나지 않음
								recallThread.interrupt();
							}
						}
					}
					super.replace(fb, offset, length, text, attrs);
				}
			});

			// 엔터를 통한 시작 인터럽트, 종료 인터럽트 가능, 단 아무것도 입력되지 않았을 경우는 무시함
			cardTextField.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (isTesting) {
						if (!curCard.isFirstInput.getAndSet(true)) {
							recallThread.interrupt();
						} else {
							if (cardTextField.getText().length() > 0) {
								recallThread.interrupt();
							}
						}
					}
				}
			});
		}
		
		// key 이벤트 등록
		contentPanel.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {

				if (e.getKeyCode() == KeyEvent.VK_SPACE) {
					if (!isStarting.getAndSet(true))
						startingThread.start();

					if (isStarted) {
						if (!isCTI) {
							encodingThread.interrupt();
						}
					}

					if (isEnding) {

						new SwingWorker<Void, Void>() {

							@Override
							protected Void doInBackground() throws Exception {
								// 파일 출력
								try {
									BufferedWriter out;
									
									File resultsDir = new File("Results");
									if (resultsDir.exists()) {
										out = new BufferedWriter(new OutputStreamWriter(
												new FileOutputStream("Results/TestResult_" + subjectNumber_ + ".csv"), "UTF8"));
									}
									else {
										out = new BufferedWriter(new OutputStreamWriter(
												new FileOutputStream("TestResult_" + subjectNumber_ + ".csv"), "UTF8"));
									}

									for (int i = 0; i < outputStrings.size(); i++) {
										if (i == 0)
											out.write("\uFEFF" + outputStrings.get(i));
										else
											out.write(outputStrings.get(i));

										out.newLine();
									}

									out.close();
								} catch (Exception e) {
									e.printStackTrace();
								}
								return null;
							}

							@Override
							protected void done() {
								SwingUtilities.invokeLater(new Runnable() {

									@Override
									public void run() {
										dispose();
										
										JPanel newLayer = new StartLayer();
										
										mainFrame.setLayer(newLayer);
									}
								});

							}
						}.execute();
					}
				}
			}
		});

		startLabel.setBackground(Color.WHITE);

		startLabel.setFont(MainFrame.getInstance().font.deriveFont(Font.PLAIN, (int) (getSize().width / 15)));

		// 컴포넌트 추가
		contentPanel.add(verticalGlue_1);
		contentPanel.add(horizontalBox);

		horizontalBox.add(horizontalGlue_1);
		horizontalBox.add(Cards[0]);
		horizontalBox.add(horizontalGlue_2);
		horizontalBox.add(startLabel);
		horizontalBox.add(Cards[1]);
		horizontalBox.add(horizontalGlue_3);
		horizontalBox.add(Cards[2]);
		horizontalBox.add(horizontalGlue_4);

		contentPanel.add(verticalGlue_2);

		// 시작 상태로 만듬
		for (int i = 0; i < Cards.length; i++) {
			Cards[i].setVisible(false);
		}

		startLabel.setVisible(true);
	}
}
