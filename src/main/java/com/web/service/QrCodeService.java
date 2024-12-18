package com.web.service;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
import com.web.entity.Book;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@Service
public class QrCodeService {

    public byte[] generateQrCodeForBook(Book book) throws WriterException, IOException {
        String data = "ID: " + book.getId() + ", Name: " + book.getName() + ", Publish Year: " + book.getPublishYear();
        BitMatrix bitMatrix = new QRCodeWriter().encode(data, BarcodeFormat.QR_CODE, 300, 300);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "png", outputStream);  // Sử dụng định dạng PNG
        return outputStream.toByteArray();
    }

    public void generateQr(String data, OutputStream outputStream) throws WriterException, IOException {
        BitMatrix bitMatrix = new QRCodeWriter().encode(data, BarcodeFormat.QR_CODE, 300, 300);
        MatrixToImageWriter.writeToStream(bitMatrix, "png", outputStream);  // Sử dụng định dạng PNG
    }

    public String decodeQr(byte[] data) throws IOException, NotFoundException {
        Result result = new MultiFormatReader()
                .decode(new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(
                        ImageIO.read(new ByteArrayInputStream(data))))));
        return result != null ? result.getText() : null;
    }
}
