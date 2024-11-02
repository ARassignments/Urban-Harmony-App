package com.example.urbanharmony.Models;

public class FaqModel {
    String id, question, answer;
    boolean isExpanded;

    public FaqModel() {
    }

    public FaqModel(String id, String question, String answer) {
        this.id = id;
        this.question = question;
        this.answer = answer;
        this.isExpanded = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }
}
