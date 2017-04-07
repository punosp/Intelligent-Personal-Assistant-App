package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Created by dilna on 5/27/16.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "question_data"
})

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatModel {
    @JsonProperty("question_data")
    private QuestionModel question_data = new QuestionModel();

    public QuestionModel getQuestion_data() {
        return question_data;
    }

    public void setQuestion_data(QuestionModel question_data) {
        this.question_data = question_data;
    }
}
