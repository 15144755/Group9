package com.example.testble;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.SeekBar;

import android.widget.Button;
import android.widget.ToggleButton;

import org.puredata.android.io.AudioParameters;
import org.puredata.android.io.PdAudio;
import org.puredata.android.utils.PdUiDispatcher;
import org.puredata.core.PdBase;
import org.puredata.core.PdReceiver;
import org.puredata.core.utils.IoUtils;

import java.io.File;
import java.io.IOException;

public class MatchActivity extends Activity  implements  SensorEventListener  {
    private final static String TAG = BluetoothControlActivity.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private String mDeviceName;
    private String mDeviceAddress;
    private HolloBluetooth mble;
    private Context context;

    private ScrollView scrollView;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Handler mHandler;
    public String received;
    public String A3Value;
    public String A0Value;
    public String A2Value;
    public String A1Value;

    //public String D6Value;

    private static final int MSG_DATA_CHANGE = 0x11;


    Button keyC;
    Button keyD;
    Button keyE;
    Button keyF;
    Button keyG;
    Button keyA;
    Button keyB;
    Button noteE;
    Button noteFsharp;
    Button noteGsharp;
    Button noteA;
    Button noteB;
    Button noteCsharp;
    Button noteDsharp;
    Button chordE;
    Button chordFsharpdim;
    Button chordGsharpdim;
    Button chordA;
    Button chordB;
    Button chordCsharpdim;
    Button chordDsharpdim;


    public String toSend;

    StringBuilder output = new StringBuilder();


    private PdUiDispatcher dispatcher;

    private SeekBar volume3;
    private SeekBar volume2;
    private SeekBar reverb1;


    float volume3Value = 0.0f;
    float volume2Value = 0.0f;
    float reverb1Value = 0.0f;


    private void initPD() throws IOException {
        int sampleRate = AudioParameters.suggestSampleRate();
        PdAudio.initAudio(sampleRate, 1, 2, 8, true);

        dispatcher = new PdUiDispatcher();

        PdBase.setReceiver(dispatcher);

        dispatcher.addListener("ble", receiver);
        PdBase.subscribe("ble");

    }


    public void sendPatchData(String receive, String value) {

        sendFloatPD(receive, Float.parseFloat(value));

        Log.e(receive, value);

    }

    public void sendFloatPD(String receiver, Float value) {
        PdBase.sendFloat(receiver, value);
    }

    public void sendBangPD(String receiver) {
        PdBase.sendBang(receiver);
    }


    private void loadPDPatch(String patchName) throws IOException {
        File dir = getFilesDir();
        try {
            IoUtils.extractZipResource(getResources().openRawResource(R.raw.synth), dir, true);
            File pdPatch = new File(dir, patchName);
            PdBase.openPatch(pdPatch.getAbsolutePath());
        } catch (IOException e) {

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);
        final Intent intent = getIntent();

        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        ToggleButton onOffSwitch = (ToggleButton) findViewById(R.id.onOffSwitch);
        ToggleButton onOffSwitch2 = (ToggleButton) findViewById(R.id.onOffSwitch2);
        onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                float val = (isChecked) ? 1.0f : 0.0f; // value = (get value of isChecked, if true val = 1.0f, if false val = 0.0f)
                sendFloatPD("onOff", val); //send value to patch, receiveEvent names onOff


            }
        });

        onOffSwitch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                float val = (isChecked) ? 1.0f : 0.0f; // value = (get value of isChecked, if true val = 1.0f, if false val = 0.0f)
                sendFloatPD("onOff2", val); //send value to patch, receiveEvent names onOff


            }
        });


        SeekBar reverb1 = (SeekBar) findViewById(R.id.reverb1);

        SeekBar volume2 = (SeekBar) findViewById(R.id.volume2);
        SeekBar volume3 = (SeekBar) findViewById(R.id.volume3);


        volume2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                volume2Value = i / 100.0f;
                sendFloatPD("amp3", volume2Value);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        volume3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                volume3Value = i / 100.0f;
                sendFloatPD("amp1", volume3Value);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        reverb1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                reverb1Value = i / 100.0f;
                sendFloatPD("reverbSlider", reverb1Value);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        keyC = (Button) findViewById(R.id.keyC);
        keyD = (Button) findViewById(R.id.keyD);
        keyE = (Button) findViewById(R.id.keyE);
        keyF = (Button) findViewById(R.id.keyF);
        keyG = (Button) findViewById(R.id.keyG);
        keyA = (Button) findViewById(R.id.keyA);
        keyB = (Button) findViewById(R.id.keyB);
        noteE = (Button) findViewById(R.id.noteE);
        noteFsharp = (Button) findViewById(R.id.noteFsharp);
        noteGsharp = (Button) findViewById(R.id.noteGsharp);
        noteA = (Button) findViewById(R.id.noteA);
        noteB = (Button) findViewById(R.id.noteB);
        noteCsharp = (Button) findViewById(R.id.noteCsharp);
        noteDsharp = (Button) findViewById(R.id.noteDsharp);
        chordE = (Button) findViewById(R.id.chordE);
        chordFsharpdim = (Button) findViewById(R.id.chordFsharpdim);
        chordGsharpdim = (Button) findViewById(R.id.chordGsharpdim);
        chordA = (Button) findViewById(R.id.chordA);
        chordB = (Button) findViewById(R.id.chordB);
        chordCsharpdim = (Button) findViewById(R.id.chordCsharpdim);
        chordDsharpdim = (Button) findViewById(R.id.chordDsharpdim);


        keyC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendBangPD("cKey");
                sendBangPD("B");
                sendFloatPD("B", 255.0f);

            }
        });
        keyD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendBangPD("dKey");
                sendBangPD("B");
                sendFloatPD("B", 255.0f);

            }
        });
        keyE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendBangPD("eKey");
                sendBangPD("G");
                sendFloatPD("G", 255.0f);

            }
        });
        keyF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendBangPD("fKey");
                sendBangPD("B");
                sendFloatPD("B", 255.0f);

            }
        });
        keyG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendBangPD("gKey");
                sendFloatPD("R", 255.0f);

            }
        });
        keyA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendBangPD("aKey");
                sendFloatPD("R", 255.0f);

            }
        });
        keyB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendBangPD("bKey");
                sendFloatPD("R", 255.0f);

            }
        });
        noteE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendBangPD("eNote");
                sendBangPD("vibration1");
                sendFloatPD("G", 255.0f);

            }
        });
        noteFsharp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendBangPD("f#Note");
                sendFloatPD("R", 255.0f);
                sendBangPD("vibration1");
            }
        });
        noteGsharp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendBangPD("gNote");
                sendFloatPD("R", 255.0f);
                sendBangPD("vibration1");
            }
        });
        noteA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendBangPD("aNote");
                sendFloatPD("R", 255.0f);
                sendBangPD("vibration1");
            }
        });
        noteB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendBangPD("bNote");
                sendFloatPD("R", 255.0f);
                sendBangPD("vibration1");
            }
        });
        noteCsharp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendBangPD("c#Note");
                sendFloatPD("R", 255.0f);
                sendBangPD("vibration1");
            }
        });
        noteDsharp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendBangPD("d#Note");
                sendFloatPD("R", 255.0f);
                sendBangPD("vibration1");
            }
        });

        chordE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendBangPD("eChord");
                sendBangPD("vibration1");
                sendFloatPD("G", 255.0f);

            }
        });
        chordFsharpdim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendBangPD("f#Chord");
                sendFloatPD("R", 255.0f);
                sendBangPD("vibration1");
            }
        });
        chordGsharpdim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendBangPD("g#Chord");
                sendFloatPD("R", 255.0f);
                sendBangPD("vibration1");
            }
        });
        chordA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendBangPD("aChord");
                sendFloatPD("R", 255.0f);
                sendBangPD("vibration1");
            }
        });
        chordB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendBangPD("bChord");
                sendFloatPD("R", 255.0f);
                sendBangPD("vibration1");
            }
        });
        chordCsharpdim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendBangPD("c#Chord");
                sendFloatPD("R", 255.0f);
                sendBangPD("vibration1");
            }
        });
        chordDsharpdim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendBangPD("d#Chord");
                sendFloatPD("R", 255.0f);
                sendBangPD("vibration1");
            }
        });


        getActionBar().setTitle(mDeviceName);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        context = this;

        mble = HolloBluetooth.getInstance(getApplicationContext());


        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_DATA_CHANGE:
                        int color = msg.arg1;
                        String strData = (String) msg.obj;
                        SpannableStringBuilder builder = new SpannableStringBuilder(strData);

                        //ForegroundColorSpan ，BackgroundColorSpan
                        ForegroundColorSpan colorSpan = new ForegroundColorSpan(color);
                        String string;
                        int num;
                        switch (color) {
                            case Color.BLUE: //send

                                builder.setSpan(colorSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                break;
                            case Color.RED:    //error
                                builder.setSpan(colorSpan, 0, strData.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                break;
                            case Color.BLACK: //tips
                                builder.setSpan(colorSpan, 0, strData.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                break;

                            default: //receive
                                addLogText(strData, Color.BLACK, strData.length());


                                for (int i = 0; i < strData.length(); i++) {
                                    if (strData.charAt(i) == 'A' || strData.charAt(i) == 'B' || strData.charAt(i) == 'C' || strData.charAt(i) == 'D') {
                                        received = output.toString();
                                        // sensorParse();
                                        output.delete(0, output.length());
                                        output.append(strData.charAt(i));

                                    } else {
                                        output.append(strData.charAt(i));
                                    }
                                }

                                break;
                        }

                        break;

                    default:
                        break;
                }
                super.handleMessage(msg);
            }
        };

        new Handler().post(new Runnable() {
            @Override
            public void run() {

                int i;
                for (i = 0; i < 5; i++) {
                    if (mble.connectDevice(mDeviceAddress, bleCallBack))
                        break;

                    try {
                        Thread.sleep(10, 0);
                    } catch (Exception e) {

                    }
                }
                if (i == 5) {

                    return;
                }

                try {
                    Thread.sleep(10, 0);
                } catch (Exception e) {

                }


                if (mble.wakeUpBle()) {

                } else {

                }

            }
        });

        try {
            initPD();
            loadPDPatch("synth.pd"); // This is the name of the patch in the zip

            new Handler().post(new Runnable() {
                @Override
                public void run() {

                    if (!mble.sendData("start")) {
                    }

                }
            });


        } catch (IOException e) {
            finish();
        }
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

    }


    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {

        return super.onMenuItemSelected(featureId, item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        menu.findItem(R.id.menu_refresh).setActionView(null);

        return super.onCreateOptionsMenu(menu);
    }

    void sensorParse() {
        // if(received.length()>0) {
        //if (received.charAt(0) == 'A') {
        //A2Value = received.substring(1);
        //Log.i("A2",A2Value);
        // A2Input.setText(A2Value);
        //sendPatchData("a_input_2", A2Value);
        // }
        if (received.charAt(0) == 'B') {
            A0Value = received.substring(1);
            Log.i("A0", A0Value);
            sendPatchData("a_input_0", A0Value);
        } else if (received.charAt(0) == 'C') {
            A1Value = received.substring(1);
            Log.i("A1", A2Value);
            sendPatchData("a_input_1", A1Value);
        } else if (received.charAt(0) == 'D') {
            A3Value = received.substring(1);
            Log.i("A3", A3Value);
            sendPatchData("a_input_3", A3Value);
        }


    }


    void addLogText(final String log, final int color, int byteLen) {
        Message message = new Message();
        message.what = MSG_DATA_CHANGE;
        message.arg1 = color;
        message.arg2 = byteLen;
        message.obj = log;
        mHandler.sendMessage(message);
    }

    HolloBluetooth.OnHolloBluetoothCallBack bleCallBack = new HolloBluetooth.OnHolloBluetoothCallBack() {

        @Override
        public void OnHolloBluetoothState(int state) {
            if (state == HolloBluetooth.HOLLO_BLE_DISCONNECTED) {
                onBackPressed();
            }
        }

        @Override
        public void OnReceiveData(byte[] recvData) {
            addLogText(ConvertData.bytesToHexString(recvData, false), Color.rgb(139, 0, 255), recvData.length);


        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onResume() {
        super.onResume();
        PdAudio.startAudio(this);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        PdAudio.stopAudio();
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mble.disconnectDevice();
        Log.d(TAG, "destroy");
        mble.disconnectLocalDevice();
        Log.d(TAG, "destroyed");
    }

    private PdReceiver receiver = new PdReceiver() {

        private void pdPost(final String msg) {
            Log.e("RECEIVED:", msg);


            while (!mble.sendData(msg)) {
                //  Log.e("BLEWRITE","ERROR");
            }

            sendFloatPD("stop", 1.0f);

        }


        @Override
        public void print(String s) {
            Log.i("PRINT", s);
        }

        @Override
        public void receiveBang(String source) {
            //pdPost("bang");
        }

        @Override
        public void receiveFloat(String source, float x) {

        }

        @Override
        public void receiveList(String source, Object... args) {

        }

        @Override
        public void receiveMessage(String source, String symbol, Object... args) {
            //  pdPost("list: " + Arrays.toString(args));
            toSend = symbol + ",";
            for (int i = 0; i < args.length; i++) {
                toSend += args[i].toString();
                if (i != args.length - 1) {
                    toSend += ",";
                } else {
                    toSend += ";";
                }
            }
            toSend = toSend.replace(".0", "");
            sendFloatPD("start", 1.0f);
            pdPost(toSend);

        }

        @Override
        public void receiveSymbol(String source, String symbol) {
            //pdPost("symbol: " + symbol);
        }
    };

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            sendFloatPD("accelX", x);
            // accelX.setText(String.valueOf(x));
            sendFloatPD("accelY", y);
            //accelY.setText(String.valueOf(y));
            sendFloatPD("accelZ", z);
            //accelZ.setText(String.valueOf(z));


        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}

