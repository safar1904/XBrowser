package com.xstudio.xbrowser.view;

import android.view.ActionProvider;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import java.util.ArrayList;
import android.content.ComponentName;
import android.content.res.Configuration;
import android.content.Context;
import android.view.ContextMenu;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import java.util.List;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.os.Parcelable;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import static com.xstudio.xbrowser.util.Measurements.*;


public class AppMenu extends ListView implements ListAdapter, SubMenu {

    private LayoutInflater layoutInflater;
    private WindowManager windowManager;
    private List<MenuItem> items;
    private List<DataSetObserver> observers;
    private int itemLayoutRes;
    private int widthDimenRes;
    private int heightDimenRes;
    private boolean showing;
    private WindowManager.LayoutParams windowManagerParams;
    private int preferedMargin;
    
    private final int[] drawingLocation = new int[2];
    private final int[] screenLocation = new int[2];
    private final Rect visibleFrameRect = new Rect();
    
    private boolean asSubmenu;
    private String headerTitle;
    private Drawable headerIcon;
    private MenuItem subMenuItem;
    private View asSubMenuHeader;
    
    public AppMenu(Context context, int itemRes, int widthRes, int heightRes) {
        super(context);
        itemLayoutRes = itemRes;
        widthDimenRes = widthRes;
        heightDimenRes = heightRes;
        preferedMargin = dpToPx(30F);
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        items = new ArrayList<>();
        layoutInflater = LayoutInflater.from(context);
        setAdapter(this);
        setDividerHeight(0);
        setClipToOutline(true);
        setClipToPadding(false);
        setElevation(dpToPx(5F));
        setFocusable(true);
        setFocusableInTouchMode(true);
        setBackgroundResource(android.R.color.background_light);
    }
    
    public AppMenu(Context context, int itemResId, int widthRes, int heightRes, MenuItem item) {
        this(context, itemResId, widthRes, heightRes);
        asSubmenu = true;
        subMenuItem = item;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        // EMPTY
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_OUTSIDE:
                close();
                break;
        }
        return super.onTouchEvent(ev);
    }
    
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_BACK:
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    close();
                    return true;
                }
                break;
            default:
                break;
        }
        return super.dispatchKeyEvent(event);
    }
    
    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        close();
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        if (observers == null) {
            synchronized (this) {
                if (observers == null) {
                    observers = new ArrayList<>();
                }
            }
        }
        observers.add(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        if (observers != null) {
            observers.remove(observer);
        }
    }

    @Override
    public long getItemId(int index) {
        return items.get(index).getItemId();
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int index, View convertView, ViewGroup container) {
        if (convertView == null) {
            View view = layoutInflater.inflate(itemLayoutRes, container, false);
            ViewHolder tag = new ViewHolder();
            tag.title = (AppCompatTextView) view.findViewById(android.R.id.title);
            tag.icon = (AppCompatImageView) view.findViewById(android.R.id.icon);
            tag.checkBox = (AppCompatCheckBox) view.findViewById(android.R.id.checkbox);
            if (tag.title == null || tag.icon == null || tag.checkBox == null) {
                throw new RuntimeException("Failed inflating layout : cannot find id");
            }
            view.setTag(tag);
            convertView = view;
        }
        
        MenuItem item = getItem(index);
        ViewHolder tag = (ViewHolder) convertView.getTag();
        
        tag.icon.setVisibility(item.getIcon() != null ? VISIBLE : GONE);
        tag.icon.setImageDrawable(item.getIcon());
        
        tag.title.setText(item.getTitle());
        
        tag.checkBox.setVisibility(item.isCheckable() ? VISIBLE : GONE);
        tag.checkBox.setChecked(item.isChecked());
        
        convertView.setEnabled(item.isEnabled());
        convertView.setVisibility(item.isVisible() ? VISIBLE : GONE);
        
        return convertView;
    }

    @Override
    public int getItemViewType(int p1) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return items.isEmpty();
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int index) {
        return items.get(index).isEnabled();
    }
    
    @Override
    public int getCount() {
        return size();
    }
    
    
    @Override
    public MenuItem add(CharSequence title) {
        MenuItem newItem = new Item(title.toString());
        items.add(newItem);
        notifyDataSetChanged();
        return newItem;
    }

    @Override
    public MenuItem add(int titleRes) {
        String title = getContext().getResources().getString(titleRes);
        return add(title);
    }

    @Override
    public MenuItem add(int groupId, int itemId, int order, CharSequence title) {
        Item newItem = (Item) add(title);
        newItem.groupId = groupId;
        newItem.itemId = itemId;
        newItem.order = order;
        return newItem;
    }

    @Override
    public MenuItem add(int groupId, int itemId, int order, int titleRes) {
        String title = getContext().getResources().getString(titleRes);
        return add(groupId, itemId, order, title);
    }

    @Override
    public SubMenu addSubMenu(CharSequence title) {
        Item newItem = (Item) add(title);
        SubMenu subMenu = new AppMenu(getContext(), itemLayoutRes, widthDimenRes, heightDimenRes, newItem);
        newItem.subMenu = subMenu;
        return subMenu;
    }

    @Override
    public SubMenu addSubMenu(int titleRes) {
        String title = getContext().getResources().getString(titleRes);
        return addSubMenu(title);
    }

    @Override
    public SubMenu addSubMenu(int groupId, int itemId, int order, CharSequence title) {
        Item newItem = (Item) add(title);
        newItem.groupId = groupId;
        newItem.itemId = itemId;
        newItem.order = order;
        SubMenu subMenu = new AppMenu(getContext(), itemLayoutRes, widthDimenRes, heightDimenRes, newItem);
        newItem.subMenu = subMenu;
        notifyDataSetChanged();
        return subMenu;
    }

    @Override
    public SubMenu addSubMenu(int groupId, int itemId, int order, int titleRes) {
        String title = getContext().getResources().getString(titleRes);
        return addSubMenu(groupId, itemId, order, title);
    }

    @Override
    public int addIntentOptions(int p1, int p2, int p3, ComponentName p4, Intent[] p5, Intent p6, int p7, MenuItem[] p8) {
        // TODO: Implement this method
        return 0;
    }

    @Override
    public void removeItem(int itemId) {
        MenuItem targetItem = null;
        for (MenuItem item : items) {
            if (item.getItemId() == itemId) {
                targetItem = item;
            }
        }
        if (targetItem != null) {
            items.remove(targetItem);
            notifyDataSetChanged();
        }
    }

    @Override
    public void removeGroup(int groupId) {
        
    }

    @Override
    public void clear() {
        items.clear();
        notifyDataSetInvalidated();
    }

    @Override
    public void setGroupCheckable(int groupdId, boolean checkable, boolean exlusive) {
        boolean changed = false;
        for (MenuItem item : items) {
            if (item.getGroupId() == groupdId) {
                item.setCheckable(checkable);
                changed = true;
            }
        }
        if (changed) {
            notifyDataSetInvalidated();
        }
    }

    @Override
    public void setGroupVisible(int groupdId, boolean visible) {
        boolean changed = false;
        for (MenuItem item : items) {
            if (item.getGroupId() == groupdId) {
                item.setVisible(visible);
                changed = true;
            }
        }
        if (changed) {
            notifyDataSetInvalidated();
        }
    }

    @Override
    public void setGroupEnabled(int groupId, boolean enabled) {
        boolean changed = false;
        for (MenuItem item : items) {
            if (item.getGroupId() == groupId) {
                item.setEnabled(enabled);
                changed = true;
            }
        }
        if (changed) {
            notifyDataSetInvalidated();
        }
    }

    @Override
    public boolean hasVisibleItems() {
        return true;
    }

    @Override
    public MenuItem findItem(int itemId) {
        for (MenuItem item : items) {
            if (item.getItemId() == itemId) {
                return item;
            }
        }
        return null;
    }

    @Override
    public int size() {
        return items.size();
    }

    @Override
    public MenuItem getItem(int index) {
        return items.get(index);
    }

    @Override
    public void close() {
        if (showing) {
            removeFromWindow();
        }
    }

    @Override
    public boolean performShortcut(int p1, KeyEvent p2, int p3) {
        // TODO: Implement this method
        return false;
    }

    @Override
    public boolean isShortcutKey(int p1, KeyEvent p2) {
        // TODO: Implement this method
        return false;
    }

    @Override
    public boolean performIdentifierAction(int p1, int p2) {
        // TODO: Implement this method
        return false;
    }

    @Override
    public void setQwertyMode(boolean p1) {
        // TODO: Implement this method
    }

    @Override
    public SubMenu setHeaderTitle(int titleRes) {
        setHeaderTitle(getContext().getResources().getString(titleRes));
        return this;
    }

    @Override
    public SubMenu setHeaderTitle(CharSequence title) {
        headerTitle = title.toString();
        View view = createSubHeaderView(headerTitle, headerIcon);
        setHeaderView(view);
        return this;
    }

    @Override
    public SubMenu setHeaderIcon(int iconRes) {
        return setHeaderIcon(getContext().getResources().getDrawable(iconRes));
    }

    @Override
    public SubMenu setHeaderIcon(Drawable icon) {
        headerIcon = icon;
        return this;
    }

    @Override
    public SubMenu setHeaderView(View view) {
        if (asSubMenuHeader != null) {
            removeHeaderView(asSubMenuHeader);
        }
        asSubMenuHeader = view;
        addHeaderView(view);
        return this;
    }

    @Override
    public void clearHeader() {
        headerTitle = null;
        headerIcon = null;
        if (asSubMenuHeader != null) {
            removeHeaderView(asSubMenuHeader);
        }
    }

    @Override
    public SubMenu setIcon(int iconRes) {
        if (subMenuItem != null) {
            subMenuItem.setIcon(iconRes);
        }
        return this;
    }

    @Override
    public SubMenu setIcon(Drawable icon) {
        if (subMenuItem != null) {
            subMenuItem.setIcon(icon);
        }
        return this;
    }

    @Override
    public MenuItem getItem() {
        return subMenuItem;
    }
    
    public boolean show(View anchor) {
        if (size() > 0 && !showing) {
            addToWindow(anchor);
            return true;
        }
        return false;
    }
    
    public void updatePositionAndSize(View anchor) {
        if (showing) {
            WindowManager.LayoutParams params = (WindowManager.LayoutParams) getLayoutParams();
            computePositionAndSize(anchor, params);
            windowManager.updateViewLayout(this, params);
        }
    }
    
    private void addToWindow(View anchor) {
        try {
            WindowManager.LayoutParams params = getWindowManagerParams();
            computePositionAndSize(anchor, params);
            params.token = anchor.getWindowToken();
            windowManager.addView(this, params);
            showing = true;
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }
    
    private void removeFromWindow() {
        try {
            windowManager.removeViewImmediate(this);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        showing = false;
    }
    
    private int computeWidth() {
        int itemWidth = getContext().getResources().getDimensionPixelSize(widthDimenRes);
        return itemWidth;
    }
    
    private int computeHeight() {
        int itemHeight = getContext().getResources().getDimensionPixelSize(heightDimenRes);
        int itemCount = getCount() + getHeaderViewsCount();
        return itemCount * itemHeight;
    }
    
    private WindowManager.LayoutParams getWindowManagerParams() {
        if (windowManagerParams == null) {
            windowManagerParams = new WindowManager.LayoutParams();
            windowManagerParams.type = asSubmenu ? WindowManager.LayoutParams.TYPE_APPLICATION_PANEL :
                WindowManager.LayoutParams.TYPE_APPLICATION_SUB_PANEL;
            windowManagerParams.gravity = Gravity.TOP | Gravity.LEFT;
            windowManagerParams.format = PixelFormat.TRANSLUCENT;
            windowManagerParams.windowAnimations = android.support.design.R.style.Animation_AppCompat_Dialog;
            windowManagerParams.flags &= ~(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_IGNORE_CHEEK_PRESSES |
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH |
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM |
                WindowManager.LayoutParams.FLAG_SPLIT_TOUCH);
            windowManagerParams.flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
            windowManagerParams.flags |= WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
            windowManagerParams.setTitle("AppMenu:" + Integer.toHexString(hashCode()));
            windowManagerParams.packageName = getContext().getPackageName();
        }
        return windowManagerParams;
    }
    
    private void computePositionAndSize(View anchor, WindowManager.LayoutParams params) {
        final View root = anchor.getRootView();
        root.getWindowVisibleDisplayFrame(visibleFrameRect);
        
        params.width = Math.min(computeWidth(), visibleFrameRect.right - preferedMargin * 2);
        params.height = Math.min(computeHeight(), visibleFrameRect.bottom - preferedMargin * 2);
        
        anchor.getLocationOnScreen(screenLocation);
        if (screenLocation[1] + params.height > visibleFrameRect.bottom ||
              params.x - root.getWidth() > 0 ) {
            int scrollX = anchor.getScrollX();
            int scrollY = anchor.getScrollY();
            visibleFrameRect.left = scrollX;
            visibleFrameRect.top = scrollY;
            visibleFrameRect.right = scrollX + params.width;
            visibleFrameRect.bottom = scrollY + params.height;
            anchor.requestRectangleOnScreen(visibleFrameRect);
        }
        
        anchor.getLocationInWindow(drawingLocation);
        params.x = (visibleFrameRect.right - drawingLocation[0]) > params.width ? drawingLocation[0] :
            Math.max(preferedMargin, drawingLocation[0] - params.width + anchor.getWidth());
        params.y = (visibleFrameRect.bottom - drawingLocation[1]) > params.height ? drawingLocation[1] :
            Math.max(preferedMargin, drawingLocation[1] - params.height);
    }
    
    private View createSubHeaderView(String title, Drawable icon) {
        LinearLayout view = new LinearLayout(getContext());
        view.setOrientation(LinearLayout.HORIZONTAL);
        if (icon != null) {
            AppCompatImageView iconView = new AppCompatImageView(getContext());
            iconView.setScaleType(AppCompatImageView.ScaleType.FIT_CENTER);
            iconView.setImageDrawable(icon);
            view.addView(iconView);
        }
        AppCompatTextView titleView = new AppCompatTextView(getContext());
        titleView.setTextAppearance(android.R.style.TextAppearance);
        titleView.setText(title);
        view.addView(titleView);
        return view;
    }
    
    private void notifyDataSetChanged() {
        if (observers != null && showing) {
            for (DataSetObserver observer : observers) {
                observer.onChanged();
            }
        }
    }
    
    private void notifyDataSetInvalidated() {
        if (observers != null) {
            for (DataSetObserver observer : observers) {
                observer.onInvalidated();
            }
        }
    }
    
    private class ViewHolder {
        AppCompatTextView title;
        AppCompatImageView icon;
        AppCompatCheckBox checkBox;
    }
    
    class Item implements MenuItem {

        CharSequence title;
        CharSequence titleCondensed;
        Drawable icon;
        int itemId = NONE;
        int groupId = NONE;
        int order = NONE;
        boolean checkable;
        boolean checked;
        boolean enabled = true;
        boolean visible = true;
        
        SubMenu subMenu;
        Intent intent;
        char alphabeticShortcut;
        char numericShortcut;
        
        Item(String title) {
            this.title = title;
        }
        
        @Override
        public int getItemId() {
            return this.itemId;
        }

        @Override
        public int getGroupId() {
            return this.groupId;
        }

        @Override
        public int getOrder() {
            return this.order;
        }

        @Override
        public MenuItem setTitle(CharSequence title) {
            this.title = title;
            return this;
        }

        @Override
        public MenuItem setTitle(int titleRes) {
            this.title = AppMenu.this.getContext().getResources().getString(titleRes);
            return this;
        }

        @Override
        public CharSequence getTitle() {
            return this.title;
        }

        @Override
        public MenuItem setTitleCondensed(CharSequence titleCondensed) {
            this.titleCondensed = titleCondensed;
            return this;
        }

        @Override
        public CharSequence getTitleCondensed() {
            return this.titleCondensed;
        }

        @Override
        public MenuItem setIcon(Drawable icon) {
            this.icon = icon;
            return this;
        }

        @Override
        public MenuItem setIcon(int resId) {
            if (resId != 0x0) {
                setIcon(AppMenu.this.getContext().getResources().getDrawable(resId));
            }
            return this;
        }

        @Override
        public Drawable getIcon() {
            return this.icon;
        }

        @Override
        public MenuItem setIntent(Intent intent) {
            this.intent = intent;
            return this;
        }

        @Override
        public Intent getIntent() {
            return this.intent;
        }

        @Override
        public MenuItem setShortcut(char alphaChar, char numChar) {
            this.alphabeticShortcut = alphaChar;
            this.numericShortcut = numChar;
            return this;
        }

        @Override
        public MenuItem setNumericShortcut(char numChar) {
            this.numericShortcut = numChar;
            return this;
        }

        @Override
        public char getNumericShortcut() {
            return this.numericShortcut;
        }

        @Override
        public MenuItem setAlphabeticShortcut(char alphaChar) {
            this.alphabeticShortcut = alphaChar;
            return this;
        }

        @Override
        public char getAlphabeticShortcut() {
            return this.alphabeticShortcut;
        }

        @Override
        public MenuItem setCheckable(boolean checkable) {
            this.checkable = checkable;
            return this;
        }

        @Override
        public boolean isCheckable() {
            return this.checkable;
        }

        @Override
        public MenuItem setChecked(boolean checked) {
            this.checked = checked;
            return this;
        }

        @Override
        public boolean isChecked() {
            return this.checked;
        }

        @Override
        public MenuItem setVisible(boolean visible) {
            this.visible = visible;
            return this;
        }

        @Override
        public boolean isVisible() {
            return this.visible;
        }

        @Override
        public MenuItem setEnabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        @Override
        public boolean isEnabled() {
            return enabled;
        }

        @Override
        public boolean hasSubMenu() {
            return this.subMenu != null;
        }

        @Override
        public SubMenu getSubMenu() {
            return this.subMenu;
        }

        @Override
        public MenuItem setOnMenuItemClickListener(MenuItem.OnMenuItemClickListener p1) {
            
            return this;
        }

        @Override
        public ContextMenu.ContextMenuInfo getMenuInfo() {
            return null;
        }

        @Override
        public void setShowAsAction(int flag) {
            
        }

        @Override
        public MenuItem setShowAsActionFlags(int flags) {
            return this;
        }

        @Override
        public MenuItem setActionView(View view) {
            return this;
        }

        @Override
        public MenuItem setActionView(int layoutId) {
            return this;
        }

        @Override
        public View getActionView() {
            return null;
        }

        @Override
        public MenuItem setActionProvider(ActionProvider actionProvider) {
            return this;
        }

        @Override
        public ActionProvider getActionProvider() {
            return null;
        }

        @Override
        public boolean expandActionView() {
            return false;
        }

        @Override
        public boolean collapseActionView() {
            return false;
        }

        @Override
        public boolean isActionViewExpanded() {
            return false;
        }

        @Override
        public MenuItem setOnActionExpandListener(MenuItem.OnActionExpandListener listener) {
            return this;
        }
        
    }
    
}
