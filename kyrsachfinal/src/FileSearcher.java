import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FileSearcher {
    private SignatureDatabase signatureDatabase;

    public FileSearcher(SignatureDatabase signatureDatabase) {
        this.signatureDatabase = signatureDatabase;
    }

    public List<File> searchFiles(String path, String signatureName) {
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles == null) {
            listOfFiles = new File[0];
        }

        return Arrays.stream(listOfFiles)
                .filter(File::isFile)
                .filter(file -> signatureDatabase.matchesSignature(file, signatureName))
                .collect(Collectors.toList());
    }
}
