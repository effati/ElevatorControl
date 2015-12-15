/**
 * Creates a scenario of elevator systems and passengers.
 *
 * Created by Shayan on 2015-03-03.
 */
public class Main {
    public Main() {}

    public static void main(String[] args) {
        System.out.println("Collective Control:");
        new CollectiveControlSystem();
        System.out.println();
        System.out.println("---------");
        System.out.println();
        System.out.println("Zone Approach:");
        new ZoneApproachSystem();
    }
}
