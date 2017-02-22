package com.linghu.excelapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.linghu.excelapp.entity.Cell;
import com.linghu.excelapp.entity.ColumnTitle;
import com.linghu.excelapp.entity.RowTitle;

import cn.linghu.excelpanel.BaseExcelPanelAdapter;

/**
 *
 * Created by linghu on 2017/02/17.
 * 该Adapter的思想是类似于操作系统再调用我们，不是我们调用操作系统
 */
public class ExcellAdapter extends BaseExcelPanelAdapter<ColumnTitle, RowTitle, Cell> {

    private Context context;
    /** excel每个单元点击的回调*/
    private View.OnClickListener blockListener;

    public ExcellAdapter(Context context, View.OnClickListener blockListener) {
        super(context);
        this.context = context;
        this.blockListener = blockListener;
    }

    //=================content's cell===============================================================
    @Override
    public RecyclerView.ViewHolder onCreateCellViewHolder(ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.excel_cell_item, parent, false);
        CellHolder cellHolder = new CellHolder(layout);
        return cellHolder;
    }

    @Override
    public void onBindCellViewHolder(RecyclerView.ViewHolder holder, int verticalPosition, int horizontalPosition) {
        Cell cell = getMajorItem(verticalPosition, horizontalPosition);
        if (null == holder || !(holder instanceof CellHolder) || cell == null) {
            return;
        }
        CellHolder viewHolder = (CellHolder) holder;
        viewHolder.valueTV.setText(cell.value);
    }

    static class CellHolder extends RecyclerView.ViewHolder {
        public final TextView valueTV;

        public CellHolder(View itemView) {
            super(itemView);
            valueTV = (TextView) itemView.findViewById(R.id.cell_value);
        }
    }


    //================top cell======================================================================
    @Override
    public RecyclerView.ViewHolder onCreateTopViewHolder(ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.excel_column_title_item, parent, false);
        TopHolder topHolder = new TopHolder(layout);
        return topHolder;
    }

    @Override
    public void onBindTopViewHolder(RecyclerView.ViewHolder holder, int position) {
        ColumnTitle columnTitle = getTopItem(position);
        if (null == holder || !(holder instanceof TopHolder) || columnTitle == null) {
            return;
        }
        TopHolder viewHolder = (TopHolder) holder;
        viewHolder.dateTV.setText(columnTitle.title);
    }

    static class TopHolder extends RecyclerView.ViewHolder {
        public final TextView dateTV;

        public TopHolder(View itemView) {
            super(itemView);
            dateTV = (TextView) itemView.findViewById(R.id.column_data);
        }
    }

    //===============left cell======================================================================
    @Override
    public RecyclerView.ViewHolder onCreateLeftViewHolder(ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.excel_row_title_item, parent, false);
        LeftHolder leftHolder = new LeftHolder(layout);
        return leftHolder;
    }

    @Override
    public void onBindLeftViewHolder(RecyclerView.ViewHolder holder, int position) {
        RowTitle rowTitle = getLeftItem(position);
        if (null == holder || !(holder instanceof LeftHolder) || rowTitle == null) {
            return;
        }
        LeftHolder viewHolder = (LeftHolder) holder;
        viewHolder.titleTV.setText(rowTitle.name);
    }

    static class LeftHolder extends RecyclerView.ViewHolder {
        public final TextView titleTV;

        public LeftHolder(View itemView) {
            super(itemView);
            titleTV = (TextView) itemView.findViewById(R.id.row_title);
        }
    }

    //============left-top cell=====================================================================
    @Override
    public View onCreateTopLeftView(String excelName) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.excel_left_top_item, null);
        if(!TextUtils.isEmpty(excelName)){
            TextView titleTV = (TextView) rootView.findViewById(R.id.excel_name);
            titleTV.setText(excelName);
        }

        return rootView;
    }

}