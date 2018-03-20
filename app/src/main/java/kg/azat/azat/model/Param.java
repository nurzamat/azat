package kg.azat.azat.model;

/**
 * Created by nurzamat on 8/28/16.
 */
public class Param
{
    //params
    String query = "0";       //0
    int actionType = 0;       //1
    String region = "0";      //2
    String location = "0";    //3
    String price_from = "0";  //4
    String price_to = "0";    //5
    int sex = 2;              //6
    String age_from = "0";    //7
    String age_to = "0";      //8
    //end params

    int actionPos = 0;

    public Param() {
    }

    public String getQuery()
    {
        if(!query.isEmpty())
        return query;
        else return "0";
    }

    public void setQuery(String _query) {
        this.query = _query;
    }

    public int getActionType() {
        return actionType;
    }

    public void setActionType(int _actionType) {
        this.actionType = _actionType;
    }

    public String getRegion()
    {
        if(!region.isEmpty())
        return region;
        else return "0";
    }

    public void setRegion(String _region) {
        this.region = _region;
    }

    public String getLocation() {
        if(!location.isEmpty())
            return location;
        else return "0";
    }

    public void setLocation(String _location) {
        this.location = _location;
    }

    public String getPrice_from() {
        if(!price_from.isEmpty())
            return price_from;
        else return "0";
    }

    public void setPrice_from(String _price_from) {
        this.price_from = _price_from;
    }

    public String getPrice_to() {
        if(!price_to.isEmpty())
            return price_to;
        else return "0";
    }

    public void setPrice_to(String _price_to) {
        this.price_to = _price_to;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int _sex) {
        this.sex = _sex;
    }

    public String getAge_from() {
        if(!age_from.isEmpty())
            return age_from;
        else return "0";
    }

    public void setAge_from(String _age_from) {
        this.age_from = _age_from;
    }

    public String getAge_to() {
        if(!age_to.isEmpty())
            return age_to;
        else return "0";
    }

    public void setAge_to(String _age_to) {
        this.age_to = _age_to;
    }

    public int getActionPos() {
        return actionPos;
    }

    public void setActionPos(int _actionPos) {
        this.actionPos = _actionPos;
    }

}
