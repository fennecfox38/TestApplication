package com.android.test.PersonalData;

import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;

import com.android.test.R;

import java.util.Calendar;

public class PersonalData implements Parcelable{
    private String name, password;
    private int age;
    private Sex sex;
    private Marriage marriage;
    private boolean children;
    protected Date birthday, today;

    PersonalData(){resetData();}
    public PersonalData(String name, String password, int y, int m, int d, Sex sex, Marriage marriage, boolean children) {
        this.name = name;
        this.password = password;
        birthday= new Date(y,m,d);
        age=getAge();
        this.sex = sex;
        this.marriage = marriage;
        this.children = children;
        today= new Date();
    }

    protected PersonalData(Parcel in) {
        name = in.readString();
        password = in.readString();
        birthday = new Date();
        birthday.setYear(in.readInt());
        birthday.setMonth(in.readInt());
        birthday.setDay(in.readInt());
        age = in.readInt();
        setSex(in.readInt());
        setMarriage(in.readInt());
        children = in.readByte() != 0;
    }
    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(password);
        dest.writeInt(birthday.year);
        dest.writeInt(birthday.month);
        dest.writeInt(birthday.day);
        dest.writeInt(age=getAge());
        dest.writeInt(sex.ordinal());
        dest.writeInt(marriage.ordinal());
        dest.writeByte((byte) (children ? 1 : 0));
    }
    @Override public int describeContents() { return 0; }
    public static final Creator<PersonalData> CREATOR = new Creator<PersonalData>() {
        @Override public PersonalData createFromParcel(Parcel in) { return new PersonalData(in); }
        @Override public PersonalData[] newArray(int size) { return new PersonalData[size]; }
    };

    public void resetData(){
        name=password=null;
        sex=Sex.Male;
        marriage=Marriage.Single;
        children=false;
        birthday=today=null;
        birthday=new Date();
        today=new Date();
    }

    public void setName(String name){this.name=name;}
    public void setPassword(String password){this.password=password;}
    public void setSex(Sex sex){ this.sex=sex; }
    public void setSex(int sex){
        switch(sex){
            case 0: this.sex=Sex.Male; break;
            case 1: this.sex=Sex.Female; break;
            default: this.sex=Sex.NA; break; }
    }
    public void setMarriage(Marriage marriage){ this.marriage=marriage; }
    public void setMarriage(int marriage){
        switch(marriage){
            case 0: this.marriage=Marriage.Single; break;
            case 1: this.marriage=Marriage.Married; break;
            case 2: this.marriage=Marriage.Divorced; break; }
    }
    public void setChildren(boolean children){this.children=children;}

    public String getName(){return name;}
    public String getPassword(){return password;}
    public Sex getSex(){return sex;}
    public Marriage getMarriage(){return marriage;}
    public boolean getChildren(){return children;}
    public int getAge(){
        Date today = new Date();
        int age = today.year - birthday.year;
        if(today.month<birthday.month) --age;
        else if(today.month==birthday.month&&today.day<birthday.day) --age;
        return age;
    }

    protected class Date {
        private int year, month, day;
        Date(){
            Calendar calendar = Calendar.getInstance();
            year=calendar.get(Calendar.YEAR);
            month=calendar.get(Calendar.MONTH)+1;
            day=calendar.get(Calendar.DAY_OF_MONTH);
        }
        Date(int year, int month, int day) {
            this.year = year; this.month = month; this.day = day;
        }

        public void set(int year, int month, int day){
            this.year=year; this.month=month; this.day=day;
        }
        public void setDate(String date){
            year = Integer.parseInt(date.substring(0,4));
            month = Integer.parseInt(date.substring(5,7));
            day = Integer.parseInt(date.substring(8,10));
        }
        public void setYear(int year){ this.year=year; }
        public void setMonth(int month) { this.month = month; }
        public void setDay(int day) { this.day = day; }

        public int getYear() { return year; }
        public int getMonth() { return month; }
        public int getDay() { return day; }
        public String getDate(){
            String strDate = year + "-";
            strDate += make2digit(Integer.toString(month)) + "-";
            strDate += make2digit(Integer.toString(day));
            return strDate;
        }
        private String make2digit(String num){
            return ( (num.length()<2) ? "0"+num : num );
        }
    }

    public enum Sex{
        Male(R.string.Male,"Male"),
        Female(R.string.Female,"Female"),
        NA(R.string.NA,"N/A");

        final private int strId;
        final private String str;
        Sex(int strId, String str){this.strId=strId; this.str=str;}
        public int getStrId(){return strId;}
        public String toString(){return str;}
    }
    public enum Marriage {
        Single(R.string.Single,"Single"),
        Married(R.string.Married,"Married"),
        Divorced(R.string.Divorced,"Divorced");

        final private int strId;
        final private String str;
        Marriage(int strId, String str){this.strId=strId; this.str=str;}
        public int getStrId(){return strId;}
        public String toString(){return str;}
    }

    @Override public String toString() {
        String txt= "Name: "+name+"\n";
        txt+="Password: "+password+"\n";
        txt+="Birthday: "+birthday.getDate()+"\n";
        txt+="Age: "+getAge()+"\n";
        txt+="Sex: "+sex.toString()+"\n";
        txt+="Marriage: "+marriage.toString()+"\n";
        txt+="HaveChildren: "+children+"\n";
        return txt;
    }
    public String toString(Resources resources){
        String txt= resources.getString(R.string.Name)+": "+name+"\n";
        txt+=resources.getString(R.string.Password)+": "+password+"\n";
        txt+=resources.getString(R.string.Birthday)+": "+birthday.getDate()+"\n";
        txt+=resources.getString(R.string.Age)+": "+getAge()+"\n";
        txt+=resources.getString(R.string.Sex)+": "+resources.getString(sex.getStrId())+"\n";
        txt+=resources.getString(R.string.Marriage)+": "+resources.getString(marriage.getStrId())+"\n";
        txt+=resources.getString(R.string.HaveChildren)+": "+resources.getString((children?R.string.HaveChildren:R.string.HaveNoChildren))+"\n";
        return txt;
    }
}
