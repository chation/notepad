package com.gem.notepad;

import javax.swing.SwingUtilities;

public class TestDemo {
	public static void main(String[] args) {
		// �¼�����
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				TextDAO textDAO = new FileTextDAO();
				NotePad notePad = new NotePad(textDAO);
				// ��ʾ����
				notePad.setVisible(true);
			}
		});
	}
}
