package ca.uwaterloo.lab4_201_24;

import mapper.MapLoader;
import mapper.MapView;
import mapper.NavigationalMap;
import mapper.PositionListener;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements PositionListener{
    
	//Sensor Variables
    public SensorManager sensorManager;
    public Sensor accelerometerSensor, magneticfieldSensor, lAccelerometerSensor;
    public SensorListener lAccel, Magn, Accel;
    public TextView LAsensor, mfsensor, asensor, orientTxt, distanceT, distancet2;
    Button pButton, sButton, rButton, cButton, uButton;
    Orientation orient;
    
    //Map Variables
    MapView mv;
	private TextView start;
	private TextView end;
	private TextView instructions;
	PointF scale = new PointF(40, 40);
	
	//Misc Variables
    LinearLayout ll;
    TextView title;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //Reset Button
        ll = (LinearLayout) findViewById(R.id.label1);
        //Set the texts for title to author names 
        ll.setOrientation(LinearLayout.VERTICAL);
         
        Button rpButton = (Button) this.findViewById(R.id.resetPath);
        rpButton.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	orient.resetPath();
	        }
        });
       
        //Text views that are used to output the values
        orientTxt = new TextView(getApplicationContext());//orient view
        distanceT = new TextView(getApplicationContext());//distance view
        distancet2 = new TextView(getApplicationContext());//distance view
        start = new TextView(getApplicationContext()); //Start position
        end = new TextView(getApplicationContext());//End position
        instructions =  new TextView(getApplicationContext());//Instructions for user
       
        //Creating sensors
        LAsensor = new TextView(getApplicationContext());//needed for LinearAccelerometer
        lAccel = new SensorListener(LAsensor); //Sensor listener controls Linear Accel sensor
        
        asensor = new TextView(getApplicationContext());//needed forAccelerometer
        Accel = new SensorListener(asensor);//Sensor listener controls Accel sensor
       
        mfsensor = new TextView(getApplicationContext());//needed for Magnetic 
        Magn = new SensorListener(mfsensor);//Sensor listener controls Magn sensor
        
        //New orientation using the three sensors and a textview for updating
        orient = new Orientation(Accel, Magn, lAccel, orientTxt); 
        
        //Creating MapView
        mv = new MapView(getApplicationContext(), 400, 400, 23,23);
        registerForContextMenu(mv);
       
        //Calling external map 
        NavigationalMap map = MapLoader.loadMap(getExternalFilesDir(null), "room.svg");
        
        //Adding views
        ll.addView(start);
        ll.addView(end);
        ll.addView(instructions);
        ll.addView(distanceT);
        ll.addView(distancet2);
        
        mv.setMap(map);
        mv.addListener(this);
        orient.setMap(map);
        
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);//new sensormanager instantiated
        
        //Registering sensors
        lAccelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);//make Laccel a linear sensor
        sensorManager.registerListener(lAccel , lAccelerometerSensor,SensorManager.SENSOR_DELAY_FASTEST);//register lAccel sensor
       
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);//make accel a accel sensor
        sensorManager.registerListener(Accel , accelerometerSensor,SensorManager.SENSOR_DELAY_NORMAL);//register accel sensor
       
        magneticfieldSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);//make magnet a magnetic sensor
        sensorManager.registerListener(Magn, magneticfieldSensor,SensorManager.SENSOR_DELAY_FASTEST);//register mag sensor
       
        //Outputting textViews to the view
        orient.setMapview(mv);
        orient.setInstructions(instructions);
        orient.finishInstructions(distanceT);
        orient.finishI(distancet2);
        ll.addView(mv);    
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    	super.onCreateContextMenu(menu, v, menuInfo);
    	mv.onCreateContextMenu(menu, v, menuInfo);
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	return super.onContextItemSelected(item) || mv.onContextItemSelected(item);
    }
    
	@Override
	public void originChanged(MapView source, PointF loc) {
		start.setText(String.format("Start: x: (%.2f) y:(%.2f) ", loc.x, loc.y));
		mv.setUserPoint(loc);
	}
	
	@Override
	public void destinationChanged(MapView source, PointF dest) {
		end.setText(String.format("End: x: (%.2f) y:(%.2f) ", dest.x, dest.y ));
		if(mv.getDestinationPoint()== mv.getOriginPoint())
			mv.setDestinationPoint(new PointF(0,0));
	}
	
	@Override
	//This method unregisters the accelerometer
    public void onPause() {

	    super.onPause();
	    sensorManager.unregisterListener(lAccel, lAccelerometerSensor);
	    sensorManager.unregisterListener(Accel, accelerometerSensor);
	    sensorManager.unregisterListener(Magn, magneticfieldSensor);
	}
	
	//This method checks to see if the application is resumed by user,
	//if it is then the sensors are re-registered (listened to)
	public void onResume() {

	    super.onResume();
	    sensorManager.registerListener(lAccel, lAccelerometerSensor,SensorManager.SENSOR_DELAY_FASTEST);
	    sensorManager.registerListener(Accel, accelerometerSensor,SensorManager.SENSOR_DELAY_NORMAL);
	    sensorManager.registerListener(Magn, magneticfieldSensor,SensorManager.SENSOR_DELAY_FASTEST);
	}
}