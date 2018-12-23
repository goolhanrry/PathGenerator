class GeoPolyline {
    int index, FNode, TNode, size;
    float length = 0; // 折线当前长度
    GeoPoint pts[];   // 折线包含的点集
    private int count;

    /* ***********************************************
     *  @brief GeoPolyline 类的构造函数
     *  @param index   折线编号
     *  @param FNode   起始结点
     *  @param TNode   目标结点
     *  @param size    折线包含点的数量
     * ***********************************************/
    GeoPolyline(int index, int FNode, int TNode, int size) {
        this.index = index;
        this.FNode = FNode;
        this.TNode = TNode;
        this.size = size;
        pts = new GeoPoint[size];
    }

    /* ***********************************************
     *  @brief 添加点要素并更新折线长度
     *  @param x   横坐标
     *  @param y   纵坐标
     * ***********************************************/
    void addPoint(float x, float y) {
        pts[count] = new GeoPoint(x, y);
        count++;

        if (count > 1) {
            length += Math.sqrt(Math.pow(pts[count - 2].x - x, 2) + Math.pow(pts[count - 2].y - y, 2));
        }
    }
}
