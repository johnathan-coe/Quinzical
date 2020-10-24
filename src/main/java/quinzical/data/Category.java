package quinzical.data;

import java.util.ArrayList;
import java.util.List;

/**
 * A data class that holds all the questions in their category
 */
public class Category {
	private String name;
	private List<Question> questions = new ArrayList<Question>();

	public Category(String name) {
		this.name = name;
	}

	public String name() { return name; }
	
	public List<Question> questions() { return questions; }
	
	public void addQuestion(Question question) {
		questions.add(question);
	}

	public void addQuestions(List<Question> questions) {
		this.questions.addAll(questions);
	}

	public boolean isCompleted() {
		for (Question question: questions) {
			if (!question.isCompleted()) {
				return false;
			}
		}
		return true;
	}
}
