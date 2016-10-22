package cs.pub.ro.hixmppclient.mainActivity.hiClient.statusFragment;

import org.jivesoftware.smack.packet.Presence;

import java.util.ArrayList;
import java.util.List;

import static cs.pub.ro.hixmppclient.general.Constants.AVAILABLE_ICON;
import static cs.pub.ro.hixmppclient.general.Constants.AWAY_ICON;
import static cs.pub.ro.hixmppclient.general.Constants.CHAT_ICON;
import static cs.pub.ro.hixmppclient.general.Constants.DND_ICON;

public class StatusSpinnerItems {

    private static List<SpinnerItem> list = new ArrayList<>();

    public static class SpinnerItem {

        public String statusMode;
        int statusIconId;

        public SpinnerItem(String statusMode, int statusIconId) {
            this.statusMode = statusMode;
            this.statusIconId = statusIconId;
        }
    }

    public static List<SpinnerItem> getList() {

        if (list.isEmpty()) {
            list.add(new SpinnerItem(Presence.Mode.available.name(), AVAILABLE_ICON));
            list.add(new SpinnerItem(Presence.Mode.away.name(), AWAY_ICON));
            list.add(new SpinnerItem(Presence.Mode.dnd.name(), DND_ICON));
            list.add(new SpinnerItem(Presence.Mode.chat.name(), CHAT_ICON));
        }
        return list;
    }
}
