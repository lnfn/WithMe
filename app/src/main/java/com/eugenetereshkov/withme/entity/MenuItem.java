package com.eugenetereshkov.withme.entity;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@Retention(RetentionPolicy.SOURCE)
@StringDef({MenuItem.CARD, MenuItem.HISTORY, MenuItem.LOGOUT})
public @interface MenuItem {
    String CARD = "card";
    String HISTORY = "history";
    String LOGOUT = "logout";
}
