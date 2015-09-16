package barqsoft.footballscores.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import barqsoft.footballscores.util.SyncUtils;

/**
 * Created by elnoxvie on 14/9/15.
 */
public class ServiceUpdateBroadcast extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        SyncUtils.TriggerRefresh();
    }
}
