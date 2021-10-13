package io.petermueller.petrinetz.views;

import java.awt.*;
import java.awt.image.*;
import java.io.IOException;
import javax.imageio.*;
import javax.swing.*;

public class ToolbarView{
	public JToolBar toolbar;
	public JButton buttonPrevFile,
				   buttonNextFile,
				   buttonReset,
				   buttonAddToken,
				   buttonRemoveToken,
				   buttonBoundednessAnalysis;
	
	public ToolbarView() {
		toolbar = new JToolBar();
		buttonPrevFile = createToolbarButton(
			"View Previous File",
			"ic_prev_file_36x36_default.png",
			"ic_prev_file_36x36_default@2x.png",
			"ic_prev_file_36x36_rollover.png",
			"ic_prev_file_36x36_rollover@2x.png"
		);
		buttonNextFile = createToolbarButton(
			"View Next File",
			"ic_next_file_36x36_default.png",
			"ic_next_file_36x36_default@2x.png",
			"ic_next_file_36x36_rollover.png",
			"ic_next_file_36x36_rollover@2x.png"				
		);
		buttonReset = createToolbarButton(
				"Reset Markings",
				"ic_reset_36x36_default.png",
				"ic_reset_36x36_default@2x.png",
				"ic_reset_36x36_rollover.png",
				"ic_reset_36x36_rollover@2x.png"				
			);
		buttonAddToken = createToolbarButton(
			"Add Token",
			"ic_add_marker_36x36_default.png",
			"ic_add_marker_36x36_default@2x.png",
			"ic_add_marker_36x36_rollover.png",
			"ic_add_marker_36x36_rollover@2x.png"				
		);
		buttonRemoveToken = createToolbarButton(
			"Remove Token",
			"ic_remove_marker_36x36_default.png",
			"ic_remove_marker_36x36_default@2x.png",
			"ic_remove_marker_36x36_rollover.png",
			"ic_remove_marker_36x36_rollover@2x.png"				
		);
		buttonBoundednessAnalysis = createToolbarButton(
			"Boundedness Analysis",
			"ic_boundedness_36x36_default.png",
			"ic_boundedness_36x36_default@2x.png",
			"ic_boundedness_36x36_rollover.png",
			"ic_boundedness_36x36_rollover@2x.png"				
		);
		
		toolbar.setFloatable(false);
		//toolbar.setBackground(Color.white);
		toolbar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
		/*
		toolbar.setBorder(BorderFactory.createMatteBorder(
			0, 0, 2, 0,
			new Color(218, 220, 224)
		));
		*/
		
		toolbar.add(buttonPrevFile);
		toolbar.add(buttonNextFile);
		toolbar.addSeparator();
		toolbar.add(buttonReset);
		toolbar.addSeparator();
		toolbar.add(buttonAddToken);
		toolbar.add(buttonRemoveToken);
		toolbar.addSeparator();
		toolbar.add(buttonBoundednessAnalysis);
	}
	
	private JButton createToolbarButton(	
			String tooltip,
			String iconUrl1x,
			String iconUrl2x,
			String iconRolloverUrl1x,
			String iconRolloverUrl2x) {
		JButton button = new JButton();
		BufferedImage img1x = null;
		BufferedImage img2x = null;
		/*
		 * BufferedImage rollover1x = null; BufferedImage rollover2x = null;
		 */
		try {
			img1x = ImageIO.read(getClass().getResource("/" + iconUrl1x));
			img2x = ImageIO.read(getClass().getResource("/" + iconUrl2x));
			/*
			 * rollover1x = ImageIO.read(getClass().getResource("/" + iconRolloverUrl1x));
			 * rollover2x = ImageIO.read(getClass().getResource("/" + iconRolloverUrl2x));
			 */
		} catch (IOException e) {
			e.printStackTrace();
		}	
		ImageIcon icon = new ImageIcon(
			new BaseMultiResolutionImage(0, img1x, img2x)
		);
		/*
		 * ImageIcon rolloverIcon = new ImageIcon( new BaseMultiResolutionImage(0,
		 * rollover1x, rollover2x) );
		 */
		button.setIcon(icon);
		//button.setRolloverIcon(rolloverIcon);
		button.setToolTipText(tooltip);
		//button.setBorder(new EmptyBorder(6, 6, 6, 6));;
		button.setEnabled(false);
		return button;
	}
}
