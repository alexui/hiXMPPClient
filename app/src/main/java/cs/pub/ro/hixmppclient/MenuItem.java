package cs.pub.ro.hixmppclient;

/**
 * Created by Alex on 6/5/2016.
 */
public class MenuItem {

    private boolean showNotify;
    private String itemTitle;

    public MenuItem() {

    }

    public MenuItem(boolean showNotify, String itemTitle) {
        this.showNotify = showNotify;
        this.itemTitle = itemTitle;
    }

    public boolean isShowNotify() {
        return showNotify;
    }

    public void setShowNotify(boolean showNotify) {
        this.showNotify = showNotify;
    }

    public String getTitle() {
        return itemTitle;
    }

    public void setTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }
}
