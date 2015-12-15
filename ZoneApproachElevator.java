import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Shayan on 2015-03-03.
 *
 */
public class ZoneApproachElevator {
    private boolean inTransit;
    private int currentFloor;
    private int numOfPassengers;
    private int maxFloors;
    private ArrayList<ZoneApproachPassenger> passengerQueue;
    private ArrayList<ZoneApproachPassenger> passengersOnBoard;
    private boolean upPeak;
    private int id;
    private int ticksLeft;
    private int maxPassengers;
    private int zone;

    private int totalFloorsTraveled;

    private ArrayList<Integer> waitingTicks;
    private ArrayList<Integer> travelTicks;

    private final int FLOOR_TIME = 15; // ticks - 15
    private final int STOP_TIME = 71; // ticks - 71
    private final int LOAD_TIME = 70; // ticks - 70

    public ZoneApproachElevator(int maxPassengers, int maxFloors, int zone) {
        waitingTicks = new ArrayList<Integer>();
        travelTicks = new ArrayList<Integer>();

        passengerQueue = new ArrayList<ZoneApproachPassenger>();

        this.zone = zone;
        this.maxFloors = maxFloors;
        this.maxPassengers = maxPassengers;
        inTransit = false;
        upPeak = true;
        currentFloor = 0;
        numOfPassengers = 0;
        passengersOnBoard = new ArrayList<ZoneApproachPassenger>();
        ticksLeft = STOP_TIME;
    }

    public ArrayList<ZoneApproachPassenger> tick() {
        for (ZoneApproachPassenger p : passengersOnBoard) {
            p.travelTickIncrement();
        }
        if(ticksLeft == 0) {
            if(isInTransit()) {
                moveOneFloor();
                if(timeToStop()) {
                    ticksLeft += STOP_TIME;
                    return stop();
                }
            }
/*
            if(getWaitingPassenger() != null) {
                inTransit = true;
            }*/
        } else {
            ticksLeft--;
        }
        return null;
    }

    public int getTotalFloorsTraveled() {
        return totalFloorsTraveled;
    }

    public void resetFloors() {
        totalFloorsTraveled = 0;
    }

    private ZoneApproachPassenger getWaitingPassenger() {
        if(passengerQueue.size() > 0) {
            for(ZoneApproachPassenger p : passengerQueue) {
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

    public ArrayList<ZoneApproachPassenger> getPassengerQueue() {
        return passengerQueue;
    }

    public void setPassengerQueue(ArrayList<ZoneApproachPassenger> passengerQueue) {
        this.passengerQueue = passengerQueue;
    }

    public boolean isInTransit() {
        if(passengersOnBoard.size() == 0 && passengerQueue.size() == 0)
            inTransit = false;
        else if (passengersOnBoard.size() > 0 || passengerQueue.size() > 0) {
            if(passengerQueue.size() > 0 && passengersOnBoard.size() == 0) {
                ZoneApproachPassenger p;
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

    private void addPassengerToElevator(ZoneApproachPassenger p) {
        passengersOnBoard.add(p);
        p.setInElevatorId(id);
        numOfPassengers++;
        //System.out.println("Boarded passenger: #" + p.getId() + ", depFloor: " + p.getDepartureFloor() + ", desFloor: " + p.getDestinationFloor());
    }

    private boolean timeToStop() {
        for(ZoneApproachPassenger p : passengersOnBoard) {
            if(p.getDestinationFloor() == currentFloor)
                return true;
        }
        for(ZoneApproachPassenger p : passengerQueue) {
            if(p.getDepartureFloor() == currentFloor)
                return true;
        }
        return false;
    }

    public ArrayList<ZoneApproachPassenger> stop() {
        if (passengerQueue.size() != 0 && numOfPassengers < maxPassengers) {
            departure();
        }

        ArrayList<ZoneApproachPassenger> passengersSwitchingElevator = new ArrayList<ZoneApproachPassenger>();
        boolean destination = false;

        for(ZoneApproachPassenger p : passengersOnBoard) {
            if(p.getDestinationFloor() == currentFloor)
                destination = true;
        }

        if (destination) {
            ticksLeft += LOAD_TIME;
            ArrayList<ZoneApproachPassenger> toRemove = new ArrayList<ZoneApproachPassenger>();
            for (ZoneApproachPassenger p : passengersOnBoard) {
                if (p.getDestinationFloor() == currentFloor) {
                    toRemove.add(p);
                }
            }

            for (ZoneApproachPassenger p : toRemove) {
                if(p.getNewDestinationFloor() != -1) {
                    passengersSwitchingElevator.add(p);
                }
                waitingTicks.add(p.getWaitingTicks());
                travelTicks.add(p.getTravelTicks());
                removePassenger(p);
            }
        }

        return passengersSwitchingElevator;
    }

    private void departure() {
        boolean departure = false;

        for(ZoneApproachPassenger p : passengerQueue) {
            if(p.getDepartureFloor() == currentFloor
                    && upPeak == (p.getDestinationFloor() > p.getDepartureFloor()))
                departure = true;
        }

        if (departure) {
            ArrayList<ZoneApproachPassenger> toAdd = new ArrayList<ZoneApproachPassenger>();
            ArrayList<Integer> indexesToRemove = new ArrayList<Integer>();
            for (ZoneApproachPassenger p : passengerQueue) {
                if (p.getDepartureFloor() == currentFloor) {
                    toAdd.add(p);
                    indexesToRemove.add(toAdd.indexOf(p));
                }
            }
            ArrayList<ZoneApproachPassenger> queueCopy = new ArrayList<ZoneApproachPassenger>();
            int i = 0;
            for (ZoneApproachPassenger p : toAdd) {
                addPassengerToElevator(p);
                queueCopy.remove(indexesToRemove.get(i));
                i++;
            }
            passengerQueue = queueCopy;
        }
    }

    private void removePassenger(ZoneApproachPassenger p) {
        //System.out.println("Passenger #" + p.getId() + " with depfl " + p.getDepartureFloor() + " got off at floor #" + currentFloor);
        numOfPassengers--;
        passengersOnBoard.remove(p);
    }
}
