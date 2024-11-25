package com.oberasoftware.robot.nao;

import com.aldebaran.qi.Application;
import com.aldebaran.qi.Tuple4;
import com.aldebaran.qi.helper.proxies.ALAudioRecorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleTest {
    private static final Logger LOG = LoggerFactory.getLogger(SimpleTest.class);

    public static void main(String[] args) {
        String robotUrl = "tcp://10.1.0.215:9559";
        Application application = new Application(args, robotUrl);
        application.start();
        try {
//            ALTextToSpeech tts = new ALTextToSpeech(application.session());
//            tts.say("Hello World");
//
//            listen(application);

            application.stop();
        } catch (Exception e) {
            LOG.error("Could not complete action", e);
        }
    }

    public static void listen(Application application) throws Exception {
        ALAudioRecorder recorder = new ALAudioRecorder(application.session());

        recorder.startMicrophonesRecording("/home/nao/test.wav", "wav", 16000, new Tuple4<>(0,0,0,1));
        //what is this object supposted to be replaced by - the 4
        Thread.sleep(5000);
        recorder.stopMicrophonesRecording();
    }
}
