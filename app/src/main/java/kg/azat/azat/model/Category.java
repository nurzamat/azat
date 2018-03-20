package kg.azat.azat.model;

import java.util.ArrayList;

/**
 * Created by nurzamat on 8/3/15.
 */
public class Category
{
    private String id = "";
    private String idParent = "";
    private String name = "";
    private ArrayList<Category> subcats = null;

    public Category()
    {
    }

    public Category(String _id, String _name)
    {
        this.id = _id;
        this.name = _name;
    }

    public String getId() {
        return id;
    }

    public void setId(String _id) {
        this.id = _id;
    }

    public String getIdParent() {
        return idParent;
    }

    public void setIdParent(String _id) {
        this.idParent = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String _name) {
        this.name = _name;
    }

    public ArrayList<Category> getSubcats() {
        return subcats;
    }

    public void setSubcats(ArrayList<Category> _subcats)
    {
        this.subcats = _subcats;
    }
}
