package jeopardy;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * The main application class that will be run
 *
 * Handles the loading and saving of the game data file.
 */
public class Game extends Application {
	private GameData data;
	private StartPage startPage;
	private SelectPage selectPage;
	private QuestionPage questionPage;
	private RewardsPage rewardsPage;
	private Festival festival;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		data = GameData.load();

		festival = new Festival();
		startPage = new StartPage(this, stage);
		selectPage = new SelectPage(this, stage);
		questionPage = new QuestionPage(this, stage, festival);
		rewardsPage = new RewardsPage(this, stage);

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

	public QuestionPage questionPage() { return questionPage; }

	public RewardsPage rewardsPage() { return rewardsPage; }
}
