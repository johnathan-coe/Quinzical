package quinzical.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A data class that holds all the questions in their category
 */
public class Category {
	private String name;
	private List<Question> questions = new ArrayList<Question>();

	public Category(String name) {
		this.name = name;
	}

	public static Category fromMapWithRandomValues(String name, Map<String, String[]> map) {
		Category category = new Category(name);
		int i = 0;
		for (Map.Entry<String, String[]> entry: map.entrySet()) {
			if (i >= 5) {
				break;
			}
			category.addQuestion(new Question((i+1)*100, entry.getValue()[0], entry.getKey(), entry.getValue()[1], Question.QuestionState.UNATTEMPTED));
			i++;
		}
		return category;
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
