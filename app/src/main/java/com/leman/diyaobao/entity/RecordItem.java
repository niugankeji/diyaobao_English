package com.leman.diyaobao.entity;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.List;

public class RecordItem {

    /**
     * desc :
     * totalPage : 1
     * count : 1
     * fromIndex : 1
     * toIndex : 1
     * pageIndex : 1
     * pageSize : 1
     * minData :
     * maxData :
     * data : [{"userId":"用户id1","userName":"用户名称1","logo":"logo1","step":122,"calorie":155,"moveTime":112,"movedistance":121,"isFriend":1},{"userId":"用户id2","userName":"用户名称2","logo":"logo2","step":333,"calorie":155,"moveTime":112,"movedistance":121,"isFriend":0},{"userId":"用户id3","userName":"用户名称3","logo":"logo3","step":444,"calorie":155,"moveTime":112,"movedistance":121,"isFriend":1}]
     */


    private int totalPage;
    private int count;
    private int fromIndex;
    private int toIndex;
    private int pageIndex;
    private int pageSize;
    private String totalIntegral;
    private List<DataBean> data;

    public String getTotalIntegral() {
        return totalIntegral;
    }

    public void setTotalIntegral(String totalIntegral) {
        this.totalIntegral = totalIntegral;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getFromIndex() {
        return fromIndex;
    }

    public void setFromIndex(int fromIndex) {
        this.fromIndex = fromIndex;
    }

    public int getToIndex() {
        return toIndex;
    }

    public void setToIndex(int toIndex) {
        this.toIndex = toIndex;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean implements Serializable, Comparable<DataBean> {

        private int integralRecordId;
        private String createdTime;
        private String integralValue;
        private int integralWay;

        public int getIntegralRecordId() {
            return integralRecordId;
        }

        public void setIntegralRecordId(int integralRecordId) {
            this.integralRecordId = integralRecordId;
        }

        public String getCreatedTime() {
            return createdTime;
        }

        public void setCreatedTime(String createdTime) {
            this.createdTime = createdTime;
        }

        public String getIntegralValue() {
            return integralValue;
        }

        public void setIntegralValue(String integralValue) {
            this.integralValue = integralValue;
        }

        public int getIntegralWay() {
            return integralWay;
        }

        public void setIntegralWay(int integralWay) {
            this.integralWay = integralWay;
        }

        @Override
        public int compareTo(@NonNull DataBean o) {
            return 0;
        }
    }
}
