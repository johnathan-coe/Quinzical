package quinzical;

/**
 * Implemented by Controllers that wish to act on changes to the game data.
 *
 * Author: Hiruna Jayamanne
 */
public interface GameDataListener {
	void handleGameDataChanged(GameData.GameDataChangedEvent event);
}
