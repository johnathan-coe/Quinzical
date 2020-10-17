package quinzical;

import javafx.application.Application;
import javafx.stage.Stage;
import quinzical.data.GameData;
import quinzical.festival.Festival;
import quinzical.ui.*;

/**
 * The main application class that will be run
 *
 * Handles the loading and saving of the game data file.
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
	private Festival festival;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		data = GameData.load();

		festival = new Festival(this);
		startPage = new StartPage(this, stage);
		selectPage = new MainSelectPage(this, stage);
		practiceSelectPage = new PracticeSelectPage(this, stage);
		questionPage = new MainQuestionPage(this, stage, festival);
		practiceQuestionPage = new PracticeQuestionPage(this, stage, festival);
		helpViewer = new HelpViewer(this, stage);
		rewardsPage = new RewardsPage(this, stage);
		settingsPage = new SettingsPage(this, stage);

		startPage.show();
		stage.show();
	}

	@Override
	public void stop() throws Exception {
		data.save();
		festival.destroy();
	}

	public GameData data() { return data; }

	public StartPage startPage() { return startPage; }

	public SelectPage selectPage() { return selectPage; }
	
	public PracticeSelectPage practiceSelectPage() { return practiceSelectPage; }

	public QuestionPage questionPage() { return questionPage; }

	public PracticeQuestionPage practiceQuestionPage() { return practiceQuestionPage; }
	
	public HelpViewer helpViewer() { return helpViewer; }
	
	public RewardsPage rewardsPage() { return rewardsPage; }

	public SettingsPage settingsPage() { return settingsPage; }

	public Festival festival() { return festival; }
}
