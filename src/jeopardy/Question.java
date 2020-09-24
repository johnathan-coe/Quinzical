package jeopardy;
import java.lang.Integer;

/**
 * A data class that represents a question
 */
public class Question implements Comparable<Question> {
	private final int value;
	private final String question;
	private final String answer;
	private final String[] answers;
	private QuestionState completed;

	public Question(int value, String question, String answer, QuestionState completed) {
		this.value = value;
		this.question = question.toLowerCase();
		this.answer = answer.toLowerCase();
		// List of acceptable answer strings
		this.answers = this.answer.split("/");
		this.completed = completed;
	}

	// Check if an entered answer is correct
	public boolean check(String entered) {
		String processed = entered.strip().toLowerCase();
		
		for (String correct : answers) {
			// If the user entered the correct answer verbatim
			if (correct.equals(processed)) {
				return true;
			// The user can type two characters in place of an accented one
			} else if (processed.equals(correct.replace("ā", "aa")
										       .replace("ō", "oo")
										       .replace("ē", "ee")
										       .replace("ī", "ii")
										       .replace("ū", "uu"))) {
				return true;
			}
		}
		return false;
	}

	public int value() { return value; }

	public String question() { return question; }

	public String answer() { return answer; }

	public boolean isCompleted() { return completed != QuestionState.UNATTEMPTED; }

	public QuestionState state() { return completed; }

	public void setState(QuestionState state) { this.completed = state; }

	public enum QuestionState {
		/**
		 * The user has not yet attempted this question
		 */
		UNATTEMPTED,
		/**
		 * The user has attempted this question and gotten it correct
		 */
		CORRECT,
		/**
		 * The user has attempted this question and had not gotten it correct
		 */
		INCORRECT,
		;


		@Override
		public String toString() {
			switch (this) {
				case UNATTEMPTED:
					return "UNATTEMPTED";
				case CORRECT:
					return "CORRECT";
				case INCORRECT:
					return "INCORRECT";
				default: // Impossible
					return null;
			}
		}
	}
	
	@Override
	public int compareTo(Question q) {
		if (value() == q.value()) { return 0; } 
		else if (value() < q.value()) { return -1; }
		else { return 1; }
	}
}
