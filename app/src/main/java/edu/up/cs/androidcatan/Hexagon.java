package edu.up.cs.androidcatan;

/**
 *
 */
public class Hexagon {
    private int resourceId;
    private int chitValue;

    /**
     * Hexagon constructor AW
     *
     * @param resourceType - resourceType type of hexagon
     * @param chitValue    - dice value of hexagon
     */
    Hexagon(int resourceType, int chitValue) {
        this.resourceId = resourceType;
        this.chitValue = chitValue;
    }

    /**
     * @return - hexagon resource id, [0-4]
     */
    public int getResourceId() {
        return resourceId;
    }

    /**
     * @return
     */
    public int getChitValue() {
        return chitValue;
    }

    /**
     * @return
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("");
        sb.append("Hexagon{ ");
        sb.append("resourceType: ");
        sb.append(resourceId);
        sb.append(", chitValue: ");
        sb.append(chitValue);
        sb.append("}");

        return sb.toString();
    }
}
