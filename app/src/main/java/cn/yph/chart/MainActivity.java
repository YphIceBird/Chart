package cn.yph.chart;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import cn.yph.library.LineChart;
import cn.yph.library.PointData;

public class MainActivity extends AppCompatActivity {

    private LineChart mLineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLineChart = (LineChart) findViewById(R.id.line_chart);
        mLineChart.setData(getPummyData());
    }

    private List<PointData> getPummyData() {
        List<PointData> datas = new ArrayList<>();
        datas.add(new PointData(0, 33));
        datas.add(new PointData(1, 12));
        datas.add(new PointData(2, 76));
        datas.add(new PointData(3, 78));
        datas.add(new PointData(4, 32));
        datas.add(new PointData(5, 94));
        datas.add(new PointData(6, 45));
        datas.add(new PointData(7, 23));
        datas.add(new PointData(8, 58));
        datas.add(new PointData(9, 44));
        datas.add(new PointData(10, 11));
        return datas;
    }
}
