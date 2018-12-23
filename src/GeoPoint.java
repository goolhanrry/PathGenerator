class GeoPoint {
    int id = -1;
    float x, y;

    GeoPoint(int id) {
        this.id = id;
    }

    GeoPoint(int id, float x, float y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    GeoPoint(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GeoPoint) {
            GeoPoint point = (GeoPoint) obj;
            return this.id == point.id;
        }

        return false;
    }
}
