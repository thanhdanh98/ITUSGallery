package hcmus.mdsd.itusgallery;

import android.app.AlarmManager;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import static hcmus.mdsd.itusgallery.FavoriteActivity.favoriteImages;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private ActionBarDrawerToggle toggle;
    private Toolbar toolBar;
    private MyPrefs myPrefs;

    FragmentTransaction ft;
    PicturesActivity pictures;
    AlbumActivity album;
    CloudStorageActivity cloud;
    FavoriteActivity favorite;
    PrivatePicturesActivity private_pictures;

    private int codeOfFragment;
    public static String _name_cloud = "";
    public static String _email_cloud ="";


    public static int position = -1;
    public static int dateNow=11,yearNow=2018,monthNow=11;
    public static String pathImage ="";
    public static Calendar calendar=Calendar.getInstance();
    private NotificationManagerCompat notificationManager;

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("defaultFragment", codeOfFragment);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        myPrefs = new MyPrefs(this);

        notificationManager = NotificationManagerCompat.from(this);

        setNightmode();
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        Gson gson = new Gson();
        String json = sharedPreferences.getString("savedFavoriteImages","");

        SharedPreferences sharedPreferences2 = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        MainActivity._name_cloud = sharedPreferences2.getString("name_cloud", "");

        SharedPreferences sharedPreferences3 = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        MainActivity._email_cloud = sharedPreferences3.getString("email_cloud","");

        SharedPreferences sharedPreferences4 = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        MainActivity.yearNow= sharedPreferences4.getInt("yearNo",calendar.get(Calendar.YEAR));

        SharedPreferences sharedPreferences5 = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        MainActivity.monthNow= sharedPreferences5.getInt("monthNo",calendar.get(Calendar.MONTH));

        SharedPreferences sharedPreferences6 = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        MainActivity.dateNow= sharedPreferences6.getInt("dateNo",calendar.get(Calendar.DATE));

        SharedPreferences sharedPreferences7 = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        MainActivity.pathImage= sharedPreferences7.getString("pathImage","");

        Type type = new TypeToken<ArrayList<String>>(){}.getType();
        favoriteImages = gson.fromJson(json, type);

        setContentView(R.layout.activity_main);

        toolBar = findViewById(R.id.nav_actionBar);
        setSupportActionBar(toolBar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer,R.string.open,R.string.close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        Intent intent = getIntent();
        String activity = intent.getStringExtra("SwitchTo");

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        if (activity == null)
        {
            Objects.requireNonNull(getSupportActionBar()).setTitle("Images");
        }
        else
        {
            Objects.requireNonNull(getSupportActionBar()).setTitle("Private Pictures");
        }

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState!=null)
        {
            int id = savedInstanceState.getInt("defaultFragment");
            switch(id) {
                case R.id.nav_album:{
                    toolBar.setTitle("Album");
                    ft = getFragmentManager().beginTransaction();
                    album = AlbumActivity.newInstance();
                    ft.replace(R.id.content_frame, album);
                    ft.commit();
                    break;
                }
                case R.id.nav_favorite:{
                    toolBar.setTitle("Favorite");
                    ft = getFragmentManager().beginTransaction();
                    favorite = FavoriteActivity.newInstance();
                    ft.replace(R.id.content_frame, favorite);
                    ft.commit();
                    break;
                }
                case R.id.nav_cloud:{
                    toolBar.setTitle("Cloud Storage");
                    ft = getFragmentManager().beginTransaction();
                    cloud = CloudStorageActivity.newInstance();
                    ft.replace(R.id.content_frame, cloud);
                    ft.commit();
                    break;
                }
                case R.id.nav_private_pictures:{
                    toolBar.setTitle("Private Pictures");
                    ft = getFragmentManager().beginTransaction();
                    private_pictures = PrivatePicturesActivity.newInstance();
                    ft.replace(R.id.content_frame, private_pictures);
                    ft.commit();
                    break;
                }
                default:{
                    toolBar.setTitle("Images");
                    ft = getFragmentManager().beginTransaction();
                    pictures = PicturesActivity.newInstance();
                    ft.replace(R.id.content_frame, pictures);
                    ft.commit();
                    break;
                }
            }
            codeOfFragment = id;
        }
        else {
            // Kiểm tra để chuyển về đúng fragment
            if (activity == null) {
                ft = getFragmentManager().beginTransaction();
                pictures = PicturesActivity.newInstance();
                ft.replace(R.id.content_frame, pictures);
                ft.commit();
                toolBar.setTitle("Images");
            }
            else {
                ft = getFragmentManager().beginTransaction();
                private_pictures = PrivatePicturesActivity.newInstance();
                ft.replace(R.id.content_frame, private_pictures);
                ft.commit();
                toolBar.setTitle("Private Pictures");
            }
        }
        createNotification();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; add items to the action bar
        getMenuInflater().inflate(R.menu.status_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // user clicked a menu-item from ActionBar
        if(toggle.onOptionsItemSelected(item)){
            return true;
        }
        int id = item.getItemId();
        if (id == R.id.action_search) {
            // perform SEARCH operations...
            Toast.makeText(getApplicationContext(), "This function is still under development", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.action_camera) {
            // perform CAMERA operations...
            startActivity(new Intent(getApplicationContext(),FaceActivity.class));
            return true;
        }
        if (id == R.id.action_notification) {
            if(pathImage!=""){
                Calendar cal = Calendar.getInstance();
                int date=cal.get(Calendar.DATE);
                if(getDateImage(pathImage)==date  ){
                    Intent intent = new Intent(MainActivity.this, ShareMemoryActivity.class);
                    startActivity(intent);
                }
                else if(position==-1){
                    Toast.makeText(getApplicationContext(), "Không có thông báo", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(getApplicationContext(), "Không có thông báo", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return false;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_pictures) {
            toolBar.setTitle("Images");
            ft = getFragmentManager().beginTransaction();
            pictures = PicturesActivity.newInstance();
            ft.replace(R.id.content_frame, pictures);
            ft.commit();
            codeOfFragment = R.id.nav_pictures;
        }
        else if (id == R.id.nav_album) {
            toolBar.setTitle("Album");
            ft = getFragmentManager().beginTransaction();
            album = AlbumActivity.newInstance();
            ft.replace(R.id.content_frame, album);
            ft.commit();
            codeOfFragment = R.id.nav_album;
        }
        else if (id == R.id.nav_favorite) {
            toolBar.setTitle("Favorite");

            ft = getFragmentManager().beginTransaction();
            favorite = FavoriteActivity.newInstance();
            ft.replace(R.id.content_frame, favorite);
            ft.commit();
            codeOfFragment = R.id.nav_favorite;
        }
        else if (id == R.id.nav_cloud) {
            toolBar.setTitle("Cloud Storage");
            ft = getFragmentManager().beginTransaction();
            cloud = CloudStorageActivity.newInstance();
            ft.replace(R.id.content_frame, cloud);
            ft.commit();
            codeOfFragment = R.id.nav_cloud;
        }
        else if (id == R.id.nav_private_pictures) {
            String password = myPrefs.getPassword();

            if (password.matches("")) {
                toolBar.setTitle("Private Pictures");
                ft = getFragmentManager().beginTransaction();
                private_pictures = PrivatePicturesActivity.newInstance();
                ft.replace(R.id.content_frame, private_pictures);
                ft.commit();
                codeOfFragment = R.id.nav_private_pictures;
            } else {
                String activity = "PrivatePicturesActivity";

                myPrefs.setPassMode(0);

                Intent i = new Intent(MainActivity.this, PasswordActivity.class);
                i.putExtra("SwitchTo", activity); // Lưu vị trí để chuyển sang activity kế
                startActivity(i);
            }
        }
        else if (id == R.id.nav_settings) {
            startActivity(new Intent(MainActivity.this,SettingsActivity.class));
            finish();
        }
        else if (id == R.id.nav_feedback) {
            startActivity(new Intent(MainActivity.this,FeedbackActivity.class));
        }
        else if (id == R.id.nav_help) {
            startActivity(new Intent(MainActivity.this,HelpActivity.class));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public boolean CheckTime(int curHour,int curMinute, int hourStart, int minuteStart, int hourEnd, int minuteEnd) {
        boolean nightmode = true;
        if(hourStart < hourEnd){
            if(hourStart <= curHour && curHour <= hourEnd){
                if(hourStart == curHour){
                    if(minuteStart > curMinute){
                        nightmode = false;
                    }
                }
                if(hourEnd == curHour){
                    if(curMinute > minuteEnd){
                        nightmode = false;
                    }
                }
            }
            else{
                nightmode = false;
            }
        }
        else if(hourStart == hourEnd){
            if(hourStart == curHour){
                if(minuteStart<minuteEnd){
                    if(minuteStart > curMinute || curMinute > minuteEnd){
                        nightmode = false;
                    }
                }
                else if(minuteStart>minuteEnd){
                    if(minuteEnd <= curMinute && curMinute <= minuteStart){
                        nightmode = false;
                    }
                }
            }
            else{
                nightmode = false;
            }
        }
        else{
            if(hourEnd >= curHour || curHour >= hourStart){
                if(hourStart == curHour){
                    if(minuteStart > curMinute){
                        nightmode = false;
                    }
                }
                if(hourEnd == curHour){
                    if(curMinute > minuteEnd){
                        nightmode = false;
                    }
                }
            }
        }
        return nightmode;
    }
    public void setNightmode(){
        Calendar c = Calendar.getInstance();
        int hour, minute;
        if(myPrefs.loadNightModeState() == 0){
            setTheme(R.style.DayNoActionBarTheme);
        }
        else if(myPrefs.loadNightModeState() == 1){
            setTheme(R.style.NightNoActionBarTheme);
        }
        else if(myPrefs.loadNightModeState() == 2) {
            hour = c.get(Calendar.HOUR_OF_DAY);
            if(6 <= hour && hour <= 17){
                setTheme(R.style.DayNoActionBarTheme);
            }
            else{
                setTheme(R.style.NightNoActionBarTheme);
            }
        }
        else{
            hour = c.get(Calendar.HOUR_OF_DAY);
            minute = c.get(Calendar.MINUTE);
            boolean nightmode = CheckTime(hour,minute,myPrefs.loadStartHour(),myPrefs.loadStartMinute(),myPrefs.loadEndHour(),myPrefs.loadEndMinute());
            if(nightmode){
                setTheme(R.style.NightNoActionBarTheme);
            } else{
                setTheme(R.style.DayNoActionBarTheme);
            }
        }
    }

    //memory notification
    private void getPosition(){
        Calendar cal;
        for (int i=0;i<FavoriteActivity.favoriteImages.size();i++){
            cal=getTimeImage(i);
            if (compareTime(calendar,cal)==true){
                position=i;
            }
        }
    }
    private void createNotification(){
        if(FavoriteActivity.favoriteImages!=null){

            getPosition();
            Calendar cal = Calendar.getInstance();
            int date=cal.get(Calendar.DATE);
            if(position>=0 && dateNow==date){
                pathImage=FavoriteActivity.favoriteImages.get(position);
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("pathImage",pathImage);
                editor.commit();

                Intent activityIntent = new Intent(this, ShareMemoryActivity.class);
                PendingIntent contentIntent = PendingIntent.getActivity(this,
                        0, activityIntent, 0);
                Bitmap picture = BitmapFactory.decodeFile(FavoriteActivity.favoriteImages.get(position));
                Notification notification = new NotificationCompat.Builder(this, "channel1")
                        .setSmallIcon(R.drawable.app_logo)
                        .setContentTitle("Ảnh kỉ niệm")
                        .setContentText("Ngày này 1 năm trước bạn đã có những kỉ niệm tuyệt vời")
                        .setLargeIcon(picture)
                        .setStyle(new NotificationCompat.BigPictureStyle()
                                .bigPicture(picture)
                                .bigLargeIcon(null))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .setContentIntent(contentIntent)
                        .setAutoCancel(true)
                        .setOnlyAlertOnce(true)
                        .build();
                notificationManager.notify(1, notification);
                addOneDay();
            }
            else if(dateNow==date){
                addOneDay();
            }
        }

    }
    private boolean compareTime(Calendar a, Calendar b){
        int year1=a.get(Calendar.YEAR);
        int date1=a.get(Calendar.DATE);
        int month1=a.get(Calendar.MONTH);

        int year2=b.get(Calendar.YEAR);
        int date2=b.get(Calendar.DATE);
        int month2=b.get(Calendar.MONTH);
        if(year1==year2 && month1==month2 && date1==date2){
            return true;
        }
        return false;
    }
    private Calendar getTimeImage(int i){
        File file = new File(FavoriteActivity.favoriteImages.get(i));
        Date d = new Date(file.lastModified());
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        return cal;
    }
    private int getDateImage(String path){
        int date;
        File file = new File(path);
        Date d = new Date(file.lastModified());
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        date=cal.get(Calendar.DATE);
        return date;
    }

    private void changeIntToCalender(){

        calendar.set(Calendar.YEAR,yearNow);
        calendar.set(Calendar.MONTH,monthNow);
        calendar.set(Calendar.DATE,dateNow);
    }
    private void changeCalenderToInt(){
        yearNow=calendar.get(Calendar.YEAR);
        monthNow=calendar.get(Calendar.MONTH);
        dateNow=calendar.get(Calendar.DATE);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("yearNo",yearNow);
        editor.commit();

        SharedPreferences sharedPreferences2 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor2 = sharedPreferences2.edit();
        editor2.putInt("monthNo",monthNow);
        editor2.commit();

        SharedPreferences sharedPreferences3 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor3 = sharedPreferences3.edit();
        editor3.putInt("dateNo",dateNow);
        editor3.commit();
    }
    private void addOneDay(){
        changeIntToCalender();
        calendar.add(Calendar.DATE,1);
        changeCalenderToInt();
    }
}
