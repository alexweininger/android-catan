package edu.up.cs.androidcatan.catan.gamestate;

public class Port {
    int intersection;
    int tradeRatio;
    int resourceId;

    public Port(int intersection, int tradeRatio, int resourceId){
        this.intersection = intersection;
        this.tradeRatio = tradeRatio;
        this.resourceId = resourceId;
    }

    public int getIntersection() {
        return intersection;
    }

    public int getTradeRatio() {
        return tradeRatio;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setIntersection(int intersection) {
        this.intersection = intersection;
    }

    public void setTradeRatio(int tradeRatio) {
        this.tradeRatio = tradeRatio;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }
}
