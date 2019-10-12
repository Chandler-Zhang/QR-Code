package com.moka.play;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Hashtable;

/**
 * @Title: QRCodeShowInWebController.java
 * @Project: QR Code
 * @Description: 在网页中显示二维码  +  在本地生成二维码的另一种方法
 * @Version: V2.0
 **/
@Controller
public class QRCodeShowInWebController {

    @GetMapping("/qrcode")
    public String qrcode() {
        return "/qrcode";
    }

    @GetMapping(value="/qrimage")
    public ResponseEntity<byte[]> getQRImage() {

        //二维码内的信息
        String info = "我叫 QRCode二维码";

        byte[] qrcode = null;
        try {
            qrcode = QRCodeShowInWebController.getQRCodeImageToWeb(info, 360, 360);
        } catch (WriterException e) {
            System.out.println("Could not generate QR Code, WriterException :: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Could not generate QR Code, IOException :: " + e.getMessage());
        }

        // Set headers
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);

        return new ResponseEntity<byte[]> (qrcode, headers, HttpStatus.CREATED);
    }

    //二维码生成位置
    private static final String QR_CODE_IMAGE_PATH = "D:\\Personal\\Pictures\\QR Code.gif";

    //将二维码保存为本地图片
    private static void generateQRCodeImage(String text, int width, int height, String filePath) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(new String(text.getBytes("UTF-8"), "ISO-8859-1"), BarcodeFormat.QR_CODE, width, height);
        Path path = FileSystems.getDefault().getPath(filePath);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
    }


    public static void main(String[] args) {
        try {
            generateQRCodeImage("我叫 QRCode二维码", 350, 350, QR_CODE_IMAGE_PATH);
        } catch (WriterException e) {
            System.out.println("Could not generate QR Code, WriterException :: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Could not generate QR Code, IOException :: " + e.getMessage());
        }

    }

    //将二维码保存为字节数组，将字节数组在web页面展示为图片形式
    public static byte[] getQRCodeImageToWeb(String text, int width, int height) throws WriterException, IOException {
        // 编码格式
        String CHARSET = "utf-8";
            Hashtable hints = new Hashtable();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.CHARACTER_SET, CHARSET);
            hints.put(EncodeHintType.MARGIN, 1);
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);
            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            byte[] pngData = pngOutputStream.toByteArray();
            return pngData;
    }



}
