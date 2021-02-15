package com.intelligents.haunting;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Objects;

public class HauntingJFrame extends JWindow implements ActionListener {

    private JWindow window = new JWindow();

    private String[] userResponse;
    private JTextField userInput = new JTextField();
    private JButton showJournal = new JButton("Journal");
    private JButton showMap = new JButton("Map");
//    JTextArea textDisplayGameWindow = new JTextArea();
    private JTextPane textDisplayGameWindow = new JTextPane();
    JTextArea textDisplayJournal = new JTextArea();
    private JFrame frame;
    private boolean calledOnce=false;
    private String currentRoom;
    private Game game;
    private Controller controller;
    private PrintFiles p = new PrintFiles();
    private ClassLoader cl;
    private String pathStartResources = "com/intelligents/resources/";
    private String pathStartSounds = pathStartResources + "Sounds/";
    private String pathStartImages = pathStartResources + "Images/";
    JTextArea playerLocationArea = new JTextArea();
    JPanel textDisplayPanel;
    JPanel userInputPanel;
    JPanel buttonsAndInfoPanel;
    private JPanel playerLocationPanel;
    private MusicPlayer themeSong;

    public HauntingJFrame() throws IOException {
        cl = getClass().getClassLoader();
        themeSong = new MusicPlayer(pathStartSounds + "VIKINGS THEME SONG.wav", cl);
        splashWindow(cl);
        gameWindow();
        game = new Game(this, pathStartSounds, pathStartResources, cl, p);
        controller = new Controller(game);
    }


    private void gameWindow() {
        frame = new JFrame("The Haunting of Amazon Hill");
        frame.setSize(700, 700);

        textDisplayPanel = new JPanel();
        userInputPanel = new JPanel();
        buttonsAndInfoPanel = new JPanel();
        playerLocationPanel = new JPanel();


        textDisplayPanel.setBackground(Color.black);
        buttonsAndInfoPanel.setBackground(Color.DARK_GRAY);
        buttonsAndInfoPanel.setLayout(new FlowLayout());
        buttonsAndInfoPanel.add(showJournal);
        buttonsAndInfoPanel.add(Box.createHorizontalGlue());
        buttonsAndInfoPanel.add(showMap);
        buttonsAndInfoPanel.add(Box.createHorizontalGlue());
        buttonsAndInfoPanel.add(playerLocationPanel);

        showJournal.addActionListener(this);
        showMap.addActionListener(this);

        // Unchangeable to user, a textbox to display game text
        DefaultCaret caret = (DefaultCaret) textDisplayGameWindow.getCaret();
        caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        textDisplayGameWindow.setCaretPosition(0);
        textDisplayGameWindow.setText("What game would you like to play?\n " +
                "Chapter 1. The Haunting of Amazon Hill\n " +
                "Chapter 2. Chasing Ghosts (COMING SOON!)\n " +
                "Chapter 3. Hangman's Gallows (COMING SOON!)\n " +
                "Press 4. to load saved game\n" +
                "Please enter a number for Chapter: ");
        textDisplayGameWindow.setBorder(BorderFactory.createBevelBorder(1));
        textDisplayGameWindow.setForeground(Color.white);
        textDisplayGameWindow.setFont(new Font("Comic Sans", Font.BOLD, 15));
        textDisplayGameWindow.setEditable(false);
        textDisplayGameWindow.setBackground(Color.DARK_GRAY);

        // Allows for scrolling if text extends beyond panel
        JScrollPane scrollPane = new JScrollPane(textDisplayGameWindow);
        scrollPane.setPreferredSize(new Dimension(700, 500));
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        // Text field for user to input
        userInput.setSize(new Dimension(500, 100));
        userInput.setFont(new Font("Consolas", Font.CENTER_BASELINE, 15));
        userInput.setForeground(Color.white);
        userInput.setBackground(Color.DARK_GRAY);
        userInput.setCaretColor(Color.BLACK);

        userInputPanel.setBackground(Color.BLACK);
        textDisplayPanel.add(scrollPane);
        userInputPanel.setLayout(new GridLayout(1, 2));
        userInputPanel.add(userInput);

        playerLocationPanel.setBackground(Color.white);
        playerLocationArea.setSize(200,75);
        playerLocationArea.setForeground(Color.blue);
        playerLocationArea.setEditable(false);
        playerLocationPanel.add(playerLocationArea);

        frame.add(textDisplayPanel, BorderLayout.NORTH);
        frame.add(userInputPanel, BorderLayout.CENTER);
        frame.add(buttonsAndInfoPanel, BorderLayout.SOUTH);


        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        if (!calledOnce) {
            userInput.addActionListener(this);
            calledOnce = true;
        }
        userInput.requestFocusInWindow();

        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Journal")) {
            showJournal();
        }
        if (e.getActionCommand().equals("Map")) {
            game.processInput(true, new String[]{"open"}, 0);
        }
        if (e.getSource() == userInput) {
            userResponse = userInput.getText().strip().toLowerCase().split(" ");
            userInput.setText("");
            try {
                controller.kickoffResponse(userResponse, textDisplayGameWindow.getText());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    public void setTextBox(String text, Color color) {
        textDisplayGameWindow.setForeground(color);
        textDisplayGameWindow.setText(text);
    }

    public  void appendToTextBox(String text) throws BadLocationException {
//        textDisplayGameWindow.append(text);
        Document doc = textDisplayGameWindow.getDocument();
        doc.insertString(doc.getLength(), text, null);
    }

    public void setTextColorAndDisplay(String textToDisplay, Color color) throws BadLocationException {
        StyledDocument doc = textDisplayGameWindow.getStyledDocument();
        Style style = textDisplayGameWindow.addStyle("", null);
        StyleConstants.setForeground(style, color);
        doc.insertString(doc.getLength(), textToDisplay, style);
    }

    private void showJournal() {
        frame = new JFrame("Journal");
        frame.setSize(500, 500);

        textDisplayJournal = new JTextArea();
        DefaultCaret caret = (DefaultCaret) textDisplayJournal.getCaret();
        caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        textDisplayJournal.setCaretPosition(0);
//        textDisplayJournal.setText("This is your journal! There is text added here... blah blah blah");
        game.openNewWindowJournalWithUpdatedInfo();
        textDisplayJournal.setLineWrap(true);
        textDisplayJournal.setWrapStyleWord(true);
        textDisplayJournal.setBorder(BorderFactory.createBevelBorder(1));
        textDisplayJournal.setForeground(new Color(0, 60, 70));
        textDisplayJournal.setFont(new Font("Comic Sans", Font.BOLD, 15));
        textDisplayJournal.setEditable(false);
        textDisplayJournal.setBackground(new Color(196, 223, 230));

        // Allows for scrolling if text extends beyond panel
        JScrollPane scrollPane = new JScrollPane(textDisplayJournal);
        scrollPane.setPreferredSize(new Dimension(700, 500));
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        frame.add(scrollPane, BorderLayout.CENTER);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    void showMap() throws IOException {
        currentRoom = game.currentRoom.replaceAll("\\s", "");

        frame = new JFrame("Map");
        frame.setSize(500, 500);

        JLabel picLabel = new JLabel();
        picLabel.setIcon(new ImageIcon(Objects.requireNonNull(cl.getResource(pathStartImages + "Map(" + currentRoom + ").png"))));


        frame.add(picLabel, BorderLayout.CENTER);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void splashWindow(ClassLoader cl) throws IOException {
        themeSong.playSoundEffect();
        themeSong.setVolume((float) -10.69);

        JLabel image = new JLabel();
        image.setIcon(new ImageIcon(Objects.requireNonNull(cl.getResource(pathStartImages + "asciiSplashScreen.png"))));


        window.getContentPane().add(image);
        window.setBounds(500, 150, 300, 200);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        window.setVisible(false);
        window.dispose();
    }

    public void stopThemeSong() {
        themeSong.stopSoundEffect();
    }

}

