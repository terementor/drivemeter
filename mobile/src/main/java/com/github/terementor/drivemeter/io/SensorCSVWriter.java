package com.github.terementor.drivemeter.io;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;


public class SensorCSVWriter {

    private static final String TAG = SensorCSVWriter.class.getName();
    private static final String HEADER_CSV = "This is a logfile generated by pires.obd.reader";
    //private static final String[] NAMES_COLUMNS = {"TIME", "X", "Y", "Z"};
    private static final String[] NAMES_COLUMNS = {"Name", "Vendor", "Type", "Resolution", "MinDelay", "MaxDelay"};
    private static final String[] NAMES_COLUMNS_ONLY_READINGS = {
            "BAROMETRIC_PRESSURE", "ENGINE_COOLANT_TEMP", "FUEL_LEVEL", "ENGINE_LOAD", "AMBIENT_AIR_TEMP",
            "ENGINE_RPM", "INTAKE_MANIFOLD_PRESSURE", "MAF", "Term Fuel Trim Bank 1",
            "FUEL_ECONOMY", "Long Term Fuel Trim Bank 2", "FUEL_TYPE", "AIR_INTAKE_TEMP",
            "FUEL_PRESSURE", "SPEED", "Short Term Fuel Trim Bank 2",
            "Short Term Fuel Trim Bank 1", "ENGINE_RUNTIME", "THROTTLE_POS", "DTC_NUMBER",
            "TROUBLE_CODES", "TIMING_ADVANCE", "EQUIV_RATIO"};
    private boolean isFirstLine;
    private BufferedWriter buf;
    public static SQLiteDatabase mydatabase = null;

    public SensorCSVWriter(String filename, String dirname) throws FileNotFoundException, RuntimeException {
        try{


            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File(sdCard.getAbsolutePath() + File.separator + dirname);
            if (!dir.exists()) dir.mkdirs();
            Log.d(TAG, "Path is " + sdCard.getAbsolutePath() + File.separator + dirname);
            File file = new File(dir, filename  + ".csv");
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            this.buf = new BufferedWriter(osw);
            this.isFirstLine = true;
            Log.d(TAG, "CSV" + dir + filename  + ".csv");
            Log.d(TAG, "Constructed the LogCSVWriter");

            //Create sqlitedatabase
            //mydatabase = openOrCreateDatabase(sdCard.getAbsolutePath() + File.separator + dirname, Context.MODE_PRIVATE, null);
            //File dbf = new File(dir, filename  + ".db");
            //if(!dbf.exists()){
            //mydatabase = SQLiteDatabase.openOrCreateDatabase(sdCard.getAbsolutePath() + File.separator + dirname + File.separator + filename +  ".db",null);
            //    }

            mydatabase = SQLiteDatabase.openOrCreateDatabase(sdCard.getAbsolutePath() + File.separator + dirname + File.separator + filename + ".db", null, null);
            Log.d(TAG, "DB" + sdCard.getAbsolutePath() + File.separator + dirname + File.separator + filename +  ".db");
            mydatabase.execSQL("CREATE TABLE IF NOT EXISTS MetaData (PhoneTime STRING, WearTime BIGINT, Driver INT, Situation INT, CountAccelerometer BIGINT, CountGyroskop BIGINT, CountMagnetic BIGINT," +
                    " CountWearAccelerometer BIGINT, CountWearGyroskp BIGINT, CountWearMagnetic BIGINT);");
            mydatabase.execSQL("CREATE TABLE IF NOT EXISTS Gyroskop (time BIGINT, x FLOAT, y FLOAT, z FLOAT);");
            mydatabase.execSQL("CREATE TABLE IF NOT EXISTS Accelerometer (time BIGINT, x FLOAT, y FLOAT, z FLOAT);");
            mydatabase.execSQL("CREATE TABLE IF NOT EXISTS Rotation (time BIGINT, x FLOAT, y FLOAT, z FLOAT);");
            mydatabase.execSQL("CREATE TABLE IF NOT EXISTS Magnetic (time BIGINT, x FLOAT, y FLOAT, z FLOAT);");
            mydatabase.execSQL("CREATE TABLE IF NOT EXISTS WearGyroskop (time BIGINT, x FLOAT, y FLOAT, z FLOAT);");
            mydatabase.execSQL("CREATE TABLE IF NOT EXISTS WearAccelerometer (time BIGINT, x FLOAT, y FLOAT, z FLOAT);");
            mydatabase.execSQL("CREATE TABLE IF NOT EXISTS WearRotation (time BIGINT, x FLOAT, y FLOAT, z FLOAT);");
            mydatabase.execSQL("CREATE TABLE IF NOT EXISTS WearMagnetic (time BIGINT, x FLOAT, y FLOAT, z FLOAT);");
            mydatabase.execSQL("CREATE TABLE IF NOT EXISTS GPS (time BIGINT ,Latitude FLOAT, Longitude FLOAT, Altitude FLOAT);");
            mydatabase.execSQL("CREATE TABLE IF NOT EXISTS OBD (time BIGINT, BAROMETRIC_PRESSURE STRING, ENGINE_COOLANT_TEMP STRING, FUEL_LEVEL STRING, ENGINE_LOAD STRING, AMBIENT_AIR_TEMP STRING," +
                    "ENGINE_RPM STRING, INTAKE_MANIFOLD_PRESSURE STRING, MAF STRING, Term Fuel_Trim Bank_1 STRING, FUEL_ECONOMY STRING, Long_Term_Fuel_Trim Bank_2 STRING, FUEL_TYPE STRING, AIR_INTAKE_TEMP STRING," +
                    "FUEL_PRESSURE STRING, SPEED STRING, Short_Term_Fuel_Trim_Bank_2 STRING, Short_Term_Fuel_Trim_Bank_1 STRING, ENGINE_RUNTIME STRING, THROTTLE_POS STRING, DTC_NUMBER STRING, TROUBLE_CODES STRING," +
                    "TIMING_ADVANCE STRING, EQUIV_RATIO STRING);");


            //mydatabase.close();
        }
        catch (Exception e) {
            Log.e(TAG, "LogCSVWriter constructor failed");
        }


    }


    public void closeSensorCSVWriter() {
        try {
            buf.flush();
            buf.close();
            Log.d(TAG, "Flushed and closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Variante zum rausschreiben der Sensordaten in eine CSV-Datei. Wird nun in eine SQLite DB geschrieben
    /*public void writeLineSensorCSV(SensorEvent event) {
        String crl;

        if (isFirstLine) {
            float a = event.values[0];
            float b = event.values[1];
            float c = event.values[2];
            float y = event.timestamp;
            String soso = Float.toString(y) +" "+ Float.toString(a) +" "+ Float.toString(b) +" " + Float.toString(c);

            crl = HEADER_CSV;// + reading.toString();

            addLine(crl);
            isFirstLine = false;

            // Add line with the columns
            crl = "";
            for (String ccln : NAMES_COLUMNS) {
                crl += ccln + ";";
            }
            addLine(crl.substring(0, crl.length() - 1)); // remove last ";"

        } else {

            //crl = .getTimestamp() + ";" +
            //        reading.getLatitude() + ";" +
            //        reading.getLongitude() + ";" +
            //        reading.getAltitude() + ";" +
            //        reading.getVin() + ";";
            float a = event.values[0];
            float b = event.values[1];
            float c = event.values[2];
            float y = event.timestamp;
            crl = Float.toString(y) +";"+ Float.toString(a) +";"+ Float.toString(b) +";" + Float.toString(c)+ ";";

            //Map<String, String> read = reading.getReadings();

            //for (String ccln : NAMES_COLUMNS_ONLY_READINGS) {
            //    crl += read.get(ccln) + ";";
            //}

            addLine(crl.substring(0, crl.length() ));
        }
    }*/

    public void writeLineSensorCSV(String[] data) {
        String crl;

        if (isFirstLine) {

            crl = HEADER_CSV;// + reading.toString();

            addLine(crl);
            isFirstLine = false;

            // Add line with the columns
            crl = "";
            for (String ccln : NAMES_COLUMNS) {
                crl += ccln + ";";
            }
            addLine(crl.substring(0, crl.length() - 1)); // remove last ";"

            //for (String ccln : data) {
            //    crl += ccln + ";";
            //}

        } else {
            crl = "";
            //crl = .getTimestamp() + ";" +
            //        reading.getLatitude() + ";" +
            //        reading.getLongitude() + ";" +
            //        reading.getAltitude() + ";" +
            //crl = data[0] + data[2] + data[3] + data[4] + data[5] + data[6];
            for (String ccln : data) {
                crl += ccln + ";";
            }

            //Map<String, String> read = reading.getReadings();

            //for (String ccln : NAMES_COLUMNS_ONLY_READINGS) {
            //    crl += read.get(ccln) + ";";
            //}

            addLine(crl.substring(0, crl.length() ));
        }
    }


    private void addLine(String line) {
        if (line != null) {
            try {
                buf.write(line, 0, line.length());
                buf.newLine();
                Log.d(TAG, "LogCSVWriter: Wrote" + line);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
