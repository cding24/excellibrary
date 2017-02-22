package com.linghu.excelapp;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.linghu.excelapp.entity.Cell;
import com.linghu.excelapp.entity.ColumnTitle;
import com.linghu.excelapp.entity.RowTitle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.linghu.excelpanel.ExcelPanel;

/**
 * Created by linghu on 2016/11/3. Any questes send to zhaoyanchang@163.com.
 *  纵向加载更多的ExcelView控件，支持固定长宽数据的excel表格
 *
 */
public class ExcelTestActivity extends Activity implements ExcelPanel.OnLoadMoreListener {
    private ExcelPanel enrollExcelView;
    private ExcelPanel submitExcelView;
    private ExcelPanel majorExcelView;
    private ExcellAdapter enrollAdapter;
    private ExcellAdapter submitAdapter;
    private ExcellAdapter majorAdapter;
    public static final int COLUMN_SIZE = 5;
    public static final int ROW_SIZE = 10;
    private List<ColumnTitle> columnTitles;
    private List<RowTitle> rowTitles;
    private List<List<Cell>> cells;
    private boolean isLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_college_history);

        enrollExcelView = (ExcelPanel)findViewById(R.id.enroll_excel);
        enrollAdapter = new ExcellAdapter(ExcelTestActivity.this, null);
        enrollExcelView.setAdapter(enrollAdapter);
        enrollExcelView.setOnLoadMoreListener(this);
        enrollAdapter.setExcelName("数据");
        initFixedData(enrollAdapter, 6, 5);

        submitExcelView = (ExcelPanel)findViewById(R.id.submit_excel);
        submitAdapter = new ExcellAdapter(this, null);
        submitExcelView.setAdapter(submitAdapter);
        submitExcelView.setOnLoadMoreListener(this);
        submitAdapter.setExcelName("数据");
        initFixedData(submitAdapter, 5, 5);

        majorExcelView = (ExcelPanel)findViewById(R.id.major_excel);
        majorAdapter = new ExcellAdapter(this, null);
        majorExcelView.setAdapter(majorAdapter);
        majorExcelView.setOnLoadMoreListener(this);
        majorAdapter.setExcelName("专业名");
        //第三个表格的数据
        columnTitles = new ArrayList<>();
        rowTitles = new ArrayList<>();
        cells = new ArrayList<>();
        for (int i = 0; i < COLUMN_SIZE; i++) {
            cells.add(new ArrayList<Cell>());
        }
        loadThirdData();
    }

    /**
     * ExcelPanel加载更多的回调
     */
    @Override
    public void onExcelLoadMore() {
        if (!isLoading) {
            loadThirdData();
        }
    }

    /**
     * 初始化固定行列的excel
     * @param adapter
     * @param column 列数
     * @param row 行数
     */
    private void initFixedData(ExcellAdapter adapter, int row, int column) {
        List<ColumnTitle> columnTitles = new ArrayList<>();
        List<RowTitle> rowTitles = new ArrayList<>();
        List<List<Cell>> cells = new ArrayList<>();
        for (int i = 0; i < column; i++) {
            cells.add(new ArrayList<Cell>());
        }
        List<RowTitle> rowTitles1 = genRowTitleData(row);
        List<List<Cell>> cells1 = genCellData(column,row);
        rowTitles.addAll(rowTitles1);
        for (int i = 0; i < cells1.size(); i++) {
            cells.get(i).addAll(cells1.get(i));
        }
        if (columnTitles.size() == 0) {
            columnTitles.addAll(genColumnTitleData(column));
        }
        adapter.setAllData(rowTitles, columnTitles, cells);
        adapter.disableLoadMore();
    }


    /**
     * 模拟垂直方向加载更多的数据加载过程
     */
    private void loadThirdData() {
        //模拟网络加载
        isLoading = true;
        Message message = new Message();
        loadDataHandler.sendMessageDelayed(message, 1200);
    }
    private Handler loadDataHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            isLoading = false;
            List<RowTitle> rowTitles1 = genRowTitleData(ROW_SIZE);
            List<List<Cell>> cells1 = genCellData(COLUMN_SIZE, ROW_SIZE);

            rowTitles.addAll(rowTitles1);
            for (int i = 0; i < cells1.size(); i++) {
                cells.get(i).addAll(cells1.get(i));
            }

            if (columnTitles.size() == 0) {
                columnTitles.addAll(genColumnTitleData(COLUMN_SIZE));
            }
            majorAdapter.setAllData(rowTitles, columnTitles, cells);
            majorAdapter.enableLoadMore();
        }
    };

    //====================================模拟生成数据==========================================
    private List<ColumnTitle> genColumnTitleData(int column) {
        List<ColumnTitle> columnTitles = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < column; i++) {
            ColumnTitle columnTitle = new ColumnTitle(""+(2012 + i));
            columnTitles.add(columnTitle);
        }
        return columnTitles;
    }
    private List<RowTitle> genRowTitleData(int row) {
        List<RowTitle> rowTitles = new ArrayList<>();
        for (int i = 0; i < row; i++) {
            RowTitle rowTitle = new RowTitle("专业名称" + i);
            rowTitles.add(rowTitle);
        }
        return rowTitles;
    }
    /**
     *
     * @param column
     * @param row
     * @return
     */
    private List<List<Cell>> genCellData(int column, int row) {
        List<List<Cell>> cells = new ArrayList<>();
        for (int i = 0; i < column; i++) {
            List<Cell> cellList = new ArrayList<>();
            cells.add(cellList);
            for (int j = 0; j < row; j++) {
                Cell cell = new Cell(""+(300+j));
                cellList.add(cell);
            }
        }
        return cells;
    }


}