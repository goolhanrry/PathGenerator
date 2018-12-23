import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

class Dialog extends JDialog {
    private JTextField FNodeTextField, TNodeTextField;
    private int maxNode;

    Dialog() {
        // 设置对话框属性
        this.setTitle("Path Analysis");
        this.setSize(250, 168);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setModal(true);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // 设置网格袋布局
        GridBagLayout gbl = new GridBagLayout();
        this.setLayout(gbl);

        // 添加布局约束
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;

        // 获取节点范围
        maxNode = PathGenerator.map.polyline.size();

        // 添加提示标签
        JLabel rangeLabel = new JLabel("Please input node ID (1 ~ " + maxNode + ")", JLabel.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 6;
        gbc.insets = new Insets(20, 5, 15, 5);
        gbl.setConstraints(rangeLabel, gbc);
        this.add(rangeLabel);

        JLabel FNodeLabel = new JLabel("FNode ID：", JLabel.RIGHT);
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 25, 10, 0);
        gbl.setConstraints(FNodeLabel, gbc);
        this.add(FNodeLabel);

        JLabel TNodeLabel = new JLabel("TNode ID：", JLabel.RIGHT);
        gbc.gridy = 2;
        gbl.setConstraints(TNodeLabel, gbc);
        this.add(TNodeLabel);

        // 添加输入框
        FNodeTextField = new JTextField();
        FNodeTextField.setSize(500, 20);
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridwidth = 4;
        gbc.insets = new Insets(0, 0, 10, 25);
        gbl.setConstraints(FNodeTextField, gbc);
        this.add(FNodeTextField);

        TNodeTextField = new JTextField();
        TNodeTextField.setSize(500, 20);
        gbc.gridy = 2;
        gbl.setConstraints(TNodeTextField, gbc);
        this.add(TNodeTextField);

        // 添加确认按钮
        JButton buttonOK = new JButton("OK");
        buttonOK.addActionListener(actionEvent -> onOK());
        gbc.gridx = 4;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 15, 45);
        gbl.setConstraints(buttonOK, gbc);
        this.add(buttonOK);

        // 添加取消按钮
        JButton buttonCancel = new JButton("Cancel");
        buttonCancel.addActionListener(actionEvent -> dispose());
        gbc.gridx = 1;
        gbc.insets = new Insets(0, 45, 15, 0);
        gbl.setConstraints(buttonCancel, gbc);
        this.add(buttonCancel);
    }

    private void onOK() {
        int FNode, TNode;

        // 输入合法性校验
        if (FNodeTextField.getText().equals("") && TNodeTextField.getText().equals("")) {
            dispose();
            return;
        } else {
            try {
                FNode = Integer.parseInt(FNodeTextField.getText());
                TNode = Integer.parseInt(TNodeTextField.getText());

                if (FNode < 1 || TNode > maxNode || TNode < 1 || FNode > maxNode) {
                    dispose();
                    JOptionPane.showMessageDialog(null, "Bad Input", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (Exception e) {
                dispose();
                JOptionPane.showMessageDialog(null, "Bad Input", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // 清除结点列表
        PathGenerator.map.openList = new ArrayList<>();
        PathGenerator.map.closedList = new ArrayList<>();
        PathGenerator.map.highlightPolyline.clear();

        // 执行最短路径分析
        PathGenerator.map.searchPath(FNode, TNode);

        dispose();
    }
}
