package com.gem.notepad;

import javax.swing.SwingUtilities;

public class TestDemo {
	public static void main(String[] args) {
		// 事件队列
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				TextDAO textDAO = new FileTextDAO();
				NotePad notePad = new NotePad(textDAO);
				// 显示窗口
				notePad.setVisible(true);
			}
		});
	}
}
