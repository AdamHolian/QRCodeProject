/*
 * Quick response code generator and reader
 * 
 * @author IC
 * @version 1.0.0
 */
package main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.speech.Central;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;
import javax.speech.synthesis.Voice;
import javax.swing.JTextField;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class QRCode {
	static final int ROOM_TEXT_LENGTH = 5;
	static JTextField textField = null;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String[] args) throws WriterException, IOException, NotFoundException {
		// Initial hardcoded data for test program 
		String qrCodeData = "Day: Tuesday\nTime: 09:00 to 11:00\nSubject: Software Engineering\nRoom: E2004";
		String filePath = "myQRCode.png";
		String charset = "UTF-8"; // or "ISO-8859-1"
		
		Map hintMap = new HashMap();
		hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

		createQRCode(qrCodeData, filePath, charset, hintMap, 200, 200);
		System.out.println("QR Code image created successfully!");
		
	    String myQRCode = readQRCode(filePath, charset, hintMap);
	    
	    String myRoom = myQRCode.substring(myQRCode.length() - ROOM_TEXT_LENGTH);

		System.out.println("Data read from QR Code:\n" + myRoom);
		
		
	}

	/*
	 * void createQRCode()
	 * Description: This method writes out the passes qrCodeData string to the
	 * file specified in the current directory. 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void createQRCode(String qrCodeData, String filePath, String charset,
			Map hintMap, int qrCodeheight, int qrCodewidth)
					throws WriterException, IOException {

		Path p1 = Paths.get(filePath);
		// encode the data to the hashmap.
		BitMatrix matrix = new MultiFormatWriter().encode(new String(qrCodeData.getBytes(charset), charset),
				BarcodeFormat.QR_CODE, qrCodewidth, qrCodeheight, hintMap);
		// write out QR code to file image
		MatrixToImageWriter.writeToPath(matrix, filePath.substring(filePath.lastIndexOf('.') + 1), p1);
	}

	/*
	 * String readQRCode()
	 * Description: Reads the QR data from the image and returns the string of data
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String readQRCode(String filePath, String charset, Map hintMap)
			throws FileNotFoundException, IOException, NotFoundException {
		BinaryBitmap binaryBitmap = new BinaryBitmap(
				new HybridBinarizer(new BufferedImageLuminanceSource(ImageIO.read(new FileInputStream(filePath)))));
		Result qrCodeResult = new MultiFormatReader().decode(binaryBitmap, hintMap);
		return qrCodeResult.getText();
	}
	
	/*
	 * This method speaks the passed string of text using the voice name.
	 */
	public void dospeak(String speak, String voicename) {	

		System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
	    
		speaktext = speak;				// the text to speak
		String voiceName = voicename;	// this is fixed here
		try {
			SynthesizerModeDesc desc = new SynthesizerModeDesc(null, "general", Locale.US, null, null);
			Synthesizer synthesizer = Central.createSynthesizer(desc);
			synthesizer.allocate();
			synthesizer.resume();
			desc = (SynthesizerModeDesc) synthesizer.getEngineModeDesc();
			
			// find the voice
			Voice[] voices = desc.getVoices();
			Voice voice = null;
			for (int i = 0; i < voices.length; i++) {
				if (voices[i].getName().equals(voiceName)) {
					voice = voices[i];
					break;
				}
			}
			
			synthesizer.getSynthesizerProperties().setVoice(voice);
			System.out.print("Speaking : " + speaktext);
			synthesizer.speakPlainText(speaktext, null);
			synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);
			synthesizer.deallocate();
		} catch (Exception e) {
			String message = " missing speech.properties in " + System.getProperty("user.home") + "\n";
			System.out.println("" + e);
			System.out.println(message);
		}
	}
}

