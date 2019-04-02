package com.safesoft.uk2015.restopro;

import android.graphics.Bitmap;
import android.util.Xml;

import org.firebirdsql.encodings.Encoding_Cp864;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.BitSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by UK2015 on 08/08/2016.
 */
public class Pos {
    private final byte[] INITIALIZE_PRINTER = new byte[]{0x1B,0x40};

    private final byte[] PRINT_AND_FEED_PAPER = new byte[]{0x0A};
    private final byte[] CUT_PAPER_DATA = new byte[]{0x1D,86,65,100};

    private final byte[] SELECT_BIT_IMAGE_MODE = new byte[]{(byte)0x1B, (byte)0x2A};
    private final byte[] SET_LINE_SPACING = new byte[]{0x1B, 0x33};


    private static String encoding = null;

    private Socket sock = null;
    // 通过socket流进行读写
    private OutputStream socketOut = null;
    private OutputStreamWriter writer = null;

    private OutputStream printOutput;
    /**
     * 初始化Pos实例
     *
     * @param ip 打印机IP
     * @param port  打印机端口号
     * @param encoding  编码
     * @throws IOException
     */
    public Pos(String ip, int port, String encoding) throws IOException {
        sock = new Socket(ip, port);
        socketOut = new DataOutputStream(sock.getOutputStream());
        this.encoding = encoding;
        writer = new OutputStreamWriter(socketOut, encoding);
        printOutput = sock.getOutputStream();
    }

    /**
     * 关闭IO流和Socket
     *
     * @throws IOException
     */
    public void closeIOAndSocket() throws IOException {
        writer.close();
        printOutput.close();
        socketOut.close();
        sock.close();
    }

    /**
     * 打印二维码
     *
     * @param qrData 二维码的内容
     * @throws IOException
     */
    public void qrCode(String qrData) throws IOException {
        int moduleSize = 8;
        int length = qrData.getBytes(encoding).length;

        //打印二维码矩阵
        writer.write(0x1D);// init
        writer.write("(k");// adjust height of barcode
        writer.write(length + 3); // pl
        writer.write(0); // ph
        writer.write(49); // cn
        writer.write(80); // fn
        writer.write(48); //
        writer.write(qrData);

        writer.write(0x1D);
        writer.write("(k");
        writer.write(3);
        writer.write(0);
        writer.write(49);
        writer.write(69);
        writer.write(48);

        writer.write(0x1D);
        writer.write("(k");
        writer.write(3);
        writer.write(0);
        writer.write(49);
        writer.write(67);
        writer.write(moduleSize);

        writer.write(0x1D);
        writer.write("(k");
        writer.write(3); // pl
        writer.write(0); // ph
        writer.write(49); // cn
        writer.write(81); // fn
        writer.write(48); // m

        writer.flush();

    }

    /**
     * 进纸并全部切割
     *
     * @return
     * @throws IOException
     */
    public void feedAndCut() throws IOException {
        writer.write(0x1D);
        writer.write(86);
        writer.write(65);
        //        writer.write(0);
        //切纸前走纸多少
        writer.write(100);
        writer.flush();

    }

    public void cutPaper2() throws IOException{
        //另外一种切纸的方式
        try{
            byte[] bytes = {29, 86, 0};
            socketOut.write(bytes);
        }catch (Exception e){

        }
    }
    /**
     * 打印换行
     *
     * @return length 需要打印的空行数
     * @throws IOException
     */
    public void printLine(int lineNum) throws IOException {
        for (int i = 0; i < lineNum; i++) {
            writer.write("\n");
        }
        writer.flush();
    }

    /**
     * 打印换行(只换一行)
     *
     * @throws IOException
     */
    protected void printLine() throws IOException {
        writer.write("\n");
        writer.flush();
    }

    /**
     * 打印空白(一个Tab的位置，约4个汉字)
     *
     * @param length 需要打印空白的长度,
     * @throws IOException
     */
    public void printTabSpace(int length) throws IOException {
        for (int i = 0; i < length; i++) {
            writer.write("\t");
        }
        writer.flush();
    }

    /**
     * 打印空白（一个汉字的位置）
     *
     * @param length 需要打印空白的长度,
     * @throws IOException
     */
    public void printWordSpace(int length) throws IOException {
        for (int i = 0; i < length; i++) {
            writer.write("  ");
        }
        writer.flush();
    }

    /**
     * 打印位置调整
     *
     * @param position 打印位置  0：居左(默认) 1：居中 2：居右
     * @throws IOException
     */
    public void printLocation(int position) throws IOException {
        writer.write(0x1B);
        writer.write(97);
        writer.write(position);
        writer.flush();
    }

    /**
     * 绝对打印位置
     *
     * @throws IOException
     */
    public void printLocation(int light, int weight) throws IOException {
        writer.write(0x1B);
        writer.write(0x24);
        writer.write(light);
        writer.write(weight);
        writer.flush();
    }


    /**
     * 打印文字
     *
     * @param text
     * @throws IOException
     */
    public void printText(String text) throws IOException {
        String s = text;
        byte[] content = s.getBytes("gbk");
        socketOut.write(content);
        socketOut.flush();
    }

    /**
     * 新起一行，打印文字
     *
     * @param text
     * @throws IOException
     */
    public void printTextNewLine(String text) throws IOException {
        //换行
        writer.write("\n");
        writer.flush();

        String s = text;
        byte[] content = s.getBytes("iso-8859-6");
        socketOut.write(content);
        socketOut.flush();
    }

    /**
     * 初始化打印机
     *
     * @throws IOException
     */
    public void initPos() throws IOException {
        writer.write(0x1B);
        writer.write(0x40);
        writer.flush();
    }

    public void setArabicCodePage(int c_page) throws IOException {
        writer.write(0x1B);
        writer.write(0x74);
        writer.write(Integer.toString(c_page, 16));
        writer.flush();
    }


    public void printTestArabic(String ddddd, String chzrsetnzme) throws IOException {
        String s = "الحمد لله";
        s = ddddd;
        
        byte[] content = s.getBytes(chzrsetnzme);
        socketOut.write(content);
        socketOut.flush();
    }
    /**
     * 加粗
     *
     * @param flag false为不加粗
     * @return
     * @throws IOException
     */
    public void bold(boolean flag) throws IOException {

        if (flag) {
            //常规粗细
            writer.write(0x1B);
            writer.write(69);
            writer.write(0xF);
            writer.flush();
        } else {
            //加粗
            writer.write(0x1B);
            writer.write(69);
            writer.write(0);
            writer.flush();
        }
    }


    public void beep() throws IOException {
            writer.write(0x1B);
            writer.write(0x1D);
            writer.write(07);
            writer.write(2);
            writer.write(20);
            writer.write(10);
            writer.flush();
    }

    private byte[] buildPOSCommand(byte[] command, byte... args) {
        byte[] posCommand = new byte[command.length + args.length];

        System.arraycopy(command, 0, posCommand, 0, command.length);
        System.arraycopy(args, 0, posCommand, command.length, args.length);

        return posCommand;
    }

    private BitSet getBitsImageData(Bitmap image) {
        int threshold = 127;
        int index = 0;
        int dimenssions = image.getWidth() * image.getHeight();
        BitSet imageBitsData = new BitSet(dimenssions);

        for (int y = 0; y < image.getHeight(); y++)
        {
            for (int x = 0; x < image.getWidth(); x++)
            {

                int color = image.getPixel(x, y);
                int  red = (color & 0x00ff0000) >> 16;
                int  green = (color & 0x0000ff00) >> 8;
                int  blue = color & 0x000000ff;
                int luminance = (int)(red * 0.3 + green * 0.59 + blue * 0.11);
                //dots[index] = (luminance < threshold);
                imageBitsData.set(index, (luminance < threshold));
                index++;
            }
        }

        return imageBitsData;
    }

    public void printImage(Bitmap image) {
        try {
            BitSet imageBits = getBitsImageData(image);

            byte widthLSB = (byte)(image.getWidth() & 0xFF);
            byte widthMSB = (byte)((image.getWidth() >> 8) & 0xFF);

            // COMMANDS
            byte[] selectBitImageModeCommand = buildPOSCommand(SELECT_BIT_IMAGE_MODE, (byte) 33, widthLSB, widthMSB);
            byte[] setLineSpacing24Dots = buildPOSCommand(SET_LINE_SPACING, (byte) 24);
            byte[] setLineSpacing30Dots = buildPOSCommand(SET_LINE_SPACING, (byte) 30);


            printOutput.write(INITIALIZE_PRINTER);
            printOutput.write(setLineSpacing24Dots);

            int offset = 0;
            while (offset < image.getHeight()) {
                printOutput.write(selectBitImageModeCommand);

                int imageDataLineIndex = 0;
                byte[] imageDataLine = new byte[3 * image.getWidth()];

                for (int x = 0; x < image.getWidth(); ++x) {

                    // Remember, 24 dots = 24 bits = 3 bytes.
                    // The 'k' variable keeps track of which of those
                    // three bytes that we're currently scribbling into.
                    for (int k = 0; k < 3; ++k) {
                        byte slice = 0;

                        // A byte is 8 bits. The 'b' variable keeps track
                        // of which bit in the byte we're recording.
                        for (int b = 0; b < 8; ++b) {
                            // Calculate the y position that we're currently
                            // trying to draw. We take our offset, divide it
                            // by 8 so we're talking about the y offset in
                            // terms of bytes, add our current 'k' byte
                            // offset to that, multiple by 8 to get it in terms
                            // of bits again, and add our bit offset to it.
                            int y = (((offset / 8) + k) * 8) + b;

                            // Calculate the location of the pixel we want in the bit array.
                            // It'll be at (y * width) + x.
                            int i = (y * image.getWidth()) + x;

                            // If the image (or this stripe of the image)
                            // is shorter than 24 dots, pad with zero.
                            boolean v = false;
                            if (i < imageBits.length()) {
                                v = imageBits.get(i);
                            }
                            // Finally, store our bit in the byte that we're currently
                            // scribbling to. Our current 'b' is actually the exact
                            // opposite of where we want it to be in the byte, so
                            // subtract it from 7, shift our bit into place in a temp
                            // byte, and OR it with the target byte to get it into there.
                            slice |= (byte) ((v ? 1 : 0) << (7 - b));
                        }

                        imageDataLine[imageDataLineIndex + k] = slice;

                        // Phew! Write the damn byte to the buffer
                        //printOutput.write(slice);
                    }

                    imageDataLineIndex += 3;
                }

                printOutput.write(imageDataLine);
                offset += 24;
                printOutput.write(PRINT_AND_FEED_PAPER);

            }

            // printOutput.write(CUT_PAPER_DATA);
            printOutput.write(setLineSpacing30Dots);

        } catch (IOException ex) {
            Logger.getLogger(Pos.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }
}
