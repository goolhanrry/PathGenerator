class Node {
    int id, index; // 结点 ID, 结点索引, 折线 ID
    float F;       // 结点 F 值
    private int polylineId;

    /* ***********************************************
     *  @brief Node 类的构造函数
     *  @param id      结点ID
     *  @param index   结点编号
     * ***********************************************/
    Node(int id, int index) {
        this.id = id;
        this.index = index;
    }

    /* ***********************************************
     *  @brief Node 类的构造函数
     *  @param id           结点ID
     *  @param index        结点编号
     *  @param polylineId   折线ID
     *  @param F            F值
     * ***********************************************/
    Node(int id, int index, int polylineId, float F) {
        this.id = id;
        this.index = index;
        this.polylineId = polylineId;
        this.F = F;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Node) {
            Node node = (Node) obj;
            return index == -1 ? this.id == node.id : this.id == node.id && this.polylineId == node.polylineId;
        } else if (obj instanceof Integer) {
            int id = (Integer) obj;
            return this.id == id;
        }
        return false;
    }
}
