package com.iHealth;

import com.androidplot.xy.XYSeries;

public class SampleDynamicSeries implements XYSeries {
    private SampleDynamicXYDatasource datasource;
    private int seriesIndex;
    private String title;

    public SampleDynamicSeries(SampleDynamicXYDatasource datasource, int seriesIndex, String title) {
        this.datasource = datasource;
        this.seriesIndex = seriesIndex;
        this.title = title;
    }
    public String getTitle() {
        return title;
    }

    public int size() {
        return datasource.getItemCount(seriesIndex);
    }

    public Number getX(int index) {
        return datasource.getX(seriesIndex, index);
    }

    public Number getY(int index) {
        return datasource.getY(seriesIndex, index);
    }
}
