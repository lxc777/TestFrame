package com.frame.TestFrame.View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.graphics.PathParser;


import com.frame.TestFrame.Bean.ProvinceItem;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class Test_view extends androidx.appcompat.widget.AppCompatImageView {
    private String onclick_name;//点击获取的线路名称
    private int path_num=0;//加载的Path数量
    private int num=1;//变量
    public int ID;//raw资源文件ID
    private int space = 10;//阴影大小
    private Path path = new Path();
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int mImageWidth = 1;//图片长度
    private int mImageHeight = 1;//图片高度
    private int NONE = 0;//默认移动模式
    private int DRAG = 1;
    private int ZOOM = 2;
    private int mode = NONE;
    private String name;//标记名
    private String strokeColor;//轮廓颜色
    private String fillColor;//填充颜色
    private int Color_change;//改变颜色变化
    float left = -1, top = -1, right = -1, bottom = -1;//算出当前绘制的地图的边界，用于缩放地图
    private List<ProvinceItem> provinceItems = new ArrayList<>();//存储省的path
    private Canvas canvas;
    /**
     * 模板Matrix，用以初始化
     */
    private Matrix mMatrix = new Matrix();//初始化模板
    private Matrix mMatrix_save = new Matrix();//保存初始模板
    private int change = 0;//防止频繁重绘变量
    private Bitmap bitmap;//生成画布

    /**
     * @param ID 设置raw资源文件
     */
    public void setID(int ID) {
        this.ID = ID;
        MatrixTouchListener mListener = new MatrixTouchListener();
        setOnTouchListener(mListener);
        readPath(ID);
    }

    public Test_view(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mImageWidth = w;
        mImageHeight = h;

    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        // TODO Auto-generated method stub
        super.setImageBitmap(bm);
        //设置完图片后，获取该图片的坐标变换矩阵
        mMatrix.set(getImageMatrix());
        if (num==1){
            mMatrix_save.set(getImageMatrix());
            num++;
        }
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (provinceItems.size() != path_num) {
            return;
        }
        if (change == -1) {
            mImageWidth = (int) (right + left);
            mImageHeight = (int) (bottom + top);
            bitmap = Bitmap.createBitmap(mImageWidth, mImageHeight, Bitmap.Config.ARGB_8888);
            this.canvas = new Canvas(bitmap);
            custom();
            change = 0;
        }

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * @param raw   从资源文件读取数据
     */
    public void readPath(int raw) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream inputStream = getContext().getResources().openRawResource(raw);
                try {
                    DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document document = builder.parse(inputStream);
                    Element root = document.getDocumentElement();//获取根节点
                    NodeList nodeList = root.getElementsByTagName("path");
                    path_num= nodeList.getLength();
                    for (int i = 0; i <path_num ; i++) {
                        Element element = (Element) nodeList.item(i);//path节点
                        Map<String, Path> map = new HashMap<>();
                        //获取各个省的Path
                        Path provincePath = PathParser.createPathFromPathData(element.getAttribute("android:pathData"));
                        name = element.getAttribute("android:name");
                        strokeColor = element.getAttribute("android:strokeColor");
                        fillColor = element.getAttribute("android:fillColor");
                        ProvinceItem provinceItem = new ProvinceItem();
                        provinceItem.setPath(provincePath);
                        provinceItem.setFillColor(fillColor);
                        provinceItem.setStrokeColor(strokeColor);
                        provinceItem.setName(name);
                        provinceItem.setColor_change(0);
                        provinceItems.add(provinceItem);
                        //获取Path的边界顶点值
                        RectF rectF = new RectF();
                        provincePath.computeBounds(rectF, true);
                        left = left == -1 ? rectF.left : Math.min(rectF.left, left);//取出最小的left
                        right = right == -1 ? rectF.right : Math.max(rectF.right, right);//取出最大的right
                        bottom = bottom == -1 ? rectF.bottom : Math.max(rectF.bottom, bottom);//取出最大的bottom
                        top = top == -1 ? rectF.top : Math.min(rectF.top, top);//取出最小的top
                    }
                    change=-1;
                    invalidate();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    /**
     * 绘制资源，生成Bitmap图像
     */
    public void custom() {
        for (int i = 0; i < provinceItems.size(); i++) {
            if (provinceItems.get(i).getFillColor().length() < 1) {
                paint.setStyle(Paint.Style.STROKE);
            } else {
                paint.setStyle(Paint.Style.FILL);
            }
            path = provinceItems.get(i).getPath();
            name = provinceItems.get(i).getName();
            Color_change = provinceItems.get(i).getColor_change();
            if (Color_change == 0) {
                paint.setColor(0x5503DAC5);
            } else if (Color_change == 1) {
                paint.setColor(Color.BLUE);
            } else if (Color_change == 2) {
                paint.setColor(Color.RED);
            }
            if (name.contains("port")) {
                RectF rectF = new RectF();
                path.computeBounds(rectF, true);
                rectF.bottom += 10;
                rectF.left -= 10;
                rectF.top -= 10;
                rectF.right += 10;
                canvas.drawRect(rectF, paint);
            }
            if (name.contains("line")) {
                paint.setStrokeWidth(20);
                canvas.drawPath(path, paint);
            }
            paint.setStrokeWidth(20);
            canvas.drawPath(path, paint);
            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(2);
            canvas.drawPath(path, paint);
            setImageBitmap(bitmap);
        }
    }


    /**
     * 让点击的线路区别显示
     *
     * @param point
     */
    public void onClickProvince(PointF point) {
        float[] values = new float[9];
        getImageMatrix().getValues(values);
        int x = (int) ((point.x - values[2]) / values[0]);
        int y = (int) ((point.y - values[5]) / values[4]);
        int select = 1;//多选判断
        for (int i = 0; i < provinceItems.size(); i++) {
            RectF rectF = new RectF();
            path = provinceItems.get(i).getPath();
            path.computeBounds(rectF, true);
            rectF.set((int) rectF.left - space, (int) rectF.top - space, (int) rectF.right + space, (int) rectF.bottom + space);
            if (provinceItems.get(i).getColor_change() != 1) {
                if (select == 1) {
                    if (rectF.contains(x, y)) {
                        provinceItems.get(i).setColor_change(1);
                        onclick_name=provinceItems.get(i).getName();
                        change = -1;
                        invalidate();
                        select++;
                    }
                }
            }
        }
    }

    /**
     * @return 获取点击的线路的名称
     */
    public String getOnclick_name() {
        return onclick_name;
    }


    /**
     * 自定义监听
     */
    public class MatrixTouchListener implements OnTouchListener {
        /**
         * 拖拉照片模式
         */
        private static final int MODE_DRAG = 1;
        /**
         * 放大缩小照片模式
         */
        private static final int MODE_ZOOM = 2;
        /**
         * 不支持Matrix
         */
        private static final int MODE_UNABLE = 3;
        /**
         * 最大缩放级别
         */
        float mMaxScale = 2;
        /**
         * 初始化模式
         */
        private int mMode = 0;//
        /**
         * 缩放开始时的手指间距
         */
        private float mStartDis;
        /**
         * 当前Matrix
         */
        private Matrix mCurrentMatrix = new Matrix();

        /**
         * 用于记录开始时候的坐标位置
         */
        private PointF startPoint = new PointF();

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // TODO Auto-generated method stub
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    //设置拖动模式
                    mMode = MODE_DRAG;
                    startPoint.set(event.getX(), event.getY());
                    isMatrixEnable();
                    onClickProvince(startPoint);
                    break;
                case MotionEvent.ACTION_UP:
                    performClick();//防止冲突，使得外部事件监听的可以使用
                    reSetMatrix();
                    break;
                case MotionEvent.ACTION_CANCEL:
                    reSetMatrix();
                    break;
                case MotionEvent.ACTION_MOVE://缩放
                    if (mMode == MODE_ZOOM) {
                        setZoomMatrix(event);
                    } else if (mMode == MODE_DRAG) {
                        setDragMatrix(event);
                    }
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    if (mMode == MODE_UNABLE) return true;
                    mMode = MODE_ZOOM;
                    mStartDis = distance(event);
                    break;
                default:
                    break;
            }

            return true;
        }

        /**
         * @param event 判断位移的距离
         */
        public void setDragMatrix(MotionEvent event) {
            float dx = event.getX() - startPoint.x; // 得到x轴的移动距离
            float dy = event.getY() - startPoint.y; // 得到x轴的移动距离
            //避免和双击冲突,大于10f才算是拖动
            if (Math.sqrt(dx * dx + dy * dy) > 10f) {
                startPoint.set(event.getX(), event.getY());
                //在当前基础上移动
                mCurrentMatrix.set(getImageMatrix());
                float[] values = new float[9];
                mCurrentMatrix.getValues(values);
                mCurrentMatrix.postTranslate(dx, dy);
                setImageMatrix(mCurrentMatrix);
            }
        }



        /**
         * 设置缩放Matrix
         * @param event
         */
        private void setZoomMatrix(MotionEvent event) {
            //只有同时触屏两个点的时候才执行
            if (event.getPointerCount() < 2) return;
            float endDis = distance(event);// 结束距离
            if (endDis > 10f) { // 两个手指并拢在一起的时候像素大于10
                float scale = endDis / mStartDis;// 得到缩放倍数
                mStartDis = endDis;//重置距离
                mCurrentMatrix.set(getImageMatrix());//初始化Matrix
                float[] values = new float[9];
                mCurrentMatrix.getValues(values);
                checkMaxScale(scale, values);
                setImageMatrix(mCurrentMatrix);
            }
        }

        /**
         * 检验scale，使图像缩放后不会超出最大倍数
         *
         * @param scale
         * @param values
         * @return
         */
        private float checkMaxScale(float scale, float[] values) {
            if (scale * values[Matrix.MSCALE_X] > mMaxScale){
                change = -1;
                scale = mMaxScale / values[Matrix.MSCALE_X];
            }
                mCurrentMatrix.postScale(scale, scale, getWidth() / 2, getHeight() / 2);
            return scale;
        }

        /**
         * 重置Matrix
         */
        private void reSetMatrix() {
            if (checkRest()) {
                mCurrentMatrix.set(mMatrix_save);
                setImageMatrix(mCurrentMatrix);
            }
        }

        /**
         * 判断是否需要重置
         *
         * @return 当前缩放级别小于模板缩放级别时，重置
         */
        private boolean checkRest() {
            // TODO Auto-generated method stub
            float[] values = new float[9];
            getImageMatrix().getValues(values);
            //获取当前X轴缩放级别
            float scale = values[Matrix.MSCALE_X];
            //获取模板的X轴缩放级别，两者做比较
             mMatrix_save.getValues(values);
            return scale < values[Matrix.MSCALE_X];
        }

        /**
         * 判断是否支持Matrix
         */
        private void isMatrixEnable() {
            //当加载出错时，不可缩放
            if (getScaleType() != ScaleType.CENTER) {
                setScaleType(ScaleType.MATRIX);
            } else {
                mMode = MODE_UNABLE;//设置为不支持手势
            }
        }

        /**
         * 计算两个手指间的距离
         *
         * @param event
         * @return
         */
        private float distance(MotionEvent event) {
            float dx = event.getX(1) - event.getX(0);
            float dy = event.getY(1) - event.getY(0);
            /** 使用勾股定理返回两点之间的距离 */
            return (float) Math.sqrt(dx * dx + dy * dy);
        }


    }

}
