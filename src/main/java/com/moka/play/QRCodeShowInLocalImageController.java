package com.moka.play;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Title: QRCodeShowInLocalImageController.java
 * @Project: QR Code在本地生成二维码图片
 * @Description: 二维码
 * @Version: V2.0
 **/
public class QRCodeShowInLocalImageController {

        public static void main(String[] args) {
            try {
                QREncode();

                QRReader(new File("D:\\Personal\\Pictures\\zxing1.gif"));
            } catch (WriterException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NotFoundException e) {
                e.printStackTrace();
            }
        }

        /**
         * 生成二维码
         */
        public static void QREncode() throws WriterException, IOException {
            String content = "大家好我是来自中国的最靓的仔，我叫panda";//二维码内容
            int width = 200; // 图像宽度
            int height = 200; // 图像高度
            String format = "gif";// 图像类型
            Map<EncodeHintType, Object> hints = new HashMap<>();
            //内容编码格式
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            // 指定纠错等级
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            //设置二维码边的空度，非负数
            hints.put(EncodeHintType.MARGIN, 1);
            BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
            MatrixToImageWriter.writeToPath(bitMatrix, format, new File("D:\\Personal\\Pictures\\zxing.gif").toPath());// 输出原图片
            MatrixToImageConfig matrixToImageConfig = new MatrixToImageConfig(0xFF000001, 0xFFFFFFFF);
        /*
            问题：生成二维码正常,生成带logo的二维码logo变成黑白
            原因：MatrixToImageConfig默认黑白，需要设置BLACK、WHITE
            解决：https://ququjioulai.iteye.com/blog/2254382
         */
            BufferedImage bufferedImage = LogoMatrix(MatrixToImageWriter.toBufferedImage(bitMatrix,matrixToImageConfig), new File("D:\\Personal\\Pictures\\火苗.jpg"));
//        BufferedImage bufferedImage = LogoMatrix(toBufferedImage(bitMatrix), new File("D:\\logo.png"));
            ImageIO.write(bufferedImage, "gif", new File("D:\\Personal\\Pictures\\zxing1.gif"));//输出带logo图片
            System.out.println("输出成功.");
        }

        /**
         * 识别二维码
         */
        public static void QRReader(File file) throws IOException, NotFoundException {
            MultiFormatReader formatReader = new MultiFormatReader();
            //读取指定的二维码文件
            BufferedImage bufferedImage =ImageIO.read(file);
            BinaryBitmap binaryBitmap= new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(bufferedImage)));
            //定义二维码参数
            Map  hints= new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            com.google.zxing.Result result = formatReader.decode(binaryBitmap, hints);
            //输出相关的二维码信息
            System.out.println("解析结果："+result.toString());
            System.out.println("二维码格式类型："+result.getBarcodeFormat());
            System.out.println("二维码文本内容："+result.getText());
            bufferedImage.flush();
        }

        /**
         * 二维码添加logo
         * @param matrixImage 源二维码图片
         * @param logoFile logo图片
         * @return 返回带有logo的二维码图片
         * 参考：https://blog.csdn.net/weixin_39494923/article/details/79058799
         */
        public static BufferedImage LogoMatrix(BufferedImage matrixImage, File logoFile) throws IOException{
            /**
             * 读取二维码图片，并构建绘图对象
             */
            Graphics2D g2 = matrixImage.createGraphics();

            int matrixWidth = matrixImage.getWidth();
            int matrixHeigh = matrixImage.getHeight();

            /**
             * 读取Logo图片
             */
            BufferedImage logo = ImageIO.read(logoFile);

            //开始绘制图片
            g2.drawImage(logo,matrixWidth/5*2,matrixHeigh/5*2, matrixWidth/5, matrixHeigh/5, null);//绘制
            BasicStroke stroke = new BasicStroke(5,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND);
            g2.setStroke(stroke);// 设置笔画对象
            //指定弧度的圆角矩形
            RoundRectangle2D.Float round = new RoundRectangle2D.Float(matrixWidth/5*2, matrixHeigh/5*2, matrixWidth/5, matrixHeigh/5,20,20);
            g2.setColor(Color.white);
            g2.draw(round);// 绘制圆弧矩形

            //设置logo 有一道灰色边框
            BasicStroke stroke2 = new BasicStroke(1,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND);
            g2.setStroke(stroke2);// 设置笔画对象
            RoundRectangle2D.Float round2 = new RoundRectangle2D.Float(matrixWidth/5*2+2, matrixHeigh/5*2+2, matrixWidth/5-4, matrixHeigh/5-4,20,20);
            g2.setColor(new Color(128,128,128));
            g2.draw(round2);// 绘制圆弧矩形

            g2.dispose();
            matrixImage.flush() ;
            return matrixImage ;
        }



}//class
