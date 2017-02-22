package cn.linghu.excelpanel;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by linghu on 2017/02/11.
 *
 */
public class TopRecyclerViewAdapter<T> extends RecyclerViewAdapter<T> {

    private OnExcelPanelListener excelPanelListener;

    public TopRecyclerViewAdapter(Context context, List<T> list, OnExcelPanelListener excelPanelListener) {
        super(context, list);
        this.excelPanelListener = excelPanelListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateNormalViewHolder(ViewGroup parent, int viewType) {
        if (excelPanelListener != null) {
            return excelPanelListener.onCreateTopViewHolder(parent, viewType);
        } else {
            return null;
        }
    }

    @Override
    public void onBindNormalViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (excelPanelListener != null) {
            excelPanelListener.onBindTopViewHolder(holder, position);
            //use to adjust width
            holder.itemView.setTag(new Pair<>(0, position));
            excelPanelListener.onAfterBind(holder.itemView, position, false, true);
        }
    }

}