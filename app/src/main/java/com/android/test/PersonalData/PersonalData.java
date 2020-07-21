/**************************************************************************************************************************************
 * File: PersonalData.java
 * Parent Package : com.android.test.PersonalData
 * Author: JunGu Kang (fennecfox38@gmail.com)
 *
 * This Java class 'PersonalData' is designed to hold, carry and manage personal data such as name, password, birthday, age, sex, etc.
 * This class contains String type of 'name' and 'password', int type of 'age', boolean type of 'children',
 * custom defined enum type of'sex' and 'marriage', and custom defined class 'Date' type of 'birthday' and 'today'.
 * This class is implemented by 'android.os.Parcelable' to transmit this class between Activities using Intent.
 * Materialization of Encryption and Decryption for 'password' is on planning.
 * 'age' shall not be arbitrary. set 'age' by 'int getAge()' (int getAge() calculate age from 'birthday and today')
 * Custom defined class 'Date' holds int type of 'year', 'month', 'day' and methods such like Constructor, reset method, getter and setter.
 **************************************************************************************************************************************/
package com.android.test.PersonalData;

import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;

import com.android.test.R;

import java.util.Calendar;

public class PersonalData implements Parcelable{ // class 'PersonalData' is implemented from 'android.os.Parcelable'
    private String name, password;
    private int age;
    private Sex sex;
    private Marriage marriage;
    private boolean children;
    protected Date birthday;

    PersonalData(){resetData();} // void parameter constructor just reset member value.
    public PersonalData(String name, String password, int y, int m, int d, Sex sex, Marriage marriage, boolean children) {
        this.name = name;
        this.password = password;
        birthday= new Date(y,m,d);
        age=getAge(); // 'age' shall not be arbitrary. set 'age' by 'int getAge()' (int getAge() calculate age from 'birthday and today')
        this.sex = sex;
        this.marriage = marriage;
        this.children = children;
    } // Constructor above set member values.

    protected PersonalData(Parcel in) { // this constructor set member values by Parcelable.
        name = in.readString();
        password = in.readString();
        birthday = new Date();
        birthday.setYear(in.readInt());
        birthday.setMonth(in.readInt());
        birthday.setDay(in.readInt());
        age=getAge(); //'age' shall not be arbitrary. set 'age' by 'int getAge()'
        setSex(in.readInt());
        setMarriage(in.readInt());
        children = in.readByte() != 0; // boolean type is read and written by Byte.
    }
    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(password);
        dest.writeInt(birthday.year);
        dest.writeInt(birthday.month);
        dest.writeInt(birthday.day);
        dest.writeInt(sex.ordinal()); // transmit enum as integer by ordinal()
        dest.writeInt(marriage.ordinal()); // transmit enum as integer by ordinal()
        dest.writeByte((byte) (children ? 1 : 0)); // cast boolean to byte.
    }
    @Override public int describeContents() { return 0; }
    public static final Creator<PersonalData> CREATOR = new Creator<PersonalData>() {
        @Override public PersonalData createFromParcel(Parcel in) { return new PersonalData(in); }
        @Override public PersonalData[] newArray(int size) { return new PersonalData[size]; }
    };

    public void resetData(){ // method that reset all member values by default.
        name=password=null;
        sex=Sex.Male;
        marriage=Marriage.Single;
        children=false;
        birthday=null;
        birthday=new Date();
    }
    //Below are Setters.
    public void setName(String name){this.name=name;}
    public void setPassword(String password){this.password=password;}
    public void setSex(Sex sex){ this.sex=sex; }
    public void setSex(int sex){
        switch(sex){ // convert int type of position to enum type of sex.
            case 0: this.sex=Sex.Male; break;
            case 1: this.sex=Sex.Female; break;
            default: this.sex=Sex.NA; break; }
    }
    public void setMarriage(Marriage marriage){ this.marriage=marriage; }
    public void setMarriage(int marriage){
        switch(marriage){ // convert int type of position to enum type of marriage.
            case 0: this.marriage=Marriage.Single; break;
            case 1: this.marriage=Marriage.Married; break;
            case 2: this.marriage=Marriage.Divorced; break; }
    }
    public void setChildren(boolean children){this.children=children;}

    //Below are Getters.
    public String getName(){return name;}
    public String getPassword(){return password;}
    public Sex getSex(){return sex;} // int type of 'position' can be derived from ordinal()
    public Marriage getMarriage(){return marriage;}
    public boolean getChildren(){return children;}
    public int getAge(){ // Calculate age according to birthday and today.
        Date today = new Date();
        int age = today.year - birthday.year;
        if(today.month<birthday.month) --age;
        else if(today.month==birthday.month&&today.day<birthday.day) --age;
        return age;
    }

    protected class Date {
        private int year, month, day;
        Date(){ // Default void parameter constructor just set date as today.
            Calendar calendar = Calendar.getInstance(); // get today from Calendar.getInstance()
            year=calendar.get(Calendar.YEAR);
            month=calendar.get(Calendar.MONTH)+1; // adjust month. (Calendar.MONTH start from 0)
            day=calendar.get(Calendar.DAY_OF_MONTH);
        }
        Date(int year, int month, int day) { // This constructor set member values using parameter.
            this.year = year; this.month = month; this.day = day;
        }

        public void set(int year, int month, int day){ // Set member values using parameter
            this.year=year; this.month=month; this.day=day;
        }
        public void setDate(String date){ // Set date (member values) from String type of date format.
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
        public String getDate(){ // return String type of date format
            String strDate = year + "-";
            strDate += make2digit(Integer.toString(month)) + "-";
            strDate += make2digit(Integer.toString(day));
            return strDate;
        }
        private String make2digit(String num){ // make number 2 digit by adding 0 at front.
            return ( (num.length()<2) ? "0"+num : num );
        }
    }

    public enum Sex{ // value of Sex defined that it can be more than two so that we implements enum.
        Male(R.string.Male,"Male"),
        Female(R.string.Female,"Female"),
        NA(R.string.NA,"N/A"); // Not applicable (in case)

        final private int strId; // contains string resources id.
        final private String str; // contains default string.
        Sex(int strId, String str){this.strId=strId; this.str=str;} // constructor & setter.
        public int getStrId(){return strId;}
        public String toString(){return str;}
    }
    public enum Marriage { // same concept with enum type of Sex.
        Single(R.string.Single,"Single"),
        Married(R.string.Married,"Married"),
        Divorced(R.string.Divorced,"Divorced");

        final private int strId;
        final private String str;
        Marriage(int strId, String str){this.strId=strId; this.str=str;}
        public int getStrId(){return strId;}
        public String toString(){return str;}
    }

    @Override public String toString() { // Make those information to String.
        String txt= "Name: "+name+"\n";
        txt+="Password: "+password+"\n";
        txt+="Birthday: "+birthday.getDate()+"\n";
        txt+="Age: "+getAge()+"\n";
        txt+="Sex: "+sex.toString()+"\n"; // Get the String in default language.
        txt+="Marriage: "+marriage.toString()+"\n";
        txt+="HaveChildren: "+children+"\n";
        return txt;
    }
    public String toString(Resources resources){ // Make those information to String. 'Resources' is required.
        String txt= resources.getString(R.string.Name)+": "+name+"\n"; // Get the String from string resources in order to support multi-language.
        txt+=resources.getString(R.string.Password)+": "+password+"\n";
        txt+=resources.getString(R.string.Birthday)+": "+birthday.getDate()+"\n";
        txt+=resources.getString(R.string.Age)+": "+getAge()+"\n";
        txt+=resources.getString(R.string.Sex)+": "+resources.getString(sex.getStrId())+"\n";
        txt+=resources.getString(R.string.Marriage)+": "+resources.getString(marriage.getStrId())+"\n";
        txt+=resources.getString(R.string.HaveChildren)+": "+resources.getString((children?R.string.HaveChildren:R.string.HaveNoChildren))+"\n";
        return txt;
    }
}
