package edu.up.cs.androidcatan.catan.gamestate;

public class Port {
    private int intersection, tradeRatio, resourceId;

    public Port(int intersection, int tradeRatio, int resourceId){
        this.intersection = intersection;
        this.tradeRatio = tradeRatio;
        this.resourceId = resourceId;
    }

    public Port(Port p) {
        this.setIntersection(p.getIntersection());
        this.setTradeRatio(p.getTradeRatio());
        this.setResourceId(p.getResourceId());
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

    @Override
    public String toString() {
        return "Port{" +
                "intersection=" + intersection +
                ", tradeRatio=" + tradeRatio +
                ", resourceId=" + resourceId +
                '}';
    }
}
