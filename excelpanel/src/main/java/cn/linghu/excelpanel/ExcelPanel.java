package cn.linghu.excelpanel;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cn.linghu.utils.Utils;

/**
 * Created by linghu on 2017/2/17.
 *
 * <p>
 *    A widget like Excel which can scroll in all directions but it have not split line.
 * Your adapter extends {@link cn.linghu.excelpanel.BaseExcelPanelAdapter} can provide data to excelPanel.
 * If you want to reset ExcelPanel,just call {@link #reset() reset()}
 * </p>
 *
 */
public class ExcelPanel extends FrameLayout implements OnAddHorizationScrollListener {
    public static final int DEFAULT_WIDTH = 80;
    public static final int DEFAULT_HEIGHT = 50;
    public static final int DEFAULT_LEFT_WIDTH = 100;
    public static final int DEFAULT_TOP_HEIGHT = 50;

    private int topCellHeight;
    private int leftCellWidth;
    private int normalCellHeight;
    private int normalCellWidth;

    private int amountAxisX = 0;
    private int amountAxisY = 0;
    private boolean hasLoadMore;

    protected RecyclerView topRecyclerView;
    protected RecyclerView leftRecyclerView;
    protected RecyclerView mRecyclerView;
    protected BaseExcelPanelAdapter excelPanelAdapter;
    private List<RecyclerView> list;
    private static Map<Integer, Integer> indexHeight;
    private static Map<Integer, Integer> indexWidth;

    private OnLoadMoreListener onLoadMoreListener;

    public interface OnLoadMoreListener {
        /**
         * when the loading icon appeared, this method may be called many times
         */
        void onExcelLoadMore();
    }

    public ExcelPanel(Context context) {
        this(context, null);
    }

    public ExcelPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.ExcelPanel, 0, 0);
        try {
            leftCellWidth = (int) a.getDimension(R.styleable.ExcelPanel_left_cell_width, Utils.dp2px(DEFAULT_LEFT_WIDTH, getContext()));
            topCellHeight = (int) a.getDimension(R.styleable.ExcelPanel_top_cell_height, Utils.dp2px(DEFAULT_TOP_HEIGHT, getContext()));
            normalCellWidth = (int) a.getDimension(R.styleable.ExcelPanel_normal_cell_width, Utils.dp2px(DEFAULT_WIDTH, getContext()));
            normalCellHeight = (int) a.getDimension(R.styleable.ExcelPanel_normal_cell_height, Utils.dp2px(DEFAULT_HEIGHT, getContext()));
        } finally {
            a.recycle();
        }

        initWidget();
    }

    private void initWidget() {
        list = new ArrayList<RecyclerView>();

        //content's RecyclerView
        mRecyclerView = createContentCell();
        addView(mRecyclerView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        LayoutParams mainLayoutParams = (LayoutParams) mRecyclerView.getLayoutParams();
        mainLayoutParams.leftMargin = leftCellWidth;
        mainLayoutParams.topMargin = topCellHeight;
        mRecyclerView.setLayoutParams(mainLayoutParams);

        //top RecyclerView
        topRecyclerView = createTopTitle();
        addRecyclerView(topRecyclerView);
        addView(topRecyclerView, new LayoutParams(LayoutParams.WRAP_CONTENT, topCellHeight));
        LayoutParams topLayoutParams = (LayoutParams) topRecyclerView.getLayoutParams();
        topLayoutParams.leftMargin = leftCellWidth;
        topLayoutParams.topMargin = 0;
        topRecyclerView.setLayoutParams(topLayoutParams);

        //left RecyclerView
        leftRecyclerView = createLeftTitle();
        addView(leftRecyclerView, new LayoutParams(leftCellWidth, LayoutParams.WRAP_CONTENT));
        LayoutParams leftLayoutParams = (LayoutParams) leftRecyclerView.getLayoutParams();
        leftLayoutParams.topMargin = topCellHeight;
        leftLayoutParams.leftMargin = 0;
        leftRecyclerView.setLayoutParams(leftLayoutParams);
    }

    protected RecyclerView createTopTitle() {
        RecyclerView recyclerView = new RecyclerView(getContext());
        recyclerView.setLayoutManager(getTopLayoutManager());
        recyclerView.addOnScrollListener(topScrollListener);
        return recyclerView;
    }

    protected RecyclerView createLeftTitle() {
        RecyclerView recyclerView = new RecyclerView(getContext());
        recyclerView.setLayoutManager(getLeftLayoutManager());
        recyclerView.addOnScrollListener(contentScrollListener);
        return recyclerView;
    }

    protected RecyclerView createContentCell() {
        RecyclerView recyclerView = new RecyclerView(getContext());
        recyclerView.setLayoutManager(getLayoutManager());
        recyclerView.addOnScrollListener(contentScrollListener);
        return recyclerView;
    }

    protected RecyclerView.LayoutManager getLayoutManager() {
        if (null == mRecyclerView || null == mRecyclerView.getLayoutManager()) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            return layoutManager;
        }
        return mRecyclerView.getLayoutManager();
    }

    private RecyclerView.LayoutManager getTopLayoutManager() {
        if (null == topRecyclerView || null == topRecyclerView.getLayoutManager()) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            return layoutManager;
        }
        return topRecyclerView.getLayoutManager();
    }

    private RecyclerView.LayoutManager getLeftLayoutManager() {
        if (null == leftRecyclerView || null == leftRecyclerView.getLayoutManager()) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            return layoutManager;
        }
        return leftRecyclerView.getLayoutManager();
    }

    /**
     * 内容和左标题栏的垂直滚动， 上下滑动
     */
    private RecyclerView.OnScrollListener contentScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            amountAxisY += dy;
            //让excel垂直滚动
            fastVerticalScrollTo(amountAxisY, mRecyclerView);
            fastVerticalScrollTo(amountAxisY, leftRecyclerView);

            LinearLayoutManager manager = (LinearLayoutManager) leftRecyclerView.getLayoutManager();
//            int visibleItemCount = recyclerView.getChildCount();
            int totalItemCount = manager.getItemCount();
//            int firstVisibleItem = manager.findFirstVisibleItemPosition();
            int lastVisiblePosition = manager.findLastVisibleItemPosition();
//            if(lastVisiblePosition == totalItemCount-1 && totalItemCount >= 5 && hasLoadMore){
//                onLoadMoreListener.onExcelLoadMore();
//            }
//            Rect leftRect = new Rect();
//            leftRecyclerView.getGlobalVisibleRect(leftRect);
//            int screenHeight = Utils.getScreenHeight(leftRecyclerView);
//            if(screenHeight >= leftRect.bottom + 40 && totalItemCount > 5 && hasLoadMore){
//                onLoadMoreListener.onExcelLoadMore();
//            }
            if(lastVisiblePosition == leftRecyclerView.getLayoutManager().getItemCount()-1 && totalItemCount >= 5 && hasLoadMore){
                onLoadMoreListener.onExcelLoadMore();
            }

            //得到当前显示的最后一个item的view
//            View lastChildView = leftRecyclerView.getLayoutManager().getChildAt(leftRecyclerView.getLayoutManager().getChildCount()-1);
//            Log.d("Linghu", "====================(lastChildView==null)==" + (lastChildView==null));
//            if(lastChildView != null){
//                //得到lastChildView的bottom坐标值
//                int lastChildBottom = lastChildView.getBottom();
//                //得到Recyclerview的底部坐标减去底部padding值，也就是显示内容最底部的坐标
//                int recyclerBottom =  leftRecyclerView.getBottom()-leftRecyclerView.getPaddingBottom();
//                //通过这个lastChildView得到这个view当前的position值
//                int lastPosition  = leftRecyclerView.getLayoutManager().getPosition(lastChildView);
//
//                Log.d("Linghu", "===========(lastChildBottom)=" + lastChildBottom + ",recyclerBottom="+recyclerBottom);
//                //判断lastChildView的bottom值跟recyclerBottom
//                //判断lastPosition是不是最后一个position
//                //如果两个条件都满足则说明是真正的滑动到了底部
//                if(lastChildBottom == recyclerBottom && lastPosition == leftRecyclerView.getLayoutManager().getItemCount()-1 ){
//                    onLoadMoreListener.onExcelLoadMore();
//                }
//            }

        }
    };

    /**
     * 顶部标题栏水平滚动
     */
    private RecyclerView.OnScrollListener topScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            amountAxisX += dx;
            for (RecyclerView recyclerView1 : list) {
                fastScrollHorization(amountAxisX, recyclerView1);
            }
            if (excelPanelAdapter != null) {
                excelPanelAdapter.setAmountAxisY(amountAxisX);
            }
        }
    };

    static void fastScrollHorization(int amountAxis, RecyclerView recyclerView) {
        int total = 0, count = 0;
        Iterator<Integer> iterator = indexWidth.keySet().iterator();
        while (iterator.hasNext()) {
            int height = indexWidth.get(iterator.next());
            if (total + height >= amountAxis) {
                break;
            }
            total += height;
            count++;
        }
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        //call this method the OnScrollListener's onScrolled will be called，but dx and dy always be zero.
        linearLayoutManager.scrollToPositionWithOffset(count, -(amountAxis - total));
    }

    private void fastVerticalScrollTo(int amountAxis, RecyclerView recyclerView) {
        int position = 0, height = normalCellHeight;
        position += amountAxis / height;
        amountAxis %= height;
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        //call this method the OnScrollListener's onScrolled will be called，but dx and dy always be zero.
        linearLayoutManager.scrollToPositionWithOffset(position, -amountAxis);
    }

    public void setAdapter(BaseExcelPanelAdapter excelPanelAdapter) {
        if (excelPanelAdapter != null) {
            this.excelPanelAdapter = excelPanelAdapter;
            this.excelPanelAdapter.setLeftCellWidth(leftCellWidth);
            this.excelPanelAdapter.setTopCellHeight(topCellHeight);
            this.excelPanelAdapter.setOnScrollListener(topScrollListener);
            this.excelPanelAdapter.setOnAddHorizationScrollListener(this);
            this.excelPanelAdapter.setExcelPanel(this);
            distributeAdapter();
        }
    }

    private void distributeAdapter() {
        if (leftRecyclerView != null) {
            leftRecyclerView.setAdapter(excelPanelAdapter.getLeftRecyclerViewAdapter());
        }
        if (topRecyclerView != null) {
            topRecyclerView.setAdapter(excelPanelAdapter.getTopRecyclerViewAdapter());
        }
        if (mRecyclerView != null) {
            mRecyclerView.setAdapter(excelPanelAdapter.getmRecyclerViewAdapter());
        }
    }

    @Override
    public void addRecyclerView(RecyclerView recyclerView) {
        if (recyclerView.getTag() == null) {
            recyclerView.setTag("");//just a tag
            list.add(recyclerView);
            recyclerView.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                        case MotionEvent.ACTION_POINTER_DOWN:
                            for (RecyclerView rv : list) {
                                rv.stopScroll();
                            }
                            break;
                        default:
                            break;
                    }
                    return false;
                }
            });
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        indexHeight = new TreeMap<>();
        indexWidth = new TreeMap<>();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(list != null){
            list.clear();
            list = null;
        }
        if(indexWidth != null){
            indexWidth.clear();
            indexWidth = null;
        }
        if(indexHeight != null){
            indexHeight.clear();
            indexHeight = null;
        }
    }

    /**
     * @param dx horizontal distance to scroll
     */
    void scrollBy(int dx) {
        contentScrollListener.onScrolled(mRecyclerView, dx, 0);
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    void setHasLoadMore(boolean hasLoadMore) {
        this.hasLoadMore = hasLoadMore;
    }

    public boolean canChildScrollUp() {
        return amountAxisY > 0;
    }

    public void reset() {
        if (excelPanelAdapter != null) {
            excelPanelAdapter.disableLoadMore();
        }
        if (!Utils.isEmpty(list)) {
            for (RecyclerView recyclerView : list) {
                recyclerView.setTag(null);
            }
            list.clear();
        }
        indexHeight.clear();
        indexWidth.clear();
        list.add(topRecyclerView);
        amountAxisY = 0;
        amountAxisX = 0;
    }

    public int findFirstVisibleItemPosition() {
        int position = -1;
        if (mRecyclerView.getLayoutManager() != null && excelPanelAdapter != null) {
            LinearLayoutManager mLinearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
            int firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();
            return firstVisibleItem;
        }
        return position;
    }

    /**
     * use to adjust the height and width of the normal cell
     *
     * @param view     cell's view
     * @param position horizontal or vertical position
     * @param isHeight is it use to adjust height or not
     * @param isSet    is it use to config height or width
     */
    public void onAfterBind(View view, int position, boolean isHeight, boolean isSet) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (isHeight) {
            if (indexHeight.get(position) != null) {
                int height = indexHeight.get(position);
                if (height > layoutParams.height) {
                    layoutParams.height = height;
                    view.setLayoutParams(layoutParams);//must, because this haven't been added to it's parent
                    adjustHeight(position, height);
                } else {
                    if (isSet) {
                        indexHeight.put(position, layoutParams.height);
                        adjustHeight(position, layoutParams.height);
                    }
                }
            } else {
                indexHeight.put(position, layoutParams.height);
            }
        } else {
            //adjust width ???
        }
    }

    /**
     * set the height of the line position to height
     *
     * @param position which line
     * @param height   the line's height
     */
    private void adjustHeight(int position, int height) {
        for (RecyclerView recyclerView : list) {
            for (int i = 0; i < recyclerView.getChildCount(); i++) {
                View view1 = recyclerView.getChildAt(i);
                if (view1.getTag() != null && view1.getTag() instanceof Pair) {
                    Pair pair = (Pair) view1.getTag();
                    int index = (int) pair.first;
                    ViewGroup.LayoutParams lp = view1.getLayoutParams();
                    if (index == position) {
                        lp.height = height;
                        view1.setLayoutParams(lp);
                        break;
                    }
                }
            }
        }
    }

}