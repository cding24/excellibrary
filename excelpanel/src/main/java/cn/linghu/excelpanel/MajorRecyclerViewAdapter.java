package cn.linghu.excelpanel;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linghu on 2016/12/11.
 *
 */
public class MajorRecyclerViewAdapter<M> extends RecyclerViewAdapter<M> {
    private Context context;

    protected int amountAxisX = 0;
    private List<String> list; //a virtual list
    private OnExcelPanelListener excelPanelListener;
    protected RecyclerView.OnScrollListener onScrollListener;
    protected OnAddHorizationScrollListener onAddHorizationScrollListener;

    public MajorRecyclerViewAdapter(Context context, List<M> list, OnExcelPanelListener excelPanelListener) {
        super(context, list);
        this.context = context;
        this.excelPanelListener = excelPanelListener;
    }

    public void setOnScrollListener(RecyclerView.OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    public void setOnAddHorizationScrollListener(OnAddHorizationScrollListener onAddHorizationScrollListener) {
        this.onAddHorizationScrollListener = onAddHorizationScrollListener;
    }

    @Override
    public void setData(List<M> data) {
        super.setData(data == null ? null : ((List) data.get(0)));
        if (data != null) {
            if (list == null || list.size() >= data.size()) {//refresh or first time
                list = new ArrayList<>();
            }
            for (int i = list.size(); i < data.size(); i++) {
                list.add("");
            }
        } else {
            list = null;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateNormalViewHolder(ViewGroup parent, int viewType) {
        RecyclerView recyclerView = new RecyclerView(context);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        return new RecyclerViewViewHolder(recyclerView);
    }

    @Override
    public void onBindNormalViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (!(holder instanceof RecyclerViewViewHolder)) {
            return;
        }
        RecyclerViewViewHolder viewHolder = (RecyclerViewViewHolder) holder;
        ContentRecyclerAdapter contentRecyclerAdapter = new ContentRecyclerAdapter(context, position, excelPanelListener);
        contentRecyclerAdapter.setData(list);
        viewHolder.recyclerView.setAdapter(contentRecyclerAdapter);

        viewHolder.recyclerView.removeOnScrollListener(onScrollListener);
        viewHolder.recyclerView.addOnScrollListener(onScrollListener);
        if (onAddHorizationScrollListener != null) {
            onAddHorizationScrollListener.addRecyclerView(viewHolder.recyclerView);
        }
        ExcelPanel.fastScrollHorization(amountAxisX, viewHolder.recyclerView);
    }

    static class RecyclerViewViewHolder extends RecyclerView.ViewHolder {
        public final RecyclerView recyclerView;

        public RecyclerViewViewHolder(View itemView) {
            super(itemView);
            recyclerView = (RecyclerView) itemView;
        }
    }

    static class ContentRecyclerAdapter<C> extends RecyclerViewAdapter<C> {
        private int verticalPosition;
        private OnExcelPanelListener excelPanelListener;

        public ContentRecyclerAdapter(Context context, int verticalPosition, OnExcelPanelListener excelPanelListener) {
            super(context);
            this.verticalPosition = verticalPosition;
            this.excelPanelListener = excelPanelListener;
        }

        @Override
        public RecyclerView.ViewHolder onCreateNormalViewHolder(ViewGroup parent, int viewType) {
            if (excelPanelListener != null) {
                return excelPanelListener.onCreateCellViewHolder(parent, viewType);
            } else {
                return null;
            }
        }

        @Override
        public void onBindNormalViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (excelPanelListener != null) {
                excelPanelListener.onBindCellViewHolder(holder, position, verticalPosition);
                //use to adjust height and width
                holder.itemView.setTag(new Pair<>(position, verticalPosition));
                excelPanelListener.onAfterBind(holder.itemView, position, true, false);
                excelPanelListener.onAfterBind(holder.itemView, verticalPosition, false, false);
            }
        }
    }

    public void setAmountAxisX(int amountAxisX) {
        this.amountAxisX = amountAxisX;
    }


}