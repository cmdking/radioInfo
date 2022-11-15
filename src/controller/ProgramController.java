/*
 * FILE:
 *      ProgramController.java
 *
 * DESCRIPTION:
 *      The class controls the data and puts them into GUI.
 *
 * AUTHOR:
 *      Elias Niko, c18eno
 *
 * CHANGES:
 *      20200129: Initial version.
 */
package controller;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

import model.Program;
import model.ProgramLoader;
import view.RadioInfo;


/**
 * The class controls the data and puts them into JTable.
 *
 * @author Elias Niko
 * @version 1.0
 */
public class ProgramController {
    private final RadioInfo radioInfo;
    private HashMap<Integer, ArrayList<Program>> channelPrograms;
    private ProgramLoader programLoader;
    private JComboBox<String> box;
    private JButton update;
    private ArrayList<String> channelNames;

    /**
     * Constructor for the class
     */
    public ProgramController() {
        radioInfo = new RadioInfo();
        dateAndTime();
    }

    /**
     * The methods show the GUI and call actionListener methods.
     */
    public void showGraphics() {
        SwingUtilities.invokeLater(() -> {
            radioInfo.show();
            setActionListener();
            rowActionListener();
        });
    }
    /**
     * Shows current date and time in the GUI. Runs in its own thread.
     */
    @SuppressWarnings("BusyWait")
    private void dateAndTime() {
        final Thread dateTimeThread = new Thread(() -> {
            try {
                while(true) {
                    Thread.sleep(1000);
                }
            } catch (final InterruptedException e) {
                System.err.println("Error: Clock-Thread");
            }
        });
        dateTimeThread.start();
    }

    /**
     * The method creates actionListener for each row in the JTable.
     */
    public void rowActionListener() {
        final JTable table = radioInfo.getTable();
        final ListSelectionModel selectionModel = table.getSelectionModel();
        selectionModel.addListSelectionListener(e ->{
            if (!selectionModel.isSelectionEmpty()) {
                final String channelName = radioInfo.getselectedChannelName();
                final int id = programLoader.getChannels().get(channelName);
                final ArrayList<Program> prog = channelPrograms.get(id);
                final int selectedRow = selectionModel.getMinSelectionIndex();
                final String title = prog.get(selectedRow).getTitle();
                final String imageURL = prog.get(selectedRow).getImgURL();
                final String description = prog.get(selectedRow).getDescription();
                radioInfo.updateInformation(title, description, imageURL);
            }
        });
    }

    /**
     * the method run and execute the GUI, this method runs
     * in a SwingWorker thread which does not lock the GUI.
     */
    public void start() {
        SwingWorker<Object, Object> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                update = radioInfo.getUpdateButton();
                update.setEnabled(false);
                programLoader = new ProgramLoader();
                return null;
            }

            @Override
            protected void done() {
                channelPrograms = programLoader.getChannelPrograms();
                channelNames = programLoader.getNameOfChannels();
                box = radioInfo.getComboBox();
                box.setModel(new DefaultComboBoxModel<>(channelNames.toArray(new String[0])));
                final JButton runButton = radioInfo.getStartButton();
                runButton.setEnabled(true);
                final JButton update = radioInfo.getUpdateButton();
                update.setEnabled(true);
                JLabel lastUpdate = radioInfo.getLastUpdateTime();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm ");
                String time = LocalDateTime.now().format(formatter);
                lastUpdate.setText("Uppdaterad: " + time);
            }
        };
        worker.execute();
    }

    /**
     * ActionListener for "UPDATE" button in the GUI.
     */
    public void setActionListener() {
        final JButton update = radioInfo.getUpdateButton();
        update.addActionListener(e -> start());

        final JButton startButton = radioInfo.getStartButton();
        startButton.addActionListener(e -> startButtonActionListener());
    }

    /**
     * ActionListener for "START" button.
     */
    private void startButtonActionListener() {
        radioInfo.updateInformation("", "", "");
        final String channelName = radioInfo.getselectedChannelName();
        final int id = programLoader.getChannels().get(channelName);

        if (programLoader.getNotfoundPages().contains(id))
            radioInfo.notFoundPages(channelName);
        else {
            final ArrayList<Program> prog = channelPrograms.get(id);
            final JTable table = radioInfo.getTable();
            final Object[] colNames = {"Program" , "Start" , "Slut", "Status" };
            final DefaultTableModel tableModel = new DefaultTableModel();
            tableModel.setColumnIdentifiers(colNames);
            table.setModel(tableModel);
            if (prog.size() == 0)
                radioInfo.updateInformation(channelName, "No content!", null);
            else {
                for (int i = 0; i < prog.size(); i++) {
                    final Object[] row = getTableRow(prog, i);
                    tableModel.addRow(row);
                }
            }
        }
    }
    /**
     * Gets the information form the list of Programs and puts them into table.
     * @param program as Program
     * @param i as int (index)
     * @return row as Object array
     */
    private Object[] getTableRow(final ArrayList<Program> program, final int i) {
        final Object[] row = new Object[4];
        final Program prg = program.get(i);
        row[0] = prg.getTitle();
        row[1] = prg.getStartTime();
        row[2] = prg.getEndTime();
        row[3] = prg.getStatus();
        return row;
    }
}