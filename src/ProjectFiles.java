import java.io.File;
import java.util.ArrayList;

class ProjectFiles {

    private ArrayList<File> files;

    ProjectFiles() {
        files = new ArrayList<>();
    }

    ArrayList<File> getProjectFiles() {
        File[] folderFiles;
        File in = new File("Taxi");
        folderFiles = in.listFiles();
        assert folderFiles != null;
        for (File f : folderFiles) {
            if (f.getName().endsWith(".java")) {
                files.add(f);
            }
        }
        return files;
    }
}
