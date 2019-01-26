package com.xstudio.xbrowser.webkit;

import com.xstudio.xbrowser.widget.ViewSelector;

public interface Tab extends ViewSelector.ItemProvider {
   
    int getIndex();
    
    void select();
    void close();
    void reload();
    void stopLoading();
    
    void updateInfo();
    
}
