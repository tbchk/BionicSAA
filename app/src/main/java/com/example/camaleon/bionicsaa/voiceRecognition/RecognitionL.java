package com.example.camaleon.bionicsaa.voiceRecognition;

import android.os.Bundle;
import android.speech.RecognitionListener;
import android.util.Log;

/**
 * Created by camaleon on 4/02/15.
 */
public class RecognitionL implements RecognitionListener {

        @Override
        public void onReadyForSpeech(Bundle params) {

        }

        @Override
        public void onBeginningOfSpeech() {

        }

        @Override
        public void onRmsChanged(float rmsdB) {

        }

        @Override
        public void onBufferReceived(byte[] buffer) {

        }

        @Override
        public void onEndOfSpeech() {

        }

        @Override
        public void onError(int error) {

        }

        @Override
        public void onResults(Bundle results) {
            Log.e("Voice R:", "Resultados");
        }

        @Override
        public void onPartialResults(Bundle partialResults) {

        }

        @Override
        public void onEvent(int eventType, Bundle params) {

        }
}