package com.gem.notepad;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

public class NotePad extends JFrame {

	/**
	 * autor:Chation Usefor:���±�
	 */
	private static final long serialVersionUID = 1L;
	private TextDAO textDAO;
	private JMenu fileMenu;
	private JMenuItem menuOpen; // ��
	private JMenuItem menuSave; // ����
	private JMenuItem menuSaveAs; // ���Ϊ
	private JMenuItem menuClose; // �ر�

	private JMenu editMenu;
	private JMenuItem menuCut; // ����
	private JMenuItem menuCopy; // ����
	private JMenuItem menuPast; // ճ��

	private JMenu aboutMenu;
	private JMenuItem menuAbout; // ����

	private JMenuBar menuBar;

	private JTextArea textArea;
	private JLabel stateBar;

	private JPopupMenu popupMenu; // �����˵�

	private JFileChooser fileChooser;// �ļ�ѡ����

	public NotePad(TextDAO textDAO) {
		this();
		this.textDAO = textDAO;
	}

	public NotePad() {
		// ��ʼ�����
		initComponent();
		// ��ʼ��������
		initEventListener();
	}

	private void initEventListener() {
		// ����Ĭ�Ϲرղ������ڴ��ڳ������ǡ�X����ť��
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				close();
			}
		});
		// ��ʼ����ݼ�
		initAccelerator();

		// ��ʼ���˵�����
		initMenuListener();

		// �༭�������¼�
		textArea.addKeyListener(new KeyAdapter() {

			public void keyTyped(java.awt.event.KeyEvent e) {
				stateBar.setText("���޸�");
			}
		});

		// �༭������¼�
		textArea.addMouseListener(new MouseAdapter() {
			/**
			 * �������ʱ����ָ��������Ͻ���ķ�����
			 */
			public void mouseClicked(MouseEvent e) {
				// BUTTON1,��ʾ������
				if (e.getButton() == MouseEvent.BUTTON1) {
					popupMenu.setVisible(false);
				}
			}

			/**
			 * ���������������˫��������ָ�������ķ�����
			 */
			public void mouseReleased(MouseEvent e) {
				// BUTTON3,��ʾ����Ҽ�
				if (e.getButton() == MouseEvent.BUTTON3) {
					popupMenu.show(editMenu, e.getX(), e.getY());
				}
			}

		});
	}

	private void initMenuListener() {
		/**
		 * ���û���������ĵ����˵��ϵͳ�Զ�����actionPerformed()������
		 */
		menuOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// ���ļ�
				openFile();
			}
		});

		menuSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// �����ļ�
				saveFile();
			}
		});

		menuSaveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// ����ļ�
				saveAsFile();
			}
		});

		menuClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// �ر��ļ�
				close();
			}
		});

		menuCut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// �����ļ�
				cut();
			}
		});

		menuCopy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// �����ļ�
				copy(e);
			}
		});

		menuPast.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// ճ���ļ�
				paste(e);
			}
		});

		menuAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// ��ʾһ���Ի���
				JOptionPane.showOptionDialog(null, "NotePad 0.1\n����http://openhome.cc", "����NotePad",
						JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
			}
		});
	}

	protected void paste(ActionEvent e) {
		textArea.paste();
		stateBar.setText("���޸�");
		// ���ز˵�
		popupMenu.setVisible(false);

	}

	protected void copy(ActionEvent e) {
		textArea.copy();
		// ���ز˵�
		popupMenu.setVisible(false);

	}

	protected void cut() {
		textArea.cut();
		stateBar.setText("���޸�");
		// �˵�����
		popupMenu.setVisible(false);
	}

	protected void close() {
		if (isEditFileSaved()) {
			// �ͷŴ�����Դ,Ȼ��رճ���
			dispose();
		} else {
			int option = JOptionPane.showConfirmDialog(null, "�ĵ����޸�,�Ƿ񱣴�", "�����ĵ�?", JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE, null);
			switch (option) {
			case JOptionPane.YES_OPTION:
				saveFile();
				break;
			case JOptionPane.NO_OPTION:
				break;
			}
			dispose();
		}

	}

	protected void saveAsFile() {
		int option = fileChooser.showSaveDialog(null);
		// ���ѡ��ȷ�ϰ�ť
		if (option == JFileChooser.APPROVE_OPTION) {
			// ���ñ�����
			setTitle(fileChooser.getSelectedFile().toString());
			// �����ļ�
			textDAO.create(fileChooser.getSelectedFile().toString());
			// ���ļ����浽ָ���ļ���
			saveFile(fileChooser.getSelectedFile().toString());
		}
	}

	protected void saveFile(String path) { // �����ļ�
		String text = textArea.getText();
		textDAO.save(path, text);
		// ����״̬��
		stateBar.setText("δ�޸�");
	}

	protected void openFile() {
		// to do
		if (isEditFileSaved()) {
			// ��ʾѡ���ļ��Ի���
			showFileDialog();
		} else {
			// ��ʾ��ʾ�Ի���
			int option = JOptionPane.showConfirmDialog(null, "�ĵ����޸�,�Ƿ�洢?", "�洢�ĵ�?", JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE, null);

			switch (option) {
			case JOptionPane.YES_OPTION: // ȷ��
				saveFile();
				break;
			case JOptionPane.NO_OPTION: // ȡ��
				showFileDialog();
				break;
			}
		}
	}

	private void saveFile() {
		// �жϱ��������Ƿ����ļ�·��
		File file = new File(getTitle());
		if (!file.exists()) {
			// ִ�����Ϊ
			saveAsFile();
		} else {
			// ���浽ָ��·��
			saveFile(getTitle());
		}
	}

	private void showFileDialog() {
		int option = fileChooser.showOpenDialog(null); // ��ʾ�ļ�ѡ��Ի���
		// �û�����ȷ�ϼ�
		if (option == JFileChooser.APPROVE_OPTION) {
			// �趨ѡ�е��ļ�����
			setTitle(fileChooser.getSelectedFile().toString());
			textArea.setText("");// ����ı��༭��
			stateBar.setText("δ�޸�"); // ����״̬��

			String text = textDAO.read(fileChooser.getSelectedFile().toString());
			// ���ı���ʾ�ڱ༭����
			textArea.setText(text);
		}
	}

	private boolean isEditFileSaved() {
		if (stateBar.getText().equals("δ�޸�")) {
			return true; // ��ʾ�ļ��Ѿ��洢
		} else {
			return false;
		}
	}

	private void initAccelerator() {
		// �����ĵ��� ctrl+O
		menuOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
		// �������ĵ��� ctrl+S
		menuSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
		// ������ĵ��� ctrl+shift+S
		menuSaveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
		// ���رա� ctrl+Q
		menuClose.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK));

		// �����С� ctrl+X
		menuCut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
		// �����ơ� ctrl+C
		menuCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
		// ��ճ���� ctrl+V
		menuPast.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));
	}

	private void initComponent() {
		// �趨���ڱ���
		setTitle("�ı��ĵ�");
		// �趨���ڴ�С
		setSize(800, 600);

		// ��ʼ�����ļ����˵�
		initFileMenu();
		// ��ʼ�����༭���˵�
		initEditMenu();
		// ��ʼ�������ڡ��˵�
		initAboutMenu();

		// ��ʼ���˵���
		initMenuBar();
		// ��ʼ���ı��༭��
		initTextArea();
		// ��ʼ��״̬��
		initStateBar();
		// ��ʾ�˵�
		popupMenu = editMenu.getPopupMenu();
		// ��ȡ�ļ��Ի������
		fileChooser = new JFileChooser();
	}

	private void initStateBar() {
		stateBar = new JLabel("δ�޸�");
		// ����ˮƽ���룺LEFT��� CENTER�м� RIGHT�ұ�
		stateBar.setHorizontalAlignment(SwingConstants.LEFT);
		// ����״̬������״
		stateBar.setBorder(BorderFactory.createEtchedBorder());
		// ��״̬����ӵ����������
		getContentPane().add(stateBar, BorderLayout.SOUTH);
	}

	private void initTextArea() {
		// ���ֱ༭����
		textArea = new JTextArea();
		// ���������С��������ʽ
		textArea.setFont(new Font("ϸ����", Font.PLAIN, 16));
		// �����Զ�����
		textArea.setLineWrap(true);
		// �����ַŽ����������
		JScrollPane panel = new JScrollPane(textArea, // ���ֱ༭��ͼ
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, // ��ֱ�����Ƿ���Ҫ������
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); // ˮƽ�����Ƿ���Ҫ������
		// �ѹ��������뵽���������
		getContentPane().add(panel, BorderLayout.CENTER);
	}

	private void initMenuBar() {
		menuBar = new JMenuBar();

		// �Ѳ˵���ӵ��˵�����
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(aboutMenu);

		// ���ò˵���
		setJMenuBar(menuBar);
	}

	private void initAboutMenu() {
		aboutMenu = new JMenu("����");

		menuAbout = new JMenuItem("����NotePad");
		// �Ѳ˵�����ӵ��˵���
		aboutMenu.add(menuAbout);
	}

	private void initEditMenu() {
		editMenu = new JMenu("�༭");

		menuCut = new JMenuItem("����");
		menuCopy = new JMenuItem("����");
		menuPast = new JMenuItem("ճ��");
		// �Ѳ˵�����ӵ��˵���
		editMenu.add(menuCut);
		editMenu.add(menuCopy);
		editMenu.add(menuPast);
	}

	private void initFileMenu() {
		// ���á��ĵ����˵�
		fileMenu = new JMenu("�ĵ�");

		menuOpen = new JMenuItem("���ĵ�");
		menuSave = new JMenuItem("�����ĵ�");
		menuSaveAs = new JMenuItem("������ĵ�");
		menuClose = new JMenuItem("�ر��ĵ�");

		// �Ѳ˵�����ӵ��˵���
		fileMenu.add(menuOpen);
		// ��ӷָ��
		fileMenu.addSeparator();
		fileMenu.add(menuSave);
		fileMenu.add(menuSaveAs);
		// ��ӷָ��
		fileMenu.addSeparator();
		fileMenu.add(menuClose);
	}
}