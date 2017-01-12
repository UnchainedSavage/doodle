package com.midea.fridge.fridgedoodle;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.midea.fridge.fridgedoodle.bean.DoodleInfo;
import com.midea.fridge.fridgedoodle.bean.DrawPenObj;
import com.midea.fridge.fridgedoodle.bean.DrawPenStr;
import com.midea.fridge.fridgedoodle.bean.DrawStep;
import com.midea.fridge.fridgedoodle.bean.Point;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Administrator on 2016/12/30.
 */
public class StoreUtil {
    private static final String TAG = "StoreUtil";
    private static final String CHARSET = "UTF-8";
    private static final String SUFFIX = ".fd";
    private static final String SUFFIX_IMAGE = ".png";

    /**
     * 保存为新文件
     * @param drawView
     */
    public static void saveDoodle(View drawView) {
        String fileName = createRandomName();
        saveDoodle(drawView, fileName);
    }

    /**
     * 保存时指定文件名
     * @param drawView
     * @param fileName
     */
    public static void saveDoodle(View drawView, String fileName) {
        List<DrawStep> drawSteps = new ArrayList<>();
        // 保存时无需保存DrawPenObj
        for(DrawStep drawStep: DrawHelper.getInstance().getDrawStepSet().getSaveSteps()) {
            DrawStep tempDrawStep = new DrawStep();
            tempDrawStep.setDrawPenStr(drawStep.getDrawPenStr());
            tempDrawStep.setType(drawStep.getType());
            drawSteps.add(tempDrawStep);
        }
        String str = new Gson().toJson(drawSteps);
        saveImage(drawView, getImageFilePath(fileName));
        write(str, getFilePath(fileName));
    }

    public static List<DoodleInfo> loadDoodleImages() {
        List<DoodleInfo> result = new ArrayList<>();
        File folder = new File(StoreUtil.getDirPath());
        if (!folder.exists()) {
            folder.mkdirs();
        }
        final File[] files = folder.listFiles();
        if (files.length > 0) {
            for (File f : files) {
                if(f.getName().endsWith(SUFFIX_IMAGE)) {
                    String name = f.getName().substring(0, f.getName().indexOf(SUFFIX_IMAGE));
                    Log.d(TAG, "name:" + name);
                    File saveFile = new File(getFilePath(name));
                    if(saveFile.exists()) {
                        DoodleInfo doodleInfo = new DoodleInfo();
                        doodleInfo.setName(name);
                        doodleInfo.setSavePath(saveFile.getAbsolutePath());
                        doodleInfo.setImagePath(f.getAbsolutePath());
                        doodleInfo.setTimestamp(saveFile.lastModified());
                        result.add(0, doodleInfo);
                    }
                }
            }
        }
        return result;
    }

    /**
     * 生成保存文件路径
     */
    public static String getFilePath(String fileName) {
        return getDirPath() + File.separator + fileName + SUFFIX;
    }

    public static String getImageFilePath(String fileName) {
        return getDirPath() + File.separator + fileName + SUFFIX_IMAGE;
    }

    /**
     * 获取保存目录路径
     */
    public static String getDirPath() {
        return Environment.getExternalStorageDirectory() + File.separator + "midea/fridgedoodle";
    }

    /**
     * 保存内容到文件中
     */
    public static void write(String strWb, String path) {
        if (TextUtils.isEmpty(strWb) || TextUtils.isEmpty(path)) {
            Log.d(TAG, "Trying to save null or 0 length strWb or path");
            return;
        }
        File toFile = new File(path);
        if (!toFile.getParentFile().exists()) {
            toFile.getParentFile().mkdirs();
        }
        if (toFile.exists()) {
            toFile.delete();
        }
        try {
            toFile.createNewFile();
        } catch (IOException e) {
            Log.e(TAG, "IOException：" + e.getMessage());
            toFile = null;
        } finally {
            if (null != toFile && null != strWb) {
                OutputStream outStream = null;
                try {
                    outStream = new FileOutputStream(toFile);
                } catch (FileNotFoundException e) {
                    Log.d(TAG, "FileNotFoundException：" + e.getMessage());
                    outStream = null;
                } finally {
                    if (null != outStream) {
                        try {
                            outStream.write(strWb.getBytes("utf-8"));
                            outStream.flush();
                        } catch (IOException e) {
                            Log.e(TAG, "IOException：" + e.getMessage());
                        } finally {
                            try {
                                if (null != outStream) {
                                    outStream.close();
                                }
                            } catch (IOException e) {
                                Log.d(TAG, "IOException" + e.getMessage());
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 加载文件内容
     */
    public static String read(String wbPath) {
        File file = new File(wbPath);
        if (file.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                int len = fis.available();
                if (len > 0) {
                    byte[] buf = new byte[len];
                    fis.read(buf);
                    String string = new String(buf, CHARSET);
                    return string;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            } finally {
                if (fis != null)
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        }
        return null;
    }

    /**
     * 保存当前白板为图片
     */
    public static void saveImage(View drawView, String path) {
        File file = new File(path);
        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            file.createNewFile();
            FileOutputStream out = new FileOutputStream(file);
            drawView.setDrawingCacheEnabled(true);
            drawView.buildDrawingCache();
            Bitmap bitmap = drawView.getDrawingCache();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            drawView.destroyDrawingCache();
        } catch (Exception e) {
            Log.w(TAG, "saveImage Exception");
        }
    }

    /**
     * 转化DrawPenStr到DrawPenObj
     * @param drawPenStr
     * @return
     */
    public static DrawPenObj convertDrawPenObj(DrawPenStr drawPenStr) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);//是否使用抗锯齿功能,会消耗较大资源，绘制图形速度会变慢
        paint.setDither(true);// 设定是否使用图像抖动处理，会使绘制出来的图片颜色更加平滑和饱满，图像更加清晰
        paint.setColor(drawPenStr.getColor());//设置绘制的颜色
        paint.setStyle(Paint.Style.STROKE);//设置画笔的样式
        paint.setStrokeJoin(Paint.Join.ROUND);//设置绘制时各图形的结合方式，如平滑效果等
        paint.setStrokeCap(Paint.Cap.ROUND);//当画笔样式为STROKE或FILL_OR_STROKE时，设置笔刷的图形样式，如圆形样式    Cap.ROUND,或方形样式Cap.SQUARE
        paint.setStrokeWidth(drawPenStr.getStrokeWidth());//当画笔样式为STROKE或FILL_OR_STROKE时，设置笔刷的粗细度

        Path path = new Path();
        path.moveTo(drawPenStr.getMoveTo().getX(), drawPenStr.getMoveTo().getY());
        for (int i = 0; i < drawPenStr.getQuadToA().size(); i++) {
            Point pointA = drawPenStr.getQuadToA().get(i);
            Point pointB = drawPenStr.getQuadToB().get(i);
            path.quadTo(pointA.getX(), pointA.getY(), pointB.getX(), pointB.getY());
        }
        path.lineTo(drawPenStr.getLineTo().getX(), drawPenStr.getLineTo().getY());

        DrawPenObj drawPenObj = new DrawPenObj();
        drawPenObj.setPaint(paint);
        drawPenObj.setPath(path);
        return drawPenObj;
    }

    private static String createRandomName() {
        return UUID.randomUUID().toString();
    }
}
