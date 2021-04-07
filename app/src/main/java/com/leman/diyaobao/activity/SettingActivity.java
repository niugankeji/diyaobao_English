package com.leman.diyaobao.activity;


import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.leman.diyaobao.Constant;
import com.leman.diyaobao.R;
import com.leman.diyaobao.myview.CheckBoxSample;
import com.leman.diyaobao.numpicker.NumPicker;
import com.leman.diyaobao.utils.SPUtils;
import com.warkiz.widget.IndicatorSeekBar;

/**
 * 参数设置
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener {

    private TextView title;
    private TextView rTitle;
    private LinearLayout back;

    private CheckBoxSample saturation;//饱和度
    private CheckBoxSample band;//波段
    private CheckBoxSample brightness;//亮度值
    private CheckBoxSample greenIndex;//绿度指数

    private IndicatorSeekBar seekbar;

    private int compression = 90;//压缩质量
    private String quadrat = "北师大";//样方
    private double leafArea = 0.5;//叶面积投影比
    private double dataValues = 2;//数据值
    private int FeatureMode = 2;//记录4个选项

    private EditText quadratEt;
    private EditText leafAreaEt;
    private EditText dataValuesEt;

    private LinearLayout number;
    private TextView select_number;

    @Override
    public int intiLayout() {
        return R.layout.activity_setting;
    }

    @Override
    public void initView() {
        title = findViewById(R.id.title);
        title.setText("Parameter setting");
        rTitle = findViewById(R.id.rTitle);
        rTitle.setText("OK");
        rTitle.setOnClickListener(this);
        rTitle.setVisibility(View.VISIBLE);
        back = findViewById(R.id.back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);

        seekbar = findViewById(R.id.seekbar);

        saturation = findViewById(R.id.saturation);
        saturation.setOnClickListener(this);
        band = findViewById(R.id.band);
        band.setOnClickListener(this);
        brightness = findViewById(R.id.brightness);
        brightness.setOnClickListener(this);
        greenIndex = findViewById(R.id.greenIndex);
        greenIndex.setOnClickListener(this);

        quadratEt = findViewById(R.id.quadratEt);
        leafAreaEt = findViewById(R.id.leafAreaEt);
        dataValuesEt = findViewById(R.id.dataValuesEt);


        compression = SPUtils.getInt(Constant.COMPRESSION, compression);
        FeatureMode = SPUtils.getInt(Constant.FEATUREMODE, FeatureMode);
        quadrat = SPUtils.getString(Constant.QUADRAT, "JanKin");
        leafArea = SPUtils.getFloat(Constant.LEAFAREA, 0.5f);
        dataValues = SPUtils.getFloat(Constant.DATAVALUES, (float) 2);


        seekbar.setProgress(compression);
        quadratEt.setText(quadrat);
        leafAreaEt.setText("" + leafArea);
        if (dataValues < 0) {
            dataValuesEt.setText("");
        } else {
            dataValuesEt.setText("" + dataValues);
        }
        switch (FeatureMode) {
            case 1:
                saturation.setChecked(true);
                break;
            case 2:
                band.setChecked(true);
                break;
            case 3:
                brightness.setChecked(true);
                break;
            case 4:
                greenIndex.setChecked(true);
                break;
            default:

                break;

        }
        final NumPicker np = new NumPicker(this);
        np.setOnCancelListener(new NumPicker.OnCancelClickListener() {
            @Override
            public void onClick() {
                np.dismiss();
            }
        });
        np.setOnComfirmListener(new NumPicker.onComfirmClickListener() {
            @Override
            public void onClick(int num) {
                select_number.setText(num+1+"");
                np.dismiss();
            }
        });
        select_number = findViewById(R.id.select_number);
        number = findViewById(R.id.number);
        number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                np.show();
            }
        });


    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //确定保存参数设置
            case R.id.rTitle:
                compression = seekbar.getProgress();
                quadrat = quadratEt.getText().toString();
                leafArea = Double.valueOf(leafAreaEt.getText().toString());
                if (leafArea <= 0)
                    leafArea = Double.MIN_VALUE;
                if (dataValuesEt.getText().toString().equals("")) {
                    dataValues = -1;
                } else {
                    dataValues = Double.parseDouble(dataValuesEt.getText().toString());
                }

                SPUtils.putInt(Constant.COMPRESSION, compression);
                SPUtils.putString(Constant.QUADRAT, quadrat);
                SPUtils.putFloat(Constant.LEAFAREA, (float) leafArea);
                SPUtils.putFloat(Constant.DATAVALUES, (float) dataValues);
                SPUtils.putInt(Constant.FEATUREMODE, FeatureMode);
                SPUtils.putInt(Constant.SELECTNUMBER,Integer.parseInt(select_number.getText().toString()));

                finish();

                break;
            case R.id.back:
                finish();
                break;
            //饱和度
            case R.id.saturation:
                FeatureMode = 1;
                saturation.toggle();
                band.setChecked(false);
                brightness.setChecked(false);
                greenIndex.setChecked(false);
                break;
            //波段
            case R.id.band:
                FeatureMode = 2;
                band.toggle();
                saturation.setChecked(false);
                brightness.setChecked(false);
                greenIndex.setChecked(false);
                break;
            //亮度值
            case R.id.brightness:
                FeatureMode = 3;
                brightness.toggle();
                saturation.setChecked(false);
                band.setChecked(false);
                greenIndex.setChecked(false);
                break;
            //绿度指数
            case R.id.greenIndex:
                FeatureMode = 4;
                greenIndex.toggle();
                saturation.setChecked(false);
                band.setChecked(false);
                brightness.setChecked(false);
                break;
        }
    }
}
