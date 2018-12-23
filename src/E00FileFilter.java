import java.io.File;
import javax.swing.filechooser.FileFilter;

class E00FileFilter extends FileFilter {
    public boolean accept(File f) {
        return f.getName().endsWith(".e00");
    }

    public String getDescription() {
        return "e00文件";
    }
}
