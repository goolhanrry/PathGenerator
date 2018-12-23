class Node {
    int id, index; // 结点 ID, 结点索引, 折线 ID
    float F;       // 结点 F 值
    private int polylineId;

    Node(int id, int index) {
        this.id = id;
        this.index = index;
    }

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
