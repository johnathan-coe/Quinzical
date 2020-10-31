package quinzical;

/**
 * A simple entry point so JavaFX components are included in the produced jar.
 * 
 * This class must be used as the main class in the POM for maven-shade.
 */
public class Run {
	public static void main(String[] args) {
        Game.main(args);
    }
}
