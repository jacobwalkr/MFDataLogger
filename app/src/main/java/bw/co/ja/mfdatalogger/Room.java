package bw.co.ja.mfdatalogger;

/**
 * Created by Jacob on 22/01/2017.
 */
public class Room {

    public String floor;
    public String quadrant;
    public String room;

    public Room(String floor, String quadrant, String room) {
        this.floor = floor;
        this.quadrant = quadrant;
        this.room = room;
    }

    public String getRoomCode() {
        return String.format("%s%s-%s", floor, quadrant, room);
    }
}
