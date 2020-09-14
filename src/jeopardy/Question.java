package jeopardy;

/**
 * A data class that represents a question
 */
public class Question {
	private int value;
	private String question;
	private String answer;
	private boolean completed;

	public Question(int value, String question, String answer, boolean completed) {
		this.value = value;
		this.question = question.toLowerCase();
		this.answer = answer.toLowerCase();
		this.completed = completed;
	}


	public int value() { return value; }

	public String question() { return question; }

	public String answer() { return answer; }

	public boolean isCompleted() { return completed; }

	public void complete() { completed = true; }
}
