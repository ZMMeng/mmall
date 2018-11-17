package com.mmall.util;

import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

/**
 * 验证码生成类
 * Created by 蒙卓明 on 2018/11/15
 */
public class VerifyCodeUtil {

    private static final String DEFAULT_VERIFY_CODE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static Random random = new Random();

    /**
     * 使用指定源生成指定长度的验证码文本
     *
     * @param size   验证码长度
     * @param source 所用源
     * @return
     */
    public static String getVerifyCodeText(int size, String source) {

        if (StringUtils.isBlank(source)) {
            source = DEFAULT_VERIFY_CODE;
        }
        StringBuilder textBuilder = new StringBuilder();
        random = new Random(System.currentTimeMillis());
        for (int i = 0; i < size; i++) {
            textBuilder.append(source.charAt(random.nextInt(source.length() - 1)));
        }
        return textBuilder.toString();
    }

    /**
     * 使用默认源生成指定长度的验证码文本
     *
     * @param size 验证码长度
     * @return
     */
    public static String getVerifyCodeText(int size) {
        return getVerifyCodeText(size, DEFAULT_VERIFY_CODE);
    }

    /**
     * 将验证码输出到指定输出流中
     *
     * @param width          宽度
     * @param height         高度
     * @param os             输出流
     * @param verifyCodeText 验证码文本
     * @throws IOException
     */
    public static void outputImage(int width, int height, OutputStream os, String verifyCodeText)
            throws IOException {

        //在内存中创建图像
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        //获取图形上下文
        Graphics2D g2 = image.createGraphics();
        //消除图形锯齿状功能
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        //绘制边框颜色
        g2.setColor(Color.GRAY);
        //绘制矩形背景
        g2.fillRect(0, 0, width, height);

        //设置背景色
        Color color = getRandColor(200, 250);
        g2.setColor(color);
        g2.fillRect(0, 2, width, height - 4);

        //绘制干扰线
        drawInterferingLines(g2, width, height, getRandColor(160, 200), 20);

        //添加噪点
        addYawp(image, 0.05f);

        // 使图片扭曲
        shear(g2, width, height, color);

        //设置字体
        g2.setColor(getRandColor(100, 160));
        int fontSize = height - 4;
        Font font = new Font("Algerian", Font.ITALIC, fontSize);
        g2.setFont(font);

        //写入验证码
        char[] chars = verifyCodeText.toCharArray();
        for (int i = 0; i < verifyCodeText.length(); i++) {
            AffineTransform affine = new AffineTransform();
            //将每个字符旋转，角度，中心坐标
            affine.setToRotation(Math.PI / 4 * random.nextDouble() * (random.nextBoolean() ? 1 : -1),
                    (width / verifyCodeText.length()) * i + fontSize / 2, height / 2);
            g2.setTransform(affine);
            g2.drawChars(chars, i, 1, ((width - 10) / verifyCodeText.length()) * i + 5,
                    height / 2 + fontSize / 2 - 10);
        }

        g2.dispose();
        ImageIO.write(image, "jpg", os);
    }

    /**
     * 将验证码写入到指定文件中
     *
     * @param width          宽度
     * @param height         高度
     * @param outputFile     输出文件
     * @param verifyCodeText 验证码文本
     * @throws IOException
     */
    public static void outputImage(int width, int height, File outputFile, String verifyCodeText)
            throws IOException {
        if (outputFile == null) {
            return;
        }
        File dir = outputFile.getParentFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            outputFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(outputFile);
            outputImage(width, height, fos, verifyCodeText);
            fos.close();
        } catch (IOException e) {
            throw e;
        }
    }

    /**
     * 使图片扭曲
     *
     * @param g      图片
     * @param width  宽度
     * @param height 高度
     * @param color  颜色
     */
    private static void shear(Graphics g, int width, int height, Color color) {
        shearX(g, width, height, color);
        shearY(g, width, height, color);
    }

    /**
     * 在X轴方向扭曲
     *
     * @param g      图形
     * @param width  宽度
     * @param height 高度
     * @param color  颜色
     */
    private static void shearX(Graphics g, int width, int height, Color color) {

        int period = random.nextInt(2);// 0 1

        boolean borderGap = true;
        int frames = 1;
        int phase = random.nextInt(2);// 0 1

        for (int i = 0; i < height; i++) {
            double d = (double) (period >> 1)
                    //当period等于0时，整数 0 / 0会抛异常
                    //但是将period转double后，就变为0.0
                    //0.0 / 0.0 结果就是NaN
                    * Math.sin((double) i / (double) period
                    + (2 * Math.PI * (double) phase)
                    / (double) frames);
            g.copyArea(0, i, width, 1, (int) d, 0);
            if (borderGap) {
                g.setColor(color);
                g.drawLine((int) d, i, 0, i);
                g.drawLine((int) d + width, i, height, i);
            }
        }

    }

    /**
     * 在Y轴方向扭曲
     *
     * @param g      图形
     * @param width  宽度
     * @param height 高度
     * @param color  颜色
     */
    private static void shearY(Graphics g, int width, int height, Color color) {

        int period = random.nextInt(40) + 10; // 50;

        boolean borderGap = true;
        int frames = 20;
        int phase = 7;
        for (int i = 0; i < width; i++) {
            double d = (double) (period >> 1)
                    * Math.sin((double) i / (double) period
                    + (2 * Math.PI * (double) phase)
                    / (double) frames);
            g.copyArea(i, 0, 1, height, 0, (int) d);
            if (borderGap) {
                g.setColor(color);
                g.drawLine(i, (int) d, i, 0);
                g.drawLine(i, (int) d + height, i, height);
            }
        }
    }

    /**
     * 添加噪点
     *
     * @param image 图片
     * @param yawp  噪声率
     */
    private static void addYawp(BufferedImage image, float yawp) {
        int area = (int) (image.getWidth() * image.getHeight() * yawp);
        for (int i = 0; i < area; i++) {
            int x = random.nextInt(image.getWidth());
            int y = random.nextInt(image.getHeight());
            //获取随机的RGB值
            int rgb = getRandomRgb();
            image.setRGB(x, y, rgb);
        }
    }

    /**
     * 获取随机的RGB值
     *
     * @return
     */
    private static int getRandomRgb() {

        int[] rgb = new int[]{
                random.nextInt(255),
                random.nextInt(255),
                random.nextInt(255),
        };
        int color = 0;
        for (int c : rgb) {
            color = color << 8;
            color = color | c;
        }
        return color;
    }

    /**
     * 绘制干扰线
     *
     * @param g      图片上下文
     * @param width  宽
     * @param height 高
     * @param color  干扰线颜色
     * @param num    干扰线数量
     */
    private static void drawInterferingLines(Graphics g, int width, int height, Color color, int num) {
        g.setColor(color);
        for (int i = 0; i < num; i++) {
            int x = random.nextInt(width - 1);
            int y = random.nextInt(height - 1);
            int x1 = random.nextInt(12);
            int y1 = random.nextInt(12);
            g.drawLine(x, y, x1, y1);
        }
    }


    /**
     * 获取指定亮度范围内的随机色
     *
     * @param fc 亮度最小值？
     * @param bc 亮度最大值？
     * @return
     */
    private static Color getRandColor(int fc, int bc) {
        if (fc > 255) {
            fc = 255;
        }
        if (bc > 255) {
            bc = 255;
        }
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }
}
