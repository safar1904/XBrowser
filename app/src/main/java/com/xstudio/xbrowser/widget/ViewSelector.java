package com.xstudio.xbrowser.widget;

import java.util.ArrayList;
import android.util.AttributeSet;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.widget.ImageView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import java.util.List;
import com.xstudio.xbrowser.R;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.view.View;
import android.view.ViewGroup;
import com.xstudio.xbrowser.util.*;

public abstract class ViewSelector extends RecyclerView
        implements View.OnClickListener {
    
    protected Context mContext;
    protected LayoutInflater mInflater;
    protected ItemTouchHelper mItemTouchHelper;
    protected GridLayoutManager mLayoutManager;
    protected DefaultAdapter mAdapter;
    
    public ViewSelector(Context context) {
        super(context);
        init(context);
    }
    
    public ViewSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    
    public void addItem(ItemProvider itemProvider) {
        mAdapter.mItems.add(itemProvider);
        mAdapter.notifyItemInserted(mAdapter.mItems.size() - 1);
    }
    
    public void removeItem(int index) {
        mAdapter.notifyItemRemoved(index);
        mAdapter.mItems.remove(index);
    }
    
    public void removeItem(ItemProvider item) {
        if (mAdapter.mItems.contains(item)) {
            final int index = mAdapter.mItems.indexOf(item);
            removeItem(index);
        }
    }
    
    public int indexOf(ItemProvider item) {
        return mAdapter.mItems.indexOf(item);
    }
    
    protected List<ItemProvider> getAllItems() {
        return mAdapter.mItems;
    }
    
    private void init(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mLayoutManager = new GridLayoutManager(context, 2);
        setLayoutManager(mLayoutManager);
        setAdapter((mAdapter = new DefaultAdapter()));
        
        mItemTouchHelper = new ItemTouchHelper(mItemTouchHelperCallback);
        mItemTouchHelper.attachToRecyclerView(this);
    }
    
    private View inflateItemView(ViewGroup parent) {
        View view = mInflater.inflate(R.layout.view_selector_item, parent, false);
        final int orientation = mLayoutManager.getOrientation();
        final int spanCount = mLayoutManager.getSpanCount();
        if (orientation == GridLayoutManager.VERTICAL) {
            final int viewWidth = view.getMeasuredWidth();
            if (viewWidth * spanCount > getWidth()) {
                mLayoutManager.setSpanCount(spanCount - 1);
                requestLayout();
            }
        } else if (orientation == GridLayoutManager.HORIZONTAL) {
            final int viewHeight = view.getMeasuredHeight();
            if (viewHeight * spanCount > getHeight()) {
                mLayoutManager.setSpanCount(spanCount - 1);
            }
        }
        return view;
    }
    
    
    final ItemTouchHelper.SimpleCallback mItemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.START | ItemTouchHelper.END) {
        @Override
        public boolean onMove(RecyclerView p1, RecyclerView.ViewHolder view, RecyclerView.ViewHolder viewHolder) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            ViewSelector.this.removeItem(position);
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                int w = viewHolder.itemView.getWidth();
                float alpha = 1.0F - Math.abs(dX) / w;
                viewHolder.itemView.setAlpha(alpha);
            }
        }
    };
    
    
    public static interface ItemProvider {
        Bitmap getIcon();
        String getTitle();
        Bitmap getScreenshot();
        <T extends ItemCallback> void setCallback(T callback);
    }
    
    public static interface ItemCallback {
        void updateIcon(Bitmap icon);
        void updateTitle(String title);
        void updateScreenshot(Bitmap screenshot);
        int getIndex();
    }
    
    class DefaultAdapter extends Adapter<MyViewHolder> {
        
        private final List<ItemProvider> mItems = new ArrayList<>();
        
        @Override
        public ViewSelector.MyViewHolder onCreateViewHolder(ViewGroup parent, int index) {
            View view = ViewSelector.this.inflateItemView(parent);
            MyViewHolder holder = new MyViewHolder(view);
            holder.icon = (ImageView) view.findViewById(R.id.icon);
            holder.title = (TextView) view.findViewById(R.id.title);
            holder.screenshot = (ImageView) view.findViewById(R.id.screenshot);
            return holder;
        }

        @Override
        public void onBindViewHolder(final ViewSelector.MyViewHolder viewHolder, final int index) {
            final ItemProvider itemProvider = mItems.get(index);
            viewHolder.icon.setImageBitmap(itemProvider.getIcon());
            viewHolder.title.setText(itemProvider.getTitle());
            viewHolder.screenshot.setImageBitmap(itemProvider.getScreenshot());
            itemProvider.setCallback(new ItemCallback() {
                @Override
                public void updateIcon(Bitmap icon) {
                     viewHolder.icon.setImageBitmap(icon);
                }

                @Override
                public void updateTitle(String title) {
                    viewHolder.title.setText(title);
                }

                @Override
                public void updateScreenshot(Bitmap screenshot) {
                    viewHolder.screenshot.setImageBitmap(screenshot);
                }
                
                @Override
                public int getIndex() {
                    return mItems.indexOf(itemProvider);
                }
            });
            viewHolder.itemView.setTag(itemProvider);
            viewHolder.itemView.setOnClickListener(ViewSelector.this);
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }
        
    }
    
    public static class MyViewHolder extends ViewHolder {
        
        public ImageView icon;
        public TextView title;
        public ImageView screenshot;
        public ItemProvider itemProvider;
        
        public MyViewHolder(View v) {
            super(v);
        }
        
    }
    
}
