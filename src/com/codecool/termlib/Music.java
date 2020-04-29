package com.codecool.termlib;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

public class Music {

    public static void playMusic (String musicLocation){
        try{
            File musicPath = new File(musicLocation);
            if(musicPath.exists()){
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicPath);
                Clip clip = AudioSystem.getClip();
                clip.open(audioInput);
                clip.start();

                Thread.sleep(clip.getMicrosecondLength()/1000);
            }  else {
                System.out.println("Can't find file");
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    private static float getDuration(File file) throws Exception {
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
        AudioFormat format = audioInputStream.getFormat();
        long audioFileLength = file.length();
        int frameSize = format.getFrameSize();
        float frameRate = format.getFrameRate();
        return ((audioFileLength / (frameSize * frameRate)));

    }
}
