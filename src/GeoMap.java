import javax.swing.*;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Scanner;

class GeoMap {
    ArrayList<GeoPolyline> polyline = new ArrayList<>(); // 折线列表
    private float maxX, minX, maxY, minY, length; // 地图边界坐标, 路径总长度
    float dX, dY, mX, mY;

    boolean loadMap(String fileName) {
        String buffer;
        int index, FNode, TNode, LPoly, RPoly, size;
        float x, y;
        boolean firstPoint = true;

        try {
            // 创建文件输入流
            FileInputStream fs = new FileInputStream(fileName);
            Scanner sr = new Scanner(fs);

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

                    newPolyline.addPoint(x, y);
                }
            }

            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Can't load file \"" + fileName + "\":\n\nBad Format", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // 绘图数据初始化
    void init() {
        dX = maxX - minX;
        dY = maxY - minY;
        mX = dX / 2 + minX;
        mY = dY / 2 + minY;
    }
}
