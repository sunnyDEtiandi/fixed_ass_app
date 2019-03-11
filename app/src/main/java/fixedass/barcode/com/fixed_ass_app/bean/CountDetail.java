package fixedass.barcode.com.fixed_ass_app.bean;

import android.widget.ProgressBar;

/**
 * Created by Administrator on 2018/3/14.
 */

public class CountDetail {
    private String countUUID;
    private String countbillCode;
    private String barCode;
    private String countGroup;
    private String countCompany;
    private String countDepartment;
    private String countPlace;
    private String countPeople;
    private String countTime;
    private int countState;

    public CountDetail(String countUUID, String countbillCode, String barCode, String countGroup,
                       String countCompany, String countDepartment, String countPlace,
                       String countPeople, String countTime, int countState) {
        this.countUUID = countUUID;
        this.countbillCode = countbillCode;
        this.barCode = barCode;
        this.countGroup = countGroup;
        this.countCompany = countCompany;
        this.countDepartment = countDepartment;
        this.countPlace = countPlace;
        this.countPeople = countPeople;
        this.countTime = countTime;
        this.countState = countState;
    }

    public String getCountUUID() {
        return countUUID;
    }

    public void setCountUUID(String countUUID) {
        this.countUUID = countUUID;
    }

    public String getCountbillCode() {
        return countbillCode;
    }

    public void setCountbillCode(String countbillCode) {
        this.countbillCode = countbillCode;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getCountGroup() {
        return countGroup;
    }

    public void setCountGroup(String countGroup) {
        this.countGroup = countGroup;
    }

    public String getCountCompany() {
        return countCompany;
    }

    public void setCountCompany(String countCompany) {
        this.countCompany = countCompany;
    }

    public String getCountDepartment() {
        return countDepartment;
    }

    public void setCountDepartment(String countDepartment) {
        this.countDepartment = countDepartment;
    }

    public String getCountPlace() {
        return countPlace;
    }

    public void setCountPlace(String countPlace) {
        this.countPlace = countPlace;
    }

    public String getCountPeople() {
        return countPeople;
    }

    public void setCountPeople(String countPeople) {
        this.countPeople = countPeople;
    }

    public String getCountTime() {
        return countTime;
    }

    public void setCountTime(String countTime) {
        this.countTime = countTime;
    }

    public int getCountState() {
        return countState;
    }

    public void setCountState(int countState) {
        this.countState = countState;
    }

    @Override
    public String toString() {
        return "CountDetail{" +
                "countUUID='" + countUUID + '\'' +
                ", countbillCode='" + countbillCode + '\'' +
                ", barCode='" + barCode + '\'' +
                ", countGroup='" + countGroup + '\'' +
                ", countCompany='" + countCompany + '\'' +
                ", countDepartment='" + countDepartment + '\'' +
                ", countPlace='" + countPlace + '\'' +
                ", countPeople='" + countPeople + '\'' +
                ", countTime='" + countTime + '\'' +
                ", countState=" + countState +
                '}';
    }
}
