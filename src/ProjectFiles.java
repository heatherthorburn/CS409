import java.io.File;
import java.util.ArrayList;

public class ProjectFiles {

    private ArrayList<File> files;

    public ProjectFiles() {
        files = new ArrayList<>();
    }

    public ArrayList<File> getProjectFiles() {
        File[] folderFiles;
        File in = new File("/home/heather/409groupassignment/Animal");
        folderFiles = in.listFiles();
        for (File f : folderFiles) {
            if (f.getName().endsWith(".java")) {
                files.add(f);
            }
        }
        return files;
    }
}
