package jeopardy;

/**
 * A data class that represents a question
 */
public class Question {
	private final int value;
	private final String question;
	private final String answer;
	private QuestionState completed;

	public Question(int value, String question, String answer, QuestionState completed) {
		this.value = value;
		this.question = question.toLowerCase();
		this.answer = answer.toLowerCase();
		this.completed = completed;
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
}
