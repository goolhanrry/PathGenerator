import javax.swing.*;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Scanner;

class GeoMap {
    ArrayList<GeoPolyline> polyline = new ArrayList<>(), highlightPolyline = new ArrayList<>(); // 折线列表
    ArrayList<Node> openList, closedList;                                                       // 相邻结点与已检测结点有序列表
    float dX, dY, mX, mY;
    private ArrayList<GeoPoint> nodeList = new ArrayList<>(); // 所有结点列表
    private float maxX, minX, maxY, minY, length;             // 地图边界坐标, 路径总长度
    private int index = 1, fileIndex;
    private FileInputStream fs;
    private Scanner sr;

    /* ***********************************************
     *  @brief 从 e00 文件中读取地图数据
     *  @param fileName 待读取文件的路径
     *  @return
     *      -true   读取成功
     *      -false  读取失败
     * ***********************************************/
    boolean loadMap(String fileName) {
        String buffer;
        int index, FNode, TNode, LPoly, RPoly, size;
        float x, y;
        boolean firstPoint = true;

        try {
            // 创建文件输入流
            fs = new FileInputStream(fileName);
            sr = new Scanner(fs);

            // 将文件读取指针定位到弧段（折线）要素
            do {
                buffer = sr.nextLine();
            } while (!buffer.equals("ARC  2") && !buffer.equals("ARC  3"));

            // 读取数据
            while (true) {
                buffer = sr.next();   // 折线序号
                index = sr.nextInt(); // 折线ID
                FNode = sr.nextInt(); // 起始结点
                TNode = sr.nextInt(); // 终止结点
                LPoly = sr.nextInt(); // 左多边形ID
                RPoly = sr.nextInt(); // 右多边形ID
                size = sr.nextInt();  // 折线包含的点数

                // 判断线要素是否结束
                if (size == 0) {
                    break;
                }

                // 判断文件流是否结束
                if (!sr.hasNext()) {
                    switchFile(fileName, ++fileIndex);
                }

                // 为线要素分配内存
                GeoPolyline newPolyline = new GeoPolyline(index, FNode, TNode, size);
                polyline.add(newPolyline);

                // 逐点读取坐标
                for (int i = 0; i < size; i++) {
                    x = sr.nextFloat();
                    y = sr.nextFloat();

                    // 初始化边界
                    maxX = (firstPoint || x > maxX) ? x : maxX;
                    minX = (firstPoint || x < minX) ? x : minX;
                    maxY = (firstPoint || y > maxY) ? y : maxY;
                    minY = (firstPoint || y < minY) ? y : minY;

                    firstPoint = false;

                    // 添加结点
                    if (i == 0) {
                        nodeList.add(new GeoPoint(FNode, x, y));
                    } else if (i == size - 1) {
                        nodeList.add(new GeoPoint(TNode, x, y));
                    }

                    newPolyline.addPoint(x, y);

                    // 判断文件流是否结束
                    if (!sr.hasNext()) {
                        switchFile(fileName, ++fileIndex);
                    }
                }
            }

            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Can't load file \"" + fileName + "\":\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /* ***********************************************
     *  @brief 切换待读取的文件以继续读取地图数据
     *  @param fileName     原文件路径
     *  @param fileIndex    当前文件索引
     * ***********************************************/
    private void switchFile(String fileName, int fileIndex) throws Exception {
        // 生成新文件路径
        String nextFileName = fileName.substring(0, fileName.length() - (fileIndex >= 10 ? 2 : 1)) + fileIndex;

        // 关闭当前文件流
        sr.close();

        // 打开新的文件输入流
        try {
            fs = new FileInputStream(nextFileName);
            sr = new Scanner(fs);
        } catch (Exception e) {
            throw new Exception("File \"" + nextFileName + "\" is missing");
        }
    }

    /* ***********************************************
     *  @brief 绘图数据初始化
     * ***********************************************/
    void init() {
        dX = maxX - minX;
        dY = maxY - minY;
        mX = dX / 2 + minX;
        mY = dY / 2 + minY;
    }

    /* ***********************************************
     *  @brief 使用A*算法进行路径分析
     *  @param FNode   起始结点
     *  @param TNode   目标结点
     * ***********************************************/
    void searchPath(int FNode, int TNode) {
        // 添加起始结点
        Node fNode = new Node(FNode, 0);
        closedList.add(fNode);

        while (true) {
            // 检索相邻结点
            getAdjacentNode(TNode);

            // 若无可用结点则返回
            if (openList.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Path not found", "Notice", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // 获取最优结点并移动一步
            getNearestNode();

            // 到达目标结点
            if (closedList.get(closedList.size() - 1).equals(TNode)) {
                // 生成路径向量
                generatePath();

                // 拼接路径字符串
                StringBuilder path = new StringBuilder();
                path.append("Path: ");

                for (Node item : closedList) {
                    path.append(item.id);
                    path.append(" -> ");
                }

                // 更新路径
                String pathStr = path.toString();
                PathGenerator.mainWindow.pathLabel.setText(pathStr.substring(0, pathStr.length() - 4));

                return;
            }
        }
    }

    /* ***********************************************
     *  @brief 检索当前结点的相邻结点
     *  @param TNode   目标结点
     *  @param index   结点索引
     * ***********************************************/
    private void getAdjacentNode(int TNode) {
        float tx = nodeList.get(nodeList.indexOf(new GeoPoint(TNode))).x;
        float ty = nodeList.get(nodeList.indexOf(new GeoPoint(TNode))).y;

        for (GeoPolyline item : polyline) {
            if (closedList.get(closedList.size() - 1).equals(item.FNode)) {
                // 若该结点已在 closedList 中则跳过
                if (closedList.contains(new Node(item.TNode, -1))) {
                    continue;
                }

                // F (移动总耗费) = G (从起点到该点的移动量) + 2 * H (从该点到终点的预估移动量, 使用直线距离估算)
                float F = (float) (length + item.length + 2 * Math.sqrt(Math.pow(tx - nodeList.get(nodeList.indexOf(new GeoPoint(item.TNode))).x, 2) + Math.pow(ty - nodeList.get(nodeList.indexOf(new GeoPoint(item.TNode))).y, 2)));

                // 若为新结点则加入 openList
                Node newAdjacentNode = new Node(item.TNode, index, item.index, F);
                if (!openList.contains(newAdjacentNode)) {
                    openList.add(newAdjacentNode);
                }
            }

            if (closedList.get(closedList.size() - 1).equals(item.TNode)) {
                if (closedList.contains(new Node(item.FNode, -1))) {
                    continue;
                }

                float F = (float) (length + item.length + 2 * Math.sqrt(Math.pow(tx - nodeList.get(nodeList.indexOf(new GeoPoint(item.FNode))).x, 2) + Math.pow(ty - nodeList.get(nodeList.indexOf(new GeoPoint(item.FNode))).y, 2)));

                Node newAdjacentNode = new Node(item.FNode, index, item.index, F);
                if (!openList.contains(newAdjacentNode)) {
                    openList.add(newAdjacentNode);
                }
            }
        }
    }

    /* ***********************************************
     *  @brief 获取最优结点并移动一步
     * ***********************************************/
    private void getNearestNode() {
        // 获取最优结点
        Node nextNode = new Node(0, index);
        float minF = openList.get(0).F;
        int minIndex = 0;

        for (int i = 0; i < openList.size(); i++) {
            if (openList.get(i).F <= minF) {
                minF = openList.get(i).F;
                nextNode.id = openList.get(i).id;
                minIndex = i;
            }
        }

        // 覆盖已有路径
        if (openList.get(minIndex).index < index) {
            while (closedList.get(closedList.size() - 1).index >= openList.get(minIndex).index) {
                closedList.remove(closedList.size() - 1);
            }
        }

        // 沿最小 F 值移动
        openList.remove(minIndex);
        closedList.add(nextNode);

        index++;
    }

    /* ***********************************************
     *  @brief 生成路径向量
     * ***********************************************/
    private void generatePath() {
        ArrayList<GeoPolyline> path = new ArrayList<>();
        GeoPolyline shortestPath;

        for (int i = 0; i <= closedList.size() - 2; i++) {
            path.clear();

            // 检索可能路径
            for (GeoPolyline item : polyline) {
                if ((closedList.get(i).equals(item.FNode) && closedList.get(i + 1).equals(item.TNode)) || (closedList.get(i).equals(item.TNode) && closedList.get(i + 1).equals(item.FNode))) {
                    path.add(item);
                }
            }

            // 计算最短路径
            shortestPath = path.get(0);

            for (GeoPolyline item : path) {
                if (item.length < shortestPath.length) {
                    shortestPath = item;
                }
            }

            highlightPolyline.add(shortestPath);
        }
    }

}
