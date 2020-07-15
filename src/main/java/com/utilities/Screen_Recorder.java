package com.utilities;

import org.monte.media.Format;
import org.monte.media.FormatKeys.MediaType;
import org.monte.media.math.Rational;
import org.monte.screenrecorder.ScreenRecorder;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import static org.monte.media.AudioFormatKeys.EncodingKey;
import static org.monte.media.AudioFormatKeys.FrameRateKey;
import static org.monte.media.AudioFormatKeys.KeyFrameIntervalKey;
import static org.monte.media.AudioFormatKeys.MIME_AVI;
import static org.monte.media.AudioFormatKeys.MediaTypeKey;
import static org.monte.media.AudioFormatKeys.MimeTypeKey;
import static org.monte.media.VideoFormatKeys.*;

public class Screen_Recorder extends ScreenRecorder {
	public static ScreenRecorder screenRecorder;
	public String name;

	public Screen_Recorder(GraphicsConfiguration cfg, Rectangle captureArea, Format fileFormat,
                           Format screenFormat, Format mouseFormat, Format audioFormat, File movieFolder, String name)
					throws IOException, AWTException {
		super(cfg, captureArea, fileFormat, screenFormat, mouseFormat, audioFormat, movieFolder);
		this.name = name;

	}
	/**
	 * 
	 * @author -kirankumar
	 * @createdDate - 09-May-2020
	 * @modifiedBy - 
	 * @modifiedDate - 
	 * @description - startRecording
	 */
	public static void startRecording(String methodName,File file) throws Exception {

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int width = screenSize.width;
		int height = screenSize.height;

		Rectangle captureSize = new Rectangle(0, 0, width, height);

		GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().
				getDefaultScreenDevice()
				.getDefaultConfiguration();

		screenRecorder = new Screen_Recorder(gc, captureSize,
				new Format(MediaTypeKey, MediaType.FILE, MimeTypeKey, MIME_AVI),
				new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
						CompressorNameKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE, DepthKey, 24, FrameRateKey,
						Rational.valueOf(15), QualityKey, 1.0f, KeyFrameIntervalKey, 15 * 60),
				new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, "black", FrameRateKey, Rational.valueOf(30)),
				null, file, methodName);

		screenRecorder.start();

	}

	public static void stopRecording() throws Exception {
		screenRecorder.stop();
	}

}