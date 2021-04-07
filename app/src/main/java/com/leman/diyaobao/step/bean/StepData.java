package com.leman.diyaobao.step.bean;




import io.realm.RealmModel;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Created by dylan on 2016/1/30.
 */

@RealmClass
public class StepData  implements RealmModel {

    /**
     * 步数
     */
    @PrimaryKey
    private String today;
    private int step;


    public String getToday() {
        return today;
    }

    public void setToday(String today) {
        this.today = today;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    @Override
    public String toString() {
        return "StepData{" +
                ", today='" + today + '\'' +
                ", step='" + step + '\'' +
                '}';
    }
}
