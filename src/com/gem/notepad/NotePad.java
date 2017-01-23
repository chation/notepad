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
	 * autor:Chation Usefor:记事本
	 */
	private static final long serialVersionUID = 1L;
	private TextDAO textDAO;
	private JMenu fileMenu;
	private JMenuItem menuOpen; // 打开
	private JMenuItem menuSave; // 保存
	private JMenuItem menuSaveAs; // 另存为
	private JMenuItem menuClose; // 关闭

	private JMenu editMenu;
	private JMenuItem menuCut; // 剪切
	private JMenuItem menuCopy; // 复制
	private JMenuItem menuPast; // 粘贴

	private JMenu aboutMenu;
	private JMenuItem menuAbout; // 关于

	private JMenuBar menuBar;

	private JTextArea textArea;
	private JLabel stateBar;

	private JPopupMenu popupMenu; // 弹出菜单

	private JFileChooser fileChooser;// 文件选择器

	public NotePad(TextDAO textDAO) {
		this();
		this.textDAO = textDAO;
	}

	public NotePad() {
		// 初始化组件
		initComponent();
		// 初始化监听器
		initEventListener();
	}

	private void initEventListener() {
		// 设置默认关闭操作，在窗口程序中是“X”按钮。
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				close();
			}
		});
		// 初始化快捷键
		initAccelerator();

		// 初始化菜单监听
		initMenuListener();

		// 编辑区键盘事件
		textArea.addKeyListener(new KeyAdapter() {

			public void keyTyped(java.awt.event.KeyEvent e) {
				stateBar.setText("已修改");
			}
		});

		// 编辑区鼠标事件
		textArea.addMouseListener(new MouseAdapter() {
			/**
			 * 当鼠标点击时，手指还在鼠标上进入的方法。
			 */
			public void mouseClicked(MouseEvent e) {
				// BUTTON1,表示鼠标左键
				if (e.getButton() == MouseEvent.BUTTON1) {
					popupMenu.setVisible(false);
				}
			}

			/**
			 * 当鼠标点击（单击、双击）后，手指弹起进入的方法。
			 */
			public void mouseReleased(MouseEvent e) {
				// BUTTON3,表示鼠标右键
				if (e.getButton() == MouseEvent.BUTTON3) {
					popupMenu.show(editMenu, e.getX(), e.getY());
				}
			}

		});
	}

	private void initMenuListener() {
		/**
		 * 当用户点击“打开文档”菜单项，系统自动调用actionPerformed()方法。
		 */
		menuOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 打开文件
				openFile();
			}
		});

		menuSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 保存文件
				saveFile();
			}
		});

		menuSaveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 另存文件
				saveAsFile();
			}
		});

		menuClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 关闭文件
				close();
			}
		});

		menuCut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 剪切文件
				cut();
			}
		});

		menuCopy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 复制文件
				copy(e);
			}
		});

		menuPast.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 粘贴文件
				paste(e);
			}
		});

		menuAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 显示一个对话框
				JOptionPane.showOptionDialog(null, "NotePad 0.1\n来自http://openhome.cc", "关于NotePad",
						JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
			}
		});
	}

	protected void paste(ActionEvent e) {
		textArea.paste();
		stateBar.setText("已修改");
		// 隐藏菜单
		popupMenu.setVisible(false);

	}

	protected void copy(ActionEvent e) {
		textArea.copy();
		// 隐藏菜单
		popupMenu.setVisible(false);

	}

	protected void cut() {
		textArea.cut();
		stateBar.setText("已修改");
		// 菜单隐藏
		popupMenu.setVisible(false);
	}

	protected void close() {
		if (isEditFileSaved()) {
			// 释放窗口资源,然后关闭程序
			dispose();
		} else {
			int option = JOptionPane.showConfirmDialog(null, "文档已修改,是否保存", "保存文档?", JOptionPane.YES_NO_OPTION,
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
		// 如果选择确认按钮
		if (option == JFileChooser.APPROVE_OPTION) {
			// 设置标题栏
			setTitle(fileChooser.getSelectedFile().toString());
			// 创建文件
			textDAO.create(fileChooser.getSelectedFile().toString());
			// 把文件保存到指定文件中
			saveFile(fileChooser.getSelectedFile().toString());
		}
	}

	protected void saveFile(String path) { // 保存文件
		String text = textArea.getText();
		textDAO.save(path, text);
		// 设置状态栏
		stateBar.setText("未修改");
	}

	protected void openFile() {
		// to do
		if (isEditFileSaved()) {
			// 显示选择文件对话框
			showFileDialog();
		} else {
			// 显示提示对话框
			int option = JOptionPane.showConfirmDialog(null, "文档已修改,是否存储?", "存储文档?", JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE, null);

			switch (option) {
			case JOptionPane.YES_OPTION: // 确认
				saveFile();
				break;
			case JOptionPane.NO_OPTION: // 取消
				showFileDialog();
				break;
			}
		}
	}

	private void saveFile() {
		// 判断标题栏中是否有文件路径
		File file = new File(getTitle());
		if (!file.exists()) {
			// 执行另存为
			saveAsFile();
		} else {
			// 保存到指定路径
			saveFile(getTitle());
		}
	}

	private void showFileDialog() {
		int option = fileChooser.showOpenDialog(null); // 显示文件选择对话框
		// 用户按下确认键
		if (option == JFileChooser.APPROVE_OPTION) {
			// 设定选中的文件标题
			setTitle(fileChooser.getSelectedFile().toString());
			textArea.setText("");// 清空文本编辑区
			stateBar.setText("未修改"); // 设置状态栏

			String text = textDAO.read(fileChooser.getSelectedFile().toString());
			// 把文本显示在编辑区中
			textArea.setText(text);
		}
	}

	private boolean isEditFileSaved() {
		if (stateBar.getText().equals("未修改")) {
			return true; // 表示文件已经存储
		} else {
			return false;
		}
	}

	private void initAccelerator() {
		// “打开文档” ctrl+O
		menuOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
		// “保存文档” ctrl+S
		menuSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
		// “另存文档” ctrl+shift+S
		menuSaveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
		// “关闭” ctrl+Q
		menuClose.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK));

		// “剪切” ctrl+X
		menuCut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
		// “复制” ctrl+C
		menuCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
		// “粘贴” ctrl+V
		menuPast.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));
	}

	private void initComponent() {
		// 设定窗口标题
		setTitle("文本文档");
		// 设定窗口大小
		setSize(800, 600);

		// 初始化“文件”菜单
		initFileMenu();
		// 初始化“编辑”菜单
		initEditMenu();
		// 初始化“关于”菜单
		initAboutMenu();

		// 初始化菜单栏
		initMenuBar();
		// 初始化文本编辑区
		initTextArea();
		// 初始化状态栏
		initStateBar();
		// 显示菜单
		popupMenu = editMenu.getPopupMenu();
		// 获取文件对话框对象
		fileChooser = new JFileChooser();
	}

	private void initStateBar() {
		stateBar = new JLabel("未修改");
		// 设置水平对齐：LEFT左边 CENTER中间 RIGHT右边
		stateBar.setHorizontalAlignment(SwingConstants.LEFT);
		// 设置状态栏的形状
		stateBar.setBorder(BorderFactory.createEtchedBorder());
		// 把状态栏添加到内容面板中
		getContentPane().add(stateBar, BorderLayout.SOUTH);
	}

	private void initTextArea() {
		// 文字编辑区域
		textArea = new JTextArea();
		// 设置字体大小，字体样式
		textArea.setFont(new Font("细明体", Font.PLAIN, 16));
		// 设置自动换行
		textArea.setLineWrap(true);
		// 把文字放进滚动面板中
		JScrollPane panel = new JScrollPane(textArea, // 文字编辑视图
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, // 垂直方向是否需要滚动条
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); // 水平方向是否需要滚动条
		// 把滚动面板加入到内容面板中
		getContentPane().add(panel, BorderLayout.CENTER);
	}

	private void initMenuBar() {
		menuBar = new JMenuBar();

		// 把菜单添加到菜单列中
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(aboutMenu);

		// 设置菜单列
		setJMenuBar(menuBar);
	}

	private void initAboutMenu() {
		aboutMenu = new JMenu("关于");

		menuAbout = new JMenuItem("关于NotePad");
		// 把菜单项添加到菜单中
		aboutMenu.add(menuAbout);
	}

	private void initEditMenu() {
		editMenu = new JMenu("编辑");

		menuCut = new JMenuItem("剪切");
		menuCopy = new JMenuItem("复制");
		menuPast = new JMenuItem("粘贴");
		// 把菜单项添加到菜单中
		editMenu.add(menuCut);
		editMenu.add(menuCopy);
		editMenu.add(menuPast);
	}

	private void initFileMenu() {
		// 设置“文档”菜单
		fileMenu = new JMenu("文档");

		menuOpen = new JMenuItem("打开文档");
		menuSave = new JMenuItem("保存文档");
		menuSaveAs = new JMenuItem("另存新文档");
		menuClose = new JMenuItem("关闭文档");

		// 把菜单项添加到菜单中
		fileMenu.add(menuOpen);
		// 添加分割符
		fileMenu.addSeparator();
		fileMenu.add(menuSave);
		fileMenu.add(menuSaveAs);
		// 添加分割符
		fileMenu.addSeparator();
		fileMenu.add(menuClose);
	}
}