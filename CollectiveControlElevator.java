import java.util.ArrayList;
/**
 * Created by Shayan on 2015-03-03.
 *
 */
public class CollectiveControlElevator {
    private boolean inTransit;
    private int currentFloor;
    private int numOfPassengers;
    private int maxFloors;
    private ArrayList<CollectiveControlPassenger> passengerQueue;
    private ArrayList<CollectiveControlPassenger> passengersOnBoard;
    private boolean upPeak;
    private int id;
    private int ticksLeft;
    private int maxPassengers;

    private int totalFloorsTraveled;

    private ArrayList<Integer> waitingTicks;
    private ArrayList<Integer> travelTicks;

    private final int FLOOR_TIME = 15; // ticks - 15
    private final int STOP_TIME = 71; // ticks - 71
    private final int LOAD_TIME = 70; // ticks - 70

    public CollectiveControlElevator(int maxPassengers, int maxFloors) {
        waitingTicks = new ArrayList<Integer>();
        travelTicks = new ArrayList<Integer>();

        this.maxFloors = maxFloors;
        this.maxPassengers = maxPassengers;
        inTransit = false;
        upPeak = true;
        currentFloor = 0;
        numOfPassengers = 0;
        passengersOnBoard = new ArrayList<CollectiveControlPassenger>();
        ticksLeft = 0;
    }

    public void tick() {
        for (CollectiveControlPassenger p : passengersOnBoard) {
            p.travelTickIncrement();
        }
        if(ticksLeft == 0) {
            if(isInTransit()) {
                moveOneFloor();
                if(timeToStop()) {
                    ticksLeft += STOP_TIME;
                    stop();
                }
            }
        } else {
            ticksLeft--;
        }
    }

    public int getTicksLeft() {
        return ticksLeft;
    }

    public int getTotalFloorsTraveled() {
        return totalFloorsTraveled;
    }

    public void resetFloors() {
        totalFloorsTraveled = 0;
    }

    private CollectiveControlPassenger getWaitingPassenger() {
        if(passengerQueue.size() > 0) {
            for(CollectiveControlPassenger p : passengerQueue) {
                if(!p.isElevatorOnWay()) {
                    return p;
                }
            }
        }
        return null;
    }

    public void resetTicks() {
        waitingTicks = new ArrayList<Integer>();
        travelTicks = new ArrayList<Integer>();
    }

    public int getId() {
        return id;
    }

    public ArrayList<Integer> getWaitingTicks() {
        return waitingTicks;
    }

    public ArrayList<Integer> getTravelTicks() {
        return travelTicks;
    }

    public ArrayList<CollectiveControlPassenger> getPassengerQueue() {
        return passengerQueue;
    }

    public void setPassengerQueue(ArrayList<CollectiveControlPassenger> passengerQueue) {
        this.passengerQueue = passengerQueue;
    }

    public boolean isInTransit() {
        if(passengersOnBoard.size() == 0 && passengerQueue.size() == 0)
            inTransit = false;
        else if (passengersOnBoard.size() > 0 || passengerQueue.size() > 0) {
            if(passengerQueue.size() > 0 && passengersOnBoard.size() == 0) {
                CollectiveControlPassenger p;
                if((p = getWaitingPassenger()) != null) {
                    upPeak = p.getDepartureFloor() >= currentFloor;
                    p.setElevatorOnWay(true);
                    inTransit = true;
                }
            } else {
                inTransit = true;
            }
        }
        return inTransit;
    }

    public void setId(int id) {
        this.id = id;
    }

    private void moveOneFloor() {
        ticksLeft += FLOOR_TIME;
        if (upPeak) {
            if (currentFloor < maxFloors)
                currentFloor++;
            else {
                upPeak = false;
                currentFloor--;
            }
        } else {
            if (currentFloor > 0)
                currentFloor--;
            else {
                upPeak = true;
                currentFloor++;
            }
        }

        totalFloorsTraveled++;
    }

    private void addPassengerToElevator(CollectiveControlPassenger p) {
        passengersOnBoard.add(p);
        p.setInElevatorId(id);
        numOfPassengers++;
    }

    private boolean timeToStop() {
        for(CollectiveControlPassenger p : passengersOnBoard) {
            if(p.getDestinationFloor() == currentFloor)
                return true;
        }
        for(CollectiveControlPassenger p : passengerQueue) {
            if(p.getDepartureFloor() == currentFloor)
                return true;
        }
        return false;
    }

    public void stop() {
        if (passengerQueue.size() != 0 && numOfPassengers < maxPassengers) {
            boolean departure = false;

            for(CollectiveControlPassenger p : passengerQueue) {
                boolean pUpPeak = p.getDestinationFloor() > p.getDepartureFloor();
                if(p.getDepartureFloor() == currentFloor && upPeak == pUpPeak)
                    departure = true;
            }

            if (departure) {
                ArrayList<CollectiveControlPassenger> toAdd = new ArrayList<CollectiveControlPassenger>();
                ArrayList<Integer> indexesToRemove = new ArrayList<Integer>();
                for (CollectiveControlPassenger p : passengerQueue) {
                    boolean pUpPeak = p.getDestinationFloor() > p.getDepartureFloor();
                    if (p.getDepartureFloor() == currentFloor && upPeak == pUpPeak) {
                        toAdd.add(p);
                        indexesToRemove.add(toAdd.indexOf(p));
                    }
                }
                ArrayList<CollectiveControlPassenger> queueCopy = new ArrayList<CollectiveControlPassenger>();
                int i = 0;
                for (CollectiveControlPassenger p : toAdd) {
                    addPassengerToElevator(p);
                    queueCopy.remove(indexesToRemove.get(i));
                    i++;
                }
                passengerQueue = queueCopy;
            }
        }

        boolean destination = false;

        for(CollectiveControlPassenger p : passengersOnBoard) {
            if(p.getDestinationFloor() == currentFloor)
                destination = true;
        }

        if (destination) {
            ticksLeft += LOAD_TIME;
            ArrayList<CollectiveControlPassenger> toRemove = new ArrayList<CollectiveControlPassenger>();
            for (CollectiveControlPassenger p : passengersOnBoard) {
                if (p.getDestinationFloor() == currentFloor) {
                    toRemove.add(p);
                }
            }

            for (CollectiveControlPassenger p : toRemove) {
                waitingTicks.add(p.getWaitingTicks());
                travelTicks.add(p.getTravelTicks());
                removePassenger(p);
            }
        }
    }

    private void removePassenger(CollectiveControlPassenger p) {
        //System.out.println("Passenger #" + p.getId() + " with depfl " + p.getDepartureFloor() + " got off at floor #" + currentFloor);
        numOfPassengers--;
        passengersOnBoard.remove(p);
    }
}
