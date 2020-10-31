package quinzical;

import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import quinzical.data.GameData;
import quinzical.festival.Festival;
import quinzical.ui.help.HelpViewer;
import quinzical.ui.scenes.*;
import quinzical.ui.scenes.meta.*;
import quinzical.ui.scenes.main.*;
import quinzical.ui.scenes.practice.*;

/**
 * Top-level Application subclass aggregating all pages in the application.
 * 
 * @author jej
 */
public class Game extends Application {
	private GameData data;
	private StartPage startPage;
	private SelectPage selectPage;
	private PracticeSelectPage practiceSelectPage;
	private QuestionPage questionPage;
	private PracticeQuestionPage practiceQuestionPage;
	private RewardsPage rewardsPage;
	private HelpViewer helpViewer;
	private SettingsPage settingsPage;
	private Leaderboard leaderboard;
	private Festival festival;

	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * Starts the application.
	 * Loads game data, creates the pages and displays the
	 * main game.
	 * 
	 * @param stage Main Stage
	 */
	@Override
	public void start(Stage stage) throws Exception {
		// Load font from file
		if (Font.loadFont(getClass().getResourceAsStream("/fonts/Poppins Regular Nerd Font Complete.ttf"), 25) == null) {
			System.err.println("Fonts failed to load");
		}

		// Pull in all game data (Score, question board and leaderboard)
		data = GameData.load();

		// Start a new festival instance
		festival = new Festival(this);
		
		// Initialise pages
		startPage = new StartPage(this, stage);
		selectPage = new MainSelectPage(this, stage);
		practiceSelectPage = new PracticeSelectPage(this, stage);
		questionPage = new MainQuestionPage(this, stage, festival);
		practiceQuestionPage = new PracticeQuestionPage(this, stage, festival);
		helpViewer = new HelpViewer(this);
		rewardsPage = new RewardsPage(this, stage);
		settingsPage = new SettingsPage(this, stage);
		leaderboard = new Leaderboard(this, stage);
		
		// Show the stage and start scene
		startPage.show();
		stage.show();
	}

	/**
	 * When the application is closed, save all game data
	 */
	@Override
	public void stop() throws Exception {
		data.save();
	}

	// Getters for game scenes
	public GameData data() { return data; }
	public StartPage startPage() { return startPage; }
	public SelectPage selectPage() { return selectPage; }
	public PracticeSelectPage practiceSelectPage() { return practiceSelectPage; }
	public QuestionPage questionPage() { return questionPage; }
	public PracticeQuestionPage practiceQuestionPage() { return practiceQuestionPage; }
	public HelpViewer helpViewer() { return helpViewer; }
	public RewardsPage rewardsPage() { return rewardsPage; }
	public SettingsPage settingsPage() { return settingsPage; }
	public Leaderboard leaderboard() { return leaderboard; }
	public Festival festival() { return festival; }
}
