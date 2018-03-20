package kg.azat.azat.model;

/**
 * Created by nurzamat on 8/3/15.
 */
public class Image {

    private String id = "";
    private String url = "";

    public Image(String _id, String _url)
    {
        this.id = _id;
        this.url = _url;
    }

    public String getId() {
        return id;
    }

    public void setId(String _id) {
        this.id = _id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String _url) {
        this.url = _url;
    }
}
