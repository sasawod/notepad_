package com.example.notepad;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Notepad extends JFrame {
    private final JLabel statusLabel = new JLabel("Строка: 1, Символ: 1");
    private final JTextArea textArea = new JTextArea();
    private Path currentFile = null;
    private final JFileChooser fileChooser = new JFileChooser();

    public Notepad() {
        super("Блокнотик");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1200, 720);
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);
                // Status bar
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        statusPanel.add(statusLabel, BorderLayout.WEST);
        add(statusPanel, BorderLayout.SOUTH);

        // Обновление позиции курсора
        textArea.addCaretListener(e -> updateStatusBar());

        createMenuBar();

        // File chooser default filter
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text files", "txt", "text"));
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File
        JMenu fileMenu = new JMenu("Файл");
        JMenuItem newItem = new JMenuItem("Новый");
        JMenuItem openItem = new JMenuItem("Открыть...");
        JMenuItem saveItem = new JMenuItem("Сохранить");
        JMenuItem saveAsItem = new JMenuItem("Сохранить как..."); 
        JMenuItem exitItem = new JMenuItem("Выйти");

        newItem.addActionListener(e -> newFile());
        openItem.addActionListener(e -> openFile());
        saveItem.addActionListener(e -> saveFile());
        saveAsItem.addActionListener(e -> saveFileAs());
        exitItem.addActionListener(e -> exitApp());

        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.addSeparator();
        fileMenu.add(saveItem);
        fileMenu.add(saveAsItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        // Edit
        JMenu editMenu = new JMenu("Изменить");
        JMenuItem cutItem = new JMenuItem("Вырезать");
        JMenuItem copyItem = new JMenuItem("Копировать");
        JMenuItem pasteItem = new JMenuItem("Вставить");
        JMenuItem selectAllItem = new JMenuItem("Выбрать все");

        cutItem.addActionListener(e -> textArea.cut());
        copyItem.addActionListener(e -> textArea.copy());
        pasteItem.addActionListener(e -> textArea.paste());
        selectAllItem.addActionListener(e -> textArea.selectAll());

        editMenu.add(cutItem);
        editMenu.add(copyItem);
        editMenu.add(pasteItem);
        editMenu.addSeparator();
        editMenu.add(selectAllItem);

        // Help
        JMenu helpMenu = new JMenu("Помощь");
        JMenuItem aboutItem = new JMenuItem("сюды");
        aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Блокнотик все ошибки вымышлены и специально допущены )", "", JOptionPane.INFORMATION_MESSAGE));
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        int shortcutKey = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(); // Ctrl (Win/Linux) или Command (Mac)

        newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, shortcutKey));
        openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, shortcutKey));
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, shortcutKey));
        saveAsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.SHIFT_MASK | shortcutKey));
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, shortcutKey));
        cutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, shortcutKey));
        copyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, shortcutKey));
        pasteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, shortcutKey));
        selectAllItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, shortcutKey));
    }

    private void newFile() {
        if (confirmSaveIfNeeded()) {
            textArea.setText(""); 
            currentFile = null;
            setTitle("Блокнотик");
        }
    }

    private boolean confirmSaveIfNeeded() {
        if (textArea.getText().isEmpty()) return true;
        int option = JOptionPane.showConfirmDialog(this, "Сохранить изменения?", "Да", JOptionPane.YES_NO_CANCEL_OPTION);
        if (option == JOptionPane.CANCEL_OPTION) return false;
        if (option == JOptionPane.YES_OPTION) {
            return saveFile();
        }
        return true;
    }

    private void openFile() {
        if (!confirmSaveIfNeeded()) return;
        int res = fileChooser.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File f = fileChooser.getSelectedFile();
            try {
                byte[] bytes = Files.readAllBytes(f.toPath());
                textArea.setText(new String(bytes, StandardCharsets.UTF_8));
                currentFile = f.toPath();
                setTitle(f.getName() + " - Блокнотик");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Ошибка открытия файла:\n" + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean saveFile() {
        if (currentFile == null) {
            return saveFileAs();
        } else {
            try {
                Files.write(currentFile, textArea.getText().getBytes(StandardCharsets.UTF_8));
                return true;
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Ошибка сохранения файла:\n" + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
    }

    private boolean saveFileAs() {
        int res = fileChooser.showSaveDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File f = fileChooser.getSelectedFile();
            try {
                Path p = f.toPath();
                // ensure .txt if no extension
                if (!f.getName().contains(".")) {
                    p = Paths.get(f.getAbsolutePath() + ".txt");
                }
                Files.write(p, textArea.getText().getBytes(StandardCharsets.UTF_8));
                currentFile = p;
                setTitle(p.getFileName().toString() + " - Блокнотик");
                return true;
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Ошибка сохранения файла:\n" + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        return false;
    }

    private void exitApp() {
        if (confirmSaveIfNeeded()) {
            dispose();
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            Notepad n = new Notepad();
            n.setVisible(true);
        });
    }
    private void updateStatusBar() {
    int caretPos = textArea.getCaretPosition();
    try {
        int line = textArea.getLineOfOffset(caretPos);
        int col = caretPos - textArea.getLineStartOffset(line);
        statusLabel.setText("Строка: " + (line + 1) + ", Символ: " + (col + 1));
    } catch (Exception ex) {
        statusLabel.setText("Строка: 1, Символ: 1");
    }
}

}
