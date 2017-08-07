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

	private WordCard cards[] = new WordCard[3];
	private String lastEncodingWords[] = new String[3];
	
	//저장용 string
	private Vector<Vector<String>> outputStrings = new Vector<Vector<String>>();

	//flow Control values
	private AtomicBoolean isStarting = new AtomicBoolean(false);
	private boolean isStarted = false;
	private boolean isCTI = false;
	private boolean isRecalling = false;
	private boolean isEnding = false;

	//test values
	private WordSet wordSet_;

	private String subjectNumber_;
	
	private int trial;
	private int interTrialInterval;

	private int studyTime;
	private int updatingSteps;
	
	private int CTI_Time;
	private int encodingTime;
	private int recallTime;
	private int typingTime;

	//Thread
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

				// 테스트 시작
				testingThread.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
	private Thread testingThread = new Thread() {

		private int selectedCard;

		@Override
		public void run() {
			for (int i = 1; i <= trial; i++) {
				
				isStarted = true;
				
				// Study -------------------------------------------------
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						startLabel.setVisible(false);

						for (int i = 0; i < cards.length; i++) {
							String word = wordSet_.getWordFor(i);
							lastEncodingWords[i] = word;

							cards[i].setText(word);
							cards[i].setVisible(true);
						}
					}
				});
			
				long reactionTime_Study = System.currentTimeMillis();

				try {
					Thread.sleep(studyTime);
				} catch (InterruptedException e) {
				}

				reactionTime_Study = System.currentTimeMillis() - reactionTime_Study;

				outputStrings.get(0).add(""+ i + "," + lastEncodingWords[0] + "," + lastEncodingWords[1] + ","+ lastEncodingWords[2] + ","+ reactionTime_Study);

				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						for (int i = 0; i < cards.length; i++) {
							cards[i].setText("");
						}
					}
				});
				// END Study --------------------------------------------

				// Encoding ---------------------------------------------
				for (int j = 1; j <= updatingSteps; j++) {
					// 새로운 단어를 보여줄 상자를 선택
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							selectedCard = random.nextInt(3);
							cards[selectedCard].setSelection(true);
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
							cards[selectedCard].setText(encodingWord);
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
							cards[selectedCard].setText("");
							cards[selectedCard].setSelection(false);
						}
					});

					// 결과 저장
					String position = "";
					switch (selectedCard) {
					case 0:
						position = "왼쪽";
						break;
					case 1:
						position = "중앙";
						break;
					case 2:
						position = "오른쪽";
						break;
					}
					outputStrings.get(1).add("" + i + "," + j + "," + position + "," + encodingWord + "," + reactionTime_Encoding);
				}
				// END Encoding ---------------------------------------------
				isStarted = false;
				
				// Recalling ------------------------------------------------
				isRecalling = true;

				int cardOrder[] = null;
				long reactionTime_recall;
				long reactionTime_typing;
				String testerAnswer;

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
					cardOrder = new int[] { 2, 1, 0 };
					break;
				}

				for (int j = 1; j <= cardOrder.length; j++) {
					int currentCardIndex = cardOrder[j-1];
					WordCard currnetCard = cards[currentCardIndex];
					boolean isTyped = false;
					String correction = "";

					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							currnetCard.setTestMode(true);
						}
					});

					reactionTime_recall = System.currentTimeMillis();

					try {
						Thread.sleep(recallTime);
					} catch (InterruptedException e) {
						isTyped = true;
					}
					
					reactionTime_recall = System.currentTimeMillis() - reactionTime_recall;
					
					if (isTyped) {
						reactionTime_typing = System.currentTimeMillis();

						try {
							Thread.sleep(typingTime);
						} catch (InterruptedException e) {}

						reactionTime_typing = System.currentTimeMillis() - reactionTime_typing;
						testerAnswer = currnetCard.getTextField().getText();
					}
					else {
						reactionTime_typing = 0;
						testerAnswer = "";
					}

					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							currnetCard.setTestMode(false);
							currnetCard.setText("");
						}
					});
					
					String position = "";
					switch (currentCardIndex) {
					case 0:
						position = "왼쪽";
						break;
					case 1:
						position = "중앙";
						break;
					case 2:
						position = "오른쪽";
						break;
					}
					if(stringTest(lastEncodingWords[currentCardIndex], testerAnswer))
						correction = ",correct";
					outputStrings.get(2).add("" + i + "," + j + "," + position + "," + lastEncodingWords[currentCardIndex]
							+ "," + testerAnswer + "," + reactionTime_recall + "," + reactionTime_typing + correction);
				}
				isRecalling = false;
				// END Recalling --------------------------------------------
				
				// for Next Step
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						for (int i = 0; i < cards.length; i++) {
							cards[i].setVisible(false);
							cards[i].isFirstInput.set(false);
							wordSet_.cardNumberReset();
						}
					}
				});
				
				// Inter Trial Interval----------------------------------
				if (i != trial) {
					try {
						Thread.sleep(interTrialInterval);
					} catch (InterruptedException e) {}
				}
				// END Inter Trial Interval ------------------------------
			}
			endingThread.start();
		}
	};
	private Thread endingThread = new Thread() {
		@Override
		public void run() {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					for (int i = 0; i < cards.length; i++) {
						cards[i].setVisible(false);
					}
					outputStrings.get(2).add(",,,,,,총 정답수,=COUNTA(H10:H"+(trial*3+9)+")");
					
					startLabel.setText("Thank you for participation");
					startLabel.setVisible(true);
				}
			});
			isEnding = true;
		};
	};
	
	/**
	 * Create the Frame.
	 * settingValues must contain 8 values.
	 * values order = trial, interTrialInterval, studyTime, updatingSteps, CTI_Time, encodingTime, recallTime, typingTime .
	 * @throws Exception from settingValues
	 */
	public TestFrame(WordSet wordSet, String subjectNumber, int settingValues[]) throws Exception {
		setUndecorated(true);
		
		GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		device.setFullScreenWindow(this);
		//setSize(mainFrame.getSize());
		
		// 변수 설정
		wordSet_ = wordSet;
		
		subjectNumber_ = subjectNumber;
		
		if (settingValues.length == 8) {
			trial = settingValues[0];
			interTrialInterval = settingValues[1];
			studyTime = settingValues[2];
			updatingSteps = settingValues[3];
			CTI_Time = settingValues[4];
			encodingTime = settingValues[5];
			recallTime = settingValues[6];
			typingTime = settingValues[7];
		}
		else {
			throw new Exception("settingValues must contain 8 values");
		}	
	
		// output 세팅
		for (int i = 0; i < 3; i++) {
			outputStrings.add(new Vector<String>());
			outputStrings.get(i).add("피실험자 번호," + subjectNumber_);
			outputStrings.get(i).add("");
			outputStrings.get(i).add("설정값");
			outputStrings.get(i).add("Trial,Inter Trial Interval,Study Time,Updating Steps");
			outputStrings.get(i).add("" +  trial + "," +  interTrialInterval + "," +  studyTime + "," +  updatingSteps);
			outputStrings.get(i).add("CTI Time,Encoding Time,Recall Time,Typing Time");
			outputStrings.get(i).add("" +  CTI_Time + "," +  encodingTime + "," +  recallTime + "," +  typingTime);
			outputStrings.get(i).add("");
		}
		outputStrings.get(0).add("Trial,왼쪽,중앙,오른쪽,Study Time");
		outputStrings.get(1).add("Trial,Step,단어 위치,단어,Encoding Time");
		outputStrings.get(2).add("Trial,Test 순서,단어 위치,정답,답안,Recall Time,Typing Time,정답여부");
		
		// 레이아웃 세팅
 		JPanel contentPanel = (JPanel) getContentPane();
		contentPanel.setBackground(Color.WHITE);
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		contentPanel.setFocusable(true);
		contentPanel.requestFocusInWindow();

		// 마우스 지우기
		BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");
		contentPanel.setCursor(blankCursor);

		// 컨포넌트 생성
		Component verticalGlue_1 = Box.createVerticalGlue();
		Component verticalGlue_2 = Box.createVerticalGlue();

		Box horizontalBox = Box.createHorizontalBox();

		Component horizontalGlue_1 = Box.createHorizontalGlue();
		Component horizontalGlue_2 = Box.createHorizontalGlue();
		Component horizontalGlue_3 = Box.createHorizontalGlue();
		Component horizontalGlue_4 = Box.createHorizontalGlue();

		for (int i = 0; i < cards.length; i++) {
			cards[i] = new WordCard("", getSize().width);

			WordCard curCard = cards[i];
			JTextField cardTextField = curCard.getTextField();

			// 모든 글자를 입력할때 인터럽트, 스페이스를 누르면 인터럽트, 단 아무것도 입력되지 않았을 경우는 무시함
			((AbstractDocument) cardTextField.getDocument()).setDocumentFilter(new DocumentFilter() {

				@Override
				public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) // 키보드에 의한 입력에만
																										// 반응
						throws BadLocationException {
					if (isRecalling) {
						if (!curCard.isFirstInput.getAndSet(true)) { // 첫번째 입력을 디텍팅, testingThread로 인터럽트
							testingThread.interrupt();
						}
					}
					super.insertString(fb, offset, string, attr);
				}

				@Override
				public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
						throws BadLocationException {
					if (text.equals(" ")) { // 공백이 입력되었을때
						text = ""; // text field 에 절대로 공백이 키보드에 의해 입력되지 않음

						if (isRecalling) {
							if (cardTextField.getText().length() > 0) { // 값이 없을때 끝나지 않음
								testingThread.interrupt();
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
					if (isRecalling) {
						if (!curCard.isFirstInput.getAndSet(true)) {
							testingThread.interrupt();
						} else {
							if (cardTextField.getText().length() > 0) {
								testingThread.interrupt();
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
							testingThread.interrupt();
						}
					}

					if (isEnding) {
						new SwingWorker<Void, Void>() {

							@Override
							protected Void doInBackground() throws Exception {
								// 파일 출력
								try {
									BufferedWriter out[] = new BufferedWriter[3];
									
									File resultsDir = new File("Results");
									if (resultsDir.exists()) {
										out[0] = new BufferedWriter(new OutputStreamWriter(
												new FileOutputStream("Results/TestResult_" + subjectNumber_ + "_FirstWords.csv"), "UTF8"));
										out[1] = new BufferedWriter(new OutputStreamWriter(
												new FileOutputStream("Results/TestResult_" + subjectNumber_ + "_Encoding.csv"), "UTF8"));
										out[2] = new BufferedWriter(new OutputStreamWriter(
												new FileOutputStream("Results/TestResult_" + subjectNumber_ + "_Recall.csv"), "UTF8"));
									}
									else {
										out[0] = new BufferedWriter(new OutputStreamWriter(
												new FileOutputStream("TestResult_" + subjectNumber_ + "_FirstWords.csv"), "UTF8"));
										out[1] = new BufferedWriter(new OutputStreamWriter(
												new FileOutputStream("TestResult_" + subjectNumber_ + "_Encoding.csv"), "UTF8"));
										out[2] = new BufferedWriter(new OutputStreamWriter(
												new FileOutputStream("TestResult_" + subjectNumber_ + "_Recall.csv"), "UTF8"));
									}
									for (int i = 0; i < outputStrings.size(); i++) {
										for (int j = 0; j < outputStrings.get(i).size(); j++) {
											if (j == 0)
												out[i].write("\uFEFF" + outputStrings.get(i).get(j));
											else
												out[i].write(outputStrings.get(i).get(j));

											out[i].newLine();
										}
										out[i].close();
									}
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
		horizontalBox.add(cards[0]);
		horizontalBox.add(horizontalGlue_2);
		horizontalBox.add(startLabel);
		horizontalBox.add(cards[1]);
		horizontalBox.add(horizontalGlue_3);
		horizontalBox.add(cards[2]);
		horizontalBox.add(horizontalGlue_4);

		contentPanel.add(verticalGlue_2);

		// 시작 상태로 만듬
		for (int i = 0; i < cards.length; i++) {
			cards[i].setVisible(false);
		}

		startLabel.setVisible(true);
	}
	
	/**
	 * string을 직접 byte별로 비교함 -> 한글등 유니코드 문자비교용
	 */
	private boolean stringTest(String str1, String str2) {
		if (str1.getBytes().length != str2.getBytes().length)
			return false;
		
		for (int i = 0; i < str1.getBytes().length; i++) {
			if (str1.getBytes()[i] != str2.getBytes()[i]) 
				return false;
		}

		return true;
	}
}
