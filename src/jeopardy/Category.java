package jeopardy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
}
