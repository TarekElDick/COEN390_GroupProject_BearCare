package com.example.coen390_groupproject_bearcare.questionnaire;

public class QuestionnaireTermsSingleton {

    private boolean status;
    private QuestionnaireTermsSingleton(){
        status = false;
    }

    public boolean getStatus(){
        return status;
    }

    public void areAccepted(){
        status = true;
    }

    public void reset(){
        status = false;
    }

    private static QuestionnaireTermsSingleton terms;

    //all calls to singleton must be done through here
    public static QuestionnaireTermsSingleton get(){
        if(terms == null){
            terms = new QuestionnaireTermsSingleton();
        }
        return terms;
    }

}
