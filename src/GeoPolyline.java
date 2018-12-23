class GeoPolyline {
    int index, FNode, TNode, size;
    float length = 0; // 折线当前长度
    GeoPoint pts[];   // 折线包含的点集
    private int count;

    GeoPolyline(int index, int FNode, int TNode, int size) {
        this.index = index;
        this.FNode = FNode;
        this.TNode = TNode;
        this.size = size;
        pts = new GeoPoint[size];
    }

    void addPoint(float x, float y) {
        pts[count] = new GeoPoint(x, y);
        count++;

        if (count > 1) {
            length += Math.sqrt(Math.pow(pts[count - 2].x - x, 2) + Math.pow(pts[count - 2].y - y, 2));
        }
    }
}
