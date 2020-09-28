package quinzical;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A data class that holds all the questions in their category
 */
public class Category {
	private String name;
	private Map<Integer, Question> questions = new HashMap<>();

	public Category(String name) {
		this.name = name;
	}

	public void addQuestion(Question question) {
		questions.put(question.value(), question);
	}

	public String name() { return name; }

	public List<Question> questions() { return new ArrayList<>(questions.values()); }

	public Question getQuestion(int value) { return questions.get(value); }

	public boolean isCompleted() {
		for (Question question: questions()) {
			if (!question.isCompleted()) {
				return false;
			}
		}
		return true;
	}
}
