package com.snsdevelop.tusofia.sem6.pmu.Utils.Entity;

public class QuizEntity {
    int id;
    int marker_id;
    String question;
    AnswerEntity answers[];

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMarker_id() {
        return marker_id;
    }

    public void setMarker_id(int marker_id) {
        this.marker_id = marker_id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public AnswerEntity[] getAnswers() {
        return answers;
    }

    public void setAnswers(AnswerEntity[] answers) {
        this.answers = answers;
    }
}
