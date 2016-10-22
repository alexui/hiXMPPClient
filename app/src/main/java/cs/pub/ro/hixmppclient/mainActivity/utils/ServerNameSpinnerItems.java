package cs.pub.ro.hixmppclient.mainActivity.utils;

import java.util.ArrayList;
import java.util.List;

import static cs.pub.ro.hixmppclient.general.Constants.HOT_CHILLI;
import static cs.pub.ro.hixmppclient.general.Constants.HOT_CHILLI_ICON;
import static cs.pub.ro.hixmppclient.general.Constants.HOT_CHILLI_ID;
import static cs.pub.ro.hixmppclient.general.Constants.OPENFIRE;
import static cs.pub.ro.hixmppclient.general.Constants.OPENFIRE_ICON;
import static cs.pub.ro.hixmppclient.general.Constants.OPENFIRE_ID;

public class ServerNameSpinnerItems {

    private static List<SpinnerItem> list = new ArrayList<>();

    public static class SpinnerItem {

        public long id;
        public String serverName;
        public int serverIconId;

        public SpinnerItem(long id, int serverIconId, String serverName) {
            this.id = id;
            this.serverIconId = serverIconId;
            this.serverName = serverName;
        }
    }

    public static List<SpinnerItem> getList() {

        if (list.isEmpty()) {
            list.add(new SpinnerItem(OPENFIRE_ID, OPENFIRE_ICON, OPENFIRE));
            list.add(new SpinnerItem(HOT_CHILLI_ID, HOT_CHILLI_ICON, HOT_CHILLI));
        }
        return list;
    }
}
