import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.zip.ZipEntry;

/**
 * A system of elevators. Usually used in a building.
 *
 * Can be modified to have one or several elevators with different properties.

 * Created by Shayan on 2015-03-03.
 */
public class ZoneApproachSystem {
    private final int TEST_TICKS = 360000;
    private final int RUSH_HOUR_TICKS = 75; // 75
    private final int WORKING_HOURS_TICKS = 300; // 300

    private int idCounter = 0;
    private int passengersFloorsTraveled = 0;

    private final int NUM_OF_ELEVATORS = 2;
    private final int NUM_OF_FLOORS = 11; // number of floors
    private final int MAX_PASSENGERS = 10; // maximum number of passengers
    private final int TOTAL_WORKERS = 960;
    private final int MAX_WORKERS_PER_FLOOR = TOTAL_WORKERS / (NUM_OF_FLOORS-1);
    private ZoneApproachElevator[] elevators;
    private int tick;

    private ArrayList<HashSet<ZoneApproachPassenger>> listOfOfficeFloors;

    public ZoneApproachSystem() {
        buildBuilding();
        createElevators();
        run();
    }

    private void run() {
        for(tick = 0; tick < TEST_TICKS; tick++) {
            for (ZoneApproachElevator e : elevators) {
                ArrayList<ZoneApproachPassenger> passengerQueue = e.getPassengerQueue();
                for (ZoneApproachPassenger p : passengerQueue) {
                    p.waitingTickIncrement();
                }
            }

            potentialPassengerArrival();


            ArrayList<ZoneApproachPassenger> passengersSwitchingElevator = new ArrayList<ZoneApproachPassenger>();
            for (ZoneApproachElevator e : elevators) {
                ArrayList<ZoneApproachPassenger> tempElevatorSwitch;
                tempElevatorSwitch = e.tick();
                if(tempElevatorSwitch != null) {
                    for(ZoneApproachPassenger p : tempElevatorSwitch) {
                        passengersSwitchingElevator.add(p);
                    }
                }
            }

            for(ZoneApproachPassenger p : passengersSwitchingElevator) {
                p.setNewDestinationFloor();
                if(p.getInElevatorId() == 0) {
                    elevators[1].getPassengerQueue().add(p);
                } else {
                    elevators[0].getPassengerQueue().add(p);
                }
            }

            if(tick == 72000 || tick == TEST_TICKS-1) {
                for (ZoneApproachElevator e : elevators) {
                    System.out.println("Rush hours:");
                    System.out.println("Elevator #" + e.getId());
                    System.out.println("Average waiting ticks: " + calculateAverage(e.getWaitingTicks()));
                    System.out.println("Average travel ticks: " + calculateAverage(e.getTravelTicks()));
                    System.out.println("Total floors traveled: " + e.getTotalFloorsTraveled());
                    System.out.println();
                    e.resetTicks();
                    e.resetFloors();
                }
            } else if (tick == 288000) {
                for (ZoneApproachElevator e : elevators) {
                    System.out.println("Working hours:");
                    System.out.println("Elevator #" + e.getId());
                    System.out.println("Average waiting ticks: " + calculateAverage(e.getWaitingTicks()));
                    System.out.println("Average travel ticks: " + calculateAverage(e.getTravelTicks()));
                    System.out.println("Total floors traveled: " + e.getTotalFloorsTraveled());
                    System.out.println();
                    e.resetTicks();
                    e.resetFloors();
                }
            }
        }
        System.out.println("Reached end with " + tick + " ticks and " + idCounter + " total trips.");
        System.out.println("Total floors traveled: " + passengersFloorsTraveled);
        System.out.println("Average floors traveled: " + passengersFloorsTraveled / idCounter);
    }

    private void buildBuilding() {
        listOfOfficeFloors = new ArrayList<HashSet<ZoneApproachPassenger>>();

        for(int i = 0; i < NUM_OF_FLOORS; i++) {
            listOfOfficeFloors.add(new HashSet<ZoneApproachPassenger>());
        }
    }

    private int calculateAverage(ArrayList<Integer> tickList) {
        int sum = 0;
        if(!tickList.isEmpty()) {
            for (Integer mark : tickList) {
                sum += mark;
            }
            return sum / tickList.size();
        }
        return sum;
    }

    private void potentialPassengerArrival() {
        ZoneApproachPassenger p = null;
        if (tick < 72000) { // 72000 == first 2h interval
            if(tick % RUSH_HOUR_TICKS == 0) // one passenger each 7,5 sec. totalling 8 in each minute.
                p = simulatePassenger(0);
        } else if (tick >= 72000 && tick < 288000) { // working hours
            if(tick % WORKING_HOURS_TICKS == 0) // two each minute.
                p = simulatePassenger(2);
        } else if (tick >= 288000) {
            if(tick % RUSH_HOUR_TICKS == 0)
                p = simulatePassenger(1); // one passenger each 7,5 sec. totalling 8 in each minute.
        }
        if(p != null) {
            int midway = NUM_OF_FLOORS / 2;
            if (p.getDestinationFloor() <= midway) {
                if(p.getDepartureFloor() <= midway) {
                    elevators[0].getPassengerQueue().add(p); //fulhack
                } else {
                    p.setNewDestinationFloor();
                    elevators[1].getPassengerQueue().add(p);
                }
            } else {
                if(p.getDepartureFloor() > midway || p.getDepartureFloor() == 0) {
                    elevators[1].getPassengerQueue().add(p);
                } else {
                    p.setNewDestinationFloor();
                    elevators[0].getPassengerQueue().add(p);
                }
            }
        }
    }

    /**
     * Create elevators to be used in the system.
     */
    private void createElevators() {
        elevators = new ZoneApproachElevator[NUM_OF_ELEVATORS];
        for(int i = 0; i < NUM_OF_ELEVATORS; i++) {
            elevators[i] = new ZoneApproachElevator(MAX_PASSENGERS, NUM_OF_FLOORS, i);
            elevators[i].setId(i);
        }
    }

    /**
     *
     * @param timeInterval 0 for morning, 1 for closing time, 2 for everything else
     * @return
     */
    private ZoneApproachPassenger simulatePassenger(int timeInterval) {
        int departureFloor = 0;
        int destinationFloor = 0;
        Random random = new Random();

        int workingFloor = -1;

        do {
            double randomDouble = random.nextDouble();
            switch (timeInterval) {
                case 0:
                    if (randomDouble < 0.9) {
                        departureFloor = 0;
                        while ((destinationFloor = random.nextInt(NUM_OF_FLOORS)) == departureFloor) {}
                        workingFloor = destinationFloor;
                    } else {
                        while ((departureFloor = random.nextInt(NUM_OF_FLOORS)) == 0) {
                        }
                        while ((destinationFloor = random.nextInt(NUM_OF_FLOORS)) == departureFloor) {
                        }
                    }
                    break;
                case 1:
                    if (randomDouble > 0.1) {
                        while ((departureFloor = random.nextInt(NUM_OF_FLOORS)) == 0) {
                        }
                        destinationFloor = 0;
                    } else {
                        departureFloor = random.nextInt(NUM_OF_FLOORS);
                        while ((destinationFloor = random.nextInt(NUM_OF_FLOORS)) == departureFloor) {
                        }
                    }
                    break;
                case 2:
                    departureFloor = random.nextInt(NUM_OF_FLOORS);
                    while ((destinationFloor = random.nextInt(NUM_OF_FLOORS)) == departureFloor) {
                    }
                    break;
                default:
                    break;
            }
        } while(listOfOfficeFloors.get(destinationFloor).size()  > MAX_WORKERS_PER_FLOOR);
        ZoneApproachPassenger p = new ZoneApproachPassenger(departureFloor, destinationFloor, idCounter);
        if(workingFloor != -1)
            listOfOfficeFloors.get(workingFloor).add(p);
        idCounter++;
        passengersFloorsTraveled += Math.abs(destinationFloor-departureFloor);
        return p;
    }


}
