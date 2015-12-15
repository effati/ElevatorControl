/**
 * Created by Shayan on 2015-03-03.
 */
public class ZoneApproachPassenger {
    private int departureFloor;
    private int destinationFloor;
    private int id;
    private int inElevatorId;
    private int waitingTicks;
    private int travelTicks;
    private boolean elevatorOnWay;

    private int newDestinationFloor;

    public ZoneApproachPassenger(int departureFloor, int destinationFloor, int id) {
        waitingTicks = 0;
        travelTicks = 0;
        elevatorOnWay = false;
        newDestinationFloor = -1;
        this.id = id;
        this.departureFloor = departureFloor;
        this.destinationFloor = destinationFloor;
    }

    public void setNewDestinationFloor() {
        if(newDestinationFloor == -1) {
            newDestinationFloor = destinationFloor;
            destinationFloor = 0;
        } else {
            destinationFloor = newDestinationFloor;
            newDestinationFloor = -1;
        }
    }

    public int getNewDestinationFloor() {
        return newDestinationFloor;
    }

    public boolean isElevatorOnWay() {
        return elevatorOnWay;
    }

    public void setElevatorOnWay(boolean elevatorOnWay) {
        this.elevatorOnWay = elevatorOnWay;
    }

    public void waitingTickIncrement() {
        waitingTicks++;
    }

    public int getWaitingTicks() {
        return waitingTicks;
    }

    public void travelTickIncrement() {
        travelTicks++;
    }

    public int getTravelTicks() {
        return travelTicks;
    }

    public int getDepartureFloor() {
        return departureFloor;
    }

    public int getDestinationFloor() {
        return destinationFloor;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setInElevatorId(int inElevatorId) {
        this.inElevatorId = inElevatorId;
    }

    public int getInElevatorId() {
        return inElevatorId;
    }
}
