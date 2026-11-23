package br.com.jorgetruckdriver.android;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.InputDevice;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings;

public class MainActivity extends Activity {
    private WebView web;
    private boolean up = false, down = false, accelerate = false, brake = false;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        web = new WebView(this);
        web.setWebViewClient(new WebViewClient());
        WebSettings settings = web.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setDomStorageEnabled(true);
        web.setFocusable(true);
        web.setFocusableInTouchMode(true);
        setContentView(web);
        web.loadUrl("file:///android_asset/index.html");
    }

    private void js(String code) {
        web.evaluateJavascript(code, null);
    }
    private void key(String key, boolean pressed) {
        js("(function(){var e=new KeyboardEvent('"+(pressed?"keydown":"keyup")+"',{key:'"+key+"',bubbles:true});document.dispatchEvent(e)})()");
    }
    private void lane(boolean upper, boolean pressed) {
        if (upper) { if (up != pressed) { up=pressed; key("w", pressed); } }
        else { if (down != pressed) { down=pressed; key("s", pressed); } }
    }
    private void pedal(boolean gas, boolean pressed) {
        if (gas) { if(accelerate != pressed){accelerate=pressed;key("d",pressed);} }
        else { if(brake != pressed){brake=pressed;key("a",pressed);} }
    }
    private boolean gamepad(KeyEvent e) {
        int source = e.getSource();
        return ((source & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD)
            || ((source & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK);
    }
    @Override public boolean dispatchKeyEvent(KeyEvent e) {
        if (!gamepad(e)) return super.dispatchKeyEvent(e);
        boolean pressed = e.getAction() == KeyEvent.ACTION_DOWN;
        if (e.getAction()!=KeyEvent.ACTION_DOWN && e.getAction()!=KeyEvent.ACTION_UP) return true;
        if (e.getRepeatCount()>0) return true;
        switch(e.getKeyCode()) {
            case KeyEvent.KEYCODE_DPAD_UP: lane(true,pressed);return true;
            case KeyEvent.KEYCODE_DPAD_DOWN: lane(false,pressed);return true;
            case KeyEvent.KEYCODE_BUTTON_R2: pedal(true,pressed);return true;
            case KeyEvent.KEYCODE_BUTTON_L2: pedal(false,pressed);return true;
            case KeyEvent.KEYCODE_BUTTON_A:
                if(pressed) js("document.getElementById('iniciar')?.click()");
                return true;
            case KeyEvent.KEYCODE_BUTTON_START:
                if(pressed) key("p",true);
                return true;
            case KeyEvent.KEYCODE_BUTTON_Y:
                if(pressed) js("document.getElementById('reiniciar')?.click()");
                return true;
            default:return super.dispatchKeyEvent(e);
        }
    }
    @Override public boolean onGenericMotionEvent(MotionEvent e) {
        if ((e.getSource() & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK && e.getAction()==MotionEvent.ACTION_MOVE) {
            float y=e.getAxisValue(MotionEvent.AXIS_Y);
            lane(true,y < -0.55f);
            lane(false,y > 0.55f);
            float rt=e.getAxisValue(MotionEvent.AXIS_RTRIGGER);
            float lt=e.getAxisValue(MotionEvent.AXIS_LTRIGGER);
            pedal(true,rt > 0.2f);
            pedal(false,lt > 0.2f);
            return true;
        }
        return super.onGenericMotionEvent(e);
    }
    @Override protected void onDestroy() {
        web.destroy();
        super.onDestroy();
    }
}
