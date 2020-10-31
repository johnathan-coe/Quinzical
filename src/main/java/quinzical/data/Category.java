package quinzical.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Holds a series of Question objects for a category in order.
 */
public class Category {
	private String name;
	private List<Question> questions = new ArrayList<Question>();

	/**
	 * Build an empty category with a name
	 * @param name
	 */
	public Category(String name) {
		this.name = name;
	}

	public void addQuestion(Question question) {
		questions.add(question);
	}

	public void addQuestions(List<Question> questions) {
		this.questions.addAll(questions);
	}
	
	/**
	 * Build a category a map of question strings to [prompt string, answer string].
	 * Additionally, generate random reward values..
	 * 
	 * @param name
	 * @param map
	 * 
	 * @return The Category
	 */
	public static Category fromMapWithRandomValues(String name, Map<String, String[]> map) {
		Category category = new Category(name);
		int i = 0;
		for (Map.Entry<String, String[]> entry: map.entrySet()) {
			// Stop after 5 questions for a category.
			if (i >= 5) {
				break;
			}
			category.addQuestion(new Question((i+1)*100, entry.getValue()[0], entry.getKey(), entry.getValue()[1], Question.QuestionState.UNATTEMPTED));
			i++;
		}
		
		return category;
	}

	/**
	 * Check if all questions in the category are completed 
	 * 
	 * @return boolean value representing completion
	 */
	public boolean isCompleted() {
		for (Question question: questions) {
			if (!question.isCompleted()) {
				return false;
			}
		}
		return true;
	}
	
	// Getters
	public String name() { return name; }
	public List<Question> questions() { return questions; }
}
