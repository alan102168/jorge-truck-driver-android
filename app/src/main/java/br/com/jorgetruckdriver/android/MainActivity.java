package br.com.jorgetruckdriver.android;

import android.app.Activity;
import android.os.Bundle;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import java.util.HashMap;
import java.util.Map;

// Ponte entre os controles USB/Bluetooth/receptor sem fio e o jogo HTML.
// O Android traduz os comandos; HDMI apenas espelha a imagem para a TV.
public class MainActivity extends Activity {
    private WebView web;
    private final Map<Integer, PadState> pads = new HashMap<>();
    private boolean ultimoCima, ultimoBaixo, ultimoGas, ultimoFreio;

    private static class PadState {
        boolean cimaBotao, baixoBotao, gasBotao, freioBotao;
        boolean cimaEixo, baixoEixo, gasEixo, freioEixo;
    }

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        telaCheia();
        web = new WebView(this);
        web.setWebViewClient(new WebViewClient());
        WebSettings settings = web.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        web.setFocusable(true);
        web.setFocusableInTouchMode(true);
        setContentView(web);
        web.requestFocus();
        web.loadUrl("file:///android_asset/index.html");
    }

    private void telaCheia() {
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN |
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

    // A funcao controleAndroid vive no index.html e aceita comandos normalizados.
    private void enviar(String acao, boolean ligado) {
        if (web != null) web.evaluateJavascript(
            "window.controleAndroid && window.controleAndroid('" + acao + "'," + ligado + ");", null);
    }

    private PadState pad(int dispositivo) {
        PadState p = pads.get(dispositivo);
        if (p == null) { p = new PadState(); pads.put(dispositivo, p); }
        return p;
    }

    private void atualizar() {
        boolean cima=false, baixo=false, gas=false, freio=false;
        for (PadState p : pads.values()) {
            cima |= p.cimaBotao || p.cimaEixo;
            baixo |= p.baixoBotao || p.baixoEixo;
            gas |= p.gasBotao || p.gasEixo;
            freio |= p.freioBotao || p.freioEixo;
        }
        if (cima != ultimoCima) { ultimoCima=cima; enviar("subir",cima); }
        if (baixo != ultimoBaixo) { ultimoBaixo=baixo; enviar("descer",baixo); }
        if (gas != ultimoGas) { ultimoGas=gas; enviar("acelerar",gas); }
        if (freio != ultimoFreio) { ultimoFreio=freio; enviar("frear",freio); }
    }

    private boolean controle(KeyEvent e) {
        int s=e.getSource();
        return ((s & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD)
            || ((s & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK)
            || ((s & InputDevice.SOURCE_DPAD) == InputDevice.SOURCE_DPAD);
    }

    @Override public boolean dispatchKeyEvent(KeyEvent e) {
        if (!controle(e)) return super.dispatchKeyEvent(e);
        int action=e.getAction();
        if (action!=KeyEvent.ACTION_DOWN && action!=KeyEvent.ACTION_UP) return super.dispatchKeyEvent(e);
        boolean pressed=action==KeyEvent.ACTION_DOWN;
        PadState p=pad(e.getDeviceId());
        switch(e.getKeyCode()) {
            case KeyEvent.KEYCODE_DPAD_UP:
                p.cimaBotao=pressed; atualizar(); return true;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                p.baixoBotao=pressed; atualizar(); return true;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_BUTTON_R1:
            case KeyEvent.KEYCODE_BUTTON_R2:
                p.gasBotao=pressed; atualizar(); return true;
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_BUTTON_L1:
            case KeyEvent.KEYCODE_BUTTON_L2:
                p.freioBotao=pressed; atualizar(); return true;
            case KeyEvent.KEYCODE_BUTTON_A:
            case KeyEvent.KEYCODE_BUTTON_X:
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_BUTTON_1:
                if (pressed && e.getRepeatCount()==0) enviar("iniciar",true);
                return true;
            case KeyEvent.KEYCODE_BUTTON_START:
            case KeyEvent.KEYCODE_BUTTON_SELECT:
                if (pressed && e.getRepeatCount()==0) enviar("pausar",true);
                return true;
            case KeyEvent.KEYCODE_BUTTON_Y:
                if (pressed && e.getRepeatCount()==0) enviar("reiniciar",true);
                return true;
            default:
                return super.dispatchKeyEvent(e);
        }
    }

    private float eixo(MotionEvent e, int axis) {
        InputDevice device=e.getDevice();
        if (device==null || device.getMotionRange(axis, e.getSource())==null) return 0f;
        return e.getAxisValue(axis);
    }

    @Override public boolean onGenericMotionEvent(MotionEvent e) {
        int source=e.getSource();
        if ((source & InputDevice.SOURCE_JOYSTICK)==InputDevice.SOURCE_JOYSTICK &&
                e.getActionMasked()==MotionEvent.ACTION_MOVE) {
            PadState p=pad(e.getDeviceId());
            float vertical=eixo(e,MotionEvent.AXIS_Y);
            float hat=eixo(e,MotionEvent.AXIS_HAT_Y);
            p.cimaEixo=(vertical < -0.55f || hat < -0.55f);
            p.baixoEixo=(vertical > 0.55f || hat > 0.55f);

            // Gatilhos: modelos Xbox, DualSense e muitos controles genericos.
            float rt=Math.max(eixo(e,MotionEvent.AXIS_RTRIGGER),
                              eixo(e,MotionEvent.AXIS_GAS));
            float lt=Math.max(eixo(e,MotionEvent.AXIS_LTRIGGER),
                              eixo(e,MotionEvent.AXIS_BRAKE));
            p.gasEixo=rt > 0.22f;
            p.freioEixo=lt > 0.22f;
            atualizar();
            return true;
        }
        return super.onGenericMotionEvent(e);
    }

    @Override public void onWindowFocusChanged(boolean focused) {
        super.onWindowFocusChanged(focused);
        if (focused) telaCheia();
    }

    @Override protected void onPause() {
        pads.clear();
        ultimoCima=ultimoBaixo=ultimoGas=ultimoFreio=false;
        enviar("limpar",false);
        super.onPause();
    }

    @Override protected void onDestroy() {
        if (web != null) web.destroy();
        super.onDestroy();
    }
}
