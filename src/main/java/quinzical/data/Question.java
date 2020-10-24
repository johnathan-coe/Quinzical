package quinzical.data;

/**
 * A data class that represents a question
 */
public class Question implements Comparable<Question> {
	private int value;
	private final String prompt;
	private final String question;
	private final String answer;
	private final String[] answers;
	private QuestionState completed;

	public Question(int value, String prompt, String question, String answer, QuestionState completed) {
		this.value = value;
		this.prompt = prompt;
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

	public void setValue(int value) {
		this.value = value;
	}

	public String prompt() { return prompt; }
	
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

		public static QuestionState parse(String text) {
			switch (text) {
				case "UNATTEMPTED" -> { return Question.QuestionState.UNATTEMPTED; }
				case "CORRECT" -> { return Question.QuestionState.CORRECT; }
				case "INCORRECT" -> { return Question.QuestionState.INCORRECT; }
			}
			return null;
		}

		@Override
		public String toString() {
			return switch (this) {
				case UNATTEMPTED -> "UNATTEMPTED";
				case CORRECT -> "CORRECT";
				case INCORRECT -> "INCORRECT";
			};
		}
	}
	
	@Override
	public int compareTo(Question q) {
		return Integer.compare(value(), q.value());
	}
}
