import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;

class MainWindow extends JFrame {
    static GLCanvas mapCanvas;                  // 地图绘制组件
    JLabel pathLabel = new JLabel();            // 路径分析结果
    private GLRender glRender = new GLRender(); // 绘制组件监听器

    MainWindow() {
        // 设置窗体属性
        this.setTitle("Path Generator");
        this.setSize(824, 608);
        this.setLocationRelativeTo(null);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        // 设置网格袋布局
        GridBagLayout gbl = new GridBagLayout();
        this.setLayout(gbl);

        // 添加布局约束
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;

        // 添加打开文件按钮
        JButton openFileButton = new JButton("Open");
        openFileButton.setFocusable(false);
        openFileButton.addActionListener(actionEvent -> onOpenFileButtonClicked());
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 1;
        gbc.insets = new Insets(5, 4, 1, 0);
        gbl.setConstraints(openFileButton, gbc);
        this.add(openFileButton);

        // 添加路径分析按钮
        JButton analyzeButton = new JButton("Analyze");
        analyzeButton.setFocusable(false);
        analyzeButton.addActionListener(actionEvent -> onAnalyzeButtonClicked());
        gbl.setConstraints(analyzeButton, gbc);
        this.add(analyzeButton);

        // 添加标签组件
        pathLabel.setHorizontalAlignment(JLabel.RIGHT);
        gbc.gridy = 0;
        gbc.gridwidth = 6;
        gbc.weightx = 18;
        gbc.insets = new Insets(5, 4, 1, 10);
        gbl.setConstraints(pathLabel, gbc);
        this.add(pathLabel);

        // 初始化画布
        GLProfile glProfile = GLProfile.get(GLProfile.GL2);
        GLCapabilities glCaps = new GLCapabilities(glProfile);
        mapCanvas = new GLCanvas(glCaps);

        // 设置监听器
        mapCanvas.addGLEventListener(glRender);
        mapCanvas.addMouseListener(glRender);
        mapCanvas.addMouseMotionListener(glRender);
        mapCanvas.addMouseWheelListener(glRender);

        // 添加画布组件
        gbc.gridy = 1;
        gbc.gridwidth = 8;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.insets = new Insets(0, 10, 10, 10);
        gbl.setConstraints(mapCanvas, gbc);
        this.add(mapCanvas);
    }

    private void onOpenFileButtonClicked() {
        // 初始化文件选择对话框
        JFileChooser fc = new JFileChooser(new File("."));
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setAcceptAllFileFilterUsed(false);

        // 设置文件类型过滤器
        E00FileFilter filter = new E00FileFilter();
        fc.setFileFilter(filter);

        // 选择地图文件
        if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            String fileName = fc.getSelectedFile().getAbsolutePath();

            // 文件合法性检测
            if (!fileName.endsWith(".e00")) {
                JOptionPane.showMessageDialog(null, "Please choose a \".e00\" file", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 读取文件并解码
            PathGenerator.map = new GeoMap();
            if (PathGenerator.map.loadMap(fileName)) {
                // 更新标题栏文字
                this.setTitle(fc.getSelectedFile().getName() + " - Path Generator");

                // 清除路径
                pathLabel.setText("");

                // 渲染地图
                PathGenerator.map.init();
                glRender.resetOffset();
                mapCanvas.display();
            } else {
                // 清空已读取的地图，内存交给虚拟机处理
                PathGenerator.map = null;
            }
        }
    }

    private void onAnalyzeButtonClicked() {
        // 若未打开地图文件则弹窗提示
        if (PathGenerator.map == null) {
            JOptionPane.showMessageDialog(null, "Please open a map first", "Notice", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // 打开路径分析对话框
        Dialog dialog = new Dialog();
        dialog.pack();
        dialog.setVisible(true);
    }
}
