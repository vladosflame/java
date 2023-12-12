import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SignatureDatabase {
    private Map<String, String> signatureMap;
    private static final String SIGNATURES_FILE = "signatures.txt";

    public SignatureDatabase() {
        signatureMap = new HashMap<>();
        loadSignatures();
    }

    public void addSignature(String signatureName, String signature) {
        signatureMap.put(signatureName.toUpperCase(), signature);
        saveSignatures();
    }

    public boolean matchesSignature(File file, String signatureName) {
        String fileExtension = getFileExtension(file);
        String requiredExtension = signatureMap.get(signatureName.toUpperCase());
        return requiredExtension != null && fileExtension.equalsIgnoreCase(requiredExtension);
    }

    public void removeSignature(String signatureName) {
        signatureMap.remove(signatureName.toUpperCase());
        saveSignatures();
    }

    public Set<String> getAvailableSignatures() {
        return signatureMap.keySet();
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return "";
        }
        return name.substring(lastIndexOf);
    }

    private void saveSignatures() {
        try (PrintWriter out = new PrintWriter(new FileWriter(SIGNATURES_FILE))) {
            for (Map.Entry<String, String> entry : signatureMap.entrySet()) {
                out.println(entry.getKey() + "=" + entry.getValue());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadSignatures() {
        File file = new File(SIGNATURES_FILE);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split("=", 2);
                    if (parts.length == 2) {
                        signatureMap.put(parts[0], parts[1]);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
