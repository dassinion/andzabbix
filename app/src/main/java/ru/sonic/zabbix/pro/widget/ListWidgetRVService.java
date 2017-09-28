package ru.sonic.zabbix.pro.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class ListWidgetRVService extends RemoteViewsService {
  @Override
  public RemoteViewsFactory onGetViewFactory(Intent intent) {
    return(new ListWidgetRemotevierfactory(this.getApplicationContext(),intent));
  }
}