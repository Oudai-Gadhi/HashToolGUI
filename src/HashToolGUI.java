import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;

public class HashToolGUI extends JFrame {
    private JTabbedPane tabbedPane;
    private List<String> wordsList = new ArrayList<>();
    private static final Color PRIMARY_COLOR = new Color(70, 130, 180);
    private static final Color SECONDARY_COLOR = new Color(245, 245, 245);

    private static final Map<Integer, String[]> HASH_LENGTH_TO_TYPE = new HashMap<>();
    private static final Map<String, Integer> ALGORITHM_TO_LENGTH = new HashMap<>();

    static {
        HASH_LENGTH_TO_TYPE.put(32, new String[]{"MD5"});
        HASH_LENGTH_TO_TYPE.put(40, new String[]{"SHA-1"});
        HASH_LENGTH_TO_TYPE.put(64, new String[]{"SHA-256"});
        HASH_LENGTH_TO_TYPE.put(96, new String[]{"SHA-384"});
        HASH_LENGTH_TO_TYPE.put(128, new String[]{"SHA-512"});

        ALGORITHM_TO_LENGTH.put("MD5", 32);
        ALGORITHM_TO_LENGTH.put("SHA-1", 40);
        ALGORITHM_TO_LENGTH.put("SHA-256", 64);
        ALGORITHM_TO_LENGTH.put("SHA-384", 96);
        ALGORITHM_TO_LENGTH.put("SHA-512", 128);
    }

    public HashToolGUI() {
        setTitle("Hash Tool");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initializeUI();
    }

    private void initializeUI() {

        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(SECONDARY_COLOR);
        tabbedPane.setForeground(Color.DARK_GRAY);
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));


        tabbedPane.addTab("Hash Identifier", createIcon("search"), createHashIdentifierPanel());
        tabbedPane.addTab("Hash Generator", createIcon("hash"), createHashGeneratorPanel());
        tabbedPane.addTab("Hash Cracker", createIcon("lock"), createHashCrackerPanel());

        add(tabbedPane, BorderLayout.CENTER);


        add(createStatusBar(), BorderLayout.SOUTH);
    }

    private ImageIcon createIcon(String type) {

        return new ImageIcon();
    }

    private JPanel createStatusBar() {
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY));
        statusPanel.setBackground(SECONDARY_COLOR);

        JLabel statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusPanel.add(statusLabel);

        return statusPanel;
    }


    private JPanel createHashIdentifierPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setBackground(SECONDARY_COLOR);


        JLabel headerLabel = new JLabel("Hash Identifier");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerLabel.setForeground(PRIMARY_COLOR);
        panel.add(headerLabel, BorderLayout.NORTH);


        JPanel inputPanel = new JPanel(new BorderLayout(10, 10));
        inputPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        inputPanel.setBackground(SECONDARY_COLOR);

        JLabel inputLabel = new JLabel("Enter Hash:");
        inputLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JTextField hashInput = new JTextField();
        hashInput.setFont(new Font("Consolas", Font.PLAIN, 14));

        JButton identifyButton = createStyledButton("Identify Hash", PRIMARY_COLOR);
        identifyButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        identifyButton.setForeground(Color.BLACK);

        inputPanel.add(inputLabel, BorderLayout.NORTH);
        inputPanel.add(hashInput, BorderLayout.CENTER);
        inputPanel.add(identifyButton, BorderLayout.SOUTH);


        JPanel resultPanel = new JPanel(new BorderLayout(10, 10));
        resultPanel.setBackground(SECONDARY_COLOR);

        JLabel resultLabel = new JLabel("Results:");
        resultLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JTextArea resultArea = new JTextArea(5, 30);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        resultArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        resultPanel.add(resultLabel, BorderLayout.NORTH);
        resultPanel.add(scrollPane, BorderLayout.CENTER);

        identifyButton.addActionListener(e -> {
            String hash = hashInput.getText().trim();
            if (hash.isEmpty()) {
                resultArea.setText("Please enter a hash!");
                return;
            }
            resultArea.setText("Possible Algorithm(s): " + guessAlgorithm(hash));
        });

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(resultPanel, BorderLayout.CENTER);

        return panel;
    }


    private JPanel createHashGeneratorPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setBackground(SECONDARY_COLOR);


        JLabel headerLabel = new JLabel("Hash Generator");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerLabel.setForeground(PRIMARY_COLOR);
        panel.add(headerLabel, BorderLayout.NORTH);


        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(SECONDARY_COLOR);


        JPanel inputPanel = new JPanel(new BorderLayout(10, 10));
        inputPanel.setBackground(SECONDARY_COLOR);

        JLabel inputLabel = new JLabel("Text to hash:");
        inputLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JTextArea textInput = new JTextArea(5, 30);
        textInput.setLineWrap(true);
        textInput.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textInput.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        inputPanel.add(inputLabel, BorderLayout.NORTH);
        inputPanel.add(new JScrollPane(textInput), BorderLayout.CENTER);

        // Algorithm selection
        JPanel algorithmPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        algorithmPanel.setBackground(SECONDARY_COLOR);

        JLabel algorithmLabel = new JLabel("Algorithm:");
        algorithmLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JComboBox<String> algorithmComboBox = new JComboBox<>(new String[]{"MD5", "SHA-1", "SHA-256", "SHA-384", "SHA-512"});
        algorithmComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        algorithmPanel.add(algorithmLabel);
        algorithmPanel.add(algorithmComboBox);


        JPanel outputPanel = new JPanel(new BorderLayout(10, 10));
        outputPanel.setBackground(SECONDARY_COLOR);

        JLabel outputLabel = new JLabel("Hash Result:");
        outputLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JTextArea hashOutput = new JTextArea(5, 30);
        hashOutput.setEditable(false);
        hashOutput.setFont(new Font("Consolas", Font.PLAIN, 14));
        hashOutput.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JButton copyButton = createStyledButton("Copy", new Color(100, 149, 237));
        copyButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        copyButton.setForeground(Color.BLACK);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(SECONDARY_COLOR);
        buttonPanel.add(copyButton);

        outputPanel.add(outputLabel, BorderLayout.NORTH);
        outputPanel.add(new JScrollPane(hashOutput), BorderLayout.CENTER);
        outputPanel.add(buttonPanel, BorderLayout.SOUTH);


        JButton generateButton = createStyledButton("Generate Hash", PRIMARY_COLOR);
        generateButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        generateButton.setForeground(Color.BLACK);

        generateButton.addActionListener(e -> {
            String text = textInput.getText().trim();
            String algorithm = (String) algorithmComboBox.getSelectedItem();
            if (text.isEmpty()) {
                hashOutput.setText("Please enter text!");
                return;
            }
            try {
                hashOutput.setText(generateHash(text, algorithm));
            } catch (Exception ex) {
                hashOutput.setText("Error: " + ex.getMessage());
            }
        });

        copyButton.addActionListener(e -> {
            StringSelection selection = new StringSelection(hashOutput.getText());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
            JOptionPane.showMessageDialog(this, "Hash copied to clipboard!", "Success", JOptionPane.INFORMATION_MESSAGE);
        });

        // Layout
        contentPanel.add(inputPanel, BorderLayout.NORTH);
        contentPanel.add(algorithmPanel, BorderLayout.CENTER);
        contentPanel.add(generateButton, BorderLayout.SOUTH);

        panel.add(contentPanel, BorderLayout.CENTER);
        panel.add(outputPanel, BorderLayout.EAST);

        return panel;
    }


    private JPanel createHashCrackerPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setBackground(SECONDARY_COLOR);


        JLabel headerLabel = new JLabel("Hash Cracker");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerLabel.setForeground(PRIMARY_COLOR);
        panel.add(headerLabel, BorderLayout.NORTH);


        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(SECONDARY_COLOR);

        // Hash input
        JPanel hashInputPanel = new JPanel(new BorderLayout(10, 10));
        hashInputPanel.setBackground(SECONDARY_COLOR);

        JLabel hashInputLabel = new JLabel("Target Hash:");
        hashInputLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JTextField hashInput = new JTextField();
        hashInput.setFont(new Font("Consolas", Font.PLAIN, 14));

        hashInputPanel.add(hashInputLabel, BorderLayout.NORTH);
        hashInputPanel.add(hashInput, BorderLayout.CENTER);

        // Algorithm selection
        JPanel algorithmPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        algorithmPanel.setBackground(SECONDARY_COLOR);

        JLabel algorithmLabel = new JLabel("Algorithm:");
        algorithmLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JComboBox<String> algorithmComboBox = new JComboBox<>(new String[]{"MD5", "SHA-1", "SHA-256", "SHA-384", "SHA-512"});
        algorithmComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        algorithmPanel.add(algorithmLabel);
        algorithmPanel.add(algorithmComboBox);

        // Wordlist controls
        JPanel wordlistPanel = new JPanel(new BorderLayout(10, 10));
        wordlistPanel.setBackground(SECONDARY_COLOR);

        JLabel wordlistStatus = new JLabel("Wordlist: Not loaded");
        wordlistStatus.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton loadWordlistButton = createStyledButton("Load Wordlist", new Color(60, 179, 113));
        loadWordlistButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        loadWordlistButton.setForeground(Color.BLACK);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBackground(SECONDARY_COLOR);
        buttonPanel.add(loadWordlistButton);
        buttonPanel.add(wordlistStatus);

        wordlistPanel.add(buttonPanel, BorderLayout.CENTER);

        // Crack button
        JButton crackButton = createStyledButton("Crack Hash", PRIMARY_COLOR);
        crackButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        crackButton.setForeground(Color.BLACK);

        JPanel crackButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        crackButtonPanel.setBackground(SECONDARY_COLOR);
        crackButtonPanel.add(crackButton);

        // Result area
        JPanel resultPanel = new JPanel(new BorderLayout(10, 10));
        resultPanel.setBackground(SECONDARY_COLOR);

        JLabel resultLabel = new JLabel("Results:");
        resultLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JTextArea resultArea = new JTextArea(8, 30);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        resultArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        resultPanel.add(resultLabel, BorderLayout.NORTH);
        resultPanel.add(new JScrollPane(resultArea), BorderLayout.CENTER);

        // Action listeners
        loadWordlistButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                try (BufferedReader reader = new BufferedReader(new FileReader(fileChooser.getSelectedFile()))) {
                    wordsList.clear();
                    String line;
                    while ((line = reader.readLine()) != null) wordsList.add(line.trim());
                    wordlistStatus.setText("Wordlist loaded: " + wordsList.size() + " words");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error loading wordlist: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        crackButton.addActionListener(e -> {
            resultArea.setText("");
            String targetHash = hashInput.getText().trim();
            String selectedAlgorithm = (String) algorithmComboBox.getSelectedItem();

            if (targetHash.isEmpty()) {
                resultArea.setText("Please enter a hash!");
                return;
            }
            if (wordsList.isEmpty()) {
                resultArea.setText("Load a wordlist first!");
                return;
            }

            // Validate selected algorithm
            int expectedLength = ALGORITHM_TO_LENGTH.get(selectedAlgorithm);
            if (targetHash.length() != expectedLength) {
                String detectedAlgorithm = guessAlgorithm(targetHash);
                resultArea.append("⚠️ Selected algorithm may be incorrect!\n");
                resultArea.append("Detected algorithm: " + (detectedAlgorithm != null ? detectedAlgorithm : "Unknown") + "\n");
                if (detectedAlgorithm != null) selectedAlgorithm = detectedAlgorithm;
            }

            // Crack hash
            boolean found = false;
            for (String word : wordsList) {
                try {
                    String hashedWord = generateHash(word, selectedAlgorithm);
                    if (hashedWord.equals(targetHash)) {
                        resultArea.append("✅ Found match: " + word);
                        found = true;
                        break;
                    }
                } catch (Exception ex) {
                    resultArea.append("Error: " + ex.getMessage());
                    return;
                }
            }
            if (!found) resultArea.append("❌ Hash not found in wordlist.");
        });

        // Layout
        contentPanel.add(hashInputPanel, BorderLayout.NORTH);
        contentPanel.add(algorithmPanel, BorderLayout.CENTER);
        contentPanel.add(wordlistPanel, BorderLayout.SOUTH);

        panel.add(contentPanel, BorderLayout.NORTH);
        panel.add(crackButtonPanel, BorderLayout.CENTER);
        panel.add(resultPanel, BorderLayout.SOUTH);

        return panel;
    }


    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.BLACK); // Changed to black
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        return button;
    }

    private String generateHash(String text, String algorithm) throws Exception {
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        byte[] hashBytes = digest.digest(text.getBytes(StandardCharsets.UTF_8));

        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }

    private String guessAlgorithm(String hash) {
        int length = hash.length();
        return HASH_LENGTH_TO_TYPE.containsKey(length) ?
                String.join("/", HASH_LENGTH_TO_TYPE.get(length)) : "Unknown";
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            HashToolGUI gui = new HashToolGUI();
            gui.setVisible(true);
        });
    }
}