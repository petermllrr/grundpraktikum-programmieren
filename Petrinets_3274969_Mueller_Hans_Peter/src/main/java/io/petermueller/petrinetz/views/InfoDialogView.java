package io.petermueller.petrinetz.views;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.*;

public class InfoDialogView {
	public JFrame frame;
	public JButton buttonClose;
	
	public InfoDialogView(JFrame mainFrame) {
		JLabel labelVersion = new JLabel("Java-Version:");
		JLabel version = new JLabel(System.getProperty("java.version"));
		JLabel labelDirectory = new JLabel("Working Directory:");
		JTextArea directory = new JTextArea(System.getProperty("user.dir"));
		JPanel panel = new JPanel();
        buttonClose = new JButton("Close");
        JLabel headline = new JLabel("Petri Net Visualizer");
        
        headline.setFont(new java.awt.Font("Lucida Grande", 1, 18)); // NOI18N
        panel.setBackground(Color.white);
        directory.setEditable(false);
        directory.setColumns(20);
        directory.setLineWrap(true);
        directory.setRows(5);
        directory.setAutoscrolls(false);
        directory.setDragEnabled(false);

        GroupLayout panelLayout = new GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(panelLayout.createSequentialGroup()
                        .addComponent(buttonClose, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 369, GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelLayout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(panelLayout.createSequentialGroup()
                                .addComponent(headline)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(panelLayout.createSequentialGroup()
                                .addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                    .addComponent(labelDirectory, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(labelVersion, GroupLayout.PREFERRED_SIZE, 118, GroupLayout.PREFERRED_SIZE))
                                .addGap(16, 16, 16)
                                .addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                    .addComponent(version, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(directory, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))))
                .addGap(23, 23, 23))
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, panelLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(headline)
                .addGap(24, 24, 24)
                .addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(version)
                    .addComponent(labelVersion))
                .addGap(16, 16, 16)
                .addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(labelDirectory)
                    .addComponent(directory, GroupLayout.PREFERRED_SIZE, 64, GroupLayout.PREFERRED_SIZE))
                .addGap(24, 24, 24)
                .addComponent(buttonClose)
                .addGap(24, 24, 24))
        );
		
		frame = new JFrame("About Petri Net Visualizer");
		frame.setMinimumSize(new Dimension(500, 270));
		frame.setResizable(false);
		frame.setLocationRelativeTo(mainFrame);
		frame.add(panel);
		frame.setVisible(true);
	}
}
