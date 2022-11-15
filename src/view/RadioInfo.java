/*
 * FILE:
 *      ProgramLoader.java
 *
 * DESCRIPTION:
 *      The main GUI window that shows the user information about programs.
 *
 * AUTHOR:
 *      Elias Niko, c18eno
 *
 * CHANGES:
 *      20200129: Initial version.
 */
package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serial;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

/**
 * RadioInfo.java is the main GUI class that creates user interface for the program.
 *
 * @author Elias Niko
 * @version 1.0
 */
public class RadioInfo extends DefaultTableModel implements ActionListener {
    private static final int FRAME_MIN_HEIGHT = 700;
    private static final int FRAME_MIN_WIDTH = 1400;
    @Serial
    private static final long serialVersionUID = 1L;
    private static final int FRAME_WIDTH = 1450;
    private static final int FRAME_HEIGHT = 900;
    private static final Color color = new Color(224,224,224);

    private JFrame frame;
    private JMenuBar menuBar;
    private JMenu radio;
    private JMenuItem exit,aboutUs, hlp;
    private JComboBox<String> box;
    private JTable programTable;
    private JTextArea description;
    private JButton run, update;
    private JLabel imageLabel, lastUpdate;


    /**
     * The constructor for RadioInfo
     */
    public RadioInfo(){
        createFrame();
        createMenus();
    }

    /**
     * Handles action event when the user clicks to help, exit and about us button.
     */
    @Override
    public void actionPerformed(final ActionEvent e) {
        if(e.getSource() == hlp)
            JOptionPane.showMessageDialog(null, "c18eno@cs.umu.se");
        else if(e.getSource() == exit)
            System.exit(0);
        else if(e.getSource() == aboutUs) {
            JOptionPane.showMessageDialog(null, "Labb 5, Applikationsutveckling i Java 5p (5DV135)");
        }
    }

    /**
     * The method will be called when connection to the Internet is lost.
     */
    public void connectionLost() {
        final String message = "No internet connection!\n Please make sure that Internet is connected.";
        JOptionPane.showMessageDialog(frame, message,"Connection error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * creates the main frame for the GUI.
     */
    private void createFrame() {
        frame = new JFrame("Radio Proram");
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setMinimumSize(new Dimension(FRAME_MIN_WIDTH, FRAME_MIN_HEIGHT));


        final JPanel selectPanel = createTopPanel();
        final JScrollPane programTable = getProgramTable();
        final JPanel imagePanel = imgAndDescriptionPanel();

        frame.add(programTable);
        frame.add(imagePanel, BorderLayout.EAST);
        frame.add(selectPanel, BorderLayout.NORTH);
    }

    /**
     * Creates menu bar for GUI with Exit and Update item.
     */
    private void createMenus() {
        menuBar  = new JMenuBar();
        radio = new JMenu("Fil");
        exit = new JMenuItem("Stäng");
        JMenuItem updateMenuBar = new JMenuItem("Uppdatera");

        radio.add(updateMenuBar);
        radio.addSeparator();
        radio.add(exit);

        creatHelpMenu();

        aboutUs.addActionListener(this);
        hlp.addActionListener(this);
        exit.addActionListener(this);

        frame.setJMenuBar(menuBar);
    }

    /**
     * Creates top panel which includes some labels to give user
     * information about time, last update and selection of channels.
     * @return topPanel as JPanel
     */
    private JPanel createTopPanel() {
        Border border = BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2);
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        topPanel.setBorder(border);

        box = new JComboBox<>();
        box.setBounds(50, 10, 2000, 100);

        run = new JButton("Kör");
        run.setEnabled(false);
        update = new JButton("Uppdatera");
        final JLabel info = new JLabel("Välj en kanal: ");
        showLastUpdate();

        box.setPreferredSize(new Dimension(650,30));
        run.setPreferredSize(new Dimension(100,30));
        update.setPreferredSize(new Dimension(133,30));

        topPanel.add(info);
        topPanel.add(box);
        topPanel.add(run);
        topPanel.add(update);
        topPanel.add(lastUpdate);
        //topPanel.add(currentDateAndTime);
        return topPanel;
    }

    /**
     * Creates a label for last update time.
     */
    private void showLastUpdate() {
        Border border = BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2);

        lastUpdate = new JLabel();
        lastUpdate.setFont(new Font("Bahnschrift SemiBold", Font.BOLD, 14));
        lastUpdate.setBorder(border);
        lastUpdate.setPreferredSize(new Dimension(385,30));
        lastUpdate.setText("Uppdaterad: ");
    }

    /**
     * Creates help menu with two items, About and help.
     */
    private void creatHelpMenu() {
        final JMenu help = new JMenu("Hjälp");
        aboutUs = new JMenuItem("Om");
        hlp =     new JMenuItem("Hjälp");
        menuBar.  add(radio);
        menuBar.  add(help);
        help.     add(hlp);
        help.     add(aboutUs);
    }

    /**
     * Getter for a scroll pane which has a JTable in it.
     * @return new JScrollPanel every time the method been called.
     */
    private JScrollPane getProgramTable() {
        programTable = new JTable() {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            public  boolean isCellEditable(final int row, final int col) {
                return false;
            }
        };
        programTable.setForeground(Color.black);
        programTable.setFont(new Font("Dialog",Font.BOLD, 15));
        programTable.setRowHeight(35);
        return new JScrollPane(programTable);
    }

    /**
     * The method creates panels and for showing the user image
     * and description of a selected program.
     * @return imgPanel as JPanel
     */
    private JPanel imgAndDescriptionPanel() {
        JPanel imgPanel = new JPanel();
        imgPanel.setLayout(new BoxLayout(imgPanel, BoxLayout.Y_AXIS));
        imgPanel.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
        imgPanel.setPreferredSize(new Dimension(400,400));
        imgPanel.setBackground(color);

        JPanel imagePanel = new JPanel();
        imagePanel.setLayout(new GridLayout(1,1));
        imagePanel.setBackground(color);
        imagePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createMatteBorder(2, 2,
                2, 2, Color.lightGray), "Programbild", TitledBorder.LEFT, TitledBorder.TOP));
        imagePanel.setPreferredSize(new Dimension(400,300));

        imageLabel  = new JLabel();
        description = new JTextArea();
        description.setBorder(BorderFactory.createTitledBorder(BorderFactory.createMatteBorder(2, 2,
                2, 2, Color.lightGray), "PROGRAM INFORMATION", TitledBorder.LEFT, TitledBorder.TOP));

        description.setFont(new Font("Bahnschrift SemiBold", Font.BOLD, 15));
        description.setBackground(color);

        description.setEditable(false);
        description.setLineWrap(true);
        description.setWrapStyleWord(true);

        imagePanel.add(imageLabel);

        imgPanel.add(imagePanel);
        imgPanel.add(description);
        return imgPanel;
    }

    /**
     * The method will be called when the user selects a channel name that has no content.
     * @param channelName as String
     */
    public void notFoundPages(final String channelName) {
        final String message = "Hittades inte!" + "\n" + "Kanalnamn: " + channelName;
        JOptionPane.showMessageDialog(frame, message,"Empty content", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * The method updates the information about a program as image, program name , program image.
     * @param title as String
     * @param desc as String
     * @param imageURL as String
     */
    public void updateInformation(final String title, final String desc,
                                  final String imageURL) {
        description.setText(editDescription(title, desc));
        ImageIcon icon = null;
        try {
            final BufferedImage image = ImageIO.read(new URL(imageURL));
            icon = new ImageIcon(image.getScaledInstance(350,250,
                    Image.SCALE_DEFAULT));
        } catch (final IOException ignored) {

        }
        imageLabel.setIcon(icon);
    }

    /**
     * String concatenation of String.
     * @param title as String
     * @param desc as String
     * @return The concatenated String
     */
    private String editDescription(final String title, final String desc) {
        return "Program: " + title + "\n\n" + "Beskrivning: \n\n" + desc;

    }

    /**
     * Getter for last update label.
     * @return lastUpdate as JLabel.
     */
    public JLabel getLastUpdateTime() {
        return lastUpdate;
    }

    /**
     * Getter for combo box that has radio channels name as item in it.
     * @return box as JComboBox
     */
    public JComboBox<String> getComboBox(){
        return box;
    }

    /**
     * Makes the main frame visible.
     */
    public void show() {
        frame.setVisible(true);
    }

    /**
     * Getter for selected channel name form the comboBox.
     * @return name or channel as String
     */
    public String getselectedChannelName() {
        return box.getItemAt(box.getSelectedIndex());
    }

    /**
     * Getter for start button.
     * @return run as JButton
     */
    public JButton getStartButton() {
        return run;
    }

    /**
     * Getter for the main table.
     * @return programTable as JTable
     */
    public JTable getTable() {
        return programTable;
    }

    /**
     * Getter for update button.
     * @return update as JButton
     */
    public JButton getUpdateButton() {
        return update;
    }
}

