package com.tech4lyf.sbsrvending;

import java.util.ArrayList;

public class CallBacks {

    private static ArrayList<OnProductClickListener> onProductClickListeners = new ArrayList<>();




    public interface OnProductClickListener{
        void onProductClickListener(Integer id);
    }
}
