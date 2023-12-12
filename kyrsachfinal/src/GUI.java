import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

public class GUI {
    private JFrame frame;
    private JTextField pathField;
    private JComboBox<String> signatureComboBox;
    private JTextField newSignatureNameField;
    private JTextField newSignatureValueField;
    private JTextArea resultArea;
    private FileSearcher fileSearcher;
    private SignatureDatabase signatureDatabase;

    public GUI() {
        signatureDatabase = new SignatureDatabase();
        fileSearcher = new FileSearcher(signatureDatabase);
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Поиск Файлов По Сигнатуре");
        frame.setBounds(100, 100, 600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        createPathField();
        createSignatureComboBox();
        createResultArea();
        createAddSignatureSection();

        frame.setVisible(true);
    }

    private void createPathField() {
        JLabel lblPath = new JLabel("Путь:");
        lblPath.setBounds(10, 11, 46, 14);
        frame.getContentPane().add(lblPath);

        pathField = new JTextField();
        pathField.setBounds(66, 8, 358, 20);
        frame.getContentPane().add(pathField);
        pathField.setColumns(10);

        JButton btnSearch = new JButton("Поиск");
        btnSearch.addActionListener((ActionEvent e) -> performSearch());
        btnSearch.setBounds(435, 7, 139, 23);
        frame.getContentPane().add(btnSearch);
    }

    private void createSignatureComboBox() {
        JLabel lblSignature = new JLabel("Сигнатура:");
        lblSignature.setBounds(10, 42, 70, 14);
        frame.getContentPane().add(lblSignature);

        signatureComboBox = new JComboBox<>();
        signatureComboBox.setBounds(90, 39, 334, 20);
        frame.getContentPane().add(signatureComboBox);
        updateSignatureComboBox();

        JButton btnRemoveSignature = new JButton("Удалить Сигнатуру");
        btnRemoveSignature.addActionListener((ActionEvent e) -> removeSelectedSignature());
        btnRemoveSignature.setBounds(435, 38, 139, 23);
        frame.getContentPane().add(btnRemoveSignature);
    }

    private void updateSignatureComboBox() {
        signatureComboBox.removeAllItems();
        for (String signature : signatureDatabase.getAvailableSignatures()) {
            signatureComboBox.addItem(signature);
        }
    }

    private void createResultArea() {
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(10, 104, 564, 146);
        frame.getContentPane().add(scrollPane);

        resultArea = new JTextArea();
        scrollPane.setViewportView(resultArea);
    }

    private void createAddSignatureSection() {
        JLabel lblNewSignatureName = new JLabel("Новое Имя Сигнатуры:");
        lblNewSignatureName.setBounds(10, 260, 150, 14);
        frame.getContentPane().add(lblNewSignatureName);

        newSignatureNameField = new JTextField();
        newSignatureNameField.setBounds(170, 257, 254, 20);
        frame.getContentPane().add(newSignatureNameField);
        newSignatureNameField.setColumns(10);

        JLabel lblNewSignatureValue = new JLabel("Значение Сигнатуры:");
        lblNewSignatureValue.setBounds(10, 290, 150, 14);
        frame.getContentPane().add(lblNewSignatureValue);

        newSignatureValueField = new JTextField();
        newSignatureValueField.setBounds(170, 287, 254, 20);
        frame.getContentPane().add(newSignatureValueField);
        newSignatureValueField.setColumns(10);

        JButton btnAddSignature = new JButton("Добавить Сигнатуру");
        btnAddSignature.addActionListener((ActionEvent e) -> addSignature());
        btnAddSignature.setBounds(435, 256, 139, 23);
        frame.getContentPane().add(btnAddSignature);
    }

    private void performSearch() {
        String path = pathField.getText();
        String signatureName = (String) signatureComboBox.getSelectedItem();
        if (signatureName == null || signatureName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Пожалуйста, выберите сигнатуру для поиска.");
            return;
        }

        File directory = new File(path);
        if (!directory.exists() || !directory.isDirectory()) {
            JOptionPane.showMessageDialog(frame, "Указанный путь не существует или не является директорией.");
            return;
        }

        try {
            List<File> files = fileSearcher.searchFiles(path, signatureName);
            displayResults(files);
        } catch (Exception e) {
            ExceptionHandler.handle(e);
        }
    }


    private void addSignature() {
        String signatureName = newSignatureNameField.getText();
        String signatureValue = newSignatureValueField.getText();
        if (signatureName == null || signatureName.trim().isEmpty() ||
                signatureValue == null || signatureValue.trim().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Имя и значение сигнатуры не могут быть пустыми.");
            return;
        }
        try {
            signatureDatabase.addSignature(signatureName, signatureValue);
            updateSignatureComboBox();
            JOptionPane.showMessageDialog(frame, "Сигнатура успешно добавлена: " + signatureName);
        } catch (Exception e) {
            ExceptionHandler.handle(e);
        }
    }

    private void removeSelectedSignature() {
        String selectedSignature = (String) signatureComboBox.getSelectedItem();
        if (selectedSignature != null) {
            signatureDatabase.removeSignature(selectedSignature);
            updateSignatureComboBox();
            JOptionPane.showMessageDialog(frame, "Сигнатура удалена: " + selectedSignature);
        }
    }

    private void displayResults(List<File> files) {
        resultArea.setText("");
        for (File file : files) {
            resultArea.append("Файл: " + file.getPath() + "\n");
            try {
                BasicFileAttributes attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                resultArea.append("Размер: " + attrs.size() + " байт\n");
                resultArea.append("Дата последнего изменения: " + attrs.lastModifiedTime() + "\n\n");
            } catch (Exception e) {
                ExceptionHandler.handle(e);
            }
        }
    }
}
