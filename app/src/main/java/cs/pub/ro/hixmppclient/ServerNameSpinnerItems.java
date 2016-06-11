package cs.pub.ro.hixmppclient;

import java.util.ArrayList;
import java.util.List;

import static cs.pub.ro.hixmppclient.Constants.*;

/**
 * Created by Alex on 6/6/2016.
 */
public class ServerNameSpinnerItems {

    private static List<SpinnerItem> list = new ArrayList<>();

    public static class SpinnerItem {

        public String serverName;
        public int serverIconId;

        public SpinnerItem(int serverIconId, String serverName) {
            this.serverIconId = serverIconId;
            this.serverName = serverName;
        }
    }

    public static List<SpinnerItem> getList() {

        list.add(new SpinnerItem(OPENFIRE_ICON, OPENFIRE));
        list.add(new SpinnerItem(HOT_CHILLI_ICON, HOT_CHILLI));
        return list;
    }
}
