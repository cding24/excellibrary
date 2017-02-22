package cn.linghu.excelpanel;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import cn.linghu.utils.Utils;

/**
 * Created by linghu on 2017/02/17.
 *
 */
public abstract class BaseExcelPanelAdapter<T, L, M> implements OnExcelPanelListener {
    public static final int LOADING_VIEW_HEIGHT = 40;

    private Context mContext;

    private View leftTopView;
    private ExcelPanel excelPanel;
    private RecyclerViewAdapter topRecyclerViewAdapter;
    private RecyclerViewAdapter leftRecyclerViewAdapter;
    private RecyclerViewAdapter mRecyclerViewAdapter;
    protected RecyclerView.OnScrollListener onScrollListener;
    protected OnAddHorizationScrollListener onAddHorizationScrollListener;
    protected String excelTitle; //表格名称
    protected List<T> topData;
    protected List<L> leftData;
    protected List<List<M>> majorData;

    private int leftCellWidth;
    private int topCellHeight;

    protected int amountAxisY = 0;

    public BaseExcelPanelAdapter(Context context) {
        mContext = context;
        initRecyclerViewAdapter();
    }

    private void initRecyclerViewAdapter() {
        topRecyclerViewAdapter = new TopRecyclerViewAdapter(mContext, topData, this);
        leftRecyclerViewAdapter = new LeftRecyclerViewAdapter(mContext, leftData, this);
        mRecyclerViewAdapter = new MajorRecyclerViewAdapter(mContext, majorData, this);
    }

    public void setTopData(List<T> topData) {
        this.topData = topData;
        topRecyclerViewAdapter.setData(topData);
    }

    public void setLeftData(List<L> leftData) {
        this.leftData = leftData;
        leftRecyclerViewAdapter.setData(leftData);
    }

    public void setMajorData(List<List<M>> majorData) {
        this.majorData = majorData;
        mRecyclerViewAdapter.setData(majorData);
    }

    public void setExcelName(String name) {
        this.excelTitle = name;
    }

    public void setAllData(List<L> leftData, List<T> topData, List<List<M>> majorData) {
        setLeftData(leftData);
        setTopData(topData);
        setMajorData(majorData);
        excelPanel.scrollBy(0);
        if (!Utils.isEmpty(leftData) && !Utils.isEmpty(topData) && excelPanel != null
                && !Utils.isEmpty(majorData) && leftTopView == null) {
            leftTopView = onCreateTopLeftView(excelTitle);
            excelPanel.addView(leftTopView, new FrameLayout.LayoutParams(leftCellWidth, topCellHeight));
        } else if (leftTopView != null) {
            if (Utils.isEmpty(leftData)) {
                leftTopView.setVisibility(View.GONE);
            } else {
                leftTopView.setVisibility(View.VISIBLE);
            }
        }
    }

    public RecyclerViewAdapter getmRecyclerViewAdapter() {
        return mRecyclerViewAdapter;
    }

    public RecyclerViewAdapter getLeftRecyclerViewAdapter() {
        return leftRecyclerViewAdapter;
    }

    public RecyclerViewAdapter getTopRecyclerViewAdapter() {
        return topRecyclerViewAdapter;
    }

    public void setLeftCellWidth(int leftCellWidth) {
        this.leftCellWidth = leftCellWidth;
    }

    public void setTopCellHeight(int topCellHeight) {
        this.topCellHeight = topCellHeight;
    }

    public void setOnScrollListener(RecyclerView.OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
        if (mRecyclerViewAdapter != null && mRecyclerViewAdapter instanceof MajorRecyclerViewAdapter) {
            ((MajorRecyclerViewAdapter) mRecyclerViewAdapter).setOnScrollListener(onScrollListener);
        }
    }

    public void setOnAddHorizationScrollListener(OnAddHorizationScrollListener onAddHorizationScrollListener) {
        this.onAddHorizationScrollListener = onAddHorizationScrollListener;
        if (mRecyclerViewAdapter != null && mRecyclerViewAdapter instanceof MajorRecyclerViewAdapter) {
            ((MajorRecyclerViewAdapter) mRecyclerViewAdapter).setOnAddHorizationScrollListener(onAddHorizationScrollListener);
        }
    }

    public T getTopItem(int position) {
        if (Utils.isEmpty(topData) || position < 0 || position >= topData.size()) {
            return null;
        }
        return topData.get(position);
    }

    public L getLeftItem(int position) {
        if (Utils.isEmpty(leftData) || position < 0 || position >= leftData.size()) {
            return null;
        }
        return leftData.get(position);
    }

    public M getMajorItem(int x, int y) {
        if (Utils.isEmpty(majorData) || x < 0 || x >= majorData.size() || Utils
                .isEmpty(majorData.get(x)) || y < 0 || y >= majorData.get(x).size()) {
            return null;
        }
        return majorData.get(x).get(y);
    }

    public void setAmountAxisY(int amountAxisY) {
        this.amountAxisY = amountAxisY;
        if (mRecyclerViewAdapter != null && mRecyclerViewAdapter instanceof MajorRecyclerViewAdapter) {
            ((MajorRecyclerViewAdapter) mRecyclerViewAdapter).setAmountAxisX(amountAxisY);
        }
    }

    public void setExcelPanel(ExcelPanel excelPanel) {
        this.excelPanel = excelPanel;
    }

    protected View createLeftBottomFillView() {
        View topStaticView = new View(mContext);
        int loadingHeight = Utils.dp2px(LOADING_VIEW_HEIGHT, mContext);
        topStaticView.setLayoutParams(new ViewGroup.LayoutParams(loadingHeight, loadingHeight));

        return topStaticView;
    }

    protected View createMajorLoadingView() {
        int loadingHeight = Utils.dp2px(LOADING_VIEW_HEIGHT, mContext);
        LinearLayout loadingView = new LinearLayout(mContext);
        loadingView.setOrientation(LinearLayout.HORIZONTAL);
        loadingView.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams lpp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, loadingHeight);
        lpp.rightMargin = loadingHeight;
        loadingView.setLayoutParams(lpp);

        ProgressBar progressBar = new ProgressBar(mContext);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(
                Utils.dp2px(30, mContext), Utils.dp2px(30, mContext)));//android:style/Widget.ProgressBar.Small的宽高

        TextView loadingTV = new TextView(mContext);
        loadingTV.setText(mContext.getString(R.string.loading_txt));
        loadingTV.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        loadingTV.setTextColor(Color.parseColor("#393939"));
        loadingTV.setMaxLines(1);
        loadingTV.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams txtLayParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                Utils.dp2px(30, mContext));
        txtLayParams.leftMargin = Utils.dp2px(10, mContext);

        loadingView.addView(progressBar, lp);
        loadingView.addView(loadingTV, txtLayParams);

        return loadingView;
    }

    /**
     * 加载更多是垂直方向的
     */
    public void enableLoadMore() {
        if (leftRecyclerViewAdapter != null && mRecyclerViewAdapter != null && excelPanel != null &&
                (leftRecyclerViewAdapter.getFooterViewsCount() <= 0 || mRecyclerViewAdapter.getFooterViewsCount() <= 0)) {
            leftRecyclerViewAdapter.setFooterView(createLeftBottomFillView());
            mRecyclerViewAdapter.setFooterView(createMajorLoadingView());
            excelPanel.setHasLoadMore(true);
        }
    }

    public void disableLoadMore() {
        if (leftRecyclerViewAdapter != null && mRecyclerViewAdapter != null && excelPanel != null &&
                (leftRecyclerViewAdapter.getFooterViewsCount() > 0 || mRecyclerViewAdapter.getFooterViewsCount() > 0)) {
            leftRecyclerViewAdapter.setFooterView(null);
            mRecyclerViewAdapter.setFooterView(null);
            excelPanel.setHasLoadMore(false);
        }
    }

    @Override
    final public void onAfterBind(View view, int position, boolean isHeight, boolean isSet) {
        if (excelPanel != null) {
            excelPanel.onAfterBind(view, position, isHeight, isSet);
        }
    }


}