package com.xstudio.xbrowser.util;

import java.util.Stack;

public class OnBackPressedHelper {
    
    private static Stack<Receiver> stacks;
    
    public static void pushReceiver(Receiver receiver) {
        if (stacks == null) {
            synchronized (OnBackPressedHelper.class) {
                if (stacks == null) {
                    stacks = new Stack<>();
                }
            }
        }
        stacks.push(receiver);
    }
    
    public static boolean onBackPressed() {
        if (!stacks.empty()) {
            return stacks.pop().onBackPressed();
        }
        return false;
    }
    
    public static interface Receiver {
        boolean onBackPressed();
    }
    
}
