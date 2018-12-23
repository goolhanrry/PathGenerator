class GeoPoint {
    int id = -1;
    float x, y;

    /* ***********************************************
     *  @brief GeoPoint 类的构造函数
     *  @param id  点ID
     * ***********************************************/
    GeoPoint(int id) {
        this.id = id;
    }

    /* ***********************************************
     *  @brief GeoPoint 类的构造函数
     *  @param id  点ID
     *  @param x   横坐标
     *  @param y   纵坐标
     * ***********************************************/
    GeoPoint(int id, float x, float y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    /* ***********************************************
     *  @brief GeoPoint 类的构造函数
     *  @param x   横坐标
     *  @param y   纵坐标
     * ***********************************************/
    GeoPoint(float x, float y) {
        this.x = x;
        this.y = y;
    }

    // 重载 equals 方法以实现查找功能
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GeoPoint) {
            GeoPoint point = (GeoPoint) obj;
            return this.id == point.id;
        }

        return false;
    }
}
