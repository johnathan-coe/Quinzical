package quinzical.jservice;

import quinzical.data.Category;
import quinzical.data.Question;
import quinzical.json.JsonReader;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * A class that wraps around the JService API.
 *
 * More information about the API can be found at `jservice.io/`
 */
public class JService {
	/**
	 * A function that accesses jservice.io to get 5 random questions for the international category.
	 */
	public static Category load() throws URISyntaxException, IOException, InterruptedException {
		// Create an http client
		HttpClient client = HttpClient.newBuilder().build();
		List<Question> questionList = new ArrayList<>();

		// While we don't have 5 questions
		while (questionList.size() < 5) {
			// Pull questions from API
			HttpRequest request = HttpRequest.newBuilder()
					.GET()
					.uri(new URI(String.format("http://jservice.io/api/random?count=%d", 5-questionList.size())))
					.build();
			HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
			
			// Read in JSON
			ResponseReader response = new ResponseReader(httpResponse.body());
			
			// Add valid questions to the list
			questionList.addAll(response.questions());
		}

		// Overwrite the score values from JService with out own ones
		for (int i = 0; i < questionList.size(); i++) {
			questionList.get(i).setValue((i+1)*100);
		}

		// Build a category
		Category category = new Category("International");
		category.addQuestions(questionList);
		return category;
	}

	/**
	 * Reads questions from API
	 */
	private static class ResponseReader extends JsonReader {
		/**
		 * Creates a new ResponseReader that reads from a given string
		 */
		public ResponseReader(String json) {
			super(json);
		}

		/**
		 * Gets the length of the array of questions
		 */
		public int size() throws IOException {
			return Integer.parseInt(executeFilter("length", null).get(0));
		}

		public List<Question> questions() throws IOException {
			List<Question> questionList = new ArrayList<>();
			List<String> response = executeFilter(".[] | .value, .question, .answer", new String[]{"-r"});
			int i = 0;
			while (i < response.size()) {
				if (checkValues(response, i)) {
					int value = Integer.parseInt(response.get(i));
					String questionText = response.get(i + 1);
					String answerText = response.get(i + 2);
					Question question = new Question(value, "", questionText, answerText, Question.QuestionState.UNATTEMPTED);
					questionList.add(question);
				}
				i += 3;
			}
			return questionList;
		}
	}

	/**
	 * Check if a response from the API doesn't contain HTML
	 * @param response Reponses from the api for value, question and and answer.
	 * @param index Question to check in response.
	 * @return Boolean
	 */
	private static boolean checkValues(List<String> response, int index) {
		for (int i = 0; i < 3; i++) {
			String resp = response.get(index + i);
			if (resp.equals("null") || resp.isBlank() || resp.matches("</?[A-Z|a-z]+>")) {
				// Reject all values that are empty or have html tags in them
				System.out.println("Rejected: " + resp);
				return false;
			}
		}
		return true;
	}
}
